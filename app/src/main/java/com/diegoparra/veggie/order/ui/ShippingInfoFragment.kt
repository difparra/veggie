package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.auth.ui_utils.getDefaultErrorMessage
import com.diegoparra.veggie.auth.utils.AuthConstants
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.getColorFromAttr
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.runIfTrue
import com.diegoparra.veggie.databinding.FragmentShippingInfoBinding
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.TimeRange
import com.diegoparra.veggie.order.viewmodels.OrderViewModel
import com.diegoparra.veggie.user.address.domain.AddressConstants
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate

@AndroidEntryPoint
class ShippingInfoFragment : Fragment() {

    private var _binding: FragmentShippingInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var titleDeliveryDateTime: DeliveryScheduleTitle
    private val viewModel: OrderViewModel by hiltNavGraphViewModels(R.id.nav_order)
    private val adapter by lazy {
        ShippingScheduleAdapter { date: LocalDate, timeRange: TimeRange, cost: Int ->
            Timber.d("Date selected: date=$date, from=${timeRange.from}, to=${timeRange.to}")
            titleDeliveryDateTime.setAndExpandError(null)
            viewModel.selectDeliverySchedule(
                deliverySchedule = DeliverySchedule(date = date, timeRange = timeRange),
                cost = cost
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val shippingInfoFragmentAsBackStackEntry =
            navController.getBackStackEntry(R.id.shippingInfoFragment)
        val savedStateHandle = shippingInfoFragmentAsBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(AuthConstants.LOGIN_SUCCESSFUL)
            .observe(shippingInfoFragmentAsBackStackEntry) {
                //  It is just being called when view is visible,
                //  not while in another fragment or when rotating device.
                Timber.d("${AuthConstants.LOGIN_SUCCESSFUL} = $it")
                if (!it) {
                    navController.popBackStack()
                }
            }
        savedStateHandle.getLiveData<Boolean>(AddressConstants.ADDRESS_SELECTED_SUCCESSFUL)
            .observe(shippingInfoFragmentAsBackStackEntry) {
                Timber.d("${AddressConstants.ADDRESS_SELECTED_SUCCESSFUL} = $it")
                it.runIfTrue { viewModel.refreshAddress() }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShippingInfoBinding.inflate(inflater, container, false)
        titleDeliveryDateTime = DeliveryScheduleTitle(title = binding.titleDeliveryDateTime, errorView = binding.errorDeliveryDateTime)
        return binding.root
    }

    private fun setUpToSendBackOrderSendResult() {
        val navController = findNavController()
        OrderResultNavigation.setPreviousDestinationAsOriginal(navController)
        OrderResultNavigation.setResult(navController, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToSendBackOrderSendResult()
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.phoneNumber.setOnClickListener {
            binding.phoneNumberLayout.setAndExpandError(null)
            val action =
                ShippingInfoFragmentDirections.actionShippingInfoFragmentToNavVerifyPhoneNumber()
            findNavController().navigate(action)
        }

        binding.address.setOnClickListener {
            binding.addressLayout.setAndExpandError(null)
            val action = ShippingInfoFragmentDirections.actionShippingInfoFragmentToNavUserAddress()
            findNavController().navigate(action)
        }

        //  To init view, so that it does not matter visibility values in xml.
        titleDeliveryDateTime.setAndExpandError(null)

        binding.recyclerShippingSchedule.setHasFixedSize(true)
        binding.recyclerShippingSchedule.adapter = adapter
        binding.recyclerShippingSchedule.addItemDecoration(
            HeaderItemDecoration(
                parent = binding.recyclerShippingSchedule,
                isHeader = adapter::isHeader
            )
        )

        //  Must observe shippingInfo. Otherwise, value in buttonContinue clickListener will always be null
        viewModel.shippingInfo.observe(viewLifecycleOwner) {}
        binding.buttonContinue.setOnClickListener {
            Timber.d("clickListener. viewModelShippingInfo.value = ${viewModel.shippingInfo.value}")
            viewModel.shippingInfo.value?.let {
                when (it) {
                    is Either.Right -> navigateSuccess()
                    is Either.Left -> handleBtnContinueFailures(it.a)
                }
            }
        }
    }

    private fun subscribeUi() {
        viewModel.isSignedIn.observe(viewLifecycleOwner, EventObserver {
            if(!it){
                findNavController().navigate(ShippingInfoFragmentDirections.actionShippingInfoFragmentToNavSignIn())
            }
        })
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                is AuthFailure.SignInState -> {
                    // Do nothing it has already be handled in isSignedIn observer
                    //  It is important to don't try to navigate from here, as it could be triggered
                    //  more than once (i.e. when fetching address) and cause a failure with navigation
                    //   component as current destination is not correct.
                }
                is Failure.NetworkConnection -> { /* TODO: Handle no network connection */ }
                else -> {
                    //  TODO:   Hide view and display error, possibly set a try again button
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.phoneNumber.observe(viewLifecycleOwner) {
            Timber.d("phoneNumber = $it")
            binding.phoneNumber.setText(it)
        }

        viewModel.address.observe(viewLifecycleOwner) {
            Timber.d("address = ${it?.fullAddress()}")
            binding.address.setText(it?.fullAddress())
        }

        viewModel.deliveryCosts.observe(viewLifecycleOwner) {
            adapter.submitCustomList(it)
        }
    }

    /*
            ----------------------------------------------------------------------------------------
     */


    private fun navigateSuccess() {
        val action = ShippingInfoFragmentDirections.actionShippingInfoFragmentToOrderSummaryFragment()
        findNavController().navigate(action)
    }

    private fun handleBtnContinueFailures(failures: List<Failure>) {
        binding.nestedScrollView?.scrollTo(0, 0)
        binding.phoneNumberLayout.isEndIconVisible = false
        failures.forEach {
            if (it is AuthFailure.WrongInput.Empty) {
                when (it.field) {
                    OrderViewModel.PHONE_NUMBER -> binding.phoneNumberLayout.setAndExpandError(
                        getString(R.string.failure_no_selected_phone_number)
                    )
                    OrderViewModel.ADDRESS -> binding.addressLayout.setAndExpandError(getString(R.string.failure_no_selected_address))
                    OrderViewModel.DELIVERY_DATE_TIME -> titleDeliveryDateTime.setAndExpandError(getString(R.string.failure_no_selected_address))
                }
            } else {
                Snackbar.make(
                    binding.root,
                    if (it is AuthFailure) it.getDefaultErrorMessage(binding.root.context) else it.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }


    /*
            ----------------------------------------------------------------------------------------
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


//      HELPERS

private fun TextInputLayout.setAndExpandError(error: String?) {
    this.error = error
    this.isErrorEnabled = error != null
}

private class DeliveryScheduleTitle(private val title: TextView, private val errorView: TextView) {
    private val errorUiState: Boolean? = null
    private val context get() = title.context
    private val originalColor = title.currentTextColor
    private val errorColor = context.getColorFromAttr(R.attr.colorError)
    init { errorView.setTextColor(errorColor) }

    fun setAndExpandError(error: String?) {
        errorView.text = error
        setErrorUi(error != null)
    }
    private fun setErrorUi(error: Boolean) {
        if(errorUiState == error){
            return
        }
        if(error) {
            errorView.isVisible = true
            title.setTextColor(errorColor)
        }else {
            errorView.isVisible = false
            title.setTextColor(originalColor)
        }
    }
}