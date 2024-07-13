package com.example.criapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
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

class ChildActivity : AppCompatActivity() {
    private lateinit var children : Array<ChildResponse>;
    private lateinit var criAppClient : CriAppClient;
    private val gson = Gson();
    private lateinit var aLoadingDialog : ALoadingDialog;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)
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
                val button = ViewFactory.generateButton(this, resources, child.name, child.toString(), UpdateChildActivity::class.java, "child");

                view.addView(button);
            }

            val buttonAddChild = ViewFactory.generateAddButton(this, resources, "Añadir niño(a)", CreateChildActivity::class.java)
            view.addView(buttonAddChild);
        }
    }

    override fun onBackPressed() {
        val nextIntent = Intent(this, MenuActivity::class.java);
        startActivity(nextIntent);
    }

}