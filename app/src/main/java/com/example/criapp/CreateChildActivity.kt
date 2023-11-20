package com.example.criapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.criapp.databinding.ActivityCreateChildBinding
import com.example.criapp.databinding.ActivitySignUpBinding
import java.util.Arrays

class CreateChildActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityCreateChildBinding;
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private val monthsOfTheYear = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Noviembre", "Deciembre");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateChildBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        configSexSpinner();
        configPrematureSpinner();
        configBirthDateDaySpinner();
        configBirthDateMonthSpinner();
        configBirthDateYearSpinner();
    }

    private fun configPrematureSpinner(){
        val aaPremature= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaPremature.addAll(Arrays.asList("Seleccionar", "Sí", "No"));

        mBinding.spinnerPremature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                //user.sex = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //user.sex = 0;
            }
        };
        mBinding.spinnerPremature.adapter = aaPremature;
    }

    private fun configSexSpinner(){
        val aaSex= ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);

        aaSex.addAll(Arrays.asList("Seleccionar", "Hombre", "Mujer", "Otro"));

        mBinding.spinnerSex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                //user.sex = position;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //user.sex = 0;
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