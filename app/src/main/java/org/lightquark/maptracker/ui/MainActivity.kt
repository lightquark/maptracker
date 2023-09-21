package org.lightquark.maptracker.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import org.lightquark.maptracker.R
import org.lightquark.maptracker.data.db.LocationEntity
import org.lightquark.maptracker.viewmodels.LocationUpdateViewModel

/**
 * An activity that displays a Google map with route, and UI elements for configuring location updates.
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // TODO:
    //  Using class level variables is not recommended because they may be lost if an activity changes the state
    //  Find a way to retrieve GoogleMap object in `observe` function
    private lateinit var googleMap: GoogleMap

    private val locationUpdateViewModel by lazy {
        ViewModelProvider(this).get(LocationUpdateViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the content view that renders the map
        setContentView(R.layout.activity_maps)

        configureOnClickListeners()
        createGoogleMapInstance()

        configureLocationListListener()
        configureTrackingButtonStateListener()
    }

    private fun configureOnClickListeners() {

        findViewById<Button>(R.id.trackingButton)
            .setOnClickListener {
                Log.d(TAG, "User clicked Tracking Button")
            }

        findViewById<Button>(R.id.cleanupButton)
            .setOnClickListener {
                Log.d(TAG, "User clicked Cleanup Button")
                locationUpdateViewModel.cleanupDatabase()
            }
    }

    /**
     * Get the SupportMapFragment and request notification when the map is ready to be used
     */
    private fun createGoogleMapInstance() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "OnMapReady")
        this.googleMap = googleMap

        // Position the map's camera near Microsoft Tallinn office.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(59.399750, 24.659275), 12f))

        Toast.makeText(this, "Map loaded successfully", Toast.LENGTH_SHORT).show()
    }

    private fun configureLocationListListener() {
        locationUpdateViewModel.locationListLiveData.observe(this) { locations ->
            locations?.let {
                Log.d(TAG, "Got ${locations.size} locations")
                displayRecentLocations(locations);
                drawRoute(locations);
            }
        }
    }

    private fun displayRecentLocations(locations: List<LocationEntity>) {
        findViewById<TextView>(R.id.locationsText).text = getRecentLocationAsText(locations)
    }

    private fun getRecentLocationAsText(locations: List<LocationEntity>): String {
        if (locations.isNotEmpty()) {
            val outputStringBuilder = StringBuilder("")
            locations.forEach { location ->
                outputStringBuilder.append(location.toString() + "\n")
            }
            return outputStringBuilder.toString()
        }
        return getString(R.string.empty)
    }

    private fun drawRoute(locations: List<LocationEntity>) {
        Log.d(TAG, "DrawRoute map=$googleMap")

        googleMap.clear();
        Log.d(TAG, "Cleared map")

        if (locations.isEmpty()) {
            Log.d(TAG, "No locations in the list")
            return
        }

        val polylineOptions = PolylineOptions()
            .startCap(RoundCap())
            .endCap(RoundCap())
            .jointType(JointType.ROUND)
            //.color(COLOR_BLACK_ARGB)
            .width(POLYLINE_STROKE_WIDTH_PX)

        // TODO: Aggregate adjacent locations
        locations.forEach { location ->
            polylineOptions.add(LatLng(location.latitude, location.longitude))
        }

        googleMap.addPolyline(polylineOptions)
        Log.d(TAG, "Route added to the map")
    }

    private fun configureTrackingButtonStateListener() {
        locationUpdateViewModel.receivingLocationUpdates.observe(this) { isTrackingActive ->
            Log.d(TAG, "observe: isTrackingActive = $isTrackingActive")
            updateTrackingButtonState(isTrackingActive)
        }
    }

    private fun updateTrackingButtonState(isTrackingActive: Boolean) {

        val trackingButton = findViewById<Button>(R.id.trackingButton)

        if (isTrackingActive) {
            trackingButton.apply {
                text = getString(R.string.stop_tracking)
                setOnClickListener {
                    locationUpdateViewModel.stopLocationUpdates()
                }
            }
        } else {
            trackingButton.apply {
                text = getString(R.string.start_tracking)
                setOnClickListener {
                    locationUpdateViewModel.startLocationUpdates()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val COLOR_BLACK_ARGB = -0x1000000
        private const val POLYLINE_STROKE_WIDTH_PX = 12.0f
    }
}
