package com.diegoparra.veggie.order.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.databinding.ListItemShippingHeaderBinding
import com.diegoparra.veggie.user.address.domain.Address

class ShippingHeaderAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ShippingHeaderAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onPhoneNumberClick()
        fun onAddressClick()
    }

    private var data = Data(null, null)
    fun setData(data: Data) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemShippingHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data = data)
    }

    override fun getItemCount(): Int = 1


    class ViewHolder(
        private val binding: ListItemShippingHeaderBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.phoneNumber.setOnClickListener { listener.onPhoneNumberClick() }
            binding.address.setOnClickListener { listener.onAddressClick() }
        }

        fun bind(data: Data) {
            binding.phoneNumber.setText(data.phoneNumber)
            binding.address.setText(data.address?.fullAddress())
        }
    }

    companion object {
        data class Data(
            val phoneNumber: String?,
            val address: Address?
        )
    }

}