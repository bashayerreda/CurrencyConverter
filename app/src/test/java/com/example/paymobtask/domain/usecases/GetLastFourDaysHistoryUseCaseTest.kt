package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetLastFourDaysHistoryUseCaseTest {

    private lateinit var repository: CurrencyConversionHistoryRepository
    private lateinit var useCase: GetLastFourDaysHistoryUseCase

    @Before
    fun setUp() {
        repository = mock(CurrencyConversionHistoryRepository::class.java)
        useCase = GetLastFourDaysHistoryUseCase(repository)
    }

    @Test
    fun `invoke calls repository with correct startTime and returns flow`() = runTest {
        val expectedList = listOf<CurrencyHistoryItem>()
        val expectedFlow: Flow<List<CurrencyHistoryItem>> = flowOf(expectedList)
        val startTimeCaptor = org.mockito.ArgumentCaptor.forClass(Long::class.java)
        `when`(repository.getLastFourDaysHistory(anyLong()))
            .thenReturn(expectedFlow)
        val beforeCall = System.currentTimeMillis()
        val result = useCase()
        val afterCall = System.currentTimeMillis()
        verify(repository).getLastFourDaysHistory(startTimeCaptor.capture())
        val capturedStartTime = startTimeCaptor.value
        val expectedMin = beforeCall - (4L * 24 * 60 * 60 * 1000)
        val expectedMax = afterCall - (4L * 24 * 60 * 60 * 1000)
        assert(capturedStartTime in expectedMin..expectedMax)
        assertEquals(expectedFlow, result)
    }
}