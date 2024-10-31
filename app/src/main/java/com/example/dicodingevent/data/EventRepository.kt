package com.example.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.dicodingevent.data.entity.BookmarkEntity
import com.example.dicodingevent.data.entity.EventEntity
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.retrofit.ApiService
import com.example.dicodingevent.data.room.BookmarkDao
import com.example.dicodingevent.data.room.EventDao
import com.example.dicodingevent.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val bookmarkDao: BookmarkDao,
    private val appExecutors: AppExecutors
) {
    private val activeResult = MediatorLiveData<Result<List<EventEntity>>>()
    private val finishedResult = MediatorLiveData<Result<List<EventEntity>>>()

    //get active event
    fun getActiveEvents(): LiveData<Result<List<EventEntity>>> {
        activeResult.value = Result.Loading
        val client = apiService.getActiveEvents()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()

                    events?.let { eventItems ->
                        eventItems.forEach { eventItem ->
                            val bookmarkStatusLiveData = bookmarkDao.getBookmarkStatus(eventItem.id)

                            bookmarkStatusLiveData.observeForever { bookmarkEntity ->
                                val isBookmarked = bookmarkEntity?.isBookmarked ?: false

                                val event = EventEntity(
                                    eventItem.id,
                                    eventItem.name,
                                    eventItem.summary,
                                    eventItem.description,
                                    eventItem.ownerName,
                                    eventItem.cityName,
                                    eventItem.beginTime,
                                    eventItem.endTime,
                                    eventItem.category,
                                    eventItem.mediaCover,
                                    eventItem.imageLogo,
                                    eventItem.link,
                                    eventItem.registrants,
                                    eventItem.quota,
                                    active = true,
                                    isBookmarked = isBookmarked
                                )
                                eventList.add(event)

                                if (eventList.size == eventItems.size) {
                                    appExecutors.diskIO.execute {
                                        eventDao.deleteActiveEvents()
                                        eventDao.insertEvents(eventList)
                                    }
                                }
                            }
                        }
                    } ?: run {
                        activeResult.postValue(Result.Success(emptyList()))
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                activeResult.value = Result.Error(t.message.toString())
            }
        })


        val localData = eventDao.getActiveEvents()
        activeResult.addSource(localData) { newData ->
            activeResult.value = Result.Success(newData)
        }

        return activeResult
    }

    //get finished event
    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> {
        finishedResult.value = Result.Loading
        val client = apiService.getFinishedEvents()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()

                    events?.let { eventItems ->
                        eventItems.forEach { eventItem ->
                            val bookmarkStatusLiveData = bookmarkDao.getBookmarkStatus(eventItem.id)

                            bookmarkStatusLiveData.observeForever { bookmarkEntity ->
                                val isBookmarked = bookmarkEntity?.isBookmarked ?: false

                                val event = EventEntity(
                                    eventItem.id,
                                    eventItem.name,
                                    eventItem.summary,
                                    eventItem.description,
                                    eventItem.ownerName,
                                    eventItem.cityName,
                                    eventItem.beginTime,
                                    eventItem.endTime,
                                    eventItem.category,
                                    eventItem.mediaCover,
                                    eventItem.imageLogo,
                                    eventItem.link,
                                    eventItem.registrants,
                                    eventItem.quota,
                                    active = false,
                                    isBookmarked = isBookmarked
                                )
                                eventList.add(event)

                                if (eventList.size == eventItems.size) {
                                    appExecutors.diskIO.execute {
                                        eventDao.deleteFinishedEvents()
                                        eventDao.insertEvents(eventList)
                                    }
                                }
                            }
                        }
                    } ?: run {
                        finishedResult.postValue(Result.Success(emptyList()))
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                finishedResult.value = Result.Error(t.message.toString())
            }
        })

        val localData = eventDao.getFinishedEvents()
        finishedResult.addSource(localData) { newData ->
            finishedResult.value = Result.Success(newData)
        }

        return finishedResult
    }

    //search function
    fun searchEvents(active: Int, keyword: String): LiveData<Result<List<EventEntity>>> {
        val searchResult = MediatorLiveData<Result<List<EventEntity>>>()

        val localData = if (active == 1) {
            eventDao.searchActiveEvents("%$keyword%")
        } else {
            eventDao.searchFinishedEvents("%$keyword%")
        }

        searchResult.addSource(localData) { newData ->
            searchResult.value = Result.Success(newData)
        }

        return searchResult
    }

    //bookmark switch function
    fun toggleBookmark(eventId: Int, isBookmarked: Boolean) {
        appExecutors.diskIO.execute {
            if (isBookmarked) {
                bookmarkDao.insertBookmark(BookmarkEntity(eventId, true))
            } else {
                bookmarkDao.deleteBookmark(BookmarkEntity(eventId, false))
            }
        }
    }

    //get bookmark status
    fun getBookmarkStatus(eventId: Int): LiveData<BookmarkEntity?> {
        return bookmarkDao.getBookmarkStatus(eventId)
    }

    //get event bookmarked
    fun getBookmarkedEvents(): LiveData<List<EventEntity>> {
        return eventDao.getEventsByBookmark()
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            bookmarkDao: BookmarkDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, bookmarkDao, appExecutors)
            }.also { instance = it }
    }
}