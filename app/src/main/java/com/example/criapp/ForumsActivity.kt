package com.example.criapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.criapp.helpers.ViewFactory
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ForumsActivity : AppCompatActivity() {
    private lateinit var forums : Array<ForumResponse>;
    private lateinit var criAppClient : CriAppClient;
    private val gson = Gson();
    private lateinit var aLoadingDialog : ALoadingDialog;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)
        aLoadingDialog = ALoadingDialog(this);
        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));
        aLoadingDialog.show()
        fetchData();
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.getAuthenticated("/forum");
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
            val json = gson.toJson(result.data);
            val data = gson.fromJson(json, Array<ForumResponse>::class.java);

            forums = data;

            val view = findViewById<LinearLayout>(R.id.forums);

            if (forums.isEmpty()){
                val text = ViewFactory.generateEmptyTextMessage(this, resources);

                view.addView(text);
            }

            for (forum in forums){
                val button = ViewFactory.generateButton(this, resources, forum.name, forum.toString(), ForumActivity::class.java);

                view.addView(button);
            }

            val buttonAddChild = ViewFactory.generateAddButton(this, resources, "Añadir foro", CreateForumActivity::class.java)
            view.addView(buttonAddChild);
        }
    }
}