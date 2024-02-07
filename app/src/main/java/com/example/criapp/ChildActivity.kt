package com.example.criapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    private var children : Array<ChildResponse>? = null;
    private var criAppClient : CriAppClient? = null;
    private val gson = Gson();
    private var aLoadingDialog : ALoadingDialog? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)
        aLoadingDialog = ALoadingDialog(this);
        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));
        aLoadingDialog!!.show()
        fetchData();
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.getAuthenticated("/child");
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result == null || !result.success){
            val dialog = DialogFactory.createErrorDialog(this, result!!.Messages!!.joinToString(separator = "\n"))
            dialog.show()
        }
        else {
            val json = gson.toJson(result!!.data!!.first());
            val data = gson.fromJson(json, Array<ChildResponse>::class.java);

            children = data;

            val view = findViewById<LinearLayout>(R.id.children);

            if (children!!.isEmpty()){
                val text = generateEmptyTextMessage();

                view.addView(text);
            }

            for (child in children!!){
                val button = generateChildButton(child);

                view.addView(button);
            }

            val buttonAddChild = generateAddChildButton()
            view.addView(buttonAddChild);
        }
    }

    private fun generateChildButton(child: ChildResponse) : Button {
        val button = Button(this)
        button.gravity = Gravity.CENTER_HORIZONTAL
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        button.textSize = 20f
        button.setTypeface(null, Typeface.BOLD)
        button.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = resources.getDimension(R.dimen.border_radius)
        shape.setColor(ContextCompat.getColor(this, R.color.white))

        button.background = shape

        button.text = child.name

        button.setOnClickListener { view ->
            val intent = Intent(this, UpdateChildActivity::class.java);
            intent.putExtra("child", child.toString())
            startActivity(intent);
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_top)

        button.layoutParams = params

        return button;
    }

    private fun generateEmptyTextMessage() : TextView{
        val text = TextView(this);

        text.text = "Aún no hay nada por aquí";
        text.gravity = Gravity.CENTER_HORIZONTAL;
        text.textSize = 20f
        text.setTextColor(ContextCompat.getColor(this, R.color.white))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_top)

        text.layoutParams = params

        return text;
    }

    private fun generateAddChildButton() : Button {
        val button = Button(this)
        button.gravity = Gravity.CENTER_HORIZONTAL
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        button.textSize = 20f
        button.setTypeface(null, Typeface.BOLD)
        button.setTextColor(ContextCompat.getColor(this, R.color.white))

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = resources.getDimension(R.dimen.border_radius)
        shape.setColor(ContextCompat.getColor(this, R.color.dark_blue))

        button.background = shape

        button.text = "Añadir niño(a)"

        button.setOnClickListener { view ->
            val intent = Intent(this, CreateChildActivity::class.java);
            startActivity(intent);
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_top)

        button.layoutParams = params

        return button;
    }

    override fun onBackPressed() {
        val nextIntent = Intent(this, MenuActivity::class.java);
        startActivity(nextIntent);
    }

}