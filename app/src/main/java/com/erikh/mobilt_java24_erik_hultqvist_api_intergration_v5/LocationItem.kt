package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

data class LocationItem(
    val id: String = "",          // Firestore doc id for deleting
    val userId: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
