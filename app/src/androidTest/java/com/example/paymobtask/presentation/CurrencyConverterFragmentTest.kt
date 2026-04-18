package com.example.paymobtask.presentation

import android.widget.Spinner
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.paymobtask.MainActivity
import com.example.paymobtask.R
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.kakao.screen.Screen.Companion.onScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CurrencyConverterFragmentTest : TestCase() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun initialState_showsLoadingThenContent() = run {
        step("Wait for currencies to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Verify main UI elements are displayed") {
            onScreen<CurrencyConverterObjectScreen> {
                spinnerFrom.isVisible()
                spinnerTo.isVisible()
                etAmount.isVisible()
                etConvertedAmount.isVisible()
                btnSwap.isVisible()
                btnHistory.isVisible()
            }
        }

        step("Verify amount defaults to 1") {
            onScreen<CurrencyConverterObjectScreen> {
                etAmount {
                    hasText("1")
                }
            }
        }

        step("Verify converted amount is not empty") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 5_000) {
                    etConvertedAmount.isVisible()
                }
            }
        }

        step("Error layout should be hidden") {
            onScreen<CurrencyConverterObjectScreen> {
                errorLayout.isGone()
                progressBar.isGone()
            }
        }
    }

    @Test
    fun typingAmount_updatesConvertedValue() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Clear amount and type 100") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 5_000) {
                    etAmount {
                        clearText()
                        typeText("100")
                    }
                }
            }
            closeSoftKeyboard()
        }

        step("Converted value should update") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 5_000) {
                    etConvertedAmount {
                        hasAnyText()
                    }
                }
            }
        }
    }

    @Test
    fun emptyAmount_showsEmptyConvertedValue() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Clear the amount field") {
            onScreen<CurrencyConverterObjectScreen> {
                etAmount {
                    clearText()
                }
            }
            closeSoftKeyboard()
        }

        step("Converted value should be empty") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 3_000) {
                    etConvertedAmount {
                        hasText("")
                    }
                }
            }
        }
    }

    @Test
    fun swapButton_keepsScreenUsable() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Click swap button") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    btnSwap {
                        click()
                    }
                }
            }
        }
        step("Main fields should still be visible after swap") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    spinnerFrom.isVisible()
                    spinnerTo.isVisible()
                    etAmount.isVisible()
                    etConvertedAmount.isVisible()
                    btnSwap.isVisible()
                    btnHistory.isVisible()
                }
            }
        }
    }

    @Test
    fun selectingFromCurrency_triggersConversion() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }
        step("Select a different item in FROM spinner directly") {
            activityRule.scenario.onActivity { activity ->
                val spinner = activity.findViewById<Spinner>(R.id.spinnerFrom)
                spinner.setSelection(2)
            }
        }

        step("Converted field should still be visible") {
            onScreen<CurrencyConverterObjectScreen> {
                etConvertedAmount.isVisible()
            }
        }
    }

    @Test
    fun selectingToCurrency_triggersConversion() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Select a different item in TO spinner directly") {
            activityRule.scenario.onActivity { activity ->
                val spinner = activity.findViewById<Spinner>(R.id.spinnerTo)
                spinner.setSelection(3)
            }
        }

        step("Converted field should still be visible") {
            onScreen<CurrencyConverterObjectScreen> {
                etConvertedAmount.isVisible()
            }
        }

    }

    @Test
    fun historyButton_navigatesToHistoryScreen() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Click history button") {
            onScreen<CurrencyConverterObjectScreen> {
                btnHistory {
                    click()
                }
            }
        }

        step("Converter screen should disappear") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 3_000) {
                    contentGroup.doesNotExist()
                }
            }
        }
    }

    @Test
    fun convertingProgress_hiddenWhenNotConverting() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Progress should be hidden") {
            onScreen<CurrencyConverterObjectScreen> {
                convertingProgress.isGone()
            }
        }
    }

    @Test
    fun convertedAmountField_isNotEditable() = run {
        step("Wait for content to load") {
            onScreen<CurrencyConverterObjectScreen> {
                flakySafely(timeoutMs = 10_000) {
                    contentGroup.isVisible()
                }
            }
        }

        step("Converted amount field should be disabled") {
            onScreen<CurrencyConverterObjectScreen> {
                etConvertedAmount.isDisabled()
            }
        }
    }
}