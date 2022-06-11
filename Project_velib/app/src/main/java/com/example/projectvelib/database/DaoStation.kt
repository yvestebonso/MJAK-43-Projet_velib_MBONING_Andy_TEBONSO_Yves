package com.example.projectvelib.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoStation {
    @Query("SELECT * FROM EntityStation")
    suspend fun getAll(): List<EntityStation>

    @Insert
    suspend fun insert(vararg station: EntityStation)

    @Delete
    suspend fun delete(station: EntityStation)
}