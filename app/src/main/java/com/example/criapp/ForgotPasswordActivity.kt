package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.AuthRepositoryImpl
import com.example.criapp.models.ForgotPasswordModel
import com.example.criapp.presentation.auth.ForgotPasswordViewModel
import com.example.criapp.presentation.auth.ForgotPasswordViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory

class ForgotPasswordActivity : AppCompatActivity() {

    private val viewModel: ForgotPasswordViewModel by viewModels {
        ForgotPasswordViewModelFactory(AuthRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: ForgotPasswordModel
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE

        val editTextUsername = findViewById<EditText>(R.id.edit_text_username)
        model = ForgotPasswordModel(editTextUsername)

        findViewById<Button>(R.id.button_forgot_password).setOnClickListener {
            if (model.validate(errorTextView)) {
                viewModel.forgotPassword(model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    intent.putExtra("username", model.editTextUsername.text.toString())
                    startActivity(intent)
                    finish()
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }
    }
}