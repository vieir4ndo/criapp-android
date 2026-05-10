package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AuthRepositoryImpl
import com.example.criapp.databinding.ActivitySignInBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.LoginUserModel
import com.example.criapp.presentation.auth.SignInViewModel
import com.example.criapp.presentation.auth.SignInViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory

class SignInActivity : AppCompatActivity() {

    private val prefsManager by lazy { PreferencesManager(this) }
    private val viewModel: SignInViewModel by viewModels {
        SignInViewModelFactory(AuthRepositoryImpl(RetrofitProvider.create(prefsManager)))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var mBinding: ActivitySignInBinding
    private lateinit var loginUserModel: LoginUserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (prefsManager.isSessionActive()) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            return
        }

        mBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        mBinding.textViewErrors.visibility = View.GONE

        mBinding.labelEditTextUsername.markRequired()
        mBinding.labelEditTextPassword.markRequired()

        loginUserModel = LoginUserModel(mBinding.editTextUsername, mBinding.editTextPassword)

        mBinding.redirectSignUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        mBinding.redirectForgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        mBinding.buttonLoginUser.setOnClickListener {
            if (loginUserModel.validate(mBinding.textViewErrors, this)) {
                mBinding.textViewErrors.visibility = View.GONE
                val username = mBinding.editTextUsername.text.toString()
                val password = mBinding.editTextPassword.text.toString()
                viewModel.login(username, password)
            } else {
                mBinding.textViewErrors.visibility = View.VISIBLE
            }
        }

        viewModel.loginState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    prefsManager.saveAuthData(
                        state.data.token,
                        state.data.expiresIn,
                        state.data.user?.id,
                        state.data.user?.name
                    )
                    val destination = if (prefsManager.hasCompletedOnboarding(state.data.user?.id ?: "")) {
                        MenuActivity::class.java
                    } else {
                        WelcomeMessagesActivity::class.java
                    }
                    startActivity(Intent(this, destination))
                    finish()
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })
    }
}