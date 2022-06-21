package com.example.visio.Services

import com.example.visio.DataModel.Login.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServices {
//    @Header("Accept: application/json")

    @POST("login")
    @FormUrlEncoded
    fun login (
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("device_type") device_type:String,
        @Field("token") token:String
    ): Call<LoginResponse>

    @POST("signup")
    @FormUrlEncoded
    fun signUp(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirm_password: String,
        @Field("device_type") device_type: String,
        @Field("token") token: String
    ): Call<LoginResponse>
}