package com.example.projectvelib.model

data class StatusStation (
    val stationId: Int,
    val isInstalled: Int,
    val isRenting: Int,
    val isReturning: Int,
    val numBikesAvailable: Int,
    val numDocksAvailable: Int,
    val eBike: Int,
    val mechanical: Int
)