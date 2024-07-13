package com.example.criapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.criapp.helpers.ViewFactory
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuestionsActivity : AppCompatActivity() {
    private lateinit var children : Array<ChildResponse>
    private lateinit var criAppClient : CriAppClient;
    private val gson = Gson();
    private lateinit var aLoadingDialog : ALoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        aLoadingDialog = ALoadingDialog(this);
        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));
        aLoadingDialog.show()
        fetchData();
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.getAuthenticated("/child");
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog.cancel()
        if (result == null || !result.success){
            val dialog = DialogFactory.createErrorDialog(this, result!!.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            val json = gson.toJson(result.data.first());
            val data = gson.fromJson(json, Array<ChildResponse>::class.java);

            children = data;

            val view = findViewById<LinearLayout>(R.id.children);

            if (children.isEmpty()){
                val text = ViewFactory.generateEmptyTextMessage(this, resources);

                view.addView(text);
            }

            for (child in children){
                val button = ViewFactory.generateButton(this, resources, "Para " + child.name, child.toString(), AnswerActivity::class.java, "child");

                view.addView(button);
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java);
        startActivity(intent);
    }
}