package com.diegoparra.veggie.order.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.ListItemOrderHistoryBinding
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.ui.ui_utils.print
import com.diegoparra.veggie.order.ui.ui_utils.printFullWithShortFormat

class OrderHistoryAdapter(private val onItemClick: (orderId: String) -> Unit) :
    ListAdapter<Order, OrderHistoryAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemOrderHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(
        private val binding: ListItemOrderHistoryBinding,
        private val onClickListener: (orderId: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private var order: Order? = null

        init {
            binding.root.setOnClickListener {
                order?.let { onClickListener(it.id) }
            }
        }

        fun bind(order: Order) {
            this.order = order
            val context = binding.root.context
            binding.orderId.text = context.getString(R.string.order_no, order.id)
            binding.deliveryDateTime.text =
                order.shippingInfo.deliverySchedule.printFullWithShortFormat()
            binding.deliveryAddress.text = order.shippingInfo.address.fullAddress(singleLine = true)
            binding.totalCost.text = order.total.total.addPriceFormat()
            binding.orderStatus.text = order.status.print(binding.orderStatus.context)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

}