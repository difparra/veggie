package com.diegoparra.veggie.products.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.databinding.ListItemVariationHeaderBinding
import com.diegoparra.veggie.databinding.ListItemVariationItemBinding
import timber.log.Timber

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
        Timber.d("position: $position \n viewHolder: $holder \n item: ${getItem(position)} ")
        when(holder){
            is ViewHolder.HeaderViewHolder -> holder.bind(getItem(position) as VariationUi.Header)
            is ViewHolder.ItemForHeaderViewHolder -> holder.bind(getItem(position) as VariationUi.ItemForHeader)
            is ViewHolder.BasicItemViewHolder -> holder.bind(getItem(position) as VariationUi.BasicItem)
        }
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class HeaderViewHolder(private var binding: ListItemVariationHeaderBinding) : ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(header: VariationUi.Header) {
                binding.title.text = "${header.unit} (±${header.weightGr}g)"
            }
        }

        class ItemForHeaderViewHolder(private var binding: ListItemVariationItemBinding) : ViewHolder(binding.root) {
            fun bind(item: VariationUi.ItemForHeader) {
                binding.price.text = item.price.toString()
                binding.description.text = item.detail ?: ""
                binding.quantity.text = item.quantity.toString()
                binding.btnAdd.setQuantityState(quantity = item.quantity, maxOrder = item.maxOrder)
                binding.btnReduce.setQuantityState(quantity = item.quantity, maxOrder = item.maxOrder)
            }
        }

        class BasicItemViewHolder(private var binding: ListItemVariationItemBinding) : ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(item: VariationUi.BasicItem) {
                binding.price.text = item.price.toString()
                binding.description.text = "${item.unit} (±${item.weightGr}g)"
                binding.quantity.text = item.quantity.toString()
                binding.btnAdd.setQuantityState(quantity = item.quantity, maxOrder = item.maxOrder)
                binding.btnReduce.setQuantityState(quantity = item.quantity, maxOrder = item.maxOrder)
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
