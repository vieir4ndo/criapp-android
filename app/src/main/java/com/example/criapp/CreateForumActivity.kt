package com.example.criapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.models.CreateForumModel
import com.example.criapp.models.CreateOrUpdateChildModel
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CreateForumActivity : AppCompatActivity() {
    private lateinit var criAppClient : CriAppClient
    private lateinit var model : CreateForumModel;
    private lateinit var aLoadingDialog : ALoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_forum)
        aLoadingDialog = ALoadingDialog(this);
        this.criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));
        val errorTextView = findViewById<TextView>(R.id.text_view_errors);
        errorTextView.visibility = View.GONE

        val editTextName = findViewById<EditText>(R.id.edit_text_name);
        val editTextDescription = findViewById<EditText>(R.id.edit_text_description);
        val button = findViewById<Button>(R.id.button_create);

        model = CreateForumModel(editTextName,editTextDescription)

        button.setOnClickListener{

            if (model.validate(errorTextView)){
                fetchData(model.toRequest())
            }
            else { errorTextView.visibility = View.VISIBLE }
        }
    }

    private fun fetchData(body : String) {
        aLoadingDialog.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient.postAuthenticated("/forum",body)
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
            Toast.makeText(this, "Hubo un error al guardar el foro, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val nextIntent = Intent(this, ForumsActivity::class.java);
            startActivity(nextIntent);
            Toast.makeText(this, "Foro creado con exito", Toast.LENGTH_SHORT).show()
        }
    }
}