package com.example.criapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.criapp.model.LoginUserModel
import com.example.criapp.model.LoginUserResponse
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriappClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private var user = LoginUserModel();
    private val criappClient = CriappClient();
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val buttonRedirectSignUpActivity = findViewById<Button>(R.id.redirect_sign_up_button);

        buttonRedirectSignUpActivity.setOnClickListener {
            val openSignUpActivityIntent = Intent(this, SignUpActivity::class.java);
            startActivity(openSignUpActivityIntent);
        }

        val buttonLoginUser = findViewById<Button>(R.id.button_login_user);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_username);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);

        buttonLoginUser.setOnClickListener {
            user.username = editTextUsername.text.toString();
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

    private suspend fun fetchDataFromApi(): ResponseModel? {
        return criappClient.postWithoutCredentials("/user/login", user.toString());
    }

    private fun handleResult(result: ResponseModel?) {
        if (result != null && !result.success) {
            //TODO: Adicionar mensagens de erro
        } else {

            val json = gson.toJson(result!!.data!!.first());
            val tokenData = gson.fromJson(json, LoginUserResponse::class.java)

            val sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("token", tokenData.token)
            editor.putString("token_expires_in", tokenData.expiresIn)
            editor.putString("user_id", tokenData.user?.id)
            editor.apply()

            val openFirstWelcomeActivityIntent = Intent(this, FirstWelcomePageActivity::class.java);
            startActivity(openFirstWelcomeActivityIntent);
        }
    }
}