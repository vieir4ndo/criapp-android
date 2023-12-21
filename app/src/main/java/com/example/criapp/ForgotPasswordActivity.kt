package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.model.ForgotPasswordModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private var model = ForgotPasswordModel();
    private var criappClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criappClient = CriAppClient(sharedPreferences!!)

        val buttonForgotPassword = findViewById<Button>(R.id.button_forgot_password);

        val editTextUsername = findViewById<EditText>(R.id.edit_text_username);

        buttonForgotPassword.setOnClickListener {
            model.username = editTextUsername.text.toString();

            fetchData()
        }
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criappClient!!.post("/user/forgot-password", model.toString());
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        if (result != null && !result.Success) {
            Toast.makeText(this, result!!.Messages.joinToString(separator = "\n"), Toast.LENGTH_SHORT).show()
        } else {
            val openResetPasswordActivityIntent = Intent(this, ResetPasswordActivity::class.java);
            openResetPasswordActivityIntent.putExtra("username", model.username)
            startActivity(openResetPasswordActivityIntent);
        }
    }
}