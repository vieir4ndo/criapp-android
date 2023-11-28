package com.example.criapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.criapp.model.ChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AnswerActivity : AppCompatActivity() {
    private var child : ChildModel? = null;
    private val gson = Gson();
    private var textView : TextView? = null;
    private var criAppClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);

        child = gson.fromJson(intent.getStringExtra("child"), ChildModel::class.java)

        textView = findViewById(R.id.text_child);
        textView!!.text = "Respondendo preguntas para " + child!!.name;
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.getAuthenticated("/child")
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        if (result == null || !result.success) {
            //TODO: Adicionar mensagens de erro
        } else {
            val json = gson.toJson(result!!.data!!.first());

            //val data = gson.fromJson(json, Array<ChildModel>::class.java);

        }
    }
}