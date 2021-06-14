package com.diegoparra.veggie.user.address.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.getColorFromAttr
import com.diegoparra.veggie.user.address.viewmodels.AddressViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

@AndroidEntryPoint
class AddressActionsDialogFragment : DialogFragment() {

    val viewModel: AddressViewModel by hiltNavGraphViewModels(R.id.nav_main)
    private val args: AddressActionsDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)

            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_generic, null)
            builder.setView(view)
            loadDialogProperties(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun loadDialogProperties(rootView: View) {
        val message = rootView.findViewById<TextView>(R.id.dialog_message)
        val checkAddressBtn = rootView.findViewById<Button>(R.id.dialog_positive_button)
        val deleteAddressBtn = rootView.findViewById<Button>(R.id.dialog_negative_button)

        message.text = getString(R.string.address_actions_dialog_message, args.addressString)

        with(checkAddressBtn) {
            text = getString(R.string.address_actions_dialog_check_address)
            setOnClickListener {
                viewModel.selectAddress(addressId = args.addressId)
                dismiss()
            }
        }
        with(deleteAddressBtn) {
            text = getString(R.string.address_actions_dialog_delete_address)
            setTextColor(rootView.context.getColorFromAttr(R.attr.colorError))
            setOnClickListener {
                viewModel.deleteAddress(addressId = args.addressId)
                dismiss()
            }
        }
    }

}