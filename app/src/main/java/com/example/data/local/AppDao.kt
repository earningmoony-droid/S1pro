package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<AnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity): Long

    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun deleteAnalysis(id: Long)

    @Query("SELECT * FROM seo_packs ORDER BY timestamp DESC")
    fun getAllSeoPacks(): Flow<List<SeoProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeoPack(seoPack: SeoProjectEntity): Long

    @Query("DELETE FROM seo_packs WHERE id = :id")
    suspend fun deleteSeoPack(id: Long)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    @Query("SELECT * FROM tracked_keywords ORDER BY timestamp DESC")
    fun getAllTrackedKeywords(): Flow<List<TrackedKeywordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedKeyword(item: TrackedKeywordEntity): Long

    @Query("DELETE FROM tracked_keywords WHERE id = :id")
    suspend fun deleteTrackedKeyword(id: Long)

    @Query("DELETE FROM tracked_keywords")
    suspend fun clearAllTrackedKeywords()
}
