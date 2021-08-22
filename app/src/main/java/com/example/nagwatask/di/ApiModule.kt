package com.example.nagwatask.di

import com.example.nagwatask.repository.ItemsApi
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {

    fun provideCountriesApi(retrofit: Retrofit): ItemsApi {
        return retrofit.create(ItemsApi::class.java)
    }
    single { provideCountriesApi(get()) }

}