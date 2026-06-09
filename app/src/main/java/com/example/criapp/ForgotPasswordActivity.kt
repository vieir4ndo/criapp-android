package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AuthRepositoryImpl
import com.example.criapp.databinding.ActivityForgotPasswordBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.ForgotPasswordModel
import com.example.criapp.presentation.auth.ForgotPasswordViewModel
import com.example.criapp.presentation.auth.ForgotPasswordViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.FeatureFlagsManager

class ForgotPasswordActivity : BaseActivity() {

    override val statusBarColorRes: Int get() = R.color.status_bar_anonymous
    override val navigationBarColorRes: Int get() = R.color.nav_bar_anonymous

    private val viewModel: ForgotPasswordViewModel by viewModels {
        ForgotPasswordViewModelFactory(AuthRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: ForgotPasswordModel
    private lateinit var mBinding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

        mBinding.labelEditTextUsername.markRequired()

        val usernameLabel = if (FeatureFlagsManager.phoneNumberUsernameEnabled) R.string.username_with_phone else R.string.username_email_only
        mBinding.labelEditTextUsername.text = getString(usernameLabel)
        mBinding.labelEditTextUsername.markRequired()
        mBinding.editTextUsername.hint = getString(
            if (FeatureFlagsManager.phoneNumberUsernameEnabled) R.string.hint_username_with_phone else R.string.hint_username
        )

        model = ForgotPasswordModel(mBinding.editTextUsername)

        mBinding.buttonForgotPassword.setOnClickListener {
            if (model.validate(mBinding.textViewErrors, this)) {
                viewModel.forgotPassword(model.toRequest())
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