package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5


import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun getCityFromCoordinates(context: Context, lat: Double, lon: Double, callback: (String) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            callback(addresses[0].locality ?: "Unknown")
        } else {
            callback("Unknown")
        }
    } catch (e: Exception) {
        callback("Unknown")
    }
}
