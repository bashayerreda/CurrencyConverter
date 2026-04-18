package com.example.paymobtask.presentation.currencyconverter

import android.text.InputFilter
import android.text.Spanned

class DecimalDigitsInputFilter(
    digitsBeforeZero: Int,
    digitsAfterZero: Int
) : InputFilter {

    private val pattern = Regex("^\\d{0,$digitsBeforeZero}(\\.\\d{0,$digitsAfterZero})?$")

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val newValue = dest.substring(0, dstart) +
                source.substring(start, end) +
                dest.substring(dend)

        return if (newValue.isEmpty() || pattern.matches(newValue)) {
            null
        } else {
            ""
        }
    }
}