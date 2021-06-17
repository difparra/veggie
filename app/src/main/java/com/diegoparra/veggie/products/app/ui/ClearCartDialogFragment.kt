package com.diegoparra.veggie.products.app.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.products.app.viewmodels.ClearCartViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

@AndroidEntryPoint
class ClearCartDialogFragment : DialogFragment() {

    val viewModel: ClearCartViewModel by viewModels()

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
        val positiveButton = rootView.findViewById<Button>(R.id.dialog_positive_button)
        val negativeButton = rootView.findViewById<Button>(R.id.dialog_negative_button)

        message.text = getString(R.string.dialog_clear_cart)

        with(positiveButton) {
            text = getString(R.string.yes)
            setOnClickListener {
                viewModel.clearCartList()
                findNavController().popBackStack()
            }
        }
        with(negativeButton) {
            text = getString(R.string.no)
            setOnClickListener { findNavController().popBackStack() }
        }
    }

}