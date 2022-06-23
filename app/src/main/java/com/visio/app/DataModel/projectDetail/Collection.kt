package com.visio.app.DataModel.projectDetail

import com.google.gson.annotations.SerializedName

data class Collection(

    var id: Int,
    var collection_name: String,
    var card_id1: String,
    var card_id2: String,
    var card_id3: String,
    var latitude: String,
    var longitude: String,
    var collection_image: String,
    var collection_images: ArrayList<CollectionImages>,

)
