package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.model.CreateUserModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.SpinnerHelper
import com.example.criapp.services.DateHelper
import com.example.criapp.services.DialogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity(){
    private lateinit var mBinding: ActivitySignUpBinding;
    private var criAppClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    private lateinit var createUserModel : CreateUserModel;
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private var aLoadingDialog : ALoadingDialog? = null;
    private var editTextBirthDate : EditText? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        aLoadingDialog = ALoadingDialog(this);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criAppClient = CriAppClient(sharedPreferences!!)

        configSpinners();

        val buttonCreateUser = findViewById<Button>(R.id.button_create_user);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_create_username);
        val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);
        editTextBirthDate = findViewById(R.id.edit_text_birth_date);

        createUserModel = CreateUserModel(editTextName, editTextBirthDate!!, editTextUsername, editTextPassword)

        buttonCreateUser.setOnClickListener{
            if (createUserModel.isValid()){
                createUserModel.name = editTextName.text.toString();
                createUserModel.username = editTextUsername.text.toString();
                createUserModel.password = editTextPassword.text.toString();
                createUserModel.birthDate = DateHelper.formatDateYYYYMMDD(birthDateYear, birthDateMonth, birthDateDay);
                createUserModel.numberOfChildren = if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString().toInt();
                fetchData()
            }
        }
    }

    private fun configSpinners(){
        SpinnerHelper.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerHelper.sexOptions,
            { position : Int -> createUserModel.sex = position },
            { createUserModel.sex = 0 })

        SpinnerHelper.configSpinner(this,
            mBinding.spinnerEducationalLevel,
            SpinnerHelper.educationalLevelOptions,
            { position : Int -> createUserModel.educationalLevel = position },
            { createUserModel.educationalLevel = 0 })

        SpinnerHelper.configSpinner(this,
            mBinding.spinnerFamilyType,
            SpinnerHelper.familyTypeOptions,
            { position:Int -> createUserModel.familyType = position },
            { createUserModel.familyType = 0 }
        )
    }

    fun showDatePickerForBirthDate(view : View){
        val datePicker = DatePickerFragment { day: Int, month: Int, year: Int ->
            birthDateDay = day;
            birthDateMonth = month + 1;
            birthDateYear = year;
            editTextBirthDate!!.setText(DateHelper.formatDateDDMMYYYY(year, month + 1, day))
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun fetchData() {
        aLoadingDialog!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.post("/user", createUserModel.toString());
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result != null && !result.success){
            val dialog = DialogFactory.createErrorDialog(this, result.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            val openSignUpSuccessActivityIntent = Intent (this, SignUpSuccessActivity::class.java);
            startActivity(openSignUpSuccessActivityIntent);
        }
    }
}