package com.diegoparra.veggie.order.ui.order_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.databinding.FragmentProductsOrderSummaryBinding
import com.diegoparra.veggie.order.viewmodels.OrderViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class OrderProdsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentProductsOrderSummaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by hiltNavGraphViewModels(R.id.nav_order)
    private val adapter by lazy { OrderProdsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.productsList.adapter = adapter
        binding.btnClose.setOnClickListener { findNavController().popBackStack() }
        binding.goToCart.setOnClickListener {
            val action = OrderProdsFragmentDirections.actionNavOrderPop()
            findNavController().navigate(action)
        }
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
        viewModel.productsList.asLiveData().observe(viewLifecycleOwner) {
            Timber.d("productsList = $it, products = ${it?.products}")
            adapter.submitList(it?.products ?: listOf())
            setErrorIfEmptyList(it?.products.isNullOrEmpty())
        }
        /*  //  There seems to be an issue when using value classes in suspend functions (i.e. inside collect),
            //  and because of that I transformed to liveData, as code inside is not a suspend function.
        lifecycleScope.launchWhenStarted {
            viewModel.productsList.collect {
                Timber.d("productsList = $it, products = ${it?.products}")
                adapter.submitList(it?.products ?: listOf())
                setErrorIfEmptyList(it?.products.isNullOrEmpty())
            }
        }*/
    }

    private fun setErrorIfEmptyList(emptyList: Boolean) {
        with(binding.errorText) {
            if(emptyList) {
                Timber.wtf("Ha ocurrido un error. La lista de products del carrito está vacía en order flow.")
                text = getString(R.string.failure_generic)
                isVisible = true
            }else {
                if(isVisible) isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}