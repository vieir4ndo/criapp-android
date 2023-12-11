package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.model.CreateUserModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Arrays


class SignUpActivity : AppCompatActivity(){
    private lateinit var mBinding: ActivitySignUpBinding;
    private var criappClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    private var user = CreateUserModel();
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private val monthsOfTheYear = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Noviembre", "Deciembre");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criappClient = CriAppClient(sharedPreferences!!)

        configSexSpinner();
        configEducationalLevelSpinner();
        configFamilyTypeSpinner();
        configBirthDateDaySpinner();
        configBirthDateMonthSpinner();
        configBirthDateYearSpinner()

        val buttonCreateUser = findViewById<Button>(R.id.button_create_user);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_create_username);
        val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);

        buttonCreateUser.setOnClickListener{
            user.name = editTextName.text.toString();
            user.username = editTextUsername.text.toString();
            user.password = editTextPassword.text.toString();
            user.birthDate = formatDate(birthDateYear, birthDateMonth, birthDateDay);
            user.numberOfChildren = editTextNumberOfChildren.text.toString().toInt();

            fetchData()
        }
    }

    fun formatDate(ano: Int, mes: Int, dia: Int): String {
        return "$ano-${formatNumber(mes)}-${formatNumber(dia)}"
    }

    fun formatNumber(numero: Int): String {
        return if (numero < 10) "0$numero" else numero.toString()
    }

    private fun configSexSpinner(){
        val aaSex= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaSex.addAll(Arrays.asList("Seleccionar", "Hombre", "Mujer", "Otro"));

        mBinding.spinnerSex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                user.sex = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                user.sex = 0;
            }

        };
        mBinding.spinnerSex.adapter = aaSex;
    }

    private fun configEducationalLevelSpinner(){
        val aaEducationalLevel= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaEducationalLevel.addAll(Arrays.asList("Seleccionar", "Sin estudios", "Primaria/ESO", "Bachillerato", "Formación profesional", "Estudios universitarios"));

        mBinding.spinnerEducationalLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                user.educationalLevel = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                user.educationalLevel = 0;
            }

        };
        mBinding.spinnerEducationalLevel.adapter = aaEducationalLevel;
    }

    private fun configFamilyTypeSpinner(){
        val aaFamilyType= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaFamilyType.addAll(Arrays.asList("Seleccionar",
            "Monoparental femenina (mujer sola con niñas o niños a cargo)",
            "Monoparental masculina (hombre sólo con niñas o niños a cargo)",
            "Pareja de hombre y mujer con niñas o niños a cargo",
            "Pareja de hombre y hombre con niñas o niños a cargo",
            "Pareja de mujer y mujer con niñas o niños a cargo"));

        mBinding.spinnerFamilyType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                user.familyType = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                user.familyType = 0;
            }

        };
        mBinding.spinnerFamilyType.adapter = aaFamilyType;
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
        if (result != null && !result.success){
            //TODO: Adicionar mensagens de erro
        }
        else {
            val openSignUpSuccessActivityIntent = Intent (this, SignUpSuccessActivity::class.java);
            startActivity(openSignUpSuccessActivityIntent);
        }
    }
}