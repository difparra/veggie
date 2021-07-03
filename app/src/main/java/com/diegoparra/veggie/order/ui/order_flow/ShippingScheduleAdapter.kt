package com.diegoparra.veggie.order.ui.order_flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.formatApp
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.ListItemShippingDayBinding
import com.diegoparra.veggie.databinding.ListItemShippingTimeBinding
import com.diegoparra.veggie.order.domain.TimeRange
import com.diegoparra.veggie.order.viewmodels.OrderViewModel
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.time.LocalDate

private const val HEADER = R.layout.list_item_shipping_day
private const val ITEM = R.layout.list_item_shipping_time

class ShippingScheduleAdapter(private val onDateTimeSelected: (date: LocalDate, timeRange: TimeRange, cost: Int) -> Unit) :
    ListAdapter<ShippingScheduleAdapter.Item, ShippingScheduleAdapter.ViewHolder>(DiffCallback) {

    //  Supporting function to add sticky headers
    fun isHeader(position: Int) = getItemViewType(position) == HEADER


    //  Submit list with class coming from ViewModel, so that the ScheduleAdapterItems classes keep encapsulated.
    fun submitCustomList(list: List<OrderViewModel.DeliveryScheduleAndCost>) {
        val listToSubmit = list.map {
            Item.ShippingItem(
                date = it.schedule.date,
                timeRange = it.schedule.timeRange,
                cost = it.cost,
                isSelected = it.isSelected
            )
        }.addHeaders()
        submitList(listToSubmit)
    }

    private fun List<Item.ShippingItem>.addHeaders(): List<Item> {
        val list = mutableListOf<Item>()
        var currentDay: LocalDate? = null
        this.forEach {
            if (it.date != currentDay) {
                list.add(Item.Header(it.date))
                currentDay = it.date
            }
            list.add(it)
        }
        return list
    }




    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Header -> HEADER
            is Item.ShippingItem -> ITEM
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
        data class ShippingItem(
            val date: LocalDate,
            val timeRange: TimeRange,
            val cost: Int,
            val isSelected: Boolean
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
                    binding.day.text = it.date.formatApp()
                }
            }
        }

        class ItemViewHolder(
            private val binding: ListItemShippingTimeBinding,
            private val onDateTimeSelected: (date: LocalDate, timeRange: TimeRange, cost: Int) -> Unit
        ) : ViewHolder(binding.root) {

            private var item: Item.ShippingItem? = null

            init {
                binding.root.setOnClickListener {
                    item?.let {
                        onDateTimeSelected(it.date, it.timeRange, it.cost)
                    } ?: throw NullPointerException("item is null. You must set it in bind method.")
                }
            }

            override fun bind(item: Item) {
                this.item = item as Item.ShippingItem
                this.item?.let {
                    val timeRange = it.timeRange
                    binding.deliveryTime.text = Pair(timeRange.from, timeRange.to).formatApp()
                    binding.deliveryCost.text = it.cost.addPriceFormat()
                    binding.deliveryTime.isChecked = it.isSelected
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return if (oldItem is Item.Header && newItem is Item.Header) {
                oldItem.date == oldItem.date
            } else if (oldItem is Item.ShippingItem && newItem is Item.ShippingItem) {
                oldItem.date == newItem.date && oldItem.timeRange == newItem.timeRange
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

}