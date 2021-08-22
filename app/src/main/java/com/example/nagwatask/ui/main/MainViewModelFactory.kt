package com.example.nagwatask.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nagwatask.repository.ItemsRepository


class MainViewModelFactory(
    private val repository: ItemsRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return MainViewModel(repository) as T
    }
}