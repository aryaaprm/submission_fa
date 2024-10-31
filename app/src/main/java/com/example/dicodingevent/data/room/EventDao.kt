package com.example.dicodingevent.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

import com.example.dicodingevent.data.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM evententity WHERE id = :eventId")
    fun getEventById(eventId: Int): LiveData<EventEntity>

    @Query("SELECT * FROM evententity WHERE active = 1")
    fun getActiveEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM evententity WHERE active = 0")
    fun getFinishedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM evententity WHERE active = 1 AND name LIKE :keyword")
    fun searchActiveEvents(keyword: String): LiveData<List<EventEntity>>

    @Query("SELECT * FROM evententity WHERE active = 0 AND name LIKE :keyword")
    fun searchFinishedEvents(keyword: String): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(events: List<EventEntity>)

    @Query("DELETE FROM evententity WHERE active = 1")
    fun deleteActiveEvents()

    @Query("DELETE FROM evententity WHERE active = 0")
    fun deleteFinishedEvents()

    @Query("""SELECT * FROM evententity WHERE id IN (SELECT eventId FROM bookmark)""")
    fun getEventsByBookmark(): LiveData<List<EventEntity>>
}
