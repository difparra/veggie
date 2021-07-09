package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.android.getColorFromAttr
import com.diegoparra.veggie.core.android.getColorWithAlphaFromAttrs
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.FragmentCartBinding
import com.diegoparra.veggie.products.app.entities.ProductCart
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.app.viewmodels.CartViewModel
import com.diegoparra.veggie.products.app.viewmodels.CartViewModel.Total
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private val adapter by lazy {
        CartAdapter(object : CartAdapter.OnItemClickListener {
            override fun onItemClick(productId: ProductId, position: Int, which: Int) {
                when (which) {
                    CartAdapter.OnItemClickListener.BUTTON_ADD -> viewModel.addQuantity(productId)
                    CartAdapter.OnItemClickListener.BUTTON_REDUCE -> viewModel.reduceQuantity(
                        productId
                    )
                    CartAdapter.OnItemClickListener.VIEW_QUANTITY -> viewModel.setEditablePosition(
                        position
                    )
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        clearCartFunctionality()
        cartProductsFunctionality()
        totalFunctionality()
        makeOrderListener()
    }

    //      ----------------------------------------------------------------------------------------

    private fun clearCartFunctionality() {
        binding.clearCart.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToClearCartDialog()
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
            //  Set views visibility based on Resource state
            binding.progressBar.isVisible = it is Resource.Loading
            binding.cartList.isVisible = it is Resource.Success
            binding.layoutEmptyCart.isVisible = false
                //  Set always not visible, and just set visible if necessary
                //  It should be more frequent to have it false than same as Success.
            binding.errorText.isVisible = it is Resource.Error

            //  Display list on error based on data
            when (it) {
                is Resource.Loading -> { /* no-op */ }
                is Resource.Success -> renderProducts(it.data)
                is Resource.Error -> renderFailure(it.failure)
            }
        }
    }

    private fun renderProducts(products: List<ProductCart>) {
        adapter.submitList(products)
        if (products.isEmpty()) {
            binding.layoutEmptyCart.isVisible = true
        }
    }

    private fun renderFailure(failure: Failure) {
        binding.errorText.text = failure.toString()
    }


    //      ----------------------------------------------------------------------------------------

    private fun totalFunctionality() {
        val warningLabel = WarningLabel(
            textView = binding.cartTotalWarning,
            progressBar = binding.cartTotalProgressBar
        )
        val btnMakeOrder = BtnMakeOrder(
            layout = binding.btnMakeOrder,
            text = binding.btnMakeOrderText,
            total = binding.cartTotal
        )

        viewModel.total.observe(viewLifecycleOwner) {
            warningLabel.setState(it)
            btnMakeOrder.setTotalState(it)
        }
        viewModel.isInternetAvailable.observe(viewLifecycleOwner) {
            warningLabel.setIsInternetAvailable(it)
        }
        viewModel.btnMakeOrderEnabled.observe(viewLifecycleOwner) {
            btnMakeOrder.setEnabled(it)
        }
    }


    //      ----------------------------------------------------------------------------------------

    private fun makeOrderListener() {
        binding.btnMakeOrder.setOnClickListener {
            if (viewModel.total.value is Total.OK) {
                val action = CartFragmentDirections.actionCartFragmentToNavOrder()
                findNavController().navigate(action)
            } else {
                Timber.wtf("Error while getting total from viewModel. Check, it should be Total.OK")
            }
        }
    }


    //      ----------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


//      ------      HELPER CLASSES TO CHANGE STATE IN BUTTONS ACCORDING TO TOTAL STATE

private class WarningLabel(
    private val textView: TextView,
    private val progressBar: ProgressBar
) {
    private var totalState: Total? = null
    private var isVisible: Boolean? = null
    private var isInternetAvailable: Boolean? = null

    private val context get() = textView.context
    private val stringEmpty = context.getString(R.string.empty_cart_list)
    private val colorError = context.getColorFromAttr(R.attr.colorError)
    private val colorOnError = context.getColorFromAttr(R.attr.colorOnError)
    private val colorWarning = context.getColorFromAttr(R.attr.colorWarning)
    private val colorOnWarning = context.getColorFromAttr(R.attr.colorOnWarning)
    private val colorDisabled = context.getColorFromAttr(R.attr.colorGrayDefault)
    val colorOnDisabled =
        context.getColorWithAlphaFromAttrs(R.attr.colorOnGrayDefault, R.attr.alphaDisabled)
    private val stringNoInternet = context.getString(R.string.no_internet_connection)

    fun setIsInternetAvailable(value: Boolean) {
        isInternetAvailable = value
        onTotalOrInternetAccessChanged()
    }

    fun setState(totalState: Total) {
        if (this.totalState == totalState) {
            return
        }
        this.totalState = totalState
        when (totalState) {
            is Total.EmptyCart -> setEmptyCartState()
            is Total.MinNotReached -> setMinNotReachedState(totalState)
            is Total.OK -> setTotalOkState()
            is Total.Error -> setTotalErrorState()
        }
        onTotalOrInternetAccessChanged()
    }

    private fun setVisibility(isVisible: Boolean) {
        if (this.isVisible == isVisible) {
            return
        }
        this.isVisible = isVisible
        textView.isVisible = isVisible
        progressBar.isVisible = isVisible
    }

    private fun setEmptyCartState() {
        setVisibility(true)
        textView.text = stringEmpty
        textView.setBackgroundColor(colorError)
        textView.setTextColor(colorOnError)
        progressBar.progress = 0
    }

    private fun setMinNotReachedState(total: Total.MinNotReached) {
        setVisibility(true)
        val valueToMinOrder = (total.minOrder - total.totalValue).addPriceFormat()
        textView.text =
            context.getString(R.string.total_x_to_complete_min_order, valueToMinOrder)
        textView.setBackgroundColor(colorWarning)
        textView.setTextColor(colorOnWarning)
        progressBar.progress = (100 * total.totalValue) / total.minOrder
    }

    private fun setTotalOkState() {
        setVisibility(false)
        progressBar.progress = 100
    }

    private fun setTotalErrorState() {
        setVisibility(false)
    }


    //  When totalState either totalState or internetAccess has changed
    private fun onTotalOrInternetAccessChanged() {
        if (totalState is Total.OK) {
            isInternetAvailable?.let {
                if (it) {
                    setVisibility(false)
                } else {
                    setInternetAccessWarningInTotalState()
                }
            }
        }
    }

    private fun setInternetAccessWarningInTotalState() {
        setVisibility(true)
        textView.text = stringNoInternet
        textView.setBackgroundColor(colorDisabled)
        textView.setTextColor(colorOnDisabled)
        progressBar.progress = 100
    }

}

private class BtnMakeOrder(
    private val layout: RelativeLayout,
    val text: TextView,
    val total: TextView
) {
    private var currentState: Total? = null
    private var isVisible: Boolean? = null
    private var isEnabled: Boolean? = null

    private val context get() = layout.context

    init {
        text.text = context.getString(R.string.btn_make_order)
    }

    val colorEnabled = context.getColorFromAttr(R.attr.colorPrimary)
    val colorOnEnabled = context.getColorFromAttr(R.attr.colorOnPrimary)
    val colorDisabled = context.getColorFromAttr(R.attr.colorGrayDefault)
    val colorOnDisabled =
        context.getColorWithAlphaFromAttrs(R.attr.colorOnGrayDefault, R.attr.alphaDisabled)
    val children = listOf(text, total)

    fun setTotalState(totalState: Total) {
        total.text = totalState.totalValue.addPriceFormat()
        if (currentState == totalState) {
            return
        }
        if (totalState is Total.Error) {
            setVisibility(false)
        } else {
            setVisibility(true)
        }
    }

    private fun setVisibility(isVisible: Boolean) {
        if (this.isVisible == isVisible) {
            return
        }
        this.isVisible = isVisible
        layout.isVisible = isVisible
    }

    /*
        It has now its custom observer in the viewModel, as it also depends on internetAccessState
        Because of that, I have to remove the control over enabled state from setState, and let open
        to implement with the respective observer in viewModel.
     */
    fun setEnabled(isEnabled: Boolean) {
        if (this.isEnabled == isEnabled) {
            return
        } else {
            this.isEnabled = isEnabled
            layout.isEnabled = isEnabled
            if (isEnabled) {
                layout.setBackgroundColor(colorEnabled)
                children.forEach { it.setTextColor(colorOnEnabled) }
            } else {
                layout.setBackgroundColor(colorDisabled)
                children.forEach { it.setTextColor(colorOnDisabled) }
            }
        }
    }
}