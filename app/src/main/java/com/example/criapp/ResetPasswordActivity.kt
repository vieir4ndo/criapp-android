package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.AuthRepositoryImpl
import com.example.criapp.models.ResetPasswordModel
import com.example.criapp.presentation.auth.ResetPasswordViewModel
import com.example.criapp.presentation.auth.ResetPasswordViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory

class ResetPasswordActivity : AppCompatActivity() {

    private val viewModel: ResetPasswordViewModel by viewModels {
        ResetPasswordViewModelFactory(AuthRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: ResetPasswordModel
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE

        model = ResetPasswordModel(
            intent.getStringExtra("username").toString(),
            findViewById(R.id.edit_text_new_password),
            findViewById(R.id.edit_text_code)
        )

        findViewById<Button>(R.id.button_reset_password).setOnClickListener {
            if (model.validate(errorTextView)) {
                viewModel.resetPassword(model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Contraseña actualizada con exito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
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