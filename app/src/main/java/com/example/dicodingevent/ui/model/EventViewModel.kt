package com.example.dicodingevent.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.entity.EventEntity
import com.example.dicodingevent.data.Result
import com.example.dicodingevent.data.entity.BookmarkEntity

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _upcomingEvents = MediatorLiveData<List<EventEntity>>()
    val upcomingEvents: LiveData<List<EventEntity>> = _upcomingEvents

    private val _finishedEvents = MediatorLiveData<List<EventEntity>>()
    val finishedEvents: LiveData<List<EventEntity>> = _finishedEvents

    init {
        val activeEventsSource = eventRepository.getActiveEvents()
        _upcomingEvents.addSource(activeEventsSource) { result ->
            handleResult(result, _upcomingEvents)
        }

        val finishedEventsSource = eventRepository.getFinishedEvents()
        _finishedEvents.addSource(finishedEventsSource) { result ->
            handleResult(result, _finishedEvents)
        }
    }

    fun searchEvents(active: Int, keyword: String): LiveData<List<EventEntity>> {
        val searchResult = MediatorLiveData<List<EventEntity>>()
        val searchSource = eventRepository.searchEvents(active, keyword)

        searchResult.addSource(searchSource) { result ->
            handleResult(result, searchResult)
        }

        return searchResult
    }

    fun getBookmarkStatus(eventId: Int): LiveData<BookmarkEntity?> {
        return eventRepository.getBookmarkStatus(eventId)
    }

    fun toggleBookmark(eventId: Int, isBookmarked: Boolean) {
        eventRepository.toggleBookmark(eventId, isBookmarked)
    }

    val bookmarkedEvents: LiveData<List<EventEntity>> = eventRepository.getBookmarkedEvents()

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    private fun handleResult(result: Result<List<EventEntity>>, liveData: MediatorLiveData<List<EventEntity>>) {
        when (result) {
            is Result.Loading -> {
                _isLoading.value = true
            }
            is Result.Success -> {
                _isLoading.value = false
                liveData.value = result.data
            }
            is Result.Error -> {
                _errorMessage.value = result.error
                _isLoading.value = true
            }
        }
    }
}