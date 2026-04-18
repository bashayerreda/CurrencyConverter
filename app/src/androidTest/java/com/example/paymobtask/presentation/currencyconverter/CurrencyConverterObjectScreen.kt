package com.example.paymobtask.presentation.currencyconverter

import com.example.paymobtask.R
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.spinner.KSpinner
import io.github.kakaocup.kakao.text.KButton

/**
 * Kaspresso Screen object for the Currency Converter Fragment
 */
class CurrencyConverterObjectScreen : KScreen<CurrencyConverterObjectScreen>() {

    override val layoutId: Int = R.layout.fragment_converter
    override val viewClass: Class<*> = CurrencyConverterFragment::class.java

    val progressBar = KProgressBar { withId(R.id.progressBar) }

    val errorLayout = KView { withId(R.id.errorLayout) }

    val contentGroup = KView { withId(R.id.contentGroup) }

    val spinnerFrom = KSpinner(
        builder = { withId(R.id.spinnerFrom) },
        itemTypeBuilder = {}
    )

    val etAmount = KEditText { withId(R.id.etAmount) }

    val btnSwap = KButton { withId(R.id.btnSwap) }

    val spinnerTo = KSpinner(
        builder = { withId(R.id.spinnerTo) },
        itemTypeBuilder = {}
    )
    val etConvertedAmount = KEditText { withId(R.id.etConvertedAmount) }
    val convertingProgress = KProgressBar { withId(R.id.convertingProgress) }

    val btnHistory = KButton { withId(R.id.btnHistory) }

}