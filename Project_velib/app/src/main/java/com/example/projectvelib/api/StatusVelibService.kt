package com.example.projectvelib.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET

interface StatusVelibService {
    @GET("/opendata/Velib_Metropole/station_status.json")
    fun getStatus() : Call<JsonObject>
}