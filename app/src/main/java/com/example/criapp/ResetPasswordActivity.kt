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
import com.example.criapp.databinding.ActivityResetPasswordBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.ResetPasswordModel
import com.example.criapp.presentation.auth.ResetPasswordViewModel
import com.example.criapp.presentation.auth.ResetPasswordViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.FeatureFlagsManager

class ResetPasswordActivity : BaseActivity() {

    override val statusBarColorRes: Int get() = R.color.status_bar_anonymous
    override val navigationBarColorRes: Int get() = R.color.nav_bar_anonymous

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

        aLoadingDialog = ALoadingDialog(this)

        val hintRes = if (FeatureFlagsManager.phoneNumberUsernameEnabled)
            R.string.reset_password_hint_with_phone
        else
            R.string.reset_password_hint_email_only
        mBinding.textViewResetHint.setText(hintRes)

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
            }
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createConfirmationDialog(
                        this,
                        getString(R.string.dialog_success_title),
                        getString(R.string.password_updated_success)
                    ) {
                        startActivity(Intent(this, SignInActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }.show()
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