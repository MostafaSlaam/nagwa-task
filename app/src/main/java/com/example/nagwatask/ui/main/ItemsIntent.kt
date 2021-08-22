package com.example.nagwatask.ui.main
sealed class ItemsIntent {
    object FetchItems : ItemsIntent()
    object FetchFakeItems : ItemsIntent()
}