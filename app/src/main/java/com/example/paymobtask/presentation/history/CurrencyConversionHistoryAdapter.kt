package com.example.paymobtask.presentation.history


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paymobtask.databinding.ItemHistoryBinding
import com.example.paymobtask.domain.model.local.CurrencyHistoryItem

class CurrencyConversionHistoryAdapter(
    private val onItemClick: ((CurrencyHistoryItem, Int) -> Unit)? = null
) : ListAdapter<CurrencyHistoryItem, CurrencyConversionHistoryAdapter.HistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick?.invoke(getItem(adapterPosition), adapterPosition)
            }
        }
    }

    class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CurrencyHistoryItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CurrencyHistoryItem>() {
            override fun areItemsTheSame(
                oldItem: CurrencyHistoryItem,
                newItem: CurrencyHistoryItem
            ): Boolean = oldItem.savedAt == newItem.savedAt

            override fun areContentsTheSame(
                oldItem: CurrencyHistoryItem,
                newItem: CurrencyHistoryItem
            ): Boolean = oldItem == newItem
        }
    }
}
