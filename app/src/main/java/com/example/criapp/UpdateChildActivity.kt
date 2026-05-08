package com.example.criapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.databinding.ActivityUpdateChildBinding
import com.example.criapp.models.CreateOrUpdateChildModel
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.presentation.child.ChildViewModel
import com.example.criapp.presentation.child.ChildViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.SpinnerHelper
import com.google.gson.Gson

class UpdateChildActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityUpdateChildBinding
    private val viewModel: ChildViewModel by viewModels {
        ChildViewModelFactory(ChildRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private lateinit var child: ChildResponse
    private val gson = Gson()
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private lateinit var model: CreateOrUpdateChildModel
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var editTextBirthDate: EditText
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUpdateChildBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

        child = gson.fromJson(intent.getStringExtra("child"), ChildResponse::class.java)

        val date = DateHelper.getDayMonthAndYearFromFormattedDate(child.birthDate)
        birthDateDay = date.first
        birthDateMonth = date.second
        birthDateYear = date.third

        configSpinners()

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE
        val editTextName = findViewById<EditText>(R.id.edit_text_name)
        val editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding)
        editTextBirthDate = findViewById(R.id.edit_text_birth_date)

        editTextName.setText(child.name)
        editTextMonthsOfBreastFeeding.setText(child.monthsOfBreastFeeding.toString())
        editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(birthDateYear, birthDateMonth, birthDateDay))

        model = CreateOrUpdateChildModel(editTextName, editTextBirthDate, mBinding.spinnerSex, editTextMonthsOfBreastFeeding, mBinding.spinnerPremature)

        findViewById<Button>(R.id.button_update_child).setOnClickListener {
            if (model.validate(errorTextView, this)) {
                viewModel.updateChild(child.id, model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.child_updated_success), Toast.LENGTH_SHORT).show()
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

    private fun configSpinners() {
        SpinnerHelper.configCriAppSpinner(mBinding.spinnerSex, R.array.sex_options)
        mBinding.spinnerSex.selectItem(child.sex)
        SpinnerHelper.configCriAppSpinner(mBinding.spinnerPremature, R.array.yes_no_options)
        mBinding.spinnerPremature.selectItem(if (child.isPremature) 1 else 2)
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
