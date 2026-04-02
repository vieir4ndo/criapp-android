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
import com.example.criapp.data.repository.ProfileRepositoryImpl
import com.example.criapp.databinding.ActivityProfileBinding
import com.example.criapp.models.UpdateUserModel
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.profile.ProfileViewModel
import com.example.criapp.presentation.profile.ProfileViewModelFactory
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.SpinnerHelper

class ProfileActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var editTextBirthDate: EditText
    private lateinit var errorTextView: TextView
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

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

                    errorTextView = findViewById(R.id.text_view_errors)
                    errorTextView.visibility = View.GONE
                    val editTextName = findViewById<EditText>(R.id.edit_text_name)
                    val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children)
                    editTextBirthDate = findViewById(R.id.edit_text_birth_date)

                    editTextName.setText(user.name)
                    editTextNumberOfChildren.setText(user.numberOfChildren.toString())
                    editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(birthDateYear, birthDateMonth, birthDateDay))
                    mBinding.spinnerSex.setSelection(user.sex)
                    mBinding.spinnerFamilyType.setSelection(user.familyType)
                    mBinding.spinnerEducationalLevel.setSelection(user.educationalLevel)

                    val updateUserModel = UpdateUserModel(
                        editTextName, editTextBirthDate, mBinding.spinnerSex,
                        mBinding.spinnerEducationalLevel, mBinding.spinnerFamilyType, editTextNumberOfChildren
                    )

                    findViewById<Button>(R.id.button_update_user).setOnClickListener {
                        if (updateUserModel.validate(errorTextView)) {
                            viewModel.updateProfile(updateUserModel.toRequest())
                        } else {
                            errorTextView.visibility = View.VISIBLE
                        }
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }

        viewModel.loadProfile()
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

    fun onButtonLogoffClick(view: View?) {
        PreferencesManager(this).clearAll()
        startActivity(Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
