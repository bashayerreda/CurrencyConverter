package com.example.paymobtask.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.paymobtask.data.local.db.AppDatabase
import com.example.paymobtask.data.local.entity.ConversionHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ConversionHistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ConversionHistoryDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = db.conversionHistoryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun createEntity(
        from: String = "USD",
        to: String = "EUR",
        amount: Double = 100.0,
        convertedAmount: Double = 85.0,
        rate: Double = 0.85,
        savedAt: Long = System.currentTimeMillis(),
        dayKey: String = "2026-04-17"
    ) = ConversionHistoryEntity(
        fromCurrency = from,
        toCurrency = to,
        amount = amount,
        convertedAmount = convertedAmount,
        rate = rate,
        savedAt = savedAt,
        dayKey = dayKey
    )

    @Test
    fun insert_returnsPositiveId() = runTest {
        val id = dao.insert(createEntity())
        assertTrue(id > 0)
    }

    @Test
    fun insert_multipleItems_allPersisted() = runTest {
        val now = System.currentTimeMillis()
        dao.insert(createEntity(savedAt = now))
        dao.insert(createEntity(savedAt = now + 1000))
        dao.insert(createEntity(savedAt = now + 2000))
        val result = dao.observeHistory(now - 1).first()
        assertEquals(3, result.size)
    }

    @Test
    fun observeHistory_filtersBy_startTime() = runTest {
        val old = 1000L
        val recent = 5000L
        dao.insert(createEntity(savedAt = old, dayKey = "2026-04-13"))
        dao.insert(createEntity(savedAt = recent, dayKey = "2026-04-17"))
        val result = dao.observeHistory(startTime = 3000L).first()
        assertEquals(1, result.size)
        assertEquals(recent, result[0].savedAt)
    }

    @Test
    fun observeHistory_orderedByTimestamp_descending() = runTest {
        dao.insert(createEntity(savedAt = 1000L, dayKey = "2026-04-14"))
        dao.insert(createEntity(savedAt = 3000L, dayKey = "2026-04-16"))
        dao.insert(createEntity(savedAt = 2000L, dayKey = "2026-04-15"))
        val result = dao.observeHistory(startTime = 0L).first()
        assertEquals(3, result.size)
        assertEquals(3000L, result[0].savedAt)
        assertEquals(2000L, result[1].savedAt)
        assertEquals(1000L, result[2].savedAt)
    }

    @Test
    fun observeHistory_returnsEmptyWhen_noMatchingData() = runTest {
        dao.insert(createEntity(savedAt = 1000L))
        val result = dao.observeHistory(startTime = 5000L).first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteBySavedAt_removesCorrectItem() = runTest {
        val target = 2000L
        dao.insert(createEntity(savedAt = 1000L, dayKey = "2026-04-14"))
        dao.insert(createEntity(savedAt = target, dayKey = "2026-04-15"))
        dao.insert(createEntity(savedAt = 3000L, dayKey = "2026-04-16"))
        dao.deleteBySavedAt(target)
        val result = dao.observeHistory(startTime = 0L).first()
        assertEquals(2, result.size)
        assertTrue(result.none { it.savedAt == target })
    }

    @Test
    fun deleteBySavedAt_doesNothingWhen_noMatch() = runTest {
        dao.insert(createEntity(savedAt = 1000L))
        dao.deleteBySavedAt(9999L)
        val result = dao.observeHistory(startTime = 0L).first()
        assertEquals(1, result.size)
    }
}