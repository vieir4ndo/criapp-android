package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.models.CreateUserModel
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.SpinnerHelper
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignUpBinding;
    private var criAppClient: CriAppClient? = null;
    private var sharedPreferences: SharedPreferences? = null;
    private lateinit var createUserModel: CreateUserModel;
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private var aLoadingDialog: ALoadingDialog? = null;
    private var editTextBirthDate: EditText? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        aLoadingDialog = ALoadingDialog(this);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criAppClient = CriAppClient(sharedPreferences!!)

        configSpinners();

        val errorTextView = findViewById<TextView>(R.id.text_view_errors);
        val buttonCreateUser = findViewById<Button>(R.id.button_create_user);
        editTextBirthDate = findViewById(R.id.edit_text_birth_date);

        createUserModel = CreateUserModel(
            findViewById(R.id.edit_text_name),
            editTextBirthDate!!,
            findViewById(R.id.edit_text_create_username),
            findViewById(R.id.edit_text_password),
            findViewById(R.id.edit_text_number_of_children),
            mBinding.spinnerSex,
            mBinding.spinnerEducationalLevel,
            mBinding.spinnerFamilyType
        )

        buttonCreateUser.setOnClickListener {
            if (createUserModel.validate(errorTextView)) {
                fetchData(createUserModel.toRequest())
            }
        }
    }

    private fun configSpinners() {
        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerSex,
            SpinnerHelper.sexOptions)

        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerEducationalLevel,
            SpinnerHelper.educationalLevelOptions)

        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerFamilyType,
            SpinnerHelper.familyTypeOptions)
    }

    fun showDatePickerForBirthDate(view: View) {
        val datePicker = DatePickerFragment { day: Int, month: Int, year: Int ->
            birthDateDay = day;
            birthDateMonth = month + 1;
            birthDateYear = year;
            editTextBirthDate!!.setText(DateHelper.formatDateDDMMYYYY(year, month + 1, day))
            editTextBirthDate!!.error = null;
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun fetchData(requestBody: String) {
        aLoadingDialog!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.post("/user", requestBody);
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result != null && !result.success) {
            val dialog = DialogFactory.createErrorDialog(
                this,
                result.Messages!!.joinToString(separator = "\n")
            )
            dialog.show()
        } else {
            val openSignUpSuccessActivityIntent = Intent(this, SignUpSuccessActivity::class.java);
            startActivity(openSignUpSuccessActivityIntent);
        }
    }
}