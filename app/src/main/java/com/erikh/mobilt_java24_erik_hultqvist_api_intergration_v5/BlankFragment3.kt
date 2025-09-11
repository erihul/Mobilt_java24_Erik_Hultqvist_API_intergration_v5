package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BlankFragment3 : Fragment() {

    private lateinit var forecastRecyclerView: RecyclerView
    private lateinit var locationText: TextView
    private lateinit var backgroundImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgrounds = listOf(
            R.drawable.bg_fragment_3_1,
            R.drawable.bg_fragment_3_2,
            R.drawable.bg_fragment_3_3,
            R.drawable.bg_fragment_4_3,
            R.drawable.bg_fragment_4_2,
            R.drawable.bg_fragment_4_1
        )
        val backgroundImage = view.findViewById<ImageView>(R.id.backgroundImage3)
        backgroundImage.setImageResource(backgrounds.random())

        forecastRecyclerView = view.findViewById(R.id.forecastRecyclerView)
        forecastRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val locationText = view.findViewById<TextView>(R.id.forecastLocationText)
        val prefs = requireContext().getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val lat = prefs.getFloat("latitude", 55.60587f).toDouble()  // Malmö default
        val lon = prefs.getFloat("longitude", 13.00073f).toDouble()
        val locationName = prefs.getString("location_name", "Unknown location")
        locationText.text = "$locationName"

        WeatherService.getForecast(
            context = requireContext(),
            lat = lat,
            lon = lon,
            onResult = { forecastList ->
                forecastRecyclerView.adapter = ForecastAdapter(forecastList)
            },
            onError = { error ->
                Log.e("ERIK", "Failed to fetch forecast: $error")
                Toast.makeText(requireContext(), "Error loading forecast", Toast.LENGTH_SHORT).show()
            }
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank3, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment3().apply {
                arguments = Bundle().apply {

                }
            }
    }
}