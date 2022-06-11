package com.example.projectvelib


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.example.projectvelib.api.DetailsVelibService
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import com.example.projectvelib.databinding.ActivityMapsBinding
import com.example.projectvelib.model.InfosStation
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnInfoWindowClickListener, OnMapReadyCallback,
    OnMyLocationButtonClickListener, OnMyLocationClickListener, OnRequestPermissionsResultCallback {

    var listStations: MutableList<InfosStation> = mutableListOf()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
        )
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        enableMyLocation()
        synchroApi()
        mMap.setOnInfoWindowClickListener(this)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if(!::mMap.isInitialized) return
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val intent = Intent(this, StationDetailsActivity::class.java)
        for(i in listStations) {
            val stationPosition = LatLng(i.lat, i.lon)
            if (marker.position == stationPosition) {
                intent.putExtra("station_id", i.station_id)
                intent.putExtra("station_name", i.name)
                intent.putExtra("station_capacity", i.capacity)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.liste_favorites_stations, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorites_stations_action -> {
                startActivity(Intent(this, StationFavoritesActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun synchroApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://velib-metropole-opendata.smoove.pro/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DetailsVelibService::class.java)
        val result = service.getStations()
        result.enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    val result = response.body()
                    val data = result?.get("data")?.asJsonObject
                    val stations = data?.get("stations")?.asJsonArray
                    if (stations != null) {
                        for(i in stations){
                            val s = InfosStation(
                                i.asJsonObject.get("station_id").asInt,
                                i.asJsonObject.get("name").asString,
                                i.asJsonObject.get("lat").asDouble,
                                i.asJsonObject.get("lon").asDouble,
                                i.asJsonObject.get("capacity").asInt,
                                i.asJsonObject.get("stationCode").asString)
                            listStations.add(s)
                            val position = LatLng(s.lat, s.lon)
                            mMap.addMarker(MarkerOptions()
                                .position(position)
                                .title(s.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .snippet("Cliquer pour plus de détails")
                            )
                            val zoomLevel = 16.0f
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext, "Erreur serveur", Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Votre position actuelle", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
    }
}