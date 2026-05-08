package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AuthRepositoryImpl
import com.example.criapp.databinding.ActivityResetPasswordBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.ResetPasswordModel
import com.example.criapp.presentation.auth.ResetPasswordViewModel
import com.example.criapp.presentation.auth.ResetPasswordViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory

class ResetPasswordActivity : AppCompatActivity() {

    private val viewModel: ResetPasswordViewModel by viewModels {
        ResetPasswordViewModelFactory(AuthRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: ResetPasswordModel
    private lateinit var mBinding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        mBinding.textViewErrors.visibility = View.GONE

        mBinding.labelEditTextNewPassword.markRequired()
        mBinding.labelEditTextCode.markRequired()

        model = ResetPasswordModel(
            intent.getStringExtra("username").toString(),
            mBinding.editTextNewPassword,
            mBinding.editTextCode
        )

        mBinding.buttonResetPassword.setOnClickListener {
            if (model.validate(mBinding.textViewErrors, this)) {
                viewModel.resetPassword(model.toRequest())
            } else {
                mBinding.textViewErrors.visibility = View.VISIBLE
            }
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.password_updated_success), Toast.LENGTH_SHORT).show()
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