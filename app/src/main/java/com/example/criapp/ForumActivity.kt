package com.example.criapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.CommentAdapter
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ForumActivity : AppCompatActivity() {
    private lateinit var criAppClient : CriAppClient
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var aLoadingDialog : ALoadingDialog
    private lateinit var recyclerView : RecyclerView
    private lateinit var forum : ForumResponse
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)
        aLoadingDialog = ALoadingDialog(this);
        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);
        criAppClient = CriAppClient(sharedPreferences);
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        forum = gson.fromJson(intent.getStringExtra("forum"), ForumResponse::class.java)

        var textViewForumName = findViewById<TextView>(R.id.forum_name);
        var textViewForumDescription = findViewById<TextView>(R.id.forum_description);
        var textViewForumOwner = findViewById<TextView>(R.id.forum_owner);

        textViewForumName.text = forum.name;
        textViewForumDescription.text = forum.description;
        textViewForumOwner.text = "Creado por " + forum.prcName;

        fetchData();
    }

    private fun fetchData() {
        aLoadingDialog.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.getAuthenticated("/forum/" + forum.id + "/comments")
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
            Toast.makeText(this, "Hubo un error al buscar los comentarios, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val json = gson.toJson(result.data);
            var comments = gson.fromJson(json, Array<CommentResponse>::class.java);
            val adapter = CommentAdapter(comments);
            recyclerView.adapter = adapter
        }
    }
}