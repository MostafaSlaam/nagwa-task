package com.example.nagwatask.repository

import com.example.nagwatask.model.ItemModel
import com.example.nagwatask.util.AppResult

interface ItemsRepository {
    suspend fun getAllJobs() : AppResult<List<ItemModel>>
}
