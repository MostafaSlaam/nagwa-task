package com.example.nagwatask.ui.main

import com.example.nagwatask.model.ItemModel


sealed class ItemsState {
    object Idle : ItemsState()
    object Loading : ItemsState()
    data class Items(val items: ArrayList<ItemModel>) : ItemsState()
    data class Error(val error: String?) : ItemsState()
}