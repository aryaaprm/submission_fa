package com.example.dicodingevent.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @PrimaryKey val eventId: Int,
    val isBookmarked: Boolean
)
