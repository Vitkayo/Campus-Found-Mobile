package com.example.lostfound.api

import com.example.lostfound.model.Item
import com.example.lostfound.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("user")
    suspend fun getUsers(@Query("email") email: String? = null): List<User>

    @POST("user")
    suspend fun createUser(@Body user: User): User

    @PUT("user/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): User

    @GET("items")
    suspend fun getItems(): List<Item>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): Item

    @POST("items")
    suspend fun createItem(@Body item: Item): Item

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body item: Item): Item

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String): Item
}
