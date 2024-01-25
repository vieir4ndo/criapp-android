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
import android.widget.Toast
import com.example.criapp.databinding.ActivityCreateChildBinding
import com.example.criapp.factories.SpinnerFactory
import com.example.criapp.helpers.DateHelper
import com.example.criapp.model.CreateOrUpdateChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Arrays

class CreateChildActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityCreateChildBinding;
    private var criappClient : CriAppClient? = null;
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private val monthsOfTheYear = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Noviembre", "Deciembre");
    private var child = CreateOrUpdateChildModel();
    private var aLoadingDialog : ALoadingDialog? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateChildBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        aLoadingDialog = ALoadingDialog(this);

        this.criappClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));

        configSpinners();
        configBirthDateDaySpinner();
        configBirthDateMonthSpinner();
        configBirthDateYearSpinner();

        val buttonCreateChild = findViewById<Button>(R.id.button_create_child);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding);

        buttonCreateChild.setOnClickListener{
            aLoadingDialog!!.show()
            child.name = editTextName.text.toString();
            child.birthDate = DateHelper.formatDate(birthDateYear, birthDateMonth, birthDateDay);
            child.monthsOfBreastFeeding = if (editTextMonthsOfBreastFeeding.text.toString() == "") 0 else editTextMonthsOfBreastFeeding.text.toString().toInt();

            fetchData()
        }
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criappClient!!.postAuthenticated("/child", child.toString());
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
            val nextActivity = Intent (this, ChildActivity::class.java);
            startActivity(nextActivity);
            Toast.makeText(this, "Niño(a) creado con exito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configSpinners(){
        SpinnerFactory.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerFactory.sexOptions,
            { position : Int -> child.sex = position },
            { child.sex = 0 })

        SpinnerFactory.configSpinner(this,
            mBinding.spinnerPremature,
            SpinnerFactory.yesOrNoOptions,
            { position : Int -> when (position) {
                1 -> child.isPremature = true
                2 -> child.isPremature = false
                else -> {
                    child.isPremature = null;
                }
            }},
            { child.isPremature = false }
        )
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