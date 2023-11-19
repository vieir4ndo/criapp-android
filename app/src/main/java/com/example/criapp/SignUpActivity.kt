package com.example.criapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.criapp.model.CreateUserModel
import com.example.criapp.services.UserService

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up);

        val service = UserService();

        val buttonCreateUser = findViewById<Button>(R.id.button_create_user);
        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_create_username);
        val editTextBirthDate = findViewById<EditText>(R.id.edit_text_birth_date);
        val editTextSex = findViewById<EditText>(R.id.edit_text_sex);
        val editTextEducationalLevel = findViewById<EditText>(R.id.edit_text_education_level);
        val editTextFamilyType = findViewById<EditText>(R.id.edit_text_family_type);
        val editTextNumberOfChildren = findViewById<EditText>(R.id.edit_text_number_of_children);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);

        buttonCreateUser.setOnClickListener{
            val user = CreateUserModel();
            user.name = editTextName.text.toString();
            user.email = editTextUsername.text.toString();
            user.birthDate = editTextBirthDate.text.toString();
            user.sex = editTextSex.text.toString().toInt();
            user.educationalLevel = editTextEducationalLevel.text.toString().toInt();
            user.familyType = editTextFamilyType.text.toString().toInt();
            user.password = editTextPassword.text.toString();

           //TODO: Adicionar chamadas assíncronas e em threads separadas
            service.createUser(user);
        }
    }
}