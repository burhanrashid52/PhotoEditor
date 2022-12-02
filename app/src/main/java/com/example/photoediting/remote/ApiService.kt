package com.example.photoediting.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddNewStoryResponse>

    @GET("/v1/stories")
    fun getAllStory(
        @Query("size") size: Int,
        @Query("location") location: Int,
    ): Call<GetAllStoryResponse>

    @GET("/v1/stories")
    suspend fun getAllStoryPager(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): GetAllStoryResponse
}