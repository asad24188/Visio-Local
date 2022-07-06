package com.fed.fedsense.RoomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DaoLocalProject {

    @Query("SELECT * FROM LocalProject  WHERE  userId = :id")
    fun loadAll(id: Int): List<LocalProject>

    @Delete
    fun deleteProject(project: LocalProject)

    @Insert
    fun addProject(project: LocalProject)
}