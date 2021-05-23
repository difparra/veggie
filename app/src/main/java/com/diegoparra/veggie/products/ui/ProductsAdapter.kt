package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.diegoparra.veggie.NavMainDirections
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.QtyButton
import com.diegoparra.veggie.core.getColorWithAlphaFromAttrs
import com.diegoparra.veggie.products.domain.Label
import com.diegoparra.veggie.databinding.ListItemMainProductBinding
import com.diegoparra.veggie.products.entities.ProductMain
import com.diegoparra.veggie.products.ui.utils.*
import timber.log.Timber

class ProductsAdapter : ListAdapter<ProductMain, ProductsAdapter.ProductViewHolder>(DiffCallback) {

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

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isNotEmpty()){
            Timber.d("payloads $payloads")
            val payload = payloads.last() as Bundle
            holder.bindFromPayload(payload)
        }else{
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class ProductViewHolder(private var binding: ListItemMainProductBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnQty.setOnClickListener { view ->
                val position = bindingAdapterPosition
                val item = if(position != RecyclerView.NO_POSITION) getItem(bindingAdapterPosition) else null
                item?.let { product ->
                    navigateToProductDetails(product, view)
                }
            }
            binding.root.setOnClickListener {
                binding.btnQty.callOnClick()
            }
        }
        fun bind(product: ProductMain){
            loadEnabledState(stock = product.stock)
            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(finalPrice = product.price, discount = product.discount, unit = product.unit)
            loadQtyButton(product.quantity)
            loadLabel(product.label)
        }
        fun bindFromPayload(bundle: Bundle){
            val quantity = bundle.getInt(PayloadConstants.QUANTITY, -1)
            if(quantity != -1){
                loadQtyButton(quantity)
            }
        }

        private fun navigateToProductDetails(product: ProductMain, view: View) {
            //  Use global action, as the original destination is unknown (could be home or search)
            val action = NavMainDirections.actionGlobalProductDetailsFragment(mainId = product.mainId, name = product.name)
            view.findNavController().navigate(action)
        }

        private fun loadEnabledState(stock: Boolean){
            binding.root.loadEnabledState(stock)
        }

        private fun loadImage(imageUrl: String){
            binding.image.load(imageUrl)
        }

        private fun loadName(name: String){
            binding.name.text = name
        }

        private fun loadDescription(finalPrice: Int, discount: Float, unit: String){
            val context = binding.description.context
            val text = getFormattedPrice(finalPrice = finalPrice, discount = discount, context = context)
            text.append(
                    " /${abbreviatedUnit(unit)}",
                    ForegroundColorSpan(context.getColorWithAlphaFromAttrs(colorAttr = R.attr.colorOnSurface, alphaAttr = R.attr.alphaSecondaryText)),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            binding.description.text = text
        }

        private fun loadQtyButton(quantity: Int){
            when(quantity){
                0 -> {
                    binding.btnQty.setQuantityState(QtyButton.Companion.QtyStates.ZERO)
                    binding.btnQty.text = ""
                }
                else -> {
                    binding.btnQty.setQuantityState(QtyButton.Companion.QtyStates.NORMAL)
                    binding.btnQty.text = quantity.toString()
                }
            }
        }

        private fun loadLabel(label: Label){
            binding.label.loadProductLabel(label)
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<ProductMain>() {
        override fun areItemsTheSame(
                oldItem: ProductMain,
                newItem: ProductMain
        ): Boolean {
            return oldItem.mainId == newItem.mainId
        }

        override fun areContentsTheSame(
                oldItem: ProductMain,
                newItem: ProductMain
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: ProductMain, newItem: ProductMain): Any? {
            return Bundle().apply {
                putInt(PayloadConstants.QUANTITY, newItem.quantity)
            }
        }

        object PayloadConstants {
            const val QUANTITY = "quantity"
        }
    }
}