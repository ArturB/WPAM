package org.brodzki.wpam.dao

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.brodzki.wpam.DialogType
import org.brodzki.wpam.VoteForUsActivity
import org.json.JSONArray
import java.net.URLEncoder
import java.util.*

class DAO(private val context: Context) {

    private val utf = "UTF-8"
    private val queue = Volley.newRequestQueue(context)
    private val dataSource =
            context.getSharedPreferences(VoteForUsActivity.SHARED_PREFS, Context.MODE_PRIVATE).
            getString(VoteForUsActivity.DEFAULT_DATA_SOURCE, "")

    private fun doQuery(url: String, successHandler: (String) -> Unit, errorHandler: (VolleyError) -> Unit) {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener { response ->
                    successHandler(response)
                },
                Response.ErrorListener {
                    errorHandler(it)
                }
        )
        stringRequest.retryPolicy = DefaultRetryPolicy(
                60000, 10, 2.0f
        )
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun doQueries(urls: Array<String>, successHandler: (String) -> Unit, errorHandler: (VolleyError) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        var requestNumber = urls.size
        // Request a string response from the provided URL.
        urls@ for(url in urls) {
            var breakF = false
            val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener { response ->
                        requestNumber--
                        if(requestNumber == 0) {
                            successHandler(response)
                        }
                    },
                    Response.ErrorListener {
                        errorHandler(it)
                        breakF = true
                    }
            )
            if (breakF) { break@urls }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                    60000, 10, 2.0f
            )
            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    private fun doJsonQuery(url: String, successHandler: (JSONArray) -> Unit, errorHandler: (VolleyError) -> Unit) {

        val queue = Volley.newRequestQueue(context)
        // Request a string response from the provided URL.
        val stringRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    successHandler(response)
                },
                Response.ErrorListener {
                    errorHandler(it)
                }
        )
        // Add the request to the RequestQueue.
        stringRequest.retryPolicy = DefaultRetryPolicy(
                60000, 10, 2.0f
        )
        queue.add(stringRequest)
    }

    /**
     * LOGIN OPERATIONS
     */

    fun checkCredentials(username: CharSequence,
                         password: CharSequence,
                         successHandler: (String) -> Unit,
                         errorHandler:  (VolleyError) -> Unit) {

        val usernameEnc = URLEncoder.encode(username.toString(), utf)
        val passwordEnc = URLEncoder.encode(password.toString(), utf)
        val url = "$dataSource/login?username=$usernameEnc&password=$passwordEnc"
        doQuery(url, successHandler, errorHandler)
    }

    fun updateUserPassword(username: CharSequence,
                           password: CharSequence,
                           successHandler: (String) -> Unit,
                           errorHandler:  (VolleyError) -> Unit) {

        val usernameEnc = URLEncoder.encode(username.toString(), utf)
        val passwordEnc = URLEncoder.encode(password.toString(), utf)
        val url = "$dataSource/user/update-password?username=$usernameEnc&password=$passwordEnc"
        doQuery(url, successHandler, errorHandler)
    }

    /**
     * RULE CRUD OPERATIONS
     */

    fun createRule(rule: Rule,
                   successHandler: (String) -> Unit,
                   errorHandler: (VolleyError) -> Unit) {

        val url = "$dataSource/rule/create?text=${URLEncoder.encode(rule.Text.toString(), utf)}"
        doQuery(url, {
            rule.id = Integer.parseInt(it)
            updateRule(rule, successHandler, errorHandler)
        }, errorHandler)
    }

    fun readRule(query: CharSequence,
                 successHandler: (Array<Rule>) -> Unit,
                 errorHandler: (VolleyError) -> Unit) {

        val queryEnc = URLEncoder.encode(query.toString(), utf)
        val url = "$dataSource/rule/read?query=$queryEnc"
        doJsonQuery(url, {jsonArray ->

            val rules = LinkedList<Rule>()
            for(i in 0 until jsonArray.length()) {

                val rule = Rule(
                    jsonArray.getJSONObject(i).getInt("id"),
                    jsonArray.getJSONObject(i).getString("Text"),
                    jsonArray.getJSONObject(i).getInt("Openness"),
                    jsonArray.getJSONObject(i).getInt("Conscientiousness"),
                    jsonArray.getJSONObject(i).getInt("Extraversion"),
                    jsonArray.getJSONObject(i).getInt("Agreeableness"),
                    jsonArray.getJSONObject(i).getInt("Neuroticism"),
                    jsonArray.getJSONObject(i).getInt("Leadership"),
                    jsonArray.getJSONObject(i).getInt("Age18-24"),
                    jsonArray.getJSONObject(i).getInt("Age25-34"),
                    jsonArray.getJSONObject(i).getInt("Age35-44"),
                    jsonArray.getJSONObject(i).getInt("Age45-64"),
                    jsonArray.getJSONObject(i).getInt("Age65"),
                    jsonArray.getJSONObject(i).getInt("GenderFemale"),
                    jsonArray.getJSONObject(i).getInt("GenderMale"),
                    jsonArray.getJSONObject(i).getInt("GenderOther"),
                    jsonArray.getJSONObject(i).getInt("OrientationHeterosexual"),
                    jsonArray.getJSONObject(i).getInt("OrientationHomosexual"),
                    jsonArray.getJSONObject(i).getInt("OrientationBisexual"),
                    jsonArray.getJSONObject(i).getInt("OrientationAsexual")
                )
                rules.push(rule)

            }
            successHandler(rules.toTypedArray())

        }, errorHandler)
    }

    fun updateRule(rule: Rule,
                   successHandler: (String) -> Unit,
                   errorHandler: (VolleyError) -> Unit) {

        val ruleEnc = rule.encode()
        val urls = Array(19, { _ -> "" })

        urls[0]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Text&value=${ruleEnc.Text}"
        urls[1]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Openness&value=${ruleEnc.Openness}"
        urls[2]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Conscientiousness&value=${ruleEnc.Conscientiousness}"
        urls[3]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Extraversion&value=${ruleEnc.Extraversion}"
        urls[4]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Agreeableness&value=${ruleEnc.Agreeableness}"
        urls[5]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Neuroticism&value=${ruleEnc.Neuroticism}"
        urls[6]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Leadership&value=${ruleEnc.Leadership}"
        urls[7]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Age18to24&value=${ruleEnc.Age18to24}"
        urls[8]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Age25to34&value=${ruleEnc.Age25to34}"
        urls[9]  = "$dataSource/rule/update?id=${ruleEnc.id}&column=Age35to44&value=${ruleEnc.Age35to44}"
        urls[10] = "$dataSource/rule/update?id=${ruleEnc.id}&column=Age45to64&value=${ruleEnc.Age45to64}"
        urls[11] = "$dataSource/rule/update?id=${ruleEnc.id}&column=Age65&value=${ruleEnc.Age65}"
        urls[12] = "$dataSource/rule/update?id=${ruleEnc.id}&column=GenderFemale&value=${ruleEnc.GenderFemale}"
        urls[13] = "$dataSource/rule/update?id=${ruleEnc.id}&column=GenderMale&value=${ruleEnc.GenderMale}"
        urls[14] = "$dataSource/rule/update?id=${ruleEnc.id}&column=GenderOther&value=${ruleEnc.GenderOther}"
        urls[15] = "$dataSource/rule/update?id=${ruleEnc.id}&column=OrientationHeterosexual&value=${ruleEnc.OrientationHeterosexual}"
        urls[16] = "$dataSource/rule/update?id=${ruleEnc.id}&column=OrientationHomosexual&value=${ruleEnc.OrientationHomosexual}"
        urls[17] = "$dataSource/rule/update?id=${ruleEnc.id}&column=OrientationBisexual&value=${ruleEnc.OrientationBisexual}"
        urls[18] = "$dataSource/rule/update?id=${ruleEnc.id}&column=OrientationAsexual&value=${ruleEnc.OrientationAsexual}"

        doQueries(urls, successHandler, errorHandler)
    }

    fun deleteRule(rule: Rule,
                   successHandler: (String) -> Unit,
                   errorHandler: (VolleyError) -> Unit) {

        val ruleEnc = rule.encode()
        val url = "$dataSource/rule/delete?id=${ruleEnc.id}"
        doQuery(url, successHandler, errorHandler)
    }

    /**
     * USER CRUD OPERATIONS
     */

    fun createUser(user: User,
                   successHandler: (String) -> Unit,
                   errorHandler: (VolleyError) -> Unit) {

        val userEnc = user.encode()
        val url = "$dataSource/user/create?username=${userEnc.Username}&password=${userEnc.Password}&role=${userEnc.Role}"
        doQuery(url, successHandler, errorHandler)
    }

    fun readUser(query: CharSequence,
                 successHandler: (Array<User>) -> Unit,
                 errorHandler: (VolleyError) -> Unit) {

        val queryEnc = URLEncoder.encode(query.toString(), utf)
        val url = "$dataSource/user/read?query=$queryEnc"

        doJsonQuery(url, {jsonArray ->

            val users = LinkedList<User>()
            for(i in 0 until jsonArray.length()) {

                val user =  User(
                    jsonArray.getJSONObject(i).getInt("id"),
                    jsonArray.getJSONObject(i).getString("Username"),
                    jsonArray.getJSONObject(i).getString("PasswordHash"),
                    jsonArray.getJSONObject(i).getString("LastLogin"),
                    Role.fromString(jsonArray.getJSONObject(i).getString("Role"))
                )
                users.push(user)
            }
            successHandler(users.toTypedArray())

        }, errorHandler)
    }

    fun updateUser(old_username: CharSequence,
                   user: User,
                   successHandler: (String) -> Unit,
                   errorHandler: (VolleyError) -> Unit) {

        val userEnc = user.encode()
        val urls = Array(2, { _ -> "" })
        urls[0] = "$dataSource/user/update?username=$old_username&column=Username&value=${userEnc.Username}"
        urls[1] = "$dataSource/user/update?username=${userEnc.Username}&column=Role&value=${userEnc.Role}"
        doQueries(urls, successHandler, errorHandler)
    }

    fun deleteUser(user: User,
                   successHandler: (String) -> Unit,
                   errorHandler: (VolleyError) -> Unit) {

        val userEnc = user.encode()
        val url = "$dataSource/user/delete?username=${userEnc.Username}"
        doQuery(url, successHandler, errorHandler)
    }

    /**
     * VOTER CRUD OPERATIONS
     */

    fun createVoter(voter: Voter,
                    successHandler: (String) -> Unit,
                    errorHandler: (VolleyError) -> Unit) {

        val url = "$dataSource/voter/create?address=${URLEncoder.encode(voter.Address.toString(), utf)}&latitude=${voter.Latitude}&longitude=${voter.Longitude}"

        //VoteForUsActivity.showSimpleDialog(context, DialogType.INFO, voter.toString())

        doQuery(url, {
            val id = Regex("[0-9]+").find(it)!!.value
            try {
                voter.id = Integer.parseInt(id)
                updateVoter(voter, successHandler, errorHandler)
            }
            catch(n: NumberFormatException) {
                VoteForUsActivity.showSimpleDialog(context, DialogType.ERROR, "Parse error: $id, parse")
            }
        }, errorHandler)

    }

    fun readVoter(query: CharSequence,
                  successHandler: (Array<Voter>) -> Unit,
                  errorHandler: (VolleyError) -> Unit) {

        val queryEnc = URLEncoder.encode(query.toString(), utf)
        val url = "$dataSource/voter/read?query=$queryEnc"
        doJsonQuery(url, {jsonArray ->

            val voters = LinkedList<Voter>()
            for(i in 0 until jsonArray.length()) {

                val voter = Voter(
                    jsonArray.getJSONObject(i).getInt("id"),
                    jsonArray.getJSONObject(i).getString("Name"),
                    jsonArray.getJSONObject(i).getString("Surname"),
                    jsonArray.getJSONObject(i).getString("Address"),
                    jsonArray.getJSONObject(i).getDouble("Latitude"),
                    jsonArray.getJSONObject(i).getDouble("Longitude"),
                    jsonArray.getJSONObject(i).getInt("Openness"),
                    jsonArray.getJSONObject(i).getInt("Conscientiousness"),
                    jsonArray.getJSONObject(i).getInt("Extraversion"),
                    jsonArray.getJSONObject(i).getInt("Agreeableness"),
                    jsonArray.getJSONObject(i).getInt("Neuroticism"),
                    jsonArray.getJSONObject(i).getInt("Leadership"),
                    jsonArray.getJSONObject(i).getInt("Age"),
                    Gender.fromString(jsonArray.getJSONObject(i).getString("Gender")),
                    Orientation.fromString(jsonArray.getJSONObject(i).getString("Orientation")),
                    jsonArray.getJSONObject(i).getString("PoliticalAffiliation")
                )

                voters.push(voter)
            }
            successHandler(voters.toTypedArray())

        }, errorHandler)
    }

    fun updateVoter(voter: Voter,
                    successHandler: (String) -> Unit,
                    errorHandler: (VolleyError) -> Unit) {

        val voterEnc = voter.encode()
        val urls = Array(13, { _ -> "" })

        urls[0]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Name&value=${voterEnc.Name}"
        urls[1]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Surname&value=${voterEnc.Surname}"
        urls[2]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Address&value=${voterEnc.Address}"
        urls[3]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Openness&value=${voterEnc.Openness}"
        urls[4]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Conscientiousness&value=${voterEnc.Conscientiousness}"
        urls[5]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Extraversion&value=${voterEnc.Extraversion}"
        urls[6]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Agreeableness&value=${voterEnc.Agreeableness}"
        urls[7]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Neuroticism&value=${voterEnc.Neuroticism}"
        urls[8]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Leadership&value=${voterEnc.Leadership}"
        urls[9]  = "$dataSource/voter/update?id=${voterEnc.id}&column=Age&value=${voterEnc.Age}"
        urls[10] = "$dataSource/voter/update?id=${voterEnc.id}&column=Gender&value=${voterEnc.Gender}"
        urls[11] = "$dataSource/voter/update?id=${voterEnc.id}&column=Orientation&value=${voterEnc.Orientation}"
        urls[12] = "$dataSource/voter/update?id=${voterEnc.id}&column=PoliticalAffiliation&value=${voterEnc.PoliticalAffiliation}"

        doQueries(urls, successHandler, errorHandler)
    }

    fun deleteVoter(voter: Voter,
                    successHandler: (String) -> Unit,
                    errorHandler: (VolleyError) -> Unit) {

        val voterEnc = voter.encode()
        val url = "$dataSource/voter/delete?id=${voterEnc.id}"
        doQuery(url, successHandler, errorHandler)
    }

}

