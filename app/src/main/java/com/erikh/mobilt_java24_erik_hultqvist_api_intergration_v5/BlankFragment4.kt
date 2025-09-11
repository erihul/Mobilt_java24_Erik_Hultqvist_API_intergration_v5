package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5.MainActivity.Companion.navController
import com.google.android.gms.location.LocationServices


class BlankFragment4 : Fragment() {

    private lateinit var btnGetLocation: Button
    private lateinit var locationInput: EditText
    private lateinit var btnSave: Button
    private lateinit var savedLocationText: TextView
    private lateinit var backgroundImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgrounds = listOf(
            R.drawable.bg_fragment_4_1,
            R.drawable.bg_fragment_4_2,
            R.drawable.bg_fragment_4_3,
        )
        val backgroundImage = view.findViewById<ImageView>(R.id.backgroundImage4)
        backgroundImage.setImageResource(backgrounds.random())

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val prefs = requireContext().getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val savedLat = prefs.getFloat("latitude", 55.60587f)
        val savedLon = prefs.getFloat("longitude", 13.00073f)

        val savedLocationText = view.findViewById<TextView>(R.id.savedlocationText)
        getCityFromCoordinates(requireContext(), savedLat.toDouble(), savedLon.toDouble()) { cityName ->
            savedLocationText.text = "Current location: $cityName"
        }
        val btnGetLocation = view.findViewById<Button>(R.id.button_get_current_location)
        val locationInput = view.findViewById<EditText>(R.id.editTextText)
        val btnSave = view.findViewById<Button>(R.id.button_save_location)

        btnGetLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            val lat = it.latitude
                            val lon = it.longitude

                            getCityFromCoordinates(requireContext(), lat, lon) { locationName ->
                                saveLocationToPreferences(lat, lon, locationName)
                                Toast.makeText(context, "Location saved: $locationName", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_blankFragment4_to_blankFragment2)
                            }
                        } ?: Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                    }
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

        val queue = Volley.newRequestQueue(context)
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val results = response.getJSONArray("results")
                    if (results.length() > 0) {
                        val components = results.getJSONObject(0).getJSONObject("components")
                        val city = components.optString("city", "Unknown city")
                        val country = components.optString("country", "Unknown country")
                        onResult("$city, $country")
                    }
                } catch (e: Exception) {
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
        val prefs = requireContext().getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat("latitude", lat.toFloat())
            .putFloat("longitude", lon.toFloat())
            .putString("location_name", name)
            .apply()
    }

}