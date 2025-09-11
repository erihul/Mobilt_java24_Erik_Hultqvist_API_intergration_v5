package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5.MainActivity.Companion.navController

class BlankFragment2 : Fragment() {

    var TAG = "ERIK"

    lateinit var weatherIcon: ImageView
    lateinit var cityText: TextView
    lateinit var tempText: TextView
    lateinit var descriptionText: TextView
    lateinit var forecastText: TextView
    lateinit var locationText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1002)
            }
        }

        val backgrounds = listOf(
            R.drawable.bg_fragment_2_1,
            R.drawable.bg_fragment_3_1,
            R.drawable.bg_fragment_3_2,
            R.drawable.bg_fragment_4_1,
            R.drawable.bg_fragment_4_3,
        )
        val backgroundImage = view.findViewById<ImageView>(R.id.backgroundImage2)
        backgroundImage.setImageResource(backgrounds.random())

        cityText = view.findViewById(R.id.textView)
        tempText = view.findViewById(R.id.textView5)
        descriptionText = view.findViewById(R.id.textView8)
        weatherIcon = view.findViewById(R.id.imageView)
        forecastText = view.findViewById(R.id.forecastText)
        locationText = view.findViewById(R.id.locationText)

       /* val lat = 55.60587  // Example: malmö
        val lon = 13.00073*/
        val prefs = requireContext().getSharedPreferences(PreferenceKeys.PREFS_NAME, Context.MODE_PRIVATE)
        Log.i(TAG, "prefs - Location: " + prefs.getString(PreferenceKeys.KEY_LOCATION_NAME, Constants.DEFAULT_LOCATION_NAME) + " lat: " + prefs.getFloat(PreferenceKeys.KEY_LATITUDE, Constants.DEFAULT_LAT) + " long: " + prefs.getFloat(PreferenceKeys.KEY_LONGITUDE, Constants.DEFAULT_LON) + " ")
        val lat = prefs.getFloat(PreferenceKeys.KEY_LATITUDE, Constants.DEFAULT_LAT).toDouble()
        val lon = prefs.getFloat(PreferenceKeys.KEY_LONGITUDE, Constants.DEFAULT_LON).toDouble()
        val locationName = prefs.getString(PreferenceKeys.KEY_LOCATION_NAME, Constants.DEFAULT_LOCATION_NAME)

        cityText.text = locationName

        WeatherService.getCurrentWeather(
            requireContext(),
            lat, lon, // Malmö coordinates
            cityText,
            tempText,
            descriptionText,
            weatherIcon
        )
        forecastText.setOnClickListener {
            navController.navigate(R.id.action_blankFragment2_to_blankFragment3)
        }
        locationText.setOnClickListener {
            navController.navigate(R.id.action_blankFragment2_to_blankFragment4)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Föra över från MainActivityn till denna fragment
        var v = inflater.inflate(R.layout.fragment_blank2, container, false)

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment2().apply {

            }
    }
}