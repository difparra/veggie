package com.diegoparra.veggie.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diegoparra.veggie.R
import com.diegoparra.veggie.databinding.FragmentSupportBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cardWhatsappText.text = getString(R.string.whatsapp_button, SupportConstants.contactPhoneNumber)
        binding.cardWhatsapp.setOnClickListener {
            Timber.d("btnWhatsapp clicked")
            openWhatsapp(SupportConstants.contactPhoneNumber)
        }

        val emailSubject = getString(R.string.email_subject)
        binding.cardEmailText.text = getString(R.string.email_button, SupportConstants.contactEmail)
        binding.cardEmail.setOnClickListener {
            Timber.d("btnEmail clicked")
            composeEmail(arrayOf(SupportConstants.contactEmail), emailSubject)
        }
    }

    // https://stackoverflow.com/questions/38422300/how-to-open-whatsapp-using-an-intent-in-your-android-app
    private fun openWhatsapp(phoneNumber: String) {
        Timber.d("openWhatsapp called with phoneNumber = $phoneNumber")
        val whatsappUrl = "https://api.whatsapp.com/send?phone=$phoneNumber"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(whatsappUrl)
        }
        startActivityIfAppExists(intent, "whatsapp")
    }

    //  https://developer.android.com/guide/components/intents-common#ComposeEmail
    private fun composeEmail(addresses: Array<String>, subject: String) {
        Timber.d("composeEmail called with addresses = $addresses, subject = $subject")
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        startActivityIfAppExists(intent, "email")
    }

    // Verify the original intent will resolve to at least one activity
    private fun startActivityIfAppExists(intent: Intent, app: String) {
        Timber.d("startActivityIfAppExists called with intent = $intent")
        /* //   This way is not working. To avoid unnecessary complications, it can be handled with a simple try-catch
        activity?.run {
            if (intent.resolveActivity(packageManager) != null) {
                Timber.d("starting activity...")
                startActivity(intent)
            }
        }*/
        try{
            startActivity(intent)
        }catch (e: Exception) {
            Snackbar.make(binding.root, "Can't open $app. The app may not be installed in the device.", Snackbar.LENGTH_SHORT).show()
            Timber.e("ExceptionClass = ${e.javaClass}, exceptionMessage = ${e.message}, exception = $e \n  It was not possible to open $app with the intent")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}