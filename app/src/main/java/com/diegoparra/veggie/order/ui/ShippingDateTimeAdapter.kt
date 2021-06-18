package com.diegoparra.veggie.order.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.ListItemShippingDayBinding
import com.diegoparra.veggie.databinding.ListItemShippingTimeBinding
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val HEADER = R.layout.list_item_shipping_day
private const val ITEM = R.layout.list_item_shipping_time

class ShippingDateTimeAdapter(private val onDateTimeSelected: (date: LocalDate, from: LocalTime, to: LocalTime) -> Unit) :
    ListAdapter<ShippingDateTimeAdapter.Item, ShippingDateTimeAdapter.ViewHolder>(DiffCallback) {


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Header -> HEADER
            is Item.DateTime -> ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> ViewHolder.HeaderViewHolder(
                ListItemShippingDayBinding.inflate(inflater, parent, false)
            )
            ITEM -> ViewHolder.ItemViewHolder(
                ListItemShippingTimeBinding.inflate(inflater, parent, false),
                onDateTimeSelected
            )
            else -> throw IllegalArgumentException("viewType is not recognized. Check how you are defining getItemViewType method.")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    /*
        --------------------------------------------------------------------------------------------
            BASE
        --------------------------------------------------------------------------------------------
     */

    sealed class Item {
        data class Header(val date: LocalDate) : Item()
        data class DateTime(
            val date: LocalDate,
            val from: LocalTime,
            val to: LocalTime,
            val cost: Int
        ) : Item()
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Item)

        class HeaderViewHolder(private val binding: ListItemShippingDayBinding) :
            ViewHolder(binding.root) {
            private lateinit var item: Item.Header
            override fun bind(item: Item) {
                this.item = item as Item.Header
                this.item.let {
                    binding.day.text = it.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
                }
            }
        }

        class ItemViewHolder(
            private val binding: ListItemShippingTimeBinding,
            private val onDateTimeSelected: (date: LocalDate, from: LocalTime, to: LocalTime) -> Unit
        ) : ViewHolder(binding.root) {

            private var item: Item.DateTime? = null

            init {
                binding.root.setOnClickListener {
                    item?.let {
                        onDateTimeSelected(it.date, it.from, it.to)
                    } ?: throw NullPointerException("item is null. You must set it in bind method.")
                }
            }

            override fun bind(item: Item) {
                this.item = item as Item.DateTime
                this.item?.let {
                    binding.deliveryTime.text = "${it.from.format()} - ${it.to.format()}"
                    binding.deliveryCost.text = it.cost.addPriceFormat()
                }
            }

            private fun LocalTime.format(): String {
                return this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

}