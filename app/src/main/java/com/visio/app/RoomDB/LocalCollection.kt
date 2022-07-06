package com.fed.fedsense.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalCollection(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "projectId") val projectId: String,
    @ColumnInfo(name = "collection_name") var collection_name: String,
    @ColumnInfo(name = "card_id1") var card_id1: String,
    @ColumnInfo(name = "card_id2") var card_id2: String,
    @ColumnInfo(name = "card_id3") var card_id3: String,
    @ColumnInfo(name = "latitude") var latitude: String,
    @ColumnInfo(name = "longitude") var longitude: String,
    @ColumnInfo(name = "checked") var checked: Boolean,
    @ColumnInfo(name = "imagePath") var imagePath: String,


)
