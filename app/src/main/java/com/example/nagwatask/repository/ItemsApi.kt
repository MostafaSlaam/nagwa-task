package com.example.nagwatask.repository

import com.example.nagwatask.model.ItemModel
import retrofit2.Response
import retrofit2.http.GET

interface ItemsApi {

    @GET("/movies")
    fun getAllItems(): Response<List<ItemModel>>
}