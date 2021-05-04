package com.diegoparra.veggie.products.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.core.QtyButton
import com.diegoparra.veggie.databinding.ListItemVariationHeaderBinding
import com.diegoparra.veggie.databinding.ListItemVariationItemBinding
import com.diegoparra.veggie.products.ui.utils.getFormattedPrice

private const val HEADER = 0
private const val ITEM_FOR_HEADER = 1
private const val BASIC_ITEM = 2

class VariationsAdapter : ListAdapter<VariationUi, VariationsAdapter.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is VariationUi.Header -> HEADER
            is VariationUi.ItemForHeader -> ITEM_FOR_HEADER
            is VariationUi.BasicItem -> BASIC_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            HEADER -> ViewHolder.HeaderViewHolder(
                    ListItemVariationHeaderBinding.inflate(inflater, parent, false)
            )
            ITEM_FOR_HEADER -> ViewHolder.ItemForHeaderViewHolder(
                    ListItemVariationItemBinding.inflate(inflater, parent, false)
            )
            else -> ViewHolder.BasicItemViewHolder(
                    ListItemVariationItemBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ViewHolder.HeaderViewHolder -> holder.bind(getItem(position) as VariationUi.Header)
            is ViewHolder.ItemForHeaderViewHolder -> holder.bind(getItem(position) as VariationUi.ItemForHeader)
            is ViewHolder.BasicItemViewHolder -> holder.bind(getItem(position) as VariationUi.BasicItem)
        }
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class HeaderViewHolder(private var binding: ListItemVariationHeaderBinding) : ViewHolder(binding.root) {
            fun bind(header: VariationUi.Header) {
                loadBasicDescription(binding.title, header.unit, header.weightGr)
            }
        }

        class ItemForHeaderViewHolder(private var binding: ListItemVariationItemBinding) : ViewHolder(binding.root) {
            fun bind(item: VariationUi.ItemForHeader) {
                loadPriceBox(binding.price, item.price, item.discount)
                binding.description.text = item.detail ?: ""
                loadQuantityViews(
                        btnReduce = binding.btnReduce, qtyView = binding.quantity, btnAdd = binding.btnAdd,
                        quantity = item.quantity, maxOrder = item.maxOrder
                )
            }
        }

        class BasicItemViewHolder(private var binding: ListItemVariationItemBinding) : ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(item: VariationUi.BasicItem) {
                loadPriceBox(binding.price, item.price, item.discount)
                loadBasicDescription(binding.description, item.unit, item.weightGr)
                loadQuantityViews(
                        btnReduce = binding.btnReduce, qtyView = binding.quantity, btnAdd = binding.btnAdd,
                        quantity = item.quantity, maxOrder = item.maxOrder
                )
            }
        }


        protected fun loadPriceBox(priceView: TextView, price: Int, discount: Float){
            val formattedPriceString = getFormattedPrice(finalPrice = price, discount = discount, context = priceView.context)
            priceView.text = formattedPriceString
        }

        @SuppressLint("SetTextI18n")
        protected fun loadBasicDescription(view: TextView, unit: String, weightGr: Int){
            view.text = "$unit (± ${weightGr}g)"
        }

        protected fun loadQuantityViews(btnReduce: QtyButton, qtyView: TextView, btnAdd: QtyButton,
                                        quantity: Int, maxOrder: Int) {
            btnAdd.setQuantityState(quantity = quantity, maxOrder = maxOrder)
            btnReduce.setQuantityState(quantity = quantity, maxOrder = maxOrder)
            qtyView.text = quantity.toString()
            if(quantity==0){
                btnReduce.visibility = View.GONE
                qtyView.visibility = View.GONE
            }else{
                btnReduce.visibility = View.VISIBLE
                qtyView.visibility = View.VISIBLE
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VariationUi>() {
        override fun areItemsTheSame(oldItem: VariationUi, newItem: VariationUi): Boolean {
            return if(oldItem is VariationUi.Header && newItem is VariationUi.Header){
                oldItem.unit == newItem.unit
            }else if(oldItem is VariationUi.ItemForHeader && newItem is VariationUi.ItemForHeader){
                oldItem.varId == newItem.varId
            }else if(oldItem is VariationUi.BasicItem && newItem is VariationUi.BasicItem){
                oldItem.varId == newItem.varId
            }else{
                false
            }
        }

        override fun areContentsTheSame(oldItem: VariationUi, newItem: VariationUi): Boolean {
            return oldItem == newItem
        }

    }
}
