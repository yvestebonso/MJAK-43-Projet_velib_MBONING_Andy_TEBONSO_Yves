package com.example.projectvelib

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.projectvelib.api.StatusVelibService
import com.example.projectvelib.database.DatabaseStation
import com.example.projectvelib.database.EntityStation
import com.example.projectvelib.model.StatusStation
import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StationDetailsActivity: AppCompatActivity() {
    lateinit var detailStation: StatusStation
    var stationId = 0
    var op = "Non"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_stations)
        stationId = intent.getIntExtra("station_id", -1)
        synchroApi()

        val addFavoritesButton = findViewById<Button>(R.id.add_to_favorites_button)
        var addOrDelete = 0

        val bdd = Room.databaseBuilder(
            applicationContext,
            DatabaseStation::class.java, "stationEntity"
        ).build()

        val stationDao = bdd.Daostation()
        runBlocking {
            val listStations = stationDao.getAll()
            var fav = false
            for(i in listStations){
                if(i.id == stationId){
                    fav = true
                }
            }
            if(fav){
                addFavoritesButton.text = "Supprimer un favoris"
                addOrDelete = 1
            }
            else addFavoritesButton.text = "Ajouter aux favoris"
        }

        addFavoritesButton.setOnClickListener {
            if(addOrDelete == 1){
                val bdd = Room.databaseBuilder(
                    applicationContext,
                    DatabaseStation::class.java, "stationEntity"
                ).build()

                val stationDao = bdd.Daostation()
                val station = EntityStation(
                    detailStation.stationId,
                    intent.getStringExtra("station_name")!!,
                    op,
                    intent.getIntExtra("station_capacity", -1),
                    detailStation.numBikesAvailable,
                    detailStation.numDocksAvailable,
                    detailStation.mechanical,
                    detailStation.eBike
                )
                runBlocking {
                    stationDao.delete(station)
                    Toast.makeText(applicationContext, "Station supprimée de vos favoris", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                val bdd = Room.databaseBuilder(
                    applicationContext,
                    DatabaseStation::class.java, "stationEntity"
                ).build()

                val stationDao = bdd.Daostation()
                val station = EntityStation(
                    detailStation.stationId,
                    intent.getStringExtra("station_name")!!,
                    op,
                    intent.getIntExtra("station_capacity", -1),
                    detailStation.numBikesAvailable,
                    detailStation.numDocksAvailable,
                    detailStation.mechanical,
                    detailStation.eBike
                )
                runBlocking {
                    stationDao.insert(station)
                    Toast.makeText(applicationContext, "Station ajoutée à vos favoris", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_station, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.id_favorites_stations -> {
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

        val service = retrofit.create(StatusVelibService::class.java)
        val result = service.getStatus()
        result.enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    val result = response.body()
                    val data = result?.get("data")?.asJsonObject
                    val stations = data?.get("stations")?.asJsonArray
                    if (stations != null) {
                        for(i in stations){
                            val s = i.asJsonObject
                            val id = s?.get("station_id")?.asInt
                            if(stationId == id){
                                val typesBike = s?.get("num_bikes_available_types")?.asJsonArray
                                val typeM = typesBike?.get(0)?.asJsonObject
                                val mechanical = typeM?.get("mechanical")?.asInt
                                val typeE = typesBike?.get(1)?.asJsonObject
                                val eBike = typeE?.get("ebike")?.asInt
                                detailStation =
                                    StatusStation(
                                        id,
                                        s?.get("is_installed").asInt,
                                        s?.get("is_renting").asInt,
                                        s?.get("is_returning").asInt,
                                        s?.get("numBikesAvailable").asInt,
                                        s?.get("numDocksAvailable").asInt,
                                        mechanical!!,
                                        eBike!!
                                    )
                                printDetails()
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext, "Erreur serveur", Toast.LENGTH_SHORT).show()
                printDetailsHC()
            }
        }
        )
    }

    private fun printDetails() {
        val name = intent.getStringExtra("station_name")
        val capacity = intent.getIntExtra("station_capacity", -1)
        val tvName = findViewById<TextView>(R.id.station_name)
//        val tvCapacity = findViewById<TextView>(R.id.capacity)
//        val capacityTV = findViewById<TextView>(R.id.capacity_tv)
        val tvNbBikeAvailable = findViewById<TextView>(R.id.nb_velo_dispo)
        val nbBikeAvailableTV = findViewById<TextView>(R.id.nb_velo_dispo_tv)
        val tvNbDockAvailable = findViewById<TextView>(R.id.nb_borne_dispo)
        val nbDockAvailableTV = findViewById<TextView>(R.id.nb_borne_dispo_tv)
        val tvNbMech = findViewById<TextView>(R.id.nb_mech)
        val nbMechTV = findViewById<TextView>(R.id.nb_mech_tv)
        val tvNbEbike = findViewById<TextView>(R.id.nb_ebike)
        val nbEbikeTV = findViewById<TextView>(R.id.nb_ebike_tv)
        val tvOperationnel = findViewById<TextView>(R.id.operationnel)
        val operationnelTV = findViewById<TextView>(R.id.operationnel_tv)
        tvName.text = name
        operationnelTV.text = "Opérationnelle : "
        if(detailStation.isInstalled == 1 && detailStation.isRenting == 1 && detailStation.isReturning ==1 ){
            tvOperationnel.text = "Oui"
            op = "Oui"
        }
        else tvOperationnel.text = op
//        capacityTV.text = "Capacité : "
        nbBikeAvailableTV.text = "Nombre de vélos disponibles : "
        nbDockAvailableTV.text = "Nombre de bornes diponibles : "
        nbMechTV.text = "Nombre de vélos mécaniques : "
        nbEbikeTV.text = "Nombre de vélos électriques : "
//        tvCapacity.text = ""+capacity
        tvNbBikeAvailable.text = ""+detailStation.numBikesAvailable
        tvNbDockAvailable.text = ""+detailStation.numDocksAvailable
        tvNbMech.text = ""+detailStation.mechanical
        tvNbEbike.text = ""+detailStation.eBike
    }

    private fun printDetailsHC() {
        val name = intent.getStringExtra("station_name")
        val capacity = intent.getIntExtra("station_capacity", -1)
        val tvName = findViewById<TextView>(R.id.station_name)
//        val tvCapacity = findViewById<TextView>(R.id.capacity)
//        val capacityTV = findViewById<TextView>(R.id.capacity_tv)
        val tvNbBikeAvailable = findViewById<TextView>(R.id.nb_velo_dispo)
        val nbBikeAvailableTV = findViewById<TextView>(R.id.nb_velo_dispo_tv)
        val tvNbDockAvailable = findViewById<TextView>(R.id.nb_borne_dispo)
        val nbDockAvailableTV = findViewById<TextView>(R.id.nb_borne_dispo_tv)
        val tvNbMech = findViewById<TextView>(R.id.nb_mech)
        val nbMechTV = findViewById<TextView>(R.id.nb_mech_tv)
        val tvNbEbike = findViewById<TextView>(R.id.nb_ebike)
        val nbEbikeTV = findViewById<TextView>(R.id.nb_ebike_tv)
        val tvOperationnel = findViewById<TextView>(R.id.operationnel)
        val operationnelTV = findViewById<TextView>(R.id.operationnel_tv)
        tvName.text = name
        operationnelTV.text = "Opérationnelle : "
//        capacityTV.text = "Capacité"
        nbBikeAvailableTV.text = "Nombre de vélos disponibles : "
        nbDockAvailableTV.text = "Nombre de bornes diponibles : "
        nbMechTV.text = "Nombre de vélos mécaniques : "
        nbEbikeTV.text = "Nombre de vélos électriques : "
        tvOperationnel.text = intent.getStringExtra("station_op")
//        tvCapacity.text = ""+capacity
        tvNbBikeAvailable.text = ""+ intent.getIntExtra("nb_bike", -1)
        tvNbDockAvailable.text = ""+ intent.getIntExtra("nb_dock", -1)
        tvNbMech.text = ""+ intent.getIntExtra("mechanical", -1)
        tvNbEbike.text = ""+ intent.getIntExtra("eBike", -1)
    }
}