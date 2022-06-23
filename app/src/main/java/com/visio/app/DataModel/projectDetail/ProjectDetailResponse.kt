package com.visio.app.DataModel.projectDetail

import com.google.gson.annotations.SerializedName

data class ProjectDetailResponse(

    var status: Boolean,
    var message: String,
    var data: ArrayList<Collection>

)
