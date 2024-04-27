package com.example.criapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FaqActivity : AppCompatActivity() {
    private lateinit var titleView : View
    private lateinit var imageView : View
    private lateinit var criAppClient : CriAppClient
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var aLoadingDialog : ALoadingDialog
    private lateinit var recyclerView : RecyclerView
    private val gson = Gson();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        aLoadingDialog = ALoadingDialog(this);
        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);
        criAppClient = CriAppClient(sharedPreferences);
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        titleView = findViewById(R.id.title_faq)
        imageView = findViewById(R.id.image_faq)

        fetchData()
    }

    private fun fetchData() {
        aLoadingDialog.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.get("/faq")
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog.cancel()

        if (result == null || !result.success)
        {
            Toast.makeText(this, "Hubo un error al buscar las preguntas, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val json = gson.toJson(result.data);
            var questions = gson.fromJson(json, Array<FaqResponse>::class.java);

            var faqData = mutableListOf<Pair<String, String>>()

            questions.forEach {
                faqData.add(Pair(it.question, it.answer))
            }

            val adapter = FaqAdapter(faqData);
            recyclerView.adapter = adapter
        }
    }
}