package com.example.criapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.adapters.FaqExpandableListAdapter
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FaqActivity : AppCompatActivity() {
    private lateinit var expandableListView : ExpandableListView
    private lateinit var titleView : View
    private lateinit var imageView : View
    private lateinit var criAppClient : CriAppClient
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var aLoadingDialog : ALoadingDialog
    private val gson = Gson();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        aLoadingDialog = ALoadingDialog(this);
        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);
        criAppClient = CriAppClient(sharedPreferences);
        expandableListView = findViewById(R.id.expandableListView)
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

            val adapter = FaqExpandableListAdapter(this@FaqActivity, faqData)

            expandableListView.setAdapter(adapter)

            val layoutParams = expandableListView.layoutParams

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenHeight = displayMetrics.heightPixels

            val otherViewsHeight = titleView.height + imageView.height
            val remainingHeight = screenHeight - otherViewsHeight

            layoutParams.height = remainingHeight
            expandableListView.layoutParams = layoutParams
        }
    }
}