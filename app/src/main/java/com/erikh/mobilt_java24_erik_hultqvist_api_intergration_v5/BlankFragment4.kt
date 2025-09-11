package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.VolleyLog.e
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5.MainActivity.Companion.navController
import com.google.android.gms.location.LocationServices


class BlankFragment4 : Fragment() {

    var TAG = "ERIK"
    private lateinit var btnGetLocation: Button
    private lateinit var locationInput: EditText
    private lateinit var btnSave: Button
    private lateinit var savedLocationText: TextView
    private lateinit var backgroundImage: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)

        val backgrounds = listOf(
            R.drawable.bg_fragment_4_1,
            R.drawable.bg_fragment_4_2,
            R.drawable.bg_fragment_4_3,
        )
        backgroundImage = view.findViewById<ImageView>(R.id.backgroundImage4)
        backgroundImage.setImageResource(backgrounds.random())

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val prefs = requireContext().getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val savedLat = prefs.getFloat(PreferenceKeys.KEY_LATITUDE, Constants.DEFAULT_LAT).toDouble()
        val savedLon = prefs.getFloat(PreferenceKeys.KEY_LONGITUDE, Constants.DEFAULT_LON).toDouble()

        savedLocationText = view.findViewById<TextView>(R.id.savedlocationText)
        getCityFromCoordinates(requireContext(), savedLat.toDouble(), savedLon.toDouble()) { cityName ->
            savedLocationText.text = "Current location: $cityName"
        }
        btnGetLocation = view.findViewById<Button>(R.id.button_get_current_location)
        locationInput = view.findViewById<EditText>(R.id.editTextText)
        btnSave = view.findViewById<Button>(R.id.button_save_location)

        btnGetLocation.setOnClickListener {
            //Log.i(TAG, "PREFLocation: " + prefs.getString("location_name", "Unknown"))
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            } else {
                progressBar.visibility = View.VISIBLE
                val loadingText = view.findViewById<TextView>(R.id.loadingText)
                loadingText.visibility = View.VISIBLE

                btnGetLocation.isEnabled = false
                btnSave.isEnabled = false

                val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                    interval = 1000
                    fastestInterval = 500
                    priority = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
                    maxWaitTime = 2000
                    numUpdates = 1 // Only one location update
                }

                val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            val lat = location.latitude
                            val lon = location.longitude
                            Log.i(TAG, "GetPosition-button - Got UPDATED location: lat=$lat, lon=$lon")

                            getCityFromCoordinates(requireContext(), lat, lon) { locationName ->
                                Log.i(TAG, "GetPosition-button - Geocoded city: $locationName")
                                saveLocationToPreferences(lat, lon, locationName)

                                progressBar.visibility = View.GONE
                                loadingText.visibility = View.GONE
                                btnGetLocation.isEnabled = true
                                btnSave.isEnabled = true

                                Toast.makeText(context, "GetPosition-button - Location saved: $locationName", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_blankFragment4_to_blankFragment2)
                            }
                        } else {
                            progressBar.visibility = View.GONE
                            loadingText.visibility = View.GONE
                            btnGetLocation.isEnabled = true
                            btnSave.isEnabled = true
                            Log.i(TAG, "GetPosition-button - LocationCallback returned null.")
                            Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                        }

                        // Important: stop updates after first result
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }

// Start the location update
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    android.os.Looper.getMainLooper()
                )

            }

        }


        btnSave.setOnClickListener {
            val input = locationInput.text.toString()

            if (input.contains(",")) {

                val parts = input.split(",")
                val lat = parts[0].toDoubleOrNull()
                val lon = parts[1].toDoubleOrNull()

                if (lat != null && lon != null) {
                    saveLocationToPreferences(lat, lon)
                    Toast.makeText(context, "Latitude and Longitude saved!", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_blankFragment4_to_blankFragment2)
                } else {
                    Toast.makeText(context, "Invalid coordinates", Toast.LENGTH_SHORT).show()
                }

            } else {

                getCoordinatesFromCity(requireContext(), input,
                    onResult = { lat, lon ->
                        saveLocationToPreferences(lat, lon, input)
                        //prefs.edit().putString("location_name", input).apply()
                        Toast.makeText(context, "Location saved! $input", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_blankFragment4_to_blankFragment2)

                    },
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank4, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment4().apply {
                arguments = Bundle().apply {
                }
            }
    }

    fun getCoordinatesFromCity(context: Context, cityName: String, onResult: (Double, Double) -> Unit, onError: (String) -> Unit) {
        val apiKey = "c117841747a04008a54a5e6a63ecf085"
        val url = "https://api.opencagedata.com/geocode/v1/json?q=${cityName}&key=$apiKey"

        val queue = Volley.newRequestQueue(context)
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val results = response.getJSONArray("results")
                    if (results.length() > 0) {
                        val geometry = results.getJSONObject(0).getJSONObject("geometry")
                        val lat = geometry.getDouble("lat")
                        val lon = geometry.getDouble("lng")
                        onResult(lat, lon)
                    } else {
                        onError("No location found")
                    }
                } catch (e: Exception) {
                    onError("Error parsing response")
                }
            },
            { error ->
                onError("Geocoding API error: ${error.message}")
            }
        )

        queue.add(jsonRequest)
    }

    fun getCityFromCoordinates(context: Context, lat: Double, lon: Double, onResult: (String) -> Unit) {
        val apiKey = "c117841747a04008a54a5e6a63ecf085"
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$lat+$lon&key=$apiKey"

        Log.i(TAG, "GetCity - Requesting location for lat=$lat, lon=$lon")

        val queue = Volley.newRequestQueue(context)
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val results = response.getJSONArray("results")
                    Log.i(TAG, "GetCity - OpenCage returned ${results.length()} results")

                    if (results.length() > 0) {
                        val components = results.getJSONObject(0).getJSONObject("components")

                        // Priority: city > town > municipality > state_district > state
                        val city = components.optString("city", null)
                            ?: components.optString("town", null)
                            ?: components.optString("municipality", null)
                            ?: components.optString("state_district", null)
                            ?: components.optString("state", "Unknown")

                        val country = components.optString("country", "Unknown country")
                        val fullLocation = "$city, $country"
                        Log.i(TAG, "GetCity - Extracted city: $fullLocation")

                        onResult(fullLocation)
                    } else {
                        Log.e(TAG, "GetCity - Error parsing geocoding response")
                        onResult("Unknown location")
                    }
                } catch (e: Exception) {
                    Log.e("GeoAPI", "GetCity - Network error calling geocoding API: ${e.message}")
                    onResult("Unknown location")
                }
            },
            { _ ->
                onResult("Error fetching location name")
            }
        )

        queue.add(jsonRequest)
    }

    private fun saveLocationToPreferences(lat: Double, lon: Double, name: String = "Unknown") {
        Log.i(TAG, "saveLocationToPreferences - Saving to prefs: lat=$lat, lon=$lon, name=$name")

        val prefs = requireContext().getSharedPreferences(PreferenceKeys.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat(PreferenceKeys.KEY_LATITUDE, lat.toFloat())
            .putFloat(PreferenceKeys.KEY_LONGITUDE, lon.toFloat())
            .putString(PreferenceKeys.KEY_LOCATION_NAME, name)
            .apply()
    }


}






/*
btnGetLocation.setOnClickListener {
    //Log.i(TAG, "PREFLocation: " + prefs.getString("location_name", "Unknown"))
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
    } else {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    Log.i(TAG, "GetPosition-button - FusedLocationProvider returned null location.")
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                } else {
                    val lat = location.latitude
                    val lon = location.longitude
                    Log.i(TAG, "GetPosition-button - Got location: lat=$lat, lon=$lon")

                    getCityFromCoordinates(requireContext(), lat, lon) { locationName ->
                        Log.i(TAG, "GetPosition-button - Geocoded city: $locationName")
                        saveLocationToPreferences(lat, lon, locationName)
                        Toast.makeText(context, "GetPosition-button - Location saved: $locationName", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_blankFragment4_to_blankFragment2)
                    }
                }
            }
    }

}*/
