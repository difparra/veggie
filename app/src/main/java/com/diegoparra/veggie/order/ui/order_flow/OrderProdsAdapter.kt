package com.diegoparra.veggie.order.ui.order_flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.ListItemProductOrderBinding
import com.diegoparra.veggie.order.domain.ProductOrder
import com.diegoparra.veggie.products.cart.domain.ProductId
import java.lang.IllegalArgumentException

private const val HEADER = R.layout.list_item_product_order_header
private const val ITEM = R.layout.list_item_product_order

class OrderProdsAdapter :
    ListAdapter<ProductOrder, OrderProdsAdapter.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER else ITEM
    }

    override fun submitList(list: List<ProductOrder>?) {
        //  Add to the list a dummy object, just to have place to the header
        val listWithHeader = list?.toMutableList()
        listWithHeader?.add(0, ProductOrder(ProductId("", ""), "", "", -1, "", 0, 0f, 0))
        //  call the normal list
        super.submitList(listWithHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> ViewHolder.HeaderViewHolder(
                inflater.inflate(R.layout.list_item_product_order_header, parent, false)
            )
            ITEM -> ViewHolder.ItemViewHolder(
                ListItemProductOrderBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("viewType is not recognized. Check how you are defining getItemViewType method.")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(product: ProductOrder)

        class HeaderViewHolder(private val headerLayout: View) :
            ViewHolder(headerLayout) {
            override fun bind(product: ProductOrder) {
                /*  There is no need to do nothing, show the layout as it is    */
            }
        }

        class ItemViewHolder(private val binding: ListItemProductOrderBinding) :
            ViewHolder(binding.root) {
            override fun bind(product: ProductOrder) {
                binding.name.text = getTitle(product.name, product.discount, product.detail)
                binding.description.text =
                    getDescription(product.packet, product.weight, product.unit)
                binding.quantity.text = product.quantity.toString()
                binding.totalProd.text = product.total.addPriceFormat(showCurrencySign = false)
            }

            private fun getTitle(name: String, discount: Float, detail: String?): String {
                val discountString = if (discount > 0) " (${(discount * 100).toInt()}% dto)" else ""
                val detailString = detail?.let { " • $it" } ?: ""
                return name + discountString + detailString
            }

            private fun getDescription(
                packet: String, weight: Int, unit: String
            ): String {
                val weightString = if (weight > 0) "(±$weight$unit)" else ""
                return "$packet $weightString"
            }

        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<ProductOrder>() {
        override fun areItemsTheSame(oldItem: ProductOrder, newItem: ProductOrder): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductOrder, newItem: ProductOrder): Boolean {
            return oldItem == newItem
        }
    }

}