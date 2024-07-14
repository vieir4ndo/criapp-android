package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.CommentAdapter
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.models.CreateCommentModel
import com.example.criapp.models.CreateOrUpdateChildModel
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
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
    private lateinit var comments : Array<CommentResponse>
    private lateinit var editTextText: EditText
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
        editTextText = findViewById<EditText>(R.id.text_comment);
        var buttomCreateComment = findViewById<ImageButton>(R.id.comment_button);

        textViewForumName.text = forum.name;
        textViewForumDescription.text = forum.description;
        textViewForumOwner.text = "Creado por " + forum.prcName;

        val errorTextView = findViewById<TextView>(R.id.text_view_errors);
        errorTextView.visibility = View.GONE

        var model = CreateCommentModel(editTextText, forum.id)
        buttomCreateComment.setOnClickListener{

            if (model.validate(errorTextView)){
                fetchComment(model.toRequest())
            }
            else { errorTextView.visibility = View.VISIBLE }
        }

        aLoadingDialog.show()
        fetchData();
    }

    private fun fetchData() {
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
            comments = gson.fromJson(json, Array<CommentResponse>::class.java);
            val userId = sharedPreferences.getString("user_id", "")
            val adapter = CommentAdapter(comments, userId!!, ::deleteComment);
            recyclerView.adapter = adapter
        }
    }


    private fun fetchComment(requestBody: String){
        aLoadingDialog.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.postAuthenticated("/comment", requestBody);
        }

        scope.launch {
            val result = job.await()

            handleResultComment(result)
        }
    }

    private fun handleResultComment(result: ResponseModel?) {
        if (result == null || !result.success){
            aLoadingDialog.cancel()
            val dialog = DialogFactory.createErrorDialog(this, result!!.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            fetchData()
            editTextText.text = null
        }
    }

    private fun deleteComment(id : String){
        aLoadingDialog.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.deleteAuthenticated("/comment/" + id);
        }

        scope.launch {
            val result = job.await()

            handleResultDelete(result)
        }
    }

    private fun handleResultDelete(result: ResponseModel?) {
        if (result == null || !result.success){
            aLoadingDialog.cancel()
            val dialog = DialogFactory.createErrorDialog(this, result!!.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            fetchData()
            editTextText.text = null
        }
    }
}