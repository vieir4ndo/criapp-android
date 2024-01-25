package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.model.CreateUserModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.factories.SpinnerFactory
import com.example.criapp.helpers.DateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity(){
    private lateinit var mBinding: ActivitySignUpBinding;
    private var criappClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    private var user = CreateUserModel();
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
        criappClient = CriAppClient(sharedPreferences!!)

        configSpinners();

        editTextBirthDate = findViewById<EditText>(R.id.edit_text_birth_date);
        val buttonCreateUser = findViewById<Button>(R.id.button_create_user);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_create_username);
        val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);

        buttonCreateUser.setOnClickListener{
            aLoadingDialog!!.show()
            user.name = editTextName.text.toString();
            user.username = editTextUsername.text.toString();
            user.password = editTextPassword.text.toString();
            user.birthDate = DateHelper.formatDateYYYYMMDD(birthDateYear, birthDateMonth, birthDateDay);
            user.numberOfChildren = if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString().toInt();

            fetchData()
        }
    }

    private fun configSpinners(){
        SpinnerFactory.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerFactory.sexOptions,
            { position : Int -> user.sex = position },
            { user.sex = 0 })

        SpinnerFactory.configSpinner(this,
            mBinding.spinnerEducationalLevel,
            SpinnerFactory.educationalLevelOptions,
            { position : Int -> user.educationalLevel = position },
            { user.educationalLevel = 0 })

        SpinnerFactory.configSpinner(this,
            mBinding.spinnerFamilyType,
            SpinnerFactory.familyTypeOptions,
            { position:Int -> user.familyType = position },
            { user.familyType = 0 }
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
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criappClient!!.post("/user", user.toString());
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result != null && !result.success){
            Toast.makeText(this, result.Messages!!.joinToString(separator = "\n"), Toast.LENGTH_SHORT).show()
        }
        else {
            val openSignUpSuccessActivityIntent = Intent (this, SignUpSuccessActivity::class.java);
            startActivity(openSignUpSuccessActivityIntent);
        }
    }
}