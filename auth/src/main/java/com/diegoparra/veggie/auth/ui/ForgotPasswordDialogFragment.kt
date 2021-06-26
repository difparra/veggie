package com.diegoparra.veggie.auth.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.viewmodels.EmailSignInViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

@AndroidEntryPoint
class ForgotPasswordDialogFragment: DialogFragment() {

    private val viewModel: EmailSignInViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
    private val args: ForgotPasswordDialogFragmentArgs by navArgs()

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

    private fun loadDialogProperties(view: View) {
        val message = view.findViewById<TextView>(R.id.dialog_message)
        val positiveBtn = view.findViewById<Button>(R.id.dialog_positive_button)
        val negativeBtn = view.findViewById<Button>(R.id.dialog_negative_button)

        message.text = getString(R.string.forgot_password_dialog_message, args.email)

        positiveBtn.text = getString(R.string.continuee)
        positiveBtn.setOnClickListener {
            viewModel.sendPasswordResetEmail(email = args.email)
            this@ForgotPasswordDialogFragment.findNavController().popBackStack()
        }

        negativeBtn.text = getString(R.string.cancel)
        negativeBtn.setOnClickListener {
            this@ForgotPasswordDialogFragment.findNavController().popBackStack()
        }
    }

}