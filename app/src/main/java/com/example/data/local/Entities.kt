package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyses")
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val urlOrTopic: String,
    val platform: String,
    val viralityScore: Int,
    val reachEstimate: String,
    val audienceAge: String,
    val bestUploadTime: String,
    val viralIngredientsJson: String,
    val contentAdvice: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "seo_packs")
data class SeoProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String,
    val titlesJson: String,
    val tagsJson: String,
    val description: String,
    val thumbnailConcept: String,
    val seoScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tracked_keywords")
data class TrackedKeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoTitle: String,
    val videoUrl: String,
    val keyword: String,
    val currentRank: Int, // e.g. 1 = #1 ranking on YouTube search
    val previousRank: Int, // e.g. 6 = previous rank
    val searchVolume: String, // e.g. "85K/mo"
    val competitionLevel: String, // "Low", "Medium", "High"
    val ctrTrendPercent: Double, // e.g. 14.5%
    val estimatedViews: Int, // e.g. 34000
    val rankHistoryJson: String, // e.g. "[18, 14, 11, 8, 6, 4, 2]"
    val ctrHistoryJson: String, // e.g. "[4.5, 6.2, 7.8, 9.5, 11.0, 13.2, 14.5]"
    val timestamp: Long = System.currentTimeMillis()
)

