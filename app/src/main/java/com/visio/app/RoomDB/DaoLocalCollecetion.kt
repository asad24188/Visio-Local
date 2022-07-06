package com.fed.fedsense.RoomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DaoLocalCollecetion {

    @Query("SELECT * FROM LocalCollection  WHERE  projectId = :id")
    fun loadAllCollections(id: Int): List<LocalCollection>

    @Delete
    fun deleteCollection(localCollection: LocalCollection)

    @Insert
    fun addCollection(localCollection: LocalCollection)
}