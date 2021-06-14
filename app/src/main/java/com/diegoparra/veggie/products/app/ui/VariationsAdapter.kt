package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.databinding.ListItemVariationHeaderBinding
import com.diegoparra.veggie.databinding.ListItemVariationItemBinding
import com.diegoparra.veggie.products.app.ui.utils.getFormattedPrice
import com.diegoparra.veggie.products.app.ui.utils.loadEnabledState

private const val HEADER = 0
private const val ITEM = 1

class VariationsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<VariationUi, VariationsAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        companion object {
            const val BUTTON_ADD = 1
            const val BUTTON_REDUCE = 2
        }

        fun onItemClick(variationId: String, detail: String?, which: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is VariationUi.Header -> HEADER
            is VariationUi.Item -> ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> ViewHolder.HeaderViewHolder(
                ListItemVariationHeaderBinding.inflate(inflater, parent, false)
            )
            else -> ViewHolder.ItemViewHolder(
                ListItemVariationItemBinding.inflate(inflater, parent, false),
                listener
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && holder is ViewHolder.ItemViewHolder) {
            val payload = payloads.last() as Bundle
            holder.bindFromPayload(payload)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(item: VariationUi)

        protected fun getBasicDescription(unit: String, weightGr: Int): String {
            return "$unit (± ${weightGr}g)"
        }

        class HeaderViewHolder(private var binding: ListItemVariationHeaderBinding) :
            ViewHolder(binding.root) {
            private var item: VariationUi.Header? = null
            override fun bind(item: VariationUi) {
                this.item = item as VariationUi.Header
                this.item?.let {
                    binding.title.text = getBasicDescription(it.unit, it.weightGr)
                }
            }
        }

        class ItemViewHolder(
            private var binding: ListItemVariationItemBinding,
            private val listener: OnItemClickListener
        ) : ViewHolder(binding.root) {
            private var item: VariationUi.Item? = null

            init {
                binding.btnAdd.setOnClickListener {
                    item?.let {
                        listener.onItemClick(
                            variationId = it.varId,
                            detail = it.detail,
                            which = OnItemClickListener.BUTTON_ADD
                        )
                    }
                }
                binding.btnReduce.setOnClickListener {
                    item?.let {
                        listener.onItemClick(
                            variationId = it.varId,
                            detail = it.detail,
                            which = OnItemClickListener.BUTTON_REDUCE
                        )
                    }
                }
            }

            override fun bind(item: VariationUi) {
                this.item = item as VariationUi.Item
                this.item?.let {
                    loadEnabledState(it.stock)
                    loadPrice(it.price, it.discount)
                    loadDescription(it.headerIsVisible, it.unit, it.weightGr, it.detail)
                    loadQuantityState(it.quantity, it.maxOrder)
                }
            }

            fun bindFromPayload(payload: Bundle) {
                if (payload.containsKey(PayloadConstants.QUANTITY)) {
                    loadQuantityState(
                        quantity = payload.getInt(PayloadConstants.QUANTITY),
                        maxOrder = payload.getInt(PayloadConstants.MAX_ORDER)
                    )
                }
            }

            private fun loadEnabledState(stock: Boolean) {
                binding.root.loadEnabledState(stock)
            }

            private fun loadPrice(price: Int, discount: Float) {
                val context = binding.price.context
                binding.price.text =
                    getFormattedPrice(finalPrice = price, discount = discount, context = context)
            }

            private fun loadDescription(
                headerIsVisible: Boolean,
                unit: String,
                weightGr: Int,
                detail: String?
            ) {
                if (!headerIsVisible) {
                    binding.description.text = getBasicDescription(unit, weightGr)
                } else {
                    binding.description.text = detail
                }
            }

            private fun loadQuantityState(quantity: Int, maxOrder: Int) {
                binding.btnAdd.setQuantityState(quantity = quantity, maxOrder = maxOrder)
                binding.btnReduce.setQuantityState(quantity = quantity, maxOrder = maxOrder)
                binding.quantity.text = quantity.toString()
                buttonsVisibilityOnQuantityState(quantity)
            }

            private fun buttonsVisibilityOnQuantityState(quantity: Int) {
                if (quantity == 0) {
                    binding.btnReduce.visibility = View.GONE
                    binding.quantity.visibility = View.GONE
                } else {
                    binding.btnReduce.visibility = View.VISIBLE
                    binding.quantity.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VariationUi>() {
        override fun areItemsTheSame(oldItem: VariationUi, newItem: VariationUi): Boolean {
            return if (oldItem is VariationUi.Header && newItem is VariationUi.Header) {
                oldItem.unit == newItem.unit
            } else if (oldItem is VariationUi.Item && newItem is VariationUi.Item) {
                oldItem.varId == newItem.varId && oldItem.detail == newItem.detail
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: VariationUi, newItem: VariationUi): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: VariationUi, newItem: VariationUi): Any? {
            return if (oldItem is VariationUi.Item && newItem is VariationUi.Item) {
                Bundle().apply {
                    putInt(PayloadConstants.QUANTITY, newItem.quantity)
                    putInt(PayloadConstants.MAX_ORDER, newItem.maxOrder)
                }
            } else {
                null
            }
        }

        object PayloadConstants {
            const val QUANTITY = "quantity"
            const val MAX_ORDER = "maxOrder"
        }
    }
}
