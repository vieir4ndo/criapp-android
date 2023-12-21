package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.criapp.model.ResetPasswordModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private var model = ResetPasswordModel();
    private var criappClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        model.username = intent.getStringExtra("username").toString();

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criappClient = CriAppClient(sharedPreferences!!)

        val buttonResetPassword = findViewById<Button>(R.id.button_reset_password);

        val editTextCode = findViewById<EditText>(R.id.edit_text_code);
        val editTextNewPassword = findViewById<EditText>(R.id.edit_text_new_password);

        buttonResetPassword.setOnClickListener {
            model.newPassword = editTextNewPassword.text.toString();
            model.code = editTextCode.text.toString();

            fetchData()
        }
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criappClient!!.post("/user/reset-password", model.toString());
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
            val openSignInActivity = Intent(this, SignInActivity::class.java);
            startActivity(openSignInActivity);
            Toast.makeText(this, "Contraseña actualizada con exito", Toast.LENGTH_SHORT).show()
        }
    }
}