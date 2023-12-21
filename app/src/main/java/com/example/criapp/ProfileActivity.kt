package com.example.criapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.criapp.databinding.ActivityProfileBinding
import com.example.criapp.databinding.ActivitySignUpBinding
import com.example.criapp.model.ChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.model.UpdateUserModel
import com.example.criapp.model.UserModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Arrays

class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding;
    private var criAppClient : CriAppClient? = null;
    private val gson = Gson();
    private var user : UpdateUserModel = UpdateUserModel();
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private val monthsOfTheYear = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Noviembre", "Deciembre")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater);
        setContentView(mBinding.root)
        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));

        consultUserAndLoadModel();
    }

    private fun consultUserAndLoadModel() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.getAuthenticated("/user");
        }

        scope.launch {
            val result = job.await()

            handleResultConsultUserAndLoadModel(result)
        }
    }

    private fun handleResultConsultUserAndLoadModel(result: ResponseModel?) {
        if (result == null || !result.success){
            Toast.makeText(this, "Hubo un error al buscar los datas del usuario, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        }
        else {
            val json = gson.toJson(result!!.data!!.first());
            val data = gson.fromJson(json, UserModel::class.java);

            user.name = data.name;
            user.birthDate = data.birthDate;

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val date = LocalDateTime.parse(data!!.birthDate, formatter)
            birthDateDay = date.dayOfMonth
            birthDateMonth = date.monthValue
            birthDateYear = date.year

            user.sex = data.sex;
            user.familyType = data.familyType;
            user.educationalLevel = data.educationalLevel;
            user.numberOfChildren = data.educationalLevel;

            configSexSpinner();
            configEducationalLevelSpinner();
            configFamilyTypeSpinner();
            configBirthDateDaySpinner();
            configBirthDateMonthSpinner();
            configBirthDateYearSpinner()

            val buttonUpdateUser = findViewById<Button>(R.id.button_update_user);
            val editTextName = findViewById<EditText>(R.id.edit_text_name);
            val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);

            editTextName.setText(user.name);
            editTextNumberOfChildren.setText(user.numberOfChildren.toString());

            buttonUpdateUser.setOnClickListener{
                user.name = editTextName.text.toString();
                user.birthDate = formatDate(birthDateYear, birthDateMonth, birthDateDay);
                user.numberOfChildren = if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString().toInt();

                updateUser()
            }
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
            }

        };
        mBinding.spinnerSex.adapter = aaSex;
        mBinding.spinnerSex.setSelection(user.sex)
    }

    private fun configEducationalLevelSpinner(){
        val aaEducationalLevel= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaEducationalLevel.addAll(Arrays.asList("Seleccionar", "Sin estudios", "Primaria/ESO", "Bachillerato", "Formación profesional", "Estudios universitarios"));

        mBinding.spinnerEducationalLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                user.educationalLevel = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        };
        mBinding.spinnerEducationalLevel.adapter = aaEducationalLevel;
        mBinding.spinnerEducationalLevel.setSelection(user.educationalLevel)
    }

    private fun configFamilyTypeSpinner(){
        val aaFamilyType= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaFamilyType.addAll(
            Arrays.asList("Seleccionar",
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
            }

        };
        mBinding.spinnerFamilyType.adapter = aaFamilyType;
        mBinding.spinnerFamilyType.setSelection(user.familyType)
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
            }

        };
        mBinding.spinnerBirthDateDay.adapter = aaBirthDateDay;
        mBinding.spinnerBirthDateDay.setSelection(birthDateDay - 1)
    }

    private fun configBirthDateMonthSpinner(){
        val aaBirthDateMonth = ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaBirthDateMonth.addAll(monthsOfTheYear);

        mBinding.spinnerBirthDateMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                birthDateMonth = position + 1
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        };
        mBinding.spinnerBirthDateMonth.adapter = aaBirthDateMonth;
        mBinding.spinnerBirthDateMonth.setSelection(birthDateMonth - 1)
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
            }

        };
        mBinding.spinnerBirthDateYear.adapter = aaBirthDateYear;
        mBinding.spinnerBirthDateYear.setSelection(birthDateYear - 1900)
    }

    private fun updateUser() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.patchAuthenticated("/user", user.toString());
        }

        scope.launch {
            val result = job.await()

            handleResultUpdateUser(result)
        }
    }

    private fun handleResultUpdateUser(result: ResponseModel?) {
        if (result != null && !result.success){
            Toast.makeText(this, "Hubo un error al realizar la operación, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show()
        }
    }
}