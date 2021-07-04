package com.diegoparra.veggie.order.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.databinding.ListItemOrderDetailsHeaderBinding
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.ui.ui_utils.print
import com.diegoparra.veggie.order.ui.ui_utils.printFullWithShortFormat

class OrderDetailsHeaderAdapter: RecyclerView.Adapter<OrderDetailsHeaderAdapter.ViewHolder>() {

    private var _order: Order? = null

    fun setOrderDetails(order: Order) {
        _order = order
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemOrderDetailsHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        _order?.let { holder.bind(order = it) }
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(private val binding: ListItemOrderDetailsHeaderBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.status.text = order.status.print(binding.status.context)
            binding.address.text = order.shippingInfo.address.fullAddress(singleLine = true)
            binding.deliveryDateTime.text = order.shippingInfo.deliverySchedule.printFullWithShortFormat()
            binding.paymentData.text = order.paymentInfo.print(binding.paymentData.context)
        }
    }
}