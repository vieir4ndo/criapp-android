package com.example.criapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.databinding.ActivityProfileBinding
import com.example.criapp.factories.SpinnerFactory
import com.example.criapp.helpers.DateHelper
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
    private var aLoadingDialog : ALoadingDialog? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater);
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this);

        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));

        consultUserAndLoadModel();
    }

    private fun consultUserAndLoadModel() {
        aLoadingDialog!!.show()
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
        aLoadingDialog!!.cancel()
        if (result == null || !result.success){
            Toast.makeText(this, result!!.Messages!!.joinToString(separator = "\n"), Toast.LENGTH_SHORT).show()
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

            configSpinners();
            configBirthDateDaySpinner();
            configBirthDateMonthSpinner();
            configBirthDateYearSpinner()

            val buttonUpdateUser = findViewById<Button>(R.id.button_update_user);
            val editTextName = findViewById<EditText>(R.id.edit_text_name);
            val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);

            editTextName.setText(user.name);
            editTextNumberOfChildren.setText(user.numberOfChildren.toString());

            buttonUpdateUser.setOnClickListener{
                aLoadingDialog!!.show()
                user.name = editTextName.text.toString();
                user.birthDate = DateHelper.formatDateYYYYMMDD(birthDateYear, birthDateMonth, birthDateDay);
                user.numberOfChildren = if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString().toInt();

                updateUser()
            }
        }
    }

    private fun configSpinners(){
        SpinnerFactory.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerFactory.sexOptions,
            { position : Int -> user.sex = position })
        mBinding.spinnerSex.setSelection(user.sex)

        SpinnerFactory.configSpinner(this,
            mBinding.spinnerEducationalLevel,
            SpinnerFactory.educationalLevelOptions,
            { position : Int -> user.educationalLevel = position })
        mBinding.spinnerEducationalLevel.setSelection(user.educationalLevel)

        SpinnerFactory.configSpinner(this,
            mBinding.spinnerFamilyType,
            SpinnerFactory.familyTypeOptions,
            { position:Int -> user.familyType = position })
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
        aLoadingDialog!!.cancel()
        if (result != null && !result.success){
            Toast.makeText(this, result!!.Messages!!.joinToString(separator = "\n"), Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show()
        }
    }
}