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
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.databinding.ActivityCreateChildBinding
import com.example.criapp.models.CreateOrUpdateChildModel
import com.example.criapp.presentation.child.ChildViewModel
import com.example.criapp.presentation.child.ChildViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.SpinnerHelper

class CreateChildActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityCreateChildBinding
    private val viewModel: ChildViewModel by viewModels {
        ChildViewModelFactory(ChildRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private lateinit var model: CreateOrUpdateChildModel
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var editTextBirthDate: EditText
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateChildBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

        configSpinners()

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE
        editTextBirthDate = findViewById(R.id.edit_text_birth_date)

        model = CreateOrUpdateChildModel(
            findViewById(R.id.edit_text_name),
            editTextBirthDate,
            mBinding.spinnerSex,
            findViewById(R.id.edit_text_months_of_breast_feeding),
            mBinding.spinnerPremature
        )

        findViewById<Button>(R.id.button_create_child).setOnClickListener {
            if (model.validate(errorTextView)) {
                viewModel.createChild(model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.createState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Niño(a) creado con exito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ChildActivity::class.java))
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
        SpinnerHelper.configCriAppSpinner(this, mBinding.spinnerPremature, SpinnerHelper.yesOrNoOptions)
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
