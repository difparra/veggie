package com.diegoparra.veggie.products.ui

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.QtyButton
import com.diegoparra.veggie.databinding.ListItemMainProductBinding
import com.diegoparra.veggie.products.domain.entities.Description
import com.diegoparra.veggie.products.domain.entities.Label
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.ui.utils.*
import com.google.android.material.color.MaterialColors

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

    inner class ProductViewHolder(private var binding: ListItemMainProductBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { view ->
                val position = bindingAdapterPosition
                val item = if(position != RecyclerView.NO_POSITION) getItem(bindingAdapterPosition) else null
                item?.let { product ->
                    navigateToProductDetails(product, view)
                }
            }
        }
        fun bind(product: MainProdWithQuantity){
            loadEnabledState(enabled = product.stock)
            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(product.description)
            loadQtyButton(product.quantity)
            loadLabel(product.label)
        }

        private fun navigateToProductDetails(product: MainProdWithQuantity, view: View) {
            val action = HomeFragmentDirections.actionNavHomeToProductDetailsFragment(mainId = product.mainId, name = product.name)
            view.findNavController().navigate(action)
        }

        private fun loadEnabledState(enabled: Boolean){
            val currentEnabledState = binding.root.isEnabled
            if(enabled != currentEnabledState){
                binding.root.isEnabled = enabled
                binding.root.children.forEach {
                    //it.isEnabled = enabled
                    it.alpha = if(enabled) MaterialColors.ALPHA_FULL else MaterialColors.ALPHA_DISABLED
                }
            }
        }

        private fun loadImage(imageUrl: String){
            binding.image.load(imageUrl)
        }

        private fun loadName(name: String){
            binding.name.text = name
            binding.image.contentDescription = name
        }

        private fun loadDescription(description: Description){
            val context = binding.description.context
            val text = getFormattedPrice(finalPrice = description.finalPrice, discount = description.discount, context = context)
            text.append(
                    " /${abbreviatedUnit(description.unit)}",
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
            when(label){
                is Label.Hidden -> {
                    binding.label.visibility = View.GONE
                }
                else -> {
                    val labelProps = getLabelProps(label = label, context = binding.label.context)
                    binding.label.text = labelProps?.first
                    binding.label.chipBackgroundColor = labelProps?.second
                    binding.label.visibility = View.VISIBLE
                }
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

        /*  TODO:   Deal with payloads, as quantity is the only value that changes
        override fun getChangePayload(oldItem: MainProdWithQuantity, newItem: MainProdWithQuantity): Any? {
            return super.getChangePayload(oldItem, newItem)
        }

        <-override fun onBindViewHolder(holder: ProductViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
        }
        if(!payloads.isEmpty()){
            Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "], payloads = [" + payloads + "]");
            Bundle payload = (Bundle) payloads.get(payloads.size() - 1);
            int qtyState = payload.getInt("qtyState");
            int quantity = payload.getInt("quantity");
            loadQuantityViews(holder, qtyState, quantity);
        }else{
            Log.d(TAG, "emptyPayload/onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "], payloads = [" + payloads + "]");
            super.onBindViewHolder(holder, position, payloads);
        }
        */

    }
}