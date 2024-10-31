package com.example.dicodingevent.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evententity")
data class EventEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String,
    val description: String,
    val ownerName: String,
    val cityName: String,
    val beginTime: String,
    val endTime: String,
    val category: String,
    val mediaCover: String,
    val imageLogo: String,
    val link: String,
    val registrants: Int,
    val quota: Int,
    val active: Boolean,
    val isBookmarked: Boolean
)
