package com.example.criapp

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.databinding.ActivityCreateChildBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.CreateOrUpdateChildModel
import com.example.criapp.presentation.child.ChildViewModel
import com.example.criapp.presentation.child.ChildViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.example.criapp.services.SpinnerHelper

class CreateChildActivity : BaseActivity() {

    private lateinit var mBinding: ActivityCreateChildBinding
    private val viewModel: ChildViewModel by viewModels {
        ChildViewModelFactory(ChildRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private lateinit var model: CreateOrUpdateChildModel
    private var aLoadingDialog: ALoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateChildBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

        configSpinners()

        mBinding.labelEditTextName.markRequired()
        mBinding.labelEditTextBirthDate.markRequired()
        mBinding.labelSpinnerSex.markRequired()
        mBinding.labelSpinnerPremature.markRequired()
        mBinding.labelEditTextMonthsOfBreastFeeding.markRequired()

        model = CreateOrUpdateChildModel(
            mBinding.editTextName,
            mBinding.editTextBirthDate,
            mBinding.spinnerSex,
            mBinding.editTextMonthsOfBreastFeeding,
            mBinding.spinnerPremature
        )

        mBinding.buttonCreateChild.setOnClickListener {
            if (model.validate(mBinding.textViewErrors, this)) {
                viewModel.createChild(model.toRequest())
            }
        }

        viewModel.createState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
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
        SpinnerHelper.configCriAppSpinner(mBinding.spinnerPremature, R.array.yes_no_options)
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
