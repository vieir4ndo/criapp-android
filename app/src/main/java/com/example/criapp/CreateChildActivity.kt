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
import com.example.criapp.databinding.ActivityCreateChildBinding
import com.example.criapp.model.CreateChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriappClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Arrays

class CreateChildActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityCreateChildBinding;
    private val criappClient = CriappClient();
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private val monthsOfTheYear = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Noviembre", "Deciembre");
    private var child = CreateChildModel();
    private var sharedPreferences : SharedPreferences? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateChildBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);

        configSexSpinner();
        configPrematureSpinner();
        configBirthDateDaySpinner();
        configBirthDateMonthSpinner();
        configBirthDateYearSpinner();

        val buttonCreateChild = findViewById<Button>(R.id.button_create_child);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding);

        buttonCreateChild.setOnClickListener{
            child.name = editTextName.text.toString();
            child.birthDate = formatDate(birthDateYear, birthDateMonth, birthDateDay);
            child.monthsOfBreastFeeding = editTextMonthsOfBreastFeeding.text.toString().toInt();

            fetchData()
        }
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            fetchDataFromApi()
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private suspend fun fetchDataFromApi() : ResponseModel? {

        val token = sharedPreferences?.getString("token", "")

        //TODO: verificar validade do token

        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
            "charset" to "utf-8"
        );

        return criappClient.post("/child", child.toString(), headers);
    }

    private fun handleResult(result: ResponseModel?) {
        if (result == null || !result.success){
            //TODO: Adicionar mensagens de erro
        }
        else {

            val nextActivity = Intent (this, MenuActivity::class.java);
            startActivity(nextActivity);
        }
    }

    fun formatDate(ano: Int, mes: Int, dia: Int): String {
        return "$ano-${formatNumber(mes)}-${formatNumber(dia)}"
    }

    fun formatNumber(numero: Int): String {
        return if (numero < 10) "0$numero" else numero.toString()
    }


    private fun configPrematureSpinner(){
        val aaPremature= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaPremature.addAll(Arrays.asList("Seleccionar", "Sí", "No"));

        mBinding.spinnerPremature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                when (position) {
                    1 -> child.isPremature = true
                    2 -> child.isPremature = false
                    else -> {
                        child.isPremature = null;
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                child.isPremature = false;
            }
        };
        mBinding.spinnerPremature.adapter = aaPremature;
    }

    private fun configSexSpinner(){
        val aaSex= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaSex.addAll(Arrays.asList("Seleccionar", "Hombre", "Mujer", "Otro"));

        mBinding.spinnerSex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                child.sex = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                child.sex = 0;
            }

        };
        mBinding.spinnerSex.adapter = aaSex;
    }

    private fun configBirthDateDaySpinner(){
        val aaBirthDateDay = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        for (i in 1..31){
            aaBirthDateDay.add(i.toString());
        }
        mBinding.spinnerBirthDateDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateDay = position + 1
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                birthDateDay = 0;
            }

        };
        mBinding.spinnerBirthDateDay.adapter = aaBirthDateDay;
    }

    private fun configBirthDateMonthSpinner(){
        val aaBirthDateMonth = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaBirthDateMonth.addAll(monthsOfTheYear);

        mBinding.spinnerBirthDateMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateMonth = position + 1
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                birthDateMonth = 0;
            }

        };
        mBinding.spinnerBirthDateMonth.adapter = aaBirthDateMonth;
    }

    private fun configBirthDateYearSpinner(){
        val aaBirthDateYear = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        for (i in 1900..2100){
            aaBirthDateYear.add(i.toString());
        }
        mBinding.spinnerBirthDateYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateYear = 1900 + position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                birthDateYear = 0;
            }

        };
        mBinding.spinnerBirthDateYear.adapter = aaBirthDateYear;
    }
}