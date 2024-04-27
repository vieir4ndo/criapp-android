package com.example.criapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.AudiovisualResourceAdapter
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.models.responses.AudiovisualResourceResponse
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AudioVisualResourcesActivity : AppCompatActivity() {
    private lateinit var criAppClient : CriAppClient
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var aLoadingDialog : ALoadingDialog
    private lateinit var recyclerView : RecyclerView
    private val gson = Gson();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiovisual_resources)

        aLoadingDialog = ALoadingDialog(this);
        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);
        criAppClient = CriAppClient(sharedPreferences);
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchData()
    }

    private fun fetchData() {
        aLoadingDialog.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.get("/audiovisual-resource")
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
            Toast.makeText(this, "Hubo un error al buscar los recursos audiovisuales, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val json = gson.toJson(result.data);
            var resources = gson.fromJson(json, Array<AudiovisualResourceResponse>::class.java);
            val adapter = AudiovisualResourceAdapter(resources);
            recyclerView.adapter = adapter
        }
    }
}