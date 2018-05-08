package org.brodzki.wpam.dao

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import org.brodzki.wpam.DialogType
import org.brodzki.wpam.VoteForUsActivity
import java.io.Serializable
import java.net.URLEncoder
import java.util.*

data class Voter (
        var id: Int,
        var Name: CharSequence?,
        var Surname: CharSequence?,
        var Address: CharSequence,
        var Latitude: Double,
        var Longitude: Double,
        var Openness: Int,
        var Conscientiousness: Int,
        var Extraversion: Int,
        var Agreeableness: Int,
        var Neuroticism: Int,
        var Leadership: Int,
        var Age: Int,
        var Gender: Gender?,
        var Orientation: Orientation?,
        var PoliticalAffiliation: CharSequence?
) : Serializable {

    val utf = "UTF-8"

    fun encode(): Voter {
        val result = this
        result.Name = URLEncoder.encode(Name.toString(), utf)
        result.Surname = URLEncoder.encode(Surname.toString(), utf)
        result.Address = URLEncoder.encode(Address.toString(), utf)
        result.PoliticalAffiliation = URLEncoder.encode(PoliticalAffiliation.toString(), utf)
        return result
    }

    companion object {

        private val imionaMeskie: Array<String> = arrayOf(
                "Jan"        , "Stanisław", "Andrzej"   , "Józef"     , "Tadeusz"   ,
                "Jerzy"      , "Zbigniew" , "Krzysztof" , "Henryk"    , "Ryszard"   ,
                "Kazimierz"  , "Marek"    , "Marian"    , "Piotr"     , "Janusz"    ,
                "Władysław"  , "Adam"     , "Wiesław"   , "Zdzisław"  , "Edward"
        )
        private val imionaZenskie: Array<String> = arrayOf(
                "Maria"      , "Krystyna" , "Anna"      , "Barbara"   , "Teresa"    ,
                "Elżbieta"   , "Janina"   , "Zofia"     , "Jadwiga"   , "Danuta"    ,
                "Halina"     , "Irena"    , "Ewa"       , "Małgorzata", "Helena"    ,
                "Grażyna"    , "Bożena"   , "Stanisława", "Jolanta"   , "Marianna"
        )
        private val nazwiska: Array<String> = arrayOf(
                "Nowak"      , "Wójcik"   , "Kowalczyk" , "Woźniak"   , "Mazur"     ,
                "Krawczyk"   , "Kaczmarek", "Zając"     , "Król"      , "Wieczorek" ,
                "Wróbel"     , "Stępień"  , "Adamczyk"  , "Dudek"     , "Pawlak"    ,
                "Walczak"    , "Sikora"   , "Baran"     , "Michalak"  , "Szewczyk"  ,
                "Pietrzak"   , "Marciniak", "Bąk"       , "Duda"      , "Włodarczyk",
                "Wilk"       , "Lis"      , "Kubiak"    , "Mazurek"   , "Kołodziej" ,
                "Kaźmierczak", "Sobczak"  , "Krupa"     , "Mróz"      , "Szulc"     ,
                "Kaczmarczyk", "Błaszczyk", "Kania"     , "Janik"     , "Szczepaniak"
        )
        private val affiliations = arrayOf(
                "PiS", "PO", "Nowoczesna", "Kukiz15", "Wolność (KORWiN)", "Razem", "SLD",
                "Prawica", "Lewica",
                "Apolityczność", "Apolityczność", "Apolityczność", "Apolityczność", "Apolityczność", "Apolityczność",
                "Nieznana", "Nieznana", "Nieznana", "Nieznana", "Nieznana"
        )

        fun random(context: Context, center: Location, radius: Int, handler: (Voter) -> Unit) {

            //auxiliary values
            fun latPerKm() = 1.0 / 111.0
            fun lonPerKm(lat: Double) = 1.0 / (Math.cos(lat / 180.0 * Math.PI) * 111.0)
            val latFrom = center.latitude  - latPerKm() * radius
            val latTo   = center.latitude  + latPerKm() * radius
            val lonFrom = center.longitude - lonPerKm(center.latitude) * radius
            val lonTo   = center.longitude + lonPerKm(center.latitude) * radius

            //Generating voter includes geocoding which is a network task and should be asynchronous
            class RandomVoterTask : AsyncTask<String, Int, Voter>() {

                override fun doInBackground(vararg params: String?): Voter {

                    //random voter properties
                    val gen = Random()
                    val randomGender: Gender = if (gen.nextBoolean()) Gender.MALE else Gender.FEMALE
                    val randomName: String = if (randomGender == Gender.MALE) {
                        val ind = Math.round(gen.nextDouble() * (imionaMeskie.size - 1)).toInt()
                        imionaMeskie[ind]
                    }
                    else {
                        val ind = Math.round(gen.nextDouble() * (imionaZenskie.size - 1)).toInt()
                        imionaZenskie[ind]
                    }
                    val randomSurname: String = { _: Int ->
                        val ind = Math.round(gen.nextDouble() * (nazwiska.size - 1)).toInt()
                        nazwiska[ind]
                    }(0)
                    var randomAddress = ""
                    val coder = Geocoder(context, Locale.getDefault())
                    try {
                        randomAddress = { _: Int ->
                            val latitude = gen.nextDouble() * (latTo - latFrom) + latFrom
                            val longitude = gen.nextDouble() * (lonTo - lonFrom) + lonFrom
                            val street = coder.getFromLocation(latitude, longitude, 1)
                            street[0].getAddressLine(0)
                        }(0)
                    }
                    catch (e: Exception) {
                        VoteForUsActivity.showSimpleDialog(context, DialogType.ERROR, "Voter location error: ${e.message.toString()}")
                    }
                    val preciseLocation = coder.getFromLocationName(randomAddress, 1)[0]
                    val randomLatitude = preciseLocation.latitude
                    val randomLongitude = preciseLocation.longitude
                    val randomAge: Int = Math.round(gen.nextDouble() * 60 + 18).toInt()
                    val randomOpenness: Int = Math.round(gen.nextDouble() * 2 - 1).toInt()
                    val randomConscientiousness: Int = Math.round(gen.nextDouble() * 2 - 1).toInt()
                    val randomExtraversion: Int = Math.round(gen.nextDouble() * 2 - 1).toInt()
                    val randomAgreeableness: Int = Math.round(gen.nextDouble() * 2 - 1).toInt()
                    val randomNeuroticism: Int = Math.round(gen.nextDouble() * 2 - 1).toInt()
                    val randomLeadership: Int = Math.round(gen.nextDouble() * 2 - 1).toInt()
                    val randomOrientation: Orientation = when (Math.round(gen.nextDouble() * 10).toInt()) {
                        0 -> Orientation.HETEROSEXUAL
                        1 -> Orientation.HETEROSEXUAL
                        2 -> Orientation.BISEXUAL
                        3 -> Orientation.ASEXUAL
                        4 -> Orientation.HOMOSEXUAL
                        5 -> Orientation.HETEROSEXUAL
                        6 -> Orientation.HETEROSEXUAL
                        else -> Orientation.UNKNOWN
                    }
                    val randomAffiliation: String = { _: Int ->
                        val ind = Math.round(gen.nextDouble() * (affiliations.size - 1)).toInt()
                        affiliations[ind]
                    }(0)

                    return Voter(
                            0,
                            randomName,
                            randomSurname,
                            randomAddress,
                            randomLatitude,
                            randomLongitude,
                            randomOpenness,
                            randomConscientiousness,
                            randomExtraversion,
                            randomAgreeableness,
                            randomNeuroticism,
                            randomLeadership,
                            randomAge,
                            randomGender,
                            randomOrientation,
                            randomAffiliation)
                }

                override fun onPostExecute(result: Voter?) {
                    super.onPostExecute(result)
                    handler(result!!)
                }

            }

            RandomVoterTask().execute()

        }
    }

}
