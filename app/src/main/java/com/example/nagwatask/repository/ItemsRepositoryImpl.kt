package com.example.nagwatask.repository

import android.content.Context
import com.example.nagwatask.model.ItemModel
import com.example.nagwatask.util.AppResult
import com.example.nagwatask.util.Utils.handleApiError
import com.example.nagwatask.util.Utils.handleSuccess
import com.example.nagwatask.util.noNetworkConnectivityError
import trust.androidtask.util.NetworkManager.isOnline

class ItemsRepositoryImpl(
    private val api: ItemsApi,
    val context: Context
) :
    ItemsRepository {
    override suspend fun getAllJobs(): AppResult<List<ItemModel>> {
        return if (isOnline(context)) {
            return try {
                val response = api.getAllItems()
                if (response.isSuccessful) {
                    //save the data
                    response.body()?.let {
//                        withContext(Dispatchers.IO) { dao.addJobs(it) }
                    }
                    handleSuccess(response)
                } else {
                    handleApiError(response)
                }
            } catch (e: Exception) {
                AppResult.Error(e)
            }
        } else {
//            //check in db if the data exists
//            val data = getCountriesDataFromCache()
//            return if (data.isNotEmpty()) {
//                Log.d(TAG, "from db")
//                AppResult.Success(data)
//            } else
            //no network
            context.noNetworkConnectivityError()
        }
    }


}