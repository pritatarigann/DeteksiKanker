package com.dicoding.asclepius.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: HistoryEntity)

    @Query("select * from history")
    fun getAll(): Flow<List<HistoryEntity>>

    @Delete
    suspend fun delete(prediction: HistoryEntity)
}