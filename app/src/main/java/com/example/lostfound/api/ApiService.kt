package com.example.lostfound.api

import com.example.lostfound.model.Item
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("items")
    fun getItems(): Call<List<Item>>

    @GET("items/{id}")
    fun getItem(@Path("id") id: String): Call<Item>

    @POST("items")
    fun createItem(@Body item: Item): Call<Item>

    @PUT("items/{id}")
    fun updateItem(@Path("id") id: String, @Body item: Item): Call<Item>

    @DELETE("items/{id}")
    fun deleteItem(@Path("id") id: String): Call<Item>
}
