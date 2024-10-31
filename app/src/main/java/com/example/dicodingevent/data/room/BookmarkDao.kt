package com.example.dicodingevent.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

import com.example.dicodingevent.data.entity.BookmarkEntity

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark WHERE eventId = :eventId")
    fun getBookmarkStatus(eventId: Int): LiveData<BookmarkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    fun deleteBookmark(bookmark: BookmarkEntity)
}
