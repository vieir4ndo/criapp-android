package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.criapp.models.ResetPasswordModel
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var model : ResetPasswordModel;
    private var criAppClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    private var aLoadingDialog : ALoadingDialog? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        aLoadingDialog = ALoadingDialog(this);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criAppClient = CriAppClient(sharedPreferences!!)

        val buttonResetPassword = findViewById<Button>(R.id.button_reset_password);
        val errorTextView = findViewById<TextView>(R.id.text_view_errors);
        errorTextView.visibility = View.GONE
        val editTextCode = findViewById<EditText>(R.id.edit_text_code);
        val editTextNewPassword = findViewById<EditText>(R.id.edit_text_new_password);

        model = ResetPasswordModel(
            intent.getStringExtra("username").toString(),
            editTextNewPassword,
            editTextCode
        );

        buttonResetPassword.setOnClickListener {
            if (model.validate(errorTextView)){
                fetchData(model.toRequest())
            }
            else { errorTextView.visibility = View.VISIBLE }
        }
    }

    private fun fetchData(requestBody : String) {
        aLoadingDialog!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.post("/user/reset-password", requestBody);
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result != null && !result.success) {
            val dialog = DialogFactory.createErrorDialog(this, result.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        } else {
            val openSignInActivity = Intent(this, SignInActivity::class.java);
            startActivity(openSignInActivity);
            Toast.makeText(this, "Contraseña actualizada con exito", Toast.LENGTH_SHORT).show()
        }
    }
}