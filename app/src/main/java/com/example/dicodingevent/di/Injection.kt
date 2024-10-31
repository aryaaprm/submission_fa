package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.room.EventDatabase
import com.example.dicodingevent.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val eventDao = database.eventDao()
        val bookmarkDao = database.bookmarkDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, eventDao, bookmarkDao,appExecutors)
    }
}