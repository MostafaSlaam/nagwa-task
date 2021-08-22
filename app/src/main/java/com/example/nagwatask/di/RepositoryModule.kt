package com.example.nagwatask.di

import android.content.Context
import com.example.nagwatask.repository.ItemsApi
import com.example.nagwatask.repository.ItemsRepository
import com.example.nagwatask.repository.ItemsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    fun provideCountryRepository(api: ItemsApi, context: Context): ItemsRepository {
        return ItemsRepositoryImpl(api, context)
    }
    single { provideCountryRepository(get(), androidContext()) }

}