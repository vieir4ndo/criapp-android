package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AuthRepositoryImpl
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
    private lateinit var errorTextView: TextView
    private lateinit var loginUserModel: LoginUserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE

        val editTextUsername = findViewById<EditText>(R.id.edit_text_username)
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password)
        loginUserModel = LoginUserModel(editTextUsername, editTextPassword)

        findViewById<Button>(R.id.redirect_sign_up_button).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        findViewById<Button>(R.id.redirect_forgot_password_button).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        findViewById<Button>(R.id.button_login_user).setOnClickListener {
            if (loginUserModel.validate(errorTextView)) {
                val username = editTextUsername.text.toString()
                val password = editTextPassword.text.toString()
                viewModel.login(username, password)
            } else {
                errorTextView.visibility = View.VISIBLE
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
                    startActivity(Intent(this, WelcomeMessagesActivity::class.java))
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