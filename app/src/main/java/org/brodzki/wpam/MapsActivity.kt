package org.brodzki.wpam

import android.content.Context
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.content.Intent
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.activity_maps.*
import org.brodzki.wpam.dao.DAO
import java.util.*
import com.google.android.gms.maps.model.MarkerOptions
import org.brodzki.wpam.dao.MarkerData
import org.brodzki.wpam.dao.Rule
import org.brodzki.wpam.dao.User
import kotlin.collections.ArrayList


class MapsActivity : SignedInActivity(), OnMapReadyCallback {

    /**
     * GLOBAL STATE
     */
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var markers = ArrayList<MarkerData>()
    private lateinit var rules: Array<Rule>
    private lateinit var currentAddress: String

    /**
     * LIFECYCLE HANDLERS
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            //checkPermissions()
            setContentView(R.layout.activity_maps)
            loggedUser = intent.extras.getSerializable(LOGGED_USER) as User

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            //set buttons handlers
            address_button.setOnClickListener { onCurrentAddressClicked() }
            refresh_map_button.setOnClickListener {
                updateLocation()
                updateMarkers()
            }

            //read rules
            DAO(this).readRule("1", {
                rules = it
            }, {  })
        } catch(e: Exception) {
            showSimpleDialog(DialogType.ERROR, "onCreate error: ${e.message}")
        }

    }

    override fun onResume() {
        super.onResume()
        //checkPermissions()
        updateMarkers()
        updateLocation()
    }

    private fun updateLocation() {
        try {
            // move camera to current location and display address on button
            val lastLocationTask = mFusedLocationProviderClient.lastLocation
            lastLocationTask.addOnCompleteListener {

                val location = it.result
                val coder = Geocoder(this, Locale.getDefault())
                val street = coder.getFromLocation(location.latitude, location.longitude, 1)
                currentAddress = street[0].getAddressLine(0)
                address_button.text = currentAddress
            }
        }
        catch(s: SecurityException) {
            showSimpleDialog(DialogType.ERROR, "updateLocation security error: ${s.message}")
        }
        catch(e: Exception) {
            showSimpleDialog(DialogType.ERROR, "updateLocation error: ${e.message}")
        }
    }

    private fun updateCamera() {
        try {
            // move camera to current location and display address on button
            val lastLocationTask = mFusedLocationProviderClient.lastLocation
            lastLocationTask.addOnCompleteListener {

                val location = it.result
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 15.0f
                ))
                val coder = Geocoder(this, Locale.getDefault())
                val street = coder.getFromLocation(location.latitude, location.longitude, 1)
                currentAddress = street[0].getAddressLine(0)
                address_button.text = currentAddress
            }
        }
        catch(s: SecurityException) {
            showSimpleDialog(DialogType.ERROR, "updateCamera security error: ${s.message}")
        }
        catch(e: Exception) {
            showSimpleDialog(DialogType.ERROR, "updateCamera error: ${e.message}")
        }

    }

    private fun updateMarkers() {

        try {
            val dao = DAO(this)
            dao.readVoter("1", {
                //update markers
                mMap.clear()
                markers = MarkerData.fromVotersList(it, rules)
                for(marker in markers) {
                    val latLong = LatLng(marker.latitude, marker.longitude)
                    val markerQuality = Math.round(100 - marker.cosDistance)
                    val lowThreshold = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getFloat(DEFAULT_LOW_PROXIMITY, DEFAULT_LOW_PROXIMITY_VALUE)
                    val highThreshold = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getFloat(DEFAULT_HIGH_PROXIMITY, DEFAULT_HIGH_PROXIMITY_VALUE)
                    val markerColor = when {
                        markerQuality < lowThreshold -> BitmapDescriptorFactory.HUE_RED
                        markerQuality < highThreshold -> BitmapDescriptorFactory.HUE_YELLOW
                        else -> BitmapDescriptorFactory.HUE_GREEN
                    }
                    mMap.addMarker(MarkerOptions().position(latLong).
                            title(marker.shortAddress).
                            icon(BitmapDescriptorFactory.defaultMarker(markerColor)).
                            snippet("${marker.voters.size} ${getString(if (marker.voters.size < 2) R.string.voter_marker else R.string.voters_marker)}"))
                }

            }, {  })
        }
        catch(s: SecurityException) {
            showSimpleDialog(DialogType.ERROR, "updateMarkers security error: ${s.message}")
        }
        catch(e: Exception) {
            showSimpleDialog(DialogType.ERROR, "updateMarkers error: ${e.message}")
        }

    }

    private fun findMarkerByShortAddress(address: String): MarkerData? {
        for(marker in markers) {
            if(marker.shortAddress == address) {
                return marker
            }
        }
        return null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            mMap = googleMap

            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.uiSettings.isCompassEnabled = true
            mMap.uiSettings.isIndoorLevelPickerEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true

            updateLocation()
            updateCamera()
            updateMarkers()

            mMap.setOnInfoWindowClickListener {
                openSelectVoter(it.title)
            }

        }
        catch(s: SecurityException) {
            showSimpleDialog(DialogType.ERROR, "onMapReady security error: ${s.message.toString()}")
        }
        catch(e: Exception) {
            showSimpleDialog(DialogType.ERROR, e.message.toString())
        }
    }

    /**
     * BUTTONS HANDLERS
     */

    private fun openSelectVoter(s: String) {
        val clickedVoters = findMarkerByShortAddress(s)
        if(clickedVoters != null) {
            val intent = Intent(this, SelectVoterActivity::class.java).apply {
                putExtra(LOGGED_USER, loggedUser)
                putExtra(SHARED_VOTERS, clickedVoters.voters)
                putExtra(RULES, clickedVoters.rules)
            }
            startActivity(intent)
        }
        else {
            showSimpleDialog(DialogType.INFO, getString(R.string.no_known_voters))
        }
    }

    private fun onCurrentAddressClicked() {
        updateLocation()
        openSelectVoter(currentAddress.split(",")[0])
    }




    /**
     * PROGRESS BAR
     */
    /*private fun showProgress(show: Boolean) {
        maps_view.visibility = if (show) View.GONE else View.VISIBLE
        maps_progress.visibility = if (show) View.VISIBLE else View.GONE
    }*/


    companion object {
        const val LOCATION_REQUEST = 1
    }

}
