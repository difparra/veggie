package com.diegoparra.veggie.order.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.ListItemOrderDetailsTotalsBinding
import com.diegoparra.veggie.order.domain.Order

class OrderDetailsTotalsAdapter: RecyclerView.Adapter<OrderDetailsTotalsAdapter.ViewHolder>() {

    private var _order: Order? = null

    fun setOrderDetails(order: Order) {
        this._order = order
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemOrderDetailsTotalsBinding.inflate(
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

    class ViewHolder(private val binding: ListItemOrderDetailsTotalsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.subtotalValue.text = order.total.subtotal.addPriceFormat()
            binding.deliveryValue.text = order.total.deliveryCost.addPriceFormat()
            binding.totalValue.text = order.total.total.addPriceFormat()
        }
    }
}