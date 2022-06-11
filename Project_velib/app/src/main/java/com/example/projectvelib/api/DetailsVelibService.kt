package com.example.projectvelib.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET

interface DetailsVelibService {
    @GET("/opendata/Velib_Metropole/station_information.json")
    fun getStations() : Call<JsonObject>
}