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
    private var model = CreateOrUpdateChildModel()
    private var aLoadingDialog : ALoadingDialog? = null;
    private var editTextBirthDate : EditText? = null;
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
        editTextBirthDate!!.setText(DateHelper.formatDateDDMMYYYY(birthDateYear, birthDateMonth, birthDateDay))

        buttonUpdateChild.setOnClickListener{
            aLoadingDialog!!.show()
            model.name = editTextName.text.toString()
            model.birthDate = DateHelper.formatDateYYYYMMDD(birthDateYear, birthDateMonth, birthDateDay)
            model.monthsOfBreastFeeding = if (editTextMonthsOfBreastFeeding.text.toString() == "") 0 else editTextMonthsOfBreastFeeding.text.toString().toInt()

            fetchData()
        }
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.patchAuthenticated("/child/" + child!!.id, model.toString())
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
        SpinnerHelper.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerHelper.sexOptions,
            { position : Int -> model.sex = position },
            { model.sex = child!!.sex })
        mBinding.spinnerSex.setSelection(child!!.sex)

        SpinnerHelper.configSpinner(this,
            mBinding.spinnerPremature,
            SpinnerHelper.yesOrNoOptions,
            { position : Int -> when (position) {
                1 -> model.isPremature = true
                2 -> model.isPremature = false
                else -> {
                    model.isPremature = null;
                }
            }},
            { model.isPremature = child!!.isPremature }
        )
        mBinding.spinnerPremature.setSelection(if (child!!.isPremature)  1 else 2)
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
}