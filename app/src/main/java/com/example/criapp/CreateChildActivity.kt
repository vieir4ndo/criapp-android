package com.example.criapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.databinding.ActivityCreateChildBinding
import com.example.criapp.services.SpinnerHelper
import com.example.criapp.services.DateHelper
import com.example.criapp.model.CreateOrUpdateChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CreateChildActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityCreateChildBinding;
    private var criappClient : CriAppClient? = null;
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private var child = CreateOrUpdateChildModel();
    private var aLoadingDialog : ALoadingDialog? = null;
    private var editTextBirthDate : EditText? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateChildBinding.inflate(layoutInflater);
        setContentView(mBinding.root);

        aLoadingDialog = ALoadingDialog(this);

        this.criappClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));

        configSpinners();

        val buttonCreateChild = findViewById<Button>(R.id.button_create_child);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextMonthsOfBreastFeeding = findViewById<EditText>(R.id.edit_text_months_of_breast_feeding);
        editTextBirthDate = findViewById(R.id.edit_text_birth_date);

        buttonCreateChild.setOnClickListener{
            aLoadingDialog!!.show()
            child.name = editTextName.text.toString();
            child.birthDate = DateHelper.formatDateYYYYMMDD(birthDateYear, birthDateMonth, birthDateDay);
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
            val dialog = DialogFactory.createErrorDialog(this, result!!.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            val nextActivity = Intent (this, ChildActivity::class.java);
            startActivity(nextActivity);
            Toast.makeText(this, "Niño(a) creado con exito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configSpinners(){
        SpinnerHelper.configSpinner(this,
            mBinding.spinnerSex,
            SpinnerHelper.sexOptions,
            { position : Int -> child.sex = position },
            { child.sex = 0 })

        SpinnerHelper.configSpinner(this,
            mBinding.spinnerPremature,
            SpinnerHelper.yesOrNoOptions,
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