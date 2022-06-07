package com.example.pagingcat.data

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CatSearchApi {

//    @Headers("x-api-key: ApiKey")
    @GET("images/search")
    suspend fun cats(@Query("limit") limit: Int, @Query("page") page: Int, @Query("api_key")apikey:String): List<Cat>

}