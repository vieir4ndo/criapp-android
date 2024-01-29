package com.example.criapp.services

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.criapp.R

class DialogBuilder {
   private var dialog : Dialog
   private var context : Context

   constructor(context: Context){
       this.context = context
       dialog = Dialog(context);
       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       dialog.setContentView(R.layout.criapp_dialog_layout)
   }
    fun withCancelable() : DialogBuilder {
        dialog.setCancelable(true);

        return this;
    }

    fun withTitle(titleText: String) : DialogBuilder{
        var title = dialog.findViewById<TextView>(R.id.title);
        title.text = titleText;

        return this;
    }

    fun withMessage(messageText: String) : DialogBuilder{
        var message = dialog.findViewById<TextView>(R.id.message);
        message.text = messageText;

        return this;
    }

    fun withSubmitButtonMessage(message: String) : DialogBuilder{
        var submitButton = dialog.findViewById<Button>(R.id.submit_button);
        submitButton.text = message;

        return this;
    }

    fun <T> withSubmitButtonRedirectingToActivity(redirectTo: Class<T>) : DialogBuilder{
        var submitButton = dialog.findViewById<Button>(R.id.submit_button);
        submitButton.setOnClickListener{
            val nextIntent = Intent(this.context, redirectTo);
            this.context.startActivity(nextIntent);
        }

        return this;
    }

    fun withSubmitButtonDismissing() : DialogBuilder{
        var submitButton = dialog.findViewById<Button>(R.id.submit_button);
        submitButton.setOnClickListener{
            dialog.dismiss()
        }

        return this;
    }

    fun create() : Dialog{
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }
}