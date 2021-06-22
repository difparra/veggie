package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.android.getColorFromAttr
import com.diegoparra.veggie.databinding.FragmentCartBinding
import com.diegoparra.veggie.products.app.entities.ProductCart
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.products.app.viewmodels.CartViewModel
import com.diegoparra.veggie.products.app.viewmodels.Total
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private val adapter by lazy { CartAdapter(object : CartAdapter.OnItemClickListener {
        override fun onItemClick(productId: ProductId, position: Int, which: Int) {
            when (which) {
                CartAdapter.OnItemClickListener.BUTTON_ADD -> viewModel.addQuantity(productId)
                CartAdapter.OnItemClickListener.BUTTON_REDUCE -> viewModel.reduceQuantity(productId)
                CartAdapter.OnItemClickListener.VIEW_QUANTITY -> viewModel.setEditablePosition(position)
            }
        }
    })}

    private fun changeBottomNavVisibility(isVisible: Boolean) {
        val bottomNavView = activity?.findViewById<BottomNavigationView>(R.id.nav_view_main)
        bottomNavView?.let {
            if(it.isVisible != isVisible){
                it.isVisible = isVisible
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        changeBottomNavVisibility(isVisible = true)
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
            when (it) {
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

    private fun renderFailure(failure: Failure) {
        when (failure) {
            is Failure.CartFailure.EmptyCartList -> {
                binding.errorText.text = getString(R.string.failure_empty_cart_list)
            }
            else ->
                binding.errorText.text = failure.toString()
        }
    }


    //      ----------------------------------------------------------------------------------------

    private fun totalFunctionality() {
        viewModel.total.observe(viewLifecycleOwner) {
            when (it) {
                is Total.EmptyCart -> {
                    binding.cartTotal.isVisible = true
                    binding.btnMakeOrder.isVisible = false
                    with(binding.cartTotal) {
                        setBackgroundColor(context.getColorFromAttr(R.attr.colorError))
                        setTextColor(context.getColorFromAttr(R.attr.colorOnError))
                        text = getString(R.string.total_empty_cart)
                    }
                }
                is Total.MinNotReached -> {
                    binding.cartTotal.isVisible = true
                    binding.btnMakeOrder.isVisible = false
                    with(binding.cartTotal) {
                        setBackgroundColor(context.getColorFromAttr(R.attr.colorWarning))
                        setTextColor(context.getColorFromAttr(R.attr.colorOnWarning))
                        val toMinOrder = (it.minOrder - it.totalValue).addPriceFormat()
                        text = getString(R.string.total_x_to_complete_min_order, toMinOrder)
                    }
                }
                is Total.OK -> {
                    binding.cartTotal.isVisible = true
                    binding.btnMakeOrder.isVisible = true
                    binding.btnMakeOrder.isEnabled = true
                    with(binding.cartTotal) {
                        setBackgroundColor(context.getColorFromAttr(R.attr.colorPrimaryVariant))
                        setTextColor(context.getColorFromAttr(R.attr.colorOnPrimary))
                        text = getString(R.string.total_order, it.totalValue.addPriceFormat())
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

    private fun makeOrderListener() {
        binding.btnMakeOrder.setOnClickListener {
            changeBottomNavVisibility(isVisible = false)
            val action = CartFragmentDirections.actionNavCartToNavOrder()
            findNavController().navigate(action)
        }
    }


    //      ----------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}