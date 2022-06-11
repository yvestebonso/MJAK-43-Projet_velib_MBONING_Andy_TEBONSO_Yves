package com.example.projectvelib.model

data class InfosStation (
    val station_id: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val station_code: String,
)