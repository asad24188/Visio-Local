package com.visio.app.Services

import com.visio.app.DataModel.BaseResponse
import com.visio.app.DataModel.Login.LoginResponse
import com.visio.app.DataModel.projectDetail.ProjectDetailResponse
import com.visio.app.DataModel.projects.ProjectsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

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


    @POST("create-project")
    @FormUrlEncoded
    fun addProject(
        @Field("user_id") user_id: String,
        @Field("project_name") project_name: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Call<BaseResponse>


    @Multipart
    @POST("create-collection")
    fun createPost(
        @Part("project_id") project_id: RequestBody,
        @Part("collection_name") collection_name: RequestBody,
        @Part("card_id1") card_id1: RequestBody,
        @Part("card_id2") card_id2: RequestBody,
        @Part("card_id3") card_id3: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part file: List<MultipartBody.Part?>,
        ):
            Call<BaseResponse>

    @GET
    fun getProjects(@Url url: String): Call<ProjectsResponse>

    @GET
    fun projectDetail(@Url url: String): Call<ProjectDetailResponse>

    @GET
    fun deleteProject(@Url url: String): Call<BaseResponse>

    @GET
    fun deleteCollection(@Url url: String): Call<BaseResponse>

}