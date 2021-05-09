package com.diegoparra.veggie.products.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.diegoparra.veggie.databinding.ListItemCartBinding
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.ui.utils.addThousandSeparator

class CartAdapter : ListAdapter<ProductCart, CartAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ListItemCartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class ViewHolder(private var binding: ListItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductCart) {
            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(product.unit, product.detail)
            loadTotal(product.price, product.quantity)
            loadQuantity(product.quantity, product.maxOrder)
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
            binding.price.text = "$" + (price*quantity).addThousandSeparator()
        }

        private fun loadQuantity(quantity: Int, maxOrder: Int){
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

    }
}