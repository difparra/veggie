package com.diegoparra.veggie.products.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
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
        fun onAddClick(productId: ProductId)
        fun onReduceClick(productId: ProductId)
        fun setEditablePosition(position: Int)
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
            binding.quantity.setOnClickListener {
                Timber.d("qtyClicked! -> bindingAdapterPosition = $bindingAdapterPosition")
                listener.setEditablePosition(bindingAdapterPosition) }
        }

        fun bind(product: ProductCart) {
            this.item = product
            loadImage(product.imageUrl)
            loadName(product.name)
            loadDescription(product.unit, product.detail)
            loadTotal(product.price, product.quantity)
            loadQuantityState(product.quantity, product.maxOrder)
            loadEditableState(product.isEditable)
        }

        fun bindFromPayload(payload: Bundle) {
            Timber.d("data received in payload: $payload")
            val quantity = payload.getInt(PayloadConstants.QUANTITY, -1)
            if(quantity != -1){
                loadTotal(payload.getInt(PayloadConstants.PRICE), quantity)
                loadQuantityState(quantity, payload.getInt(PayloadConstants.MAX_ORDER))
            }
            val editable = payload.getByte(PayloadConstants.EDITABLE, (-1).toByte())
            if(editable != (-1).toByte()){
                loadEditableState(editable == 1.toByte())
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
                if(oldItem.isEditable != newItem.isEditable){
                    putByte(PayloadConstants.EDITABLE, if(newItem.isEditable) 1.toByte() else 0.toByte() )
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