package com.diegoparra.veggie.products.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.diegoparra.veggie.R
import com.diegoparra.veggie.databinding.ListItemCartBinding
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.ui.utils.addThousandSeparator
import com.diegoparra.veggie.products.ui.utils.setBackground
import timber.log.Timber

class CartAdapter(private var listener: OnItemClickListener) : ListAdapter<ProductCart, CartAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        companion object {
            const val BUTTON_ADD = 1
            const val BUTTON_REDUCE = 2
            const val VIEW_QUANTITY = 3
        }
        fun onItemClick(productId: ProductId, position: Int, which: Int)
    }

    /*private var editablePosition: Int = 0
    fun changeEditablePosition(newEditablePosition: Int) {
        Timber.d("setEditablePosition() called with: newEditablePosition = $newEditablePosition, currentListIndices = ${currentList.indices}")
        //  Update
        if(newEditablePosition in currentList.indices){
            if(editablePosition in currentList.indices){
                notifyItemChanged(editablePosition, Bundle().apply {
                    putBoolean(PayloadConstants.EDITABLE, false)
                })
            }
            notifyItemChanged(newEditablePosition, Bundle().apply {
                putBoolean(PayloadConstants.EDITABLE, true)
            })
            editablePosition = newEditablePosition
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Timber.d("onCreateViewHolder() called with: parent = $parent, viewType = $viewType")
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
        //Timber.d("onBindViewHolder() called with: holder = $holder, position = $position")
        val product = getItem(position)
        //holder.bind(product, editablePosition)
        holder.bind(product)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        //Timber.d("onBindViewHolder() called with: holder = $holder, position = $position, payloads = $payloads")
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
            binding.btnAdd.setOnClickListener { item?.let {
                listener.onItemClick(productId = it.productId, position = bindingAdapterPosition, which = OnItemClickListener.BUTTON_ADD)
            } }
            binding.btnReduce.setOnClickListener { item?.let {
                listener.onItemClick(productId = it.productId, position = bindingAdapterPosition, which = OnItemClickListener.BUTTON_REDUCE)
            } }
            binding.quantity.setOnClickListener { item?.let {
                listener.onItemClick(productId = it.productId, position = bindingAdapterPosition, which = OnItemClickListener.VIEW_QUANTITY)
            } }
        }

        //fun bind(product: ProductCart, editablePosition: Int) {
        fun bind(product: ProductCart) {
            //Timber.d("bind() called with: product = $product")
            this.item = product
            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(product.unit, product.detail)
            loadTotal(product.price, product.quantity)
            loadQuantityState(product.quantity, product.maxOrder)
            //loadEditableState(bindingAdapterPosition == editablePosition)
            loadEditableState(product.isEditable)
        }

        fun bindFromPayload(payload: Bundle) {
            Timber.d("bindFromPayload() called with: payload = $payload")
            if(payload.containsKey(PayloadConstants.QUANTITY)){
                val quantity = payload.getInt(PayloadConstants.QUANTITY)
                loadTotal(payload.getInt(PayloadConstants.PRICE), quantity)
                loadQuantityState(quantity, payload.getInt(PayloadConstants.MAX_ORDER))
            }
            if(payload.containsKey(PayloadConstants.EDITABLE)){
                loadEditableState(payload.getBoolean(PayloadConstants.EDITABLE))
            }
        }

        private fun loadImage(imageUrl: String) {
            binding.image.load(imageUrl)
        }

        private fun loadName(name: String) {
            binding.name.text = name
        }

        @SuppressLint("SetTextI18n")
        private fun loadDescription(unit: String, detail: String?) {
            binding.description.text = unit + (detail?.let { " • $it" } ?: "")
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

        private fun loadEditableState(editable: Boolean) {
            if(editable){
                binding.btnAdd.visibility = View.VISIBLE
                binding.btnReduce.visibility = View.VISIBLE
                binding.quantity.setBackground(null)
            }else{
                binding.btnAdd.visibility = View.INVISIBLE
                binding.btnReduce.visibility = View.INVISIBLE
                binding.quantity.setBackground(R.drawable.circle_outline)
            }
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
                if(oldItem.quantity != newItem.quantity){
                    putInt(PayloadConstants.QUANTITY, newItem.quantity)
                    putInt(PayloadConstants.MAX_ORDER, newItem.maxOrder)
                    putInt(PayloadConstants.PRICE, newItem.price)
                }
                //  Not necessary when the viewModel liveData editablePosition approach
                if(oldItem.isEditable != newItem.isEditable){
                    putBoolean(PayloadConstants.EDITABLE, newItem.isEditable)
                }
            }
        }

        object PayloadConstants {
            const val QUANTITY = "quantity"
            const val MAX_ORDER = "maxOrder"
            const val PRICE = "price"
            const val EDITABLE = "editable"
        }
    }
}