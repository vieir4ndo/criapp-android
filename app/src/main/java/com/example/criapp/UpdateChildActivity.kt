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
import com.example.criapp.databinding.ActivityUpdateChildBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUpdateChildBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criAppClient = CriAppClient(sharedPreferences!!)
        child = gson.fromJson(intent.getStringExtra("child"), ChildModel::class.java)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = LocalDateTime.parse(child!!.birthDate, formatter)
        birthDateDay = date.dayOfMonth
        birthDateMonth = date.monthValue
        birthDateYear = date.year

        configSexSpinner()
        configPrematureSpinner()
        configBirthDateDaySpinner()
        configBirthDateMonthSpinner()
        configBirthDateYearSpinner()

        val buttonUpdateChild = findViewById<Button>(R.id.button_update_child)
        var editTextName = findViewById<EditText>(R.id.edit_text_name)
        var editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding)

        editTextName.setText(child!!.name);
        editTextMonthsOfBreastFeeding.setText(child!!.monthsOfBreastFeeding.toString());

        buttonUpdateChild.setOnClickListener{
            model.name = editTextName.text.toString()
            model.birthDate = formatDate(birthDateYear, birthDateMonth, birthDateDay)
            model.monthsOfBreastFeeding = editTextMonthsOfBreastFeeding.text.toString().toInt()

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
        if (result == null || !result.success){
            //TODO: Adicionar mensagens de erro
        }
        else {
            val nextActivity = Intent (this, ChildActivity::class.java)
            startActivity(nextActivity)
        }
    }

    fun formatDate(ano: Int, mes: Int, dia: Int): String {
        return "$ano-${formatNumber(mes)}-${formatNumber(dia)}"
    }

    fun formatNumber(numero: Int): String {
        return if (numero < 10) "0$numero" else numero.toString()
    }


    private fun configPrematureSpinner(){
        val aaPremature= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

        aaPremature.addAll(Arrays.asList("Seleccionar", "Sí", "No"))

        mBinding.spinnerPremature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                when (position) {
                    1 -> model.isPremature = true
                    2 -> model.isPremature = false
                    else -> {
                        model.isPremature = null
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                model.isPremature = child!!.isPremature
            }
        }
        mBinding.spinnerPremature.adapter = aaPremature
        mBinding.spinnerPremature.setSelection(if (child!!.isPremature)  1 else 2)
    }

    private fun configSexSpinner(){
        val aaSex= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

        aaSex.addAll(Arrays.asList("Seleccionar", "Hombre", "Mujer", "Otro"))

        mBinding.spinnerSex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                model.sex = position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                model.sex = child!!.sex
            }

        }
        mBinding.spinnerSex.adapter = aaSex
        mBinding.spinnerSex.setSelection(child!!.sex)
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
                birthDateYear = birthDateYear
            }

        }
        mBinding.spinnerBirthDateYear.adapter = aaBirthDateYear
        mBinding.spinnerBirthDateYear.setSelection(birthDateYear - 1900)
    }
}