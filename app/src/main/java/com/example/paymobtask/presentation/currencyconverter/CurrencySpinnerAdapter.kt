package com.example.paymobtask.presentation.currencyconverter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.paymobtask.R
import com.example.paymobtask.domain.model.remote.Currency

/**
 * Custom spinner adapter showing currency code + name
 */
class CurrencySpinnerAdapter(
    context: Context,
    currencies: List<Currency>
) : ArrayAdapter<Currency>(context, R.layout.item_currency_spinner, currencies) {
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_currency_spinner, parent, false)
        val currency = getItem(position)
        view.findViewById<TextView>(R.id.tvCurrencyCode).text = currency?.code ?: ""
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_currency_dropdown, parent, false)
        val currency = getItem(position)
        view.findViewById<TextView>(R.id.tvCurrencyCode).text = currency?.code ?: ""
        view.findViewById<TextView>(R.id.tvCurrencyName).text = currency?.name ?: ""
        return view
    }
}