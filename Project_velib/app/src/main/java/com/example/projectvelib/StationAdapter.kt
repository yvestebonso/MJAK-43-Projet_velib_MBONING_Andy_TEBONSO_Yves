package com.example.projectvelib


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectvelib.database.EntityStation

class StationAdapter (val listStation: List<EntityStation>) : RecyclerView.Adapter<StationAdapter.StationViewHolder>(){

    class StationViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val stationView = inflater.inflate(R.layout.adapter_station, parent, false)
        return StationViewHolder(stationView)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val stationEntity = listStation[position]

        holder.view.setOnClickListener{
            val context = it.context
            val intent = Intent(context, StationDetailsActivity::class.java)
            intent.putExtra("station_id", stationEntity.id)
            intent.putExtra("station_name", stationEntity.name)
            intent.putExtra("station_op", stationEntity.op)
            intent.putExtra("station_capacity", stationEntity.capacity)
            intent.putExtra("nb_bike", stationEntity.nbBikeAvailable)
            intent.putExtra("nb_dock", stationEntity.nbDockAvailable)
            intent.putExtra("mechanical", stationEntity.mechanical)
            intent.putExtra("eBike", stationEntity.eBike)
            context.startActivity(intent)
        }

        val stationNameTV = holder.view.findViewById<TextView>(R.id.adapter_station_name)
        stationNameTV.text = stationEntity.name

        val stationCapacityTV = holder.view.findViewById<TextView>(R.id.adapter_station_capacity)
        stationCapacityTV.text = "Nombre de vélos disponibles : " + stationEntity.nbBikeAvailable
    }

    override fun getItemCount(): Int = listStation.size
}