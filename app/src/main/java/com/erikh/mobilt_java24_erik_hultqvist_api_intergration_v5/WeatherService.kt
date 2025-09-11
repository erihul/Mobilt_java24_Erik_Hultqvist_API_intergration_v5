package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

object WeatherService {

    private const val TAG = "ERIK"
    private const val apiKey = "ec916cb153056fa290e2b9b6d7126267"

    fun getCurrentWeather(
        context: Context,
        lat: Double,
        lon: Double,
        cityText: TextView,
        tempText: TextView,
        descriptionText: TextView,
        weatherIcon: ImageView
    ) {
        val units = "metric"
        val url = "https://api.openweathermap.org/data/2.5/weather" +
                "?lat=$lat&lon=$lon&units=$units&appid=$apiKey"

        val requestQueue = Volley.newRequestQueue(context)

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val main = response.getJSONObject("main")
                    val temp = main.getDouble("temp")

                    val weatherArray = response.getJSONArray("weather")
                    val description = weatherArray.getJSONObject(0).getString("description")
                    val iconCode = weatherArray.getJSONObject(0).getString("icon")
                    val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"

                    // ✅ Trigger a notification if it's raining
                    if (description.contains("rain", ignoreCase = true)) {
                        showWeatherNotification(
                            context,
                            "☔ Rain Alert",
                            "It's currently raining in ${response.getString("name")}!"
                        )
                    }

                    // ✅ Or notify if it's hot
                    if (temp > 25) {
                        showWeatherNotification(
                            context,
                            "🔥 Hot Weather",
                            "It's hot in ${response.getString("name")}! Temperature: $temp°C"
                        )
                    }

                    val prefs = context.getSharedPreferences(PreferenceKeys.PREFS_NAME, Context.MODE_PRIVATE)
                    val savedName = prefs.getString(PreferenceKeys.KEY_LOCATION_NAME, "Unknown location")
                    cityText.text = savedName


                    tempText.text = "$temp°C"
                    descriptionText.text = description.capitalize()

                    Glide.with(context)
                        .load(iconUrl)
                        .into(weatherIcon)

                } catch (e: Exception) {
                    Log.e(TAG, "Parse error: ${e.message}")
                }
            },
            { error ->
                Log.e(TAG, "API error: ${error.message}")
                error.networkResponse?.let {
                    Log.e(TAG, "Status Code: ${it.statusCode}")
                    Log.e(TAG, "Error Data: ${String(it.data)}")
                }
                Toast.makeText(context, "Failed to load weather", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(jsonRequest)
    }

    fun getForecast(
        context: Context,
        lat: Double,
        lon: Double,
        onResult: (List<ForecastItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://api.openweathermap.org/data/2.5/forecast" +
                "?lat=$lat&lon=$lon&units=metric&appid=$apiKey"

        val requestQueue = Volley.newRequestQueue(context)

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val forecastArray = response.getJSONArray("list")
                    val forecastList = mutableListOf<ForecastItem>()

                    for (i in 0 until forecastArray.length()) {
                        val item = forecastArray.getJSONObject(i)
                        val dateTime = item.getString("dt_txt")
                        val temp = item.getJSONObject("main").getDouble("temp")
                        val desc = item.getJSONArray("weather")
                            .getJSONObject(0).getString("description")
                        val iconCode = item.getJSONArray("weather")
                            .getJSONObject(0).getString("icon")
                        val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"

                        forecastList.add(
                            ForecastItem(
                                dateTime = dateTime,
                                temp = temp,
                                description = desc,
                                iconUrl = iconUrl
                            )
                        )
                    }

                    Log.d(TAG, "Parsed ${forecastList.size} forecast items.")
                    onResult(forecastList)

                } catch (e: Exception) {
                    Log.e(TAG, "Parse error: ${e.message}")
                    onError(e.message ?: "Parsing error")
                }
            },
            { error ->
                val errMsg = error.message ?: "Unknown error"
                Log.e(TAG, "Forecast API error: $errMsg")
                error.networkResponse?.let {
                    Log.e(TAG, "Status Code: ${it.statusCode}")
                    Log.e(TAG, "Error Data: ${String(it.data)}")
                }
                onError(errMsg)
            }
        )

        requestQueue.add(jsonRequest)
    }

    data class ForecastItem(
        val dateTime: String,
        val temp: Double,
        val description: String,
        val iconUrl: String
    )

    fun showWeatherNotification(context: Context, title: String, message: String) {
        Log.i(TAG, "showWeatherNotification: HMM....")
        val builder = NotificationCompat.Builder(context, "weather_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use a default icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
            }
        } else {
            Log.w(TAG, "Notification permission not granted")

        }
    }


}