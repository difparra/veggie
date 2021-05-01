package com.diegoparra.veggie.products.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.core.QtyButton
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
            binding.name.text = item.name

            when(item.quantity){
                0 -> {
                    binding.btnQty.setQuantityState(QtyButton.Companion.QtyStates.ZERO)
                    binding.btnQty.text = ""
                }
                else -> {
                    binding.btnQty.setQuantityState(QtyButton.Companion.QtyStates.NORMAL)
                    binding.btnQty.text = item.quantity.toString()
                }
            }

            val label = binding.label.context.getLabel(item.stock, item.discount, item.suggestedLabel)
            if(label == null){
                binding.label.visibility = View.GONE
            }else{
                binding.label.text = label.first
                binding.label.chipBackgroundColor = label.second
                binding.label.visibility = View.VISIBLE
            }
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