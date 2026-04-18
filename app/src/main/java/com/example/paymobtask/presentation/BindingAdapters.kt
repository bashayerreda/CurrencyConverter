package com.example.paymobtask.presentation

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.BindingAdapter

/**
 * Custom Data Binding adapters.
 *
 * These connect XML binding expressions (e.g. app:visible="@{viewModel.isConverting}")
 * to programmatic view manipulation. The DataBinding compiler generates code that calls
 * these methods whenever the bound StateFlow/LiveData value changes.
 *
 * How it works:
 *  1. Layout XML declares: app:visible="@{viewModel.isConverting}"
 *  2. DataBinding compiler sees @BindingAdapter("visible") matches that attribute
 *  3. When viewModel.isConverting emits a new value, the generated code calls
 *     setVisible(view, newValue) automatically
 */
object BindingAdapters {

    /**
     * Controls View.VISIBLE / View.GONE based on a boolean.
     * Usage: app:visible="@{viewModel.someBoolean}"
     */
    @JvmStatic
    @BindingAdapter("visible")
    fun setVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    /**
     * Controls View.VISIBLE / View.INVISIBLE based on a boolean.
     * Unlike [setVisible], INVISIBLE keeps the view's space in layout.
     * Usage: app:visibleOrInvisible="@{viewModel.someBoolean}"
     */
    @JvmStatic
    @BindingAdapter("visibleOrInvisible")
    fun setVisibleOrInvisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Sets the text on an EditText only if it differs from current text,
     * preventing infinite loops when used with two-way binding or TextWatcher.
     * Usage: app:textValue="@{viewModel.convertedValue}"
     */
    @JvmStatic
    @BindingAdapter("textValue")
    fun setTextValue(editText: EditText, value: String?) {
        val newValue = value ?: ""
        if (editText.text.toString() != newValue) {
            editText.setText(newValue)
        }
    }

    /**
     * Sets spinner selection only if it differs from current position,
     * preventing unnecessary listener triggers.
     * Usage: app:selection="@{viewModel.fromIndex}"
     */
    @JvmStatic
    @BindingAdapter("selection")
    fun setSelection(spinner: Spinner, position: Int) {
        if (spinner.selectedItemPosition != position) {
            spinner.setSelection(position)
        }
    }
}