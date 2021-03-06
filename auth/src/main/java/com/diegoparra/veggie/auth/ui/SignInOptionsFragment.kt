package com.diegoparra.veggie.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.auth.databinding.FragmentSignInOptionsBinding
import com.diegoparra.veggie.auth.utils.AuthResultNavigation
import com.diegoparra.veggie.auth.viewmodels.SignInOptionsViewModel
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.kotlin.Either
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignInOptionsFragment : Fragment() {

    private var _binding: FragmentSignInOptionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInOptionsViewModel by viewModels()

    private val googleContract =
        registerForActivityResult(GoogleSignInContract()) {
            viewModel.onSignInGoogleResult(it)
        }

    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val facebookCallback: FacebookCallback<LoginResult> =
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.let {
                    viewModel.onSignInFacebookResult(Either.Right(it))
                } ?: Timber.e("facebook sign in result was null")
            }

            override fun onCancel() {
                Timber.w("facebook sign in operation was cancelled.")
            }

            override fun onError(error: FacebookException?) {
                error?.let {
                    viewModel.onSignInFacebookResult(Either.Left(it))
                } ?: Timber.e("facebook sign in error was null")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //  Important: Call in onViewCreated. If called in onCreate, when device is rotated the method
        //  will be called again even if screen is not visible and previousBackStack entry will be wrong.
        setUpToSendBackLoginResult()
        subscribeUi()

        binding.emailSignIn.setOnClickListener {
            val action =
                SignInOptionsFragmentDirections.actionSignInOptionsFragmentToEmailFragment()
            findNavController().navigate(action)
        }
        binding.googleSignIn.setOnClickListener {
            binding.progressBar.isVisible = true
            googleContract.launch(viewModel.googleSignInClient)
        }
        binding.facebookSignIn.setOnClickListener {
            binding.progressBar.isVisible = true
            viewModel.loginManager
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
            viewModel.loginManager.registerCallback(callbackManager, facebookCallback)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun setUpToSendBackLoginResult() {
        val navController = findNavController()
        //  Save the original destination in this stateHandle, because it is necessary to modify
        //  its savedStateHandle and send loginResult. It must be done here as there is no
        //  possibility to get a backStackEntry using index nor accessing the backStackIterator.
        AuthResultNavigation.setPreviousDestinationAsOriginal(navController)
        //  Set login successful to false, so that it knows that login flow was started.
        AuthResultNavigation.setResult(navController, result = false)
    }

    private fun subscribeUi() {
        viewModel.navigateSignedIn.observe(viewLifecycleOwner, EventObserver {
            AuthResultNavigation.setResultAndNavigate(findNavController(), it)
        })

        viewModel.failure.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it.getContextMessage(binding.root.context),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}