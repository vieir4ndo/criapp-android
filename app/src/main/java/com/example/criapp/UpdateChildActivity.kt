package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.databinding.ActivityUpdateChildBinding
import com.example.criapp.services.SpinnerHelper
import com.example.criapp.services.DateHelper
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.CreateOrUpdateChildModel
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UpdateChildActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityUpdateChildBinding
    private var child : ChildResponse? = null
    private val gson = Gson()
    private var criAppClient : CriAppClient? = null
    private var sharedPreferences : SharedPreferences? = null
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private lateinit var model : CreateOrUpdateChildModel
    private var aLoadingDialog : ALoadingDialog? = null;
    private lateinit var editTextBirthDate : EditText;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUpdateChildBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criAppClient = CriAppClient(sharedPreferences!!)
        child = gson.fromJson(intent.getStringExtra("child"), ChildResponse::class.java)

        val date = DateHelper.getDayMonthAndYearFromFormattedDate(child!!.birthDate)
        birthDateDay = date.first
        birthDateMonth = date.second
        birthDateYear = date.third

        configSpinners();

        val buttonUpdateChild = findViewById<Button>(R.id.button_update_child)
        var editTextName = findViewById<EditText>(R.id.edit_text_name)
        var editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding)
        editTextBirthDate = findViewById(R.id.edit_text_birth_date);

        editTextName.setText(child!!.name);
        editTextMonthsOfBreastFeeding.setText(child!!.monthsOfBreastFeeding.toString());
        editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(birthDateYear, birthDateMonth, birthDateDay))

        model = CreateOrUpdateChildModel(editTextName,editTextBirthDate, mBinding.spinnerSex, editTextMonthsOfBreastFeeding, mBinding.spinnerPremature)

        buttonUpdateChild.setOnClickListener{
           if (model.validate()){
               fetchData(model.toRequest())
           }
        }
    }

    private fun fetchData(requestBody:String) {
        aLoadingDialog!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.patchAuthenticated("/child/" + child!!.id, requestBody)
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result == null || !result.success){
            val dialog = DialogFactory.createErrorDialog(this, result!!.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            val nextActivity = Intent (this, ChildActivity::class.java)
            startActivity(nextActivity)
            Toast.makeText(this, "Niño(a) actualizado con exito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configSpinners(){
        val sexSpinnerInputLayout = findViewById<TextInputLayout>(R.id.spinner_sex_input_layout);
        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerSex,
            SpinnerHelper.sexOptions,
            sexSpinnerInputLayout
            )
        mBinding.spinnerSex.setSelection(child!!.sex)

        val prematureSpinnerInputLayout = findViewById<TextInputLayout>(R.id.spinner_premature_input_layout);
        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerPremature,
            SpinnerHelper.yesOrNoOptions,
            prematureSpinnerInputLayout
        )
        mBinding.spinnerPremature.setSelection(if (child!!.isPremature)  1 else 2)
    }

    fun showDatePickerForBirthDate(view : View){
        val datePicker = DatePickerFragment { day: Int, month: Int, year: Int ->
            birthDateDay = day;
            birthDateMonth = month + 1;
            birthDateYear = year;
            editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(year, month + 1, day))
            editTextBirthDate.error = null;
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }
}