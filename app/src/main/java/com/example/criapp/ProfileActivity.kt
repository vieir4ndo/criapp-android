package com.example.criapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.databinding.ActivityProfileBinding
import com.example.criapp.services.SpinnerHelper
import com.example.criapp.services.DateHelper
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.models.UpdateUserModel
import com.example.criapp.models.responses.UserResponse
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding;
    private var criAppClient : CriAppClient? = null;
    private val gson = Gson();
    private lateinit var updateUserModel : UpdateUserModel;
    private var birthDateDay = 0;
    private var birthDateMonth = 0;
    private var birthDateYear = 0;
    private var aLoadingDialog : ALoadingDialog? = null;
    private lateinit var editTextBirthDate : EditText;

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
            val user = gson.fromJson(json, UserResponse::class.java);

            val date = DateHelper.getDayMonthAndYearFromFormattedDate(user!!.birthDate)
            birthDateDay = date.first
            birthDateMonth = date.second
            birthDateYear = date.third

            configSpinners();

            val buttonUpdateUser = findViewById<Button>(R.id.button_update_user);
            val editTextName = findViewById<EditText>(R.id.edit_text_name);
            val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);
            editTextBirthDate = findViewById(R.id.edit_text_birth_date);

            editTextName.setText(user.name);
            editTextNumberOfChildren.setText(user.numberOfChildren.toString());
            editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(birthDateYear, birthDateMonth, birthDateDay))
            mBinding.spinnerSex.setSelection(user.sex);
            mBinding.spinnerFamilyType.setSelection(user.familyType);
            mBinding.spinnerEducationalLevel.setSelection(user.educationalLevel);

            updateUserModel = UpdateUserModel(
                editTextName,
                editTextBirthDate,
                mBinding.spinnerSex,
                mBinding.spinnerEducationalLevel,
                mBinding.spinnerFamilyType,
                editTextNumberOfChildren)

            buttonUpdateUser.setOnClickListener{
               if (updateUserModel.validate()){
                   updateUser(updateUserModel.toRequest())
               }
            }
        }
    }

    private fun configSpinners(){
        val sexSpinnerInputLayout = findViewById<TextInputLayout>(R.id.spinner_sex_input_layout);
        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerSex,
            SpinnerHelper.sexOptions,
            sexSpinnerInputLayout)

        val spinnerEducationalLevelInputLayout = findViewById<TextInputLayout>(R.id.spinner_educational_level_input_layout);
        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerEducationalLevel,
            SpinnerHelper.educationalLevelOptions,
            spinnerEducationalLevelInputLayout)

        val spinnerFamilyTypeInputLayout = findViewById<TextInputLayout>(R.id.spinner_family_type_input_layout);
        SpinnerHelper.configCriAppSpinner(this,
            mBinding.spinnerFamilyType,
            SpinnerHelper.familyTypeOptions,
            spinnerFamilyTypeInputLayout)
    }

    fun showDatePickerForBirthDate(view : View){
        val datePicker = DatePickerFragment { day: Int, month: Int, year: Int ->
            birthDateDay = day;
            birthDateMonth = month + 1;
            birthDateYear = year;
            editTextBirthDate.setText(DateHelper.formatDateDDMMYYYY(year, month + 1, day))
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun updateUser(requestBody: String) {
        aLoadingDialog!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.patchAuthenticated("/user", requestBody);
        }

        scope.launch {
            val result = job.await()

            handleResultUpdateUser(result)
        }
    }

    private fun handleResultUpdateUser(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result != null && !result.success){
            val dialog = DialogFactory.createErrorDialog(this, result.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            Toast.makeText(this, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show()
        }
    }
}