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
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AuthRepositoryImpl
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.models.CreateUserModel
import com.example.criapp.presentation.auth.SignUpViewModel
import com.example.criapp.presentation.auth.SignUpViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.SpinnerHelper

class SignUpActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(AuthRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private lateinit var createUserModel: CreateUserModel
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var editTextBirthDate: EditText
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.setNavigationBarColor(resources.getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this)

        configSpinners()

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE
        editTextBirthDate = findViewById(R.id.edit_text_birth_date)

        createUserModel = CreateUserModel(
            findViewById(R.id.edit_text_name),
            editTextBirthDate,
            findViewById(R.id.edit_text_create_username),
            findViewById(R.id.edit_text_password),
            findViewById(R.id.edit_text_number_of_children),
            mBinding.spinnerSex,
            mBinding.spinnerEducationalLevel,
            mBinding.spinnerFamilyType
        )

        findViewById<Button>(R.id.button_create_user).setOnClickListener {
            if (createUserModel.validate(errorTextView, this)) {
                viewModel.createUser(createUserModel.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
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
        SpinnerHelper.configCriAppSpinner(this, mBinding.spinnerSex, SpinnerHelper.sexOptions)
        SpinnerHelper.configCriAppSpinner(this, mBinding.spinnerEducationalLevel, SpinnerHelper.educationalLevelOptions)
        SpinnerHelper.configCriAppSpinner(this, mBinding.spinnerFamilyType, SpinnerHelper.familyTypeOptions)
    }

    fun showDatePickerForBirthDate(view: View) {
        val datePicker = DatePickerFragment { day: Int, month: Int, year: Int ->
            birthDateDay = day
            birthDateMonth = month + 1
            birthDateYear = year
            editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(year, month + 1, day))
            editTextBirthDate.error = null
        }
        datePicker.show(supportFragmentManager, "datePicker")
    }
}