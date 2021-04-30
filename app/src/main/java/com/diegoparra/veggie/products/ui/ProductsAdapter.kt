package com.diegoparra.veggie.products.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.databinding.ListItemMainProductBinding
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity

class ProductsAdapter : ListAdapter<MainProdWithQuantity, ProductsAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ListItemMainProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holderProduct: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holderProduct.bind(product)
    }

    class ProductViewHolder(private var binding: ListItemMainProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MainProdWithQuantity){
            binding.text.text = item.name
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<MainProdWithQuantity>() {
        override fun areItemsTheSame(
            oldItem: MainProdWithQuantity,
            newItem: MainProdWithQuantity
        ): Boolean {
            return oldItem.mainId == newItem.mainId
        }

        override fun areContentsTheSame(
            oldItem: MainProdWithQuantity,
            newItem: MainProdWithQuantity
        ): Boolean {
            return oldItem == newItem
        }

    }
}