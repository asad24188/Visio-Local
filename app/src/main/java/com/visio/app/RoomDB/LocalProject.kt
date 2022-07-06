package com.fed.fedsense.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalProject(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "project_name") var project_name: String,
    @ColumnInfo(name = "created_at") var created_at: String,
    @ColumnInfo(name = "latitude") var latitude: String,
    @ColumnInfo(name = "longitude") var longitude: String,
    @ColumnInfo(name = "checked") var checked: Boolean

)
