package com.example.projectvelib.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EntityStation::class], version = 1)
abstract class DatabaseStation : RoomDatabase() {
    abstract fun Daostation() : DaoStation
}