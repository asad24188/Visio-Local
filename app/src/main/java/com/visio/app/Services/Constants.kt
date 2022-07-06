package com.mtechsoft.compassapp.networking

import com.fed.fedsense.RoomDB.LocalCollection
import com.fed.fedsense.RoomDB.LocalProject


object Constants {

    const val BASE_URL = "https://tap4trip.com/visio_app/public/api/"
    const val IMAGE_BASE_URL = "https://tap4trip.com/visio_app/public/"

    const val LOGIN_STATUS = "login_status"

    var PROJECT_ID = ""
    var PROJECT_ID_TO_DELETE = ""
    var SELECTION_MODE_ON = false
    var strings: ArrayList<String> = ArrayList()
    var collections: ArrayList<String> = ArrayList()
    var localProjects: ArrayList<LocalProject> = ArrayList()
    var localCollections: ArrayList<LocalCollection> = ArrayList()


}