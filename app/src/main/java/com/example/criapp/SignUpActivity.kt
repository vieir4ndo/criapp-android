package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.criapp.model.CreateUserModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriappClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    val criappClient = CriappClient();
    var user = CreateUserModel();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up);

        val buttonCreateUser = findViewById<Button>(R.id.button_create_user);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_create_username);
        val editTextBirthDate = findViewById<EditText>(R.id.edit_text_birth_date);
        val editTextSex = findViewById<Spinner>(R.id.spinner_sex);
        val editTextEducationalLevel = findViewById<Spinner>(R.id.spinner_educational_level);
        val editTextFamilyType = findViewById<Spinner>(R.id.spinner_family_type);
        val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);

        buttonCreateUser.setOnClickListener{
            user.name = editTextName.text.toString();
            user.email = editTextUsername.text.toString();
            user.birthDate = editTextBirthDate.text.toString();
            //user.sex = editTextSex.text.toString().toInt();
            //user.educationalLevel = editTextEducationalLevel.text.toString().toInt();
            //user.familyType = editTextFamilyType.text.toString().toInt();
            user.password = editTextPassword.text.toString();

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
        return criappClient.postWithoutCredentials("/user", user.toString());
    }

    private fun handleResult(result: ResponseModel?) {
        if (result != null && !result.success){
            //TODO: Adicionar mensagens de erro
            val openSignUpActivityIntent = Intent (this, SignUpActivity::class.java);
            startActivity(openSignUpActivityIntent);
        }
        else {
            val openSignInActivityIntent = Intent (this, SignInActivity::class.java);
            startActivity(openSignInActivityIntent);
        }
    }
}