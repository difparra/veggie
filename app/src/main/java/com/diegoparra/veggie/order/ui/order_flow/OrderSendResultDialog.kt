package com.diegoparra.veggie.order.ui.order_flow

import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.diegoparra.veggie.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

@AndroidEntryPoint
class OrderSendResultDialog : DialogFragment() {

    private val args: OrderSendResultDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)

            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_order_send_result, null)
            builder.setView(view)
            loadDialogProperties(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun loadDialogProperties(rootView: View) {
        val image = rootView.findViewById<ImageView>(R.id.order_result_dialog_image)
        val message = rootView.findViewById<TextView>(R.id.order_result_dialog_message)
        val button = rootView.findViewById<Button>(R.id.order_result_dialog_button)

        if (args.success) {
            renderSuccess(image, message, button)
        } else {
            renderFailure(image, message, button)
        }
    }

    private fun renderSuccess(
        imageView: ImageView,
        messageView: TextView,
        button: Button
    ) {
        imageView.setImageResource(R.drawable.ic_success_outline)
        messageView.text = getString(R.string.success_message_dialog_order_send, args.successString)
        button.text = getString(R.string.understood)

        button.setOnClickListener { navigateSuccess() }
    }

    private fun navigateSuccess() {
        //  It is currently working, but be careful because this may be improved
        //  (Didn't work correctly when setting popUpTo inclusive false, app crashed on navigation
        //  after sending order)
        val deepLink = Uri.parse("myapp://veggie.com/user/orders")
        this@OrderSendResultDialog.findNavController()
            .navigate(deepLink, navOptions { this.popUpTo(R.id.nav_cart) { inclusive = true } })
    }

    private fun renderFailure(
        imageView: ImageView,
        messageView: TextView,
        button: Button
    ) {
        imageView.setImageResource(R.drawable.ic_error_outline)
        messageView.text = getString(R.string.failure_message_dialog_order_send, args.failureString)
        button.text = getString(R.string.close)

        button.setOnClickListener {
            this@OrderSendResultDialog.findNavController().popBackStack()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if(args.success) {
            navigateSuccess()
        }
        super.onDismiss(dialog)
    }

}