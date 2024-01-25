package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.databinding.ActivityUpdateChildBinding
import com.example.criapp.factories.SpinnerFactory
import com.example.criapp.helpers.DateHelper
import com.example.criapp.model.ChildModel
import com.example.criapp.model.CreateOrUpdateChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Arrays

class UpdateChildActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityUpdateChildBinding
    private var child : ChildModel? = null
    private val gson = Gson()
    private var criAppClient : CriAppClient? = null
    private var sharedPreferences : SharedPreferences? = null
    private var birthDateDay = 0
    private var birthDateMonth = 0
    private var birthDateYear = 0
    private val monthsOfTheYear = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Noviembre", "Deciembre")
    private var model = CreateOrUpdateChildModel()
    private var aLoadingDialog : ALoadingDialog? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUpdateChildBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criAppClient = CriAppClient(sharedPreferences!!)
        child = gson.fromJson(intent.getStringExtra("child"), ChildModel::class.java)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = LocalDateTime.parse(child!!.birthDate, formatter)
        birthDateDay = date.dayOfMonth
        birthDateMonth = date.monthValue
        birthDateYear = date.year

        configSpinners();
        configBirthDateDaySpinner()
        configBirthDateMonthSpinner()
        configBirthDateYearSpinner()

        val buttonUpdateChild = findViewById<Button>(R.id.button_update_child)
        var editTextName = findViewById<EditText>(R.id.edit_text_name)
        var editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding)

        editTextName.setText(child!!.name);
        editTextMonthsOfBreastFeeding.setText(child!!.monthsOfBreastFeeding.toString());

        buttonUpdateChild.setOnClickListener{
            aLoadingDialog!!.show()
            model.name = editTextName.text.toString()
            model.birthDate = DateHelper.formatDate(birthDateYear, birthDateMonth, birthDateDay)
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
            Toast.makeText(this, result!!.Messages!!.joinToString(separator = "\n"), Toast.LENGTH_SHORT).show()
        }
        else {
            val nextActivity = Intent (this, ChildActivity::class.java)
            startActivity(nextActivity)
            Toast.makeText(this, "Niño(a) actualizado con exito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configSpinners(){
        SpinnerFactory.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerFactory.sexOptions,
            { position : Int -> model.sex = position },
            { model.sex = child!!.sex })
        mBinding.spinnerSex.setSelection(child!!.sex)

        SpinnerFactory.configSpinner(this,
            mBinding.spinnerPremature,
            SpinnerFactory.yesOrNoOptions,
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

    private fun configBirthDateDaySpinner(){
        val aaBirthDateDay = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        for (i in 1..31){
            aaBirthDateDay.add(i.toString())
        }
        mBinding.spinnerBirthDateDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateDay = position + 1
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        mBinding.spinnerBirthDateDay.adapter = aaBirthDateDay
        mBinding.spinnerBirthDateDay.setSelection(birthDateDay - 1)
    }

    private fun configBirthDateMonthSpinner(){
        val aaBirthDateMonth = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

        aaBirthDateMonth.addAll(monthsOfTheYear)

        mBinding.spinnerBirthDateMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateMonth = position + 1
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        mBinding.spinnerBirthDateMonth.adapter = aaBirthDateMonth
        mBinding.spinnerBirthDateMonth.setSelection(birthDateMonth - 1)
    }

    private fun configBirthDateYearSpinner(){
        val aaBirthDateYear = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        for (i in 1900..2100){
            aaBirthDateYear.add(i.toString())
        }
        mBinding.spinnerBirthDateYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateYear = 1900 + position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        mBinding.spinnerBirthDateYear.adapter = aaBirthDateYear
        mBinding.spinnerBirthDateYear.setSelection(birthDateYear - 1900)
    }
}