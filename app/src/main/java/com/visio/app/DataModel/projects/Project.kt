package com.visio.app.DataModel.projects

data class Project(

    var id: Int,
    var project_name: String,
    var created_at: String,
    var updated_at: String,
    var latitude: String,
    var longitude: String,
    var checked: Boolean = false


)
