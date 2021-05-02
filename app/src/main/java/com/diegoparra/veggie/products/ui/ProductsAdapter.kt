package com.diegoparra.veggie.products.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.diegoparra.veggie.core.Constants
import com.diegoparra.veggie.core.QtyButton
import com.diegoparra.veggie.databinding.ListItemMainProductBinding
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
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

    class ProductViewHolder(private var binding: ListItemMainProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: MainProdWithQuantity){
            if(!product.stock){
                binding.root.isEnabled = false
                binding.root.children.forEach {
                    //it.isEnabled = false
                    it.alpha = MaterialColors.ALPHA_DISABLED
                }
            }else{
                if(!binding.root.isEnabled){
                    binding.root.isEnabled = true
                    binding.root.children.forEach {
                        //it.isEnabled = true
                        it.alpha = MaterialColors.ALPHA_FULL
                    }
                }
                binding.root.setOnClickListener {
                    navigateToProductDetails(product, it)
                }
            }

            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(product.price, product.discount, product.unit)
            loadQtyButton(product.quantity)
            loadLabel(product.stock, product.discount, product.suggestedLabel)
        }

        private fun navigateToProductDetails(product: MainProdWithQuantity, view: View) {
            val action = HomeFragmentDirections.actionNavHomeToProductDetailsFragment(mainId = product.mainId)
            view.findNavController().navigate(action)
        }

        private fun loadImage(imageUrl: String){
            binding.image.load(imageUrl)
        }

        private fun loadName(name: String){
            binding.name.text = name
            binding.image.contentDescription = name
        }

        private fun loadDescription(price: Int, discount: Float, unit: String){
            val descriptionState : DescriptionState =
                if(discount>0){
                    DescriptionState.Discounted(finalPrice = price, discount = discount, unit = unit, context = binding.description.context)
                }else{
                    DescriptionState.NormalState(price = price, unit = unit)
                }
            binding.description.text = getDescriptionText(descriptionState)
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

        private fun loadLabel(stock: Boolean, discount: Float, suggestedLabel: String){
            val labelState : LabelState =
                if(!stock){
                    LabelState.NoStock
                }else if(discount > 0){
                    LabelState.Discounted(discount)
                }else if(suggestedLabel == Constants.Products.NoLabel){
                    LabelState.Hidden
                }else{
                    LabelState.DisplayLabel(suggestedLabel)
                }

            when(labelState){
                is LabelState.Hidden -> {
                    binding.label.visibility = View.GONE
                }
                else -> {
                    val labelProps = getLabelProps(labelState = labelState, context = binding.label.context)
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