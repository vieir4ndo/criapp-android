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
import com.example.criapp.models.LoginUserModel
import com.example.criapp.models.responses.LoginUserResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private lateinit var loginUserModel : LoginUserModel;
    private var criappClient : CriAppClient? = null;
    private val gson = Gson()
    private var sharedPreferences : SharedPreferences? = null;
    private var aLoadingDialog : ALoadingDialog? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        window.setNavigationBarColor(getResources().getColor(R.color.light_blue))

        aLoadingDialog = ALoadingDialog(this);

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        criappClient = CriAppClient(sharedPreferences!!)

        val buttonRedirectSignUpActivity = findViewById<Button>(R.id.redirect_sign_up_button);

        buttonRedirectSignUpActivity.setOnClickListener {
            val openSignUpActivityIntent = Intent(this, SignUpActivity::class.java);
            startActivity(openSignUpActivityIntent);
        }

        val buttonRedirectForgotPasswordActivity = findViewById<Button>(R.id.redirect_forgot_password_button);

        buttonRedirectForgotPasswordActivity.setOnClickListener {
            val openForgotPasswordActivity = Intent(this, ForgotPasswordActivity::class.java);
            startActivity(openForgotPasswordActivity);
        }

        val errorTextView = findViewById<TextView>(R.id.text_view_errors);
        errorTextView.visibility = View.GONE
        val buttonLoginUser = findViewById<Button>(R.id.button_login_user);
        val editTextUsername = findViewById<EditText>(R.id.edit_text_username);
        val editTextPassword = findViewById<EditText>(R.id.edit_text_password);

        loginUserModel = LoginUserModel(editTextUsername, editTextPassword);

        buttonLoginUser.setOnClickListener {
            if (loginUserModel.validate(errorTextView)) {
                fetchData(loginUserModel.toRequest())
            }
            else { errorTextView.visibility = View.VISIBLE }
        }
    }

    private fun fetchData(requestBody : String) {
        aLoadingDialog!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criappClient!!.post("/user/login", requestBody);
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
            val json = gson.toJson(result!!.data!!.first());
            val tokenData = gson.fromJson(json, LoginUserResponse::class.java)

            val editor = sharedPreferences!!.edit()
            editor.putString("token", tokenData.token)
            editor.putString("token_expires_in", tokenData.expiresIn)
            editor.putString("user_id", tokenData.user?.id)
            editor.putString("user_name", tokenData.user?.name)
            editor.apply()

            val openWelcomeActivityIntent = Intent(this, WelcomeMessagesActivity::class.java);
            startActivity(openWelcomeActivityIntent);
        }
    }

    override fun onBackPressed() {
        return;
    }
}