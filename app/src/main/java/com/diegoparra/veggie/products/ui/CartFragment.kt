package com.diegoparra.veggie.products.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.ResourceViews
import com.diegoparra.veggie.databinding.FragmentCartBinding
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.viewmodels.CartViewModel
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
        binding.cartList.setHasFixedSize(true)
        binding.cartList.adapter = adapter

        subscribeUi()
        binding.clearCart.setOnClickListener {
            AlertDialog.Builder(binding.root.context)

                    .setMessage(R.string.dialog_clear_cart)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        viewModel.clearCartList()
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                         /* Do nothing  */
                    }
                    .create()
                    .show()
        }
    }

    override fun onItemClick(productId: ProductId, position: Int, which: Int) {
        when(which){
            CartAdapter.OnItemClickListener.BUTTON_ADD -> viewModel.addQuantity(productId)
            CartAdapter.OnItemClickListener.BUTTON_REDUCE -> viewModel.reduceQuantity(productId)
            CartAdapter.OnItemClickListener.VIEW_QUANTITY -> viewModel.setEditablePosition(position)
        }
    }


    private fun subscribeUi() {
        val resourceViews = ResourceViews(
                loadingViews = listOf(binding.progressBar),
                successViews = listOf(binding.cartList),
                failureViews = listOf(binding.errorText)
        )
        viewModel.products.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    resourceViews.displayViewsForState(ResourceViews.State.LOADING)
                }
                is Resource.Success -> {
                    resourceViews.displayViewsForState(ResourceViews.State.SUCCESS)
                    renderProducts(it.data)
                    binding.clearCart.isEnabled = true
                }
                is Resource.Error -> {
                    resourceViews.displayViewsForState(ResourceViews.State.ERROR)
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun renderProducts(products: List<ProductCart>) {
        adapter.submitList(products)
    }

    private fun renderFailure(failure: Failure) {
        when(failure) {
            is Failure.CartFailure.EmptyCartList -> {
                binding.errorText.text = getString(R.string.failure_empty_cart_list)
                binding.clearCart.isEnabled = false
            }
            else ->
                binding.errorText.text = failure.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}