package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.adapters.ForumAdapter
import com.example.criapp.helpers.ViewFactory
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

class ForumsActivity : AppCompatActivity() {
    private lateinit var forums : Array<ForumResponse>;
    private lateinit var criAppClient : CriAppClient;
    private val gson = Gson();
    private lateinit var aLoadingDialog : ALoadingDialog;
    //private lateinit var recyclerView : RecyclerView
    private lateinit var sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forums)
        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);
        aLoadingDialog = ALoadingDialog(this);
        criAppClient = CriAppClient(sharedPreferences);
        aLoadingDialog.show()
        //recyclerView = findViewById(R.id.recyclerView)
        //recyclerView.layoutManager = LinearLayoutManager(this)
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
            else {
                val userId = sharedPreferences.getString("user_id", "")
                val adapter = ForumAdapter(forums, userId!!,::redirectToView, ::redirectToUpdate);

                val recyclerView = RecyclerView(this)
                recyclerView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter

                view.addView(recyclerView)
            }

            val buttonAddChild = ViewFactory.generateAddButton(this, resources, "Añadir foro", CreateForumActivity::class.java)
            view.addView(buttonAddChild);
        }
    }

    private fun redirectToUpdate(forum : String){
        val nextIntent = Intent(this, UpdateForumActivity::class.java);
        nextIntent.putExtra("forum", forum)
        startActivity(nextIntent);
    }

    private fun redirectToView(forum : String){
        val nextIntent = Intent(this, ForumActivity::class.java);
        nextIntent.putExtra("forum", forum)
        startActivity(nextIntent);
    }

    override fun onBackPressed() {
        val nextIntent = Intent(this, MenuActivity::class.java);
        startActivity(nextIntent);
    }
}