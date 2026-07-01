package com.example.lostfound.ui.map

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfound.databinding.ActivityMapPickerBinding
import com.example.lostfound.util.LocationHelper
import com.example.lostfound.util.SystemBars
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapPickerBinding
    private var selectedPoint: GeoPoint? = null
    private var locationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemBars.apply(activity = this, root = binding.root)

        val initial = LocationHelper.parseCoordinates(
            intent.getStringExtra(EXTRA_INITIAL_LOCATION).orEmpty()
        )
        val start = initial?.let { GeoPoint(it.first, it.second) }
            ?: GeoPoint(LocationHelper.DEFAULT_CAMPUS_LAT, LocationHelper.DEFAULT_CAMPUS_LNG)

        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(16.0)
        binding.mapView.controller.setCenter(start)

        binding.mapView.overlays.add(
            MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                    point?.let { onMapPointSelected(it) }
                    return true
                }

                override fun longPressHelper(point: GeoPoint?): Boolean = false
            })
        )

        binding.backButton.setOnClickListener { finish() }
        binding.confirmLocationButton.setOnClickListener { confirmSelection() }

        if (start != null && initial != null) {
            onMapPointSelected(start)
        }
    }

    private fun onMapPointSelected(point: GeoPoint) {
        selectedPoint = point
        locationMarker?.let { binding.mapView.overlays.remove(it) }
        locationMarker = Marker(binding.mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = getString(com.example.lostfound.R.string.selected_location)
        }
        binding.mapView.overlays.add(locationMarker)
        binding.mapView.invalidate()

        binding.selectedLocationText.text = LocationHelper.formatMapSelection(
            context = this,
            lat = point.latitude,
            lng = point.longitude
        )
        binding.confirmLocationButton.isEnabled = true
    }

    private fun confirmSelection() {
        val point = selectedPoint ?: return
        val formatted = LocationHelper.formatMapSelection(this, point.latitude, point.longitude)
        setResult(
            RESULT_OK,
            Intent()
                .putExtra(EXTRA_SELECTED_LOCATION, formatted)
                .putExtra(EXTRA_LATITUDE, point.latitude)
                .putExtra(EXTRA_LONGITUDE, point.longitude)
        )
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    companion object {
        const val EXTRA_INITIAL_LOCATION = "extra_initial_location"
        const val EXTRA_SELECTED_LOCATION = "extra_selected_location"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }
}
