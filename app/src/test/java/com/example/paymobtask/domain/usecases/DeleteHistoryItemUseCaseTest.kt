package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class DeleteHistoryItemUseCaseTest {

    private lateinit var repository: CurrencyConversionHistoryRepository
    private lateinit var useCase: DeleteHistoryItemUseCase

    @Before
    fun setUp() {
        repository = mock(CurrencyConversionHistoryRepository::class.java)
        useCase = DeleteHistoryItemUseCase(repository)
    }

    @Test
    fun `invoke calls repository deleteHistoryItem with correct savedAt`() = runTest {
        val savedAt = 1713300000000L
        useCase(savedAt)
        verify(repository).deleteHistoryItem(savedAt)
    }
}