package com.diegoparra.veggie.products.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.diegoparra.veggie.databinding.ListItemCartBinding
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.ui.utils.addThousandSeparator

class CartAdapter(private var listener: OnItemClickListener) : ListAdapter<ProductCart, CartAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onAddClick(productId: ProductId)
        fun onReduceClick(productId: ProductId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ListItemCartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                ),
                listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isNotEmpty()){
            val payload = payloads.last() as Bundle
            holder.bindFromPayload(payload)
        }else{
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class ViewHolder(private var binding: ListItemCartBinding, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        private var item: ProductCart? = null
        init {
            binding.btnAdd.setOnClickListener { item?.let { listener.onAddClick(it.productId) } }
            binding.btnReduce.setOnClickListener { item?.let { listener.onReduceClick(it.productId) } }
        }

        fun bind(product: ProductCart) {
            this.item = product
            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(product.unit, product.detail)
            loadTotal(product.price, product.quantity)
            loadQuantityState(product.quantity, product.maxOrder)
        }

        fun bindFromPayload(payload: Bundle) {
            val quantity = payload.getInt(PayloadConstants.QUANTITY)
            loadTotal(payload.getInt(PayloadConstants.PRICE), quantity)
            loadQuantityState(quantity, payload.getInt(PayloadConstants.MAX_ORDER))
        }

        private fun loadImage(imageUrl: String) {
            binding.image.load(imageUrl)
        }

        private fun loadName(name: String) {
            binding.name.text = name
        }

        @SuppressLint("SetTextI18n")
        private fun loadDescription(unit: String, detail: String?) {
            binding.description.text = unit + detail?.let { " • $it" }
        }

        @SuppressLint("SetTextI18n")
        private fun loadTotal(price: Int, quantity: Int){
            binding.price.text = "Total: $" + (price*quantity).addThousandSeparator()
        }

        private fun loadQuantityState(quantity: Int, maxOrder: Int){
            binding.btnAdd.setQuantityState(quantity = quantity, maxOrder = maxOrder)
            binding.btnReduce.setQuantityState(quantity = quantity, maxOrder = maxOrder)
            binding.quantity.text = quantity.toString()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ProductCart>(){
        override fun areItemsTheSame(oldItem: ProductCart, newItem: ProductCart): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductCart, newItem: ProductCart): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: ProductCart, newItem: ProductCart): Any? {
            return Bundle().apply {
                putInt(PayloadConstants.QUANTITY, newItem.quantity)
                putInt(PayloadConstants.MAX_ORDER, newItem.maxOrder)
                putInt(PayloadConstants.PRICE, newItem.price)
            }
        }

        object PayloadConstants {
            const val QUANTITY = "quantity"
            const val MAX_ORDER = "maxOrder"
            const val PRICE = "price"
        }
    }
}