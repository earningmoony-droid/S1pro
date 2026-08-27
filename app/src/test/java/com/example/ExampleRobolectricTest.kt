package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.TrackedKeywordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SocialGrow AI", appName)
    }

    @Test
    fun `insert and retrieve tracked keywords from Room`() = runBlocking {
        val entity = TrackedKeywordEntity(
            videoTitle = "10 AI Tools 2026",
            videoUrl = "https://youtube.com/watch?v=sample",
            keyword = "best ai tools 2026",
            currentRank = 1,
            previousRank = 5,
            searchVolume = "120K/mo (Viral)",
            competitionLevel = "Low",
            ctrTrendPercent = 16.5,
            estimatedViews = 45000,
            rankHistoryJson = "[8, 6, 4, 3, 2, 1]",
            ctrHistoryJson = "[5.0, 7.5, 10.0, 13.0, 16.5]"
        )

        val id = database.appDao().insertTrackedKeyword(entity)
        assertTrue(id > 0)

        val list = database.appDao().getAllTrackedKeywords().first()
        assertEquals(1, list.size)
        assertEquals("best ai tools 2026", list[0].keyword)
        assertEquals(1, list[0].currentRank)
        assertEquals(5, list[0].previousRank)
    }
}

