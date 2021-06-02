package com.diegoparra.veggie.user.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.databinding.FragmentSignInOptionsBinding
import com.diegoparra.veggie.user.ui.utils.getDefaultWrongInputErrorMessage
import com.diegoparra.veggie.user.ui.utils.getDefaultWrongSignInMethodErrorMessage
import com.diegoparra.veggie.user.viewmodels.SignInOptionsViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignInOptionsFragment : Fragment() {

    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
        const val ORIGINAL_DESTINATION: String = "ORIGINAL_DESTINATION"
    }

    private var _binding: FragmentSignInOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedStateHandle: SavedStateHandle
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
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, facebookCallback)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun setUpToSendBackLoginResult() {
        val navController = findNavController()
        //  Set login successful to false, so that it knows that login flow was started.
        savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)
        //  Save the original destination in this stateHandle, because it is necessary to modify
        //  its savedStateHandle and send loginResult. It must be done here as there is no
        //  possibility to get a backStackEntry using index nor accessing the backStackIterator.
        navController.currentBackStackEntry?.savedStateHandle?.set(
            ORIGINAL_DESTINATION,
            navController.previousBackStackEntry?.destination?.id!!
        )
    }

    private fun subscribeUi() {
        viewModel.navigateSignedIn.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                setLoginResultAndNavigate(findNavController(), true)
            }
        })

        viewModel.failure.observe(viewLifecycleOwner) {
            val errorMessage = when (it) {
                is SignInFailure.WrongSignInMethod ->
                    getDefaultWrongSignInMethodErrorMessage(binding.root.context, it)
                is SignInFailure.WrongInput ->
                    getDefaultWrongInputErrorMessage(binding.root.context, it.field, it, false)
                else ->
                    it.toString()
            }
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
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