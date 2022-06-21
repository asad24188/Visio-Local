package com.example.visio.DataModel.Login

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("status")
    var status: Boolean,

    @SerializedName("message")
    var message: String,

    @SerializedName("data")
    var user: User,

    @SerializedName("token")
    var token:String
)
