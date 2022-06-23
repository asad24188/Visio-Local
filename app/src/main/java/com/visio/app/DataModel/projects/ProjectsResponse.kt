package com.visio.app.DataModel.projects

import com.google.gson.annotations.SerializedName

data class ProjectsResponse(

    var status: Boolean,
    var message: String,
    var data: ArrayList<Project>


)
