package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ProfileRepositoryImpl
import com.example.criapp.databinding.ActivityProfileBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.UpdateUserModel
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.profile.ProfileViewModel
import com.example.criapp.presentation.profile.ProfileViewModelFactory
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.SpinnerHelper

class ProfileActivity : BaseActivity() {

    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

        mBinding.labelEditTextName.markRequired()
        mBinding.labelEditTextBirthDate.markRequired()
        mBinding.labelSpinnerSex.markRequired()

        viewModel.profileState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    val user = state.data
                    val date = DateHelper.getDayMonthAndYearFromFormattedDate(user.birthDate)
                    birthDateDay = date.first
                    birthDateMonth = date.second
                    birthDateYear = date.third

                    configSpinners()

                    mBinding.editTextName.setText(user.name)
                    mBinding.editTextNumberOfChildren.setText(user.numberOfChildren.toString())
                    mBinding.editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(birthDateYear, birthDateMonth, birthDateDay))
                    mBinding.spinnerSex.selectItem(user.sex)
                    mBinding.spinnerFamilyType.selectItem(user.familyType)
                    mBinding.spinnerEducationalLevel.selectItem(user.educationalLevel)

                    val updateUserModel = UpdateUserModel(
                        mBinding.editTextName, mBinding.editTextBirthDate, mBinding.spinnerSex,
                        mBinding.spinnerEducationalLevel, mBinding.spinnerFamilyType, mBinding.editTextNumberOfChildren
                    )

                    mBinding.buttonUpdateUser.setOnClickListener {
                        if (updateUserModel.validate(mBinding.textViewErrors, this)) {
                            viewModel.updateProfile(updateUserModel.toRequest())
                        }
                    }

                    mBinding.deactivateAccountButton.setOnClickListener {
                        DialogFactory.createConfirmationDialog(
                            this,
                            getString(R.string.deactivate_account),
                            getString(R.string.deactivate_account_confirmation)
                        ) {
                            viewModel.deactivateAccount()
                            onButtonLogoffClick(null)
                        }.show()
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message) { finish() }.show()
                }
                else -> Unit
            }
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    showSuccessSnackbar(getString(R.string.profile_updated_success))
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    showErrorSnackbar(state.message)
                }
                else -> Unit
            }
        }

        viewModel.loadProfile()
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

    fun onButtonLogoffClick(view: View?) {
        PreferencesManager(this).clearAll()
        startActivity(Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
