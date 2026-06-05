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
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.CreateUserModel
import com.example.criapp.presentation.auth.SignUpViewModel
import com.example.criapp.presentation.auth.SignUpViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.FeatureFlagsManager
import com.example.criapp.services.SpinnerHelper

class SignUpActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(AuthRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private lateinit var createUserModel: CreateUserModel
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private var aLoadingDialog: ALoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        configSpinners()

        mBinding.textViewErrors.visibility = View.GONE

        mBinding.labelEditTextName.markRequired()
        mBinding.labelEditTextCreateUsername.markRequired()
        mBinding.labelEditTextBirthDate.markRequired()
        mBinding.labelSpinnerSex.markRequired()
        mBinding.labelEditTextPassword.markRequired()

        val usernameLabel = if (FeatureFlagsManager.phoneNumberUsernameEnabled) R.string.username_with_phone else R.string.username_email_only
        mBinding.labelEditTextCreateUsername.text = getString(usernameLabel)
        mBinding.labelEditTextCreateUsername.markRequired()

        createUserModel = CreateUserModel(
            mBinding.editTextName,
            mBinding.editTextBirthDate,
            mBinding.editTextCreateUsername,
            mBinding.editTextPassword,
            mBinding.editTextNumberOfChildren,
            mBinding.spinnerSex,
            mBinding.spinnerEducationalLevel,
            mBinding.spinnerFamilyType
        )

        mBinding.buttonCreateUser.setOnClickListener {
            if (createUserModel.validate(mBinding.textViewErrors, this)) {
                mBinding.textViewErrors.visibility = View.GONE
                viewModel.createUser(createUserModel.toRequest())
            } else {
                mBinding.textViewErrors.visibility = View.VISIBLE
            }
        }

        viewModel.signUpState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    startActivity(Intent(this, SignUpSuccessActivity::class.java))
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }
    }

    private fun configSpinners() {
        SpinnerHelper.configCriAppSpinner(mBinding.spinnerSex, R.array.sex_options)
        SpinnerHelper.configCriAppSpinner(mBinding.spinnerEducationalLevel, R.array.educational_level_options)
        SpinnerHelper.configCriAppSpinner(mBinding.spinnerFamilyType, R.array.family_type_options)
    }

    fun showDatePickerForBirthDate(view: View) {
        val datePicker = DatePickerFragment { day: Int, month: Int, year: Int ->
            birthDateDay = day
            birthDateMonth = month + 1
            birthDateYear = year
            mBinding.editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(year, month + 1, day))
            mBinding.editTextBirthDate.error = null
        }
        datePicker.show(supportFragmentManager, "datePicker")
    }
}