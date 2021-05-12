package com.diegoparra.veggie.products.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentCartBinding
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.ui.utils.addThousandSeparator
import com.diegoparra.veggie.core.getColorFromAttr
import com.diegoparra.veggie.products.viewmodels.CartViewModel
import com.diegoparra.veggie.products.viewmodels.Total
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.OnItemClickListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel : CartViewModel by viewModels()
    private val adapter by lazy { CartAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clearCartFunctionality()
        cartProductsFunctionality()
        totalFunctionality()
        makeOrderListener()
    }

    //      ----------------------------------------------------------------------------------------

    private fun clearCartFunctionality() {
        binding.clearCart.setOnClickListener {
            val action = CartFragmentDirections.actionNavCartToClearCartDialogFragment()
            findNavController().navigate(action)
        }
        viewModel.clearCartEnabledState.observe(viewLifecycleOwner) {
            binding.clearCart.isEnabled = it
        }
    }


    //      ----------------------------------------------------------------------------------------

    private fun cartProductsFunctionality() {
        binding.cartList.setHasFixedSize(true)
        binding.cartList.adapter = adapter
        subscribeProductsList()
    }

    private fun subscribeProductsList() {
        viewModel.products.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.cartList.isVisible = false
                    binding.errorText.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.cartList.isVisible = true
                    binding.errorText.isVisible = false
                    renderProducts(it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.cartList.isVisible = false
                    binding.errorText.isVisible = true
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun renderProducts(products: List<ProductCart>) {
        adapter.submitList(products)
    }

    override fun onItemClick(productId: ProductId, position: Int, which: Int) {
        when(which){
            CartAdapter.OnItemClickListener.BUTTON_ADD -> viewModel.addQuantity(productId)
            CartAdapter.OnItemClickListener.BUTTON_REDUCE -> viewModel.reduceQuantity(productId)
            CartAdapter.OnItemClickListener.VIEW_QUANTITY -> viewModel.setEditablePosition(position)
        }
    }

    private fun renderFailure(failure: Failure) {
        when(failure) {
            is Failure.CartFailure.EmptyCartList -> {
                binding.errorText.text = getString(R.string.failure_empty_cart_list)
            }
            else ->
                binding.errorText.text = failure.toString()
        }
    }


    //      ----------------------------------------------------------------------------------------

    private fun totalFunctionality(){
        viewModel.total.observe(viewLifecycleOwner) {
            when(it){
                is Total.EmptyCart -> {
                    binding.cartTotal.isVisible = true
                    binding.btnMakeOrder.isVisible = false
                    with(binding.cartTotal){
                        setBackgroundColor(context.getColorFromAttr(R.attr.colorError))
                        setTextColor(context.getColorFromAttr(R.attr.colorOnError))
                        text = getString(R.string.total_empty_cart)
                    }
                }
                is Total.MinNotReached -> {
                    binding.cartTotal.isVisible = true
                    binding.btnMakeOrder.isVisible = false
                    with(binding.cartTotal){
                        setBackgroundColor(context.getColorFromAttr(R.attr.colorWarning))
                        setTextColor(context.getColorFromAttr(R.attr.colorOnWarning))
                        val toMinOrder = "$" + (it.minOrder - it.totalValue).addThousandSeparator()
                        text = getString(R.string.total_x_to_complete_min_order, toMinOrder)
                    }
                }
                is Total.OK -> {
                    binding.cartTotal.isVisible = true
                    binding.btnMakeOrder.isVisible = true
                    binding.btnMakeOrder.isEnabled = true
                    with(binding.cartTotal){
                        setBackgroundColor(context.getColorFromAttr(R.attr.colorPrimaryVariant))
                        setTextColor(context.getColorFromAttr(R.attr.colorOnPrimary))
                        text = getString(R.string.total_order, it.totalValue.addThousandSeparator())
                    }
                }
                else -> {
                    binding.cartTotal.isVisible = false
                    binding.btnMakeOrder.isVisible = false
                }
            }
        }
    }

    //      ----------------------------------------------------------------------------------------

    private fun makeOrderListener(){
        binding.btnMakeOrder.setOnClickListener {
            //  TODO: clickListener btnMakeOrder
            Toast.makeText(context, "TODO: clickListener btnMakeOrder", Toast.LENGTH_SHORT).show()
        }
    }


    //      ----------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}