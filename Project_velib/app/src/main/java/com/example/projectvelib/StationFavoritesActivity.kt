package com.example.projectvelib

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.projectvelib.database.DatabaseStation
import kotlinx.coroutines.runBlocking

class StationFavoritesActivity : AppCompatActivity() {
    private var stationAdapter: StationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_stations)

        val recyclerView = findViewById<RecyclerView>(R.id.list_favorites_stations)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val bdd = Room.databaseBuilder(
            applicationContext,
            DatabaseStation::class.java, "stationEntity"
        ).build()

        val stationDao = bdd.Daostation()
        runBlocking {
            val listStationsFavorites = stationDao.getAll()
            stationAdapter = StationAdapter(listStationsFavorites)
            recyclerView.adapter = StationAdapter(listStationsFavorites)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_map_action -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}