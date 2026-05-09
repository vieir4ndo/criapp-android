package com.example.criapp.services

import android.app.Dialog
import android.content.Context
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
       dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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

    fun withSubmitButtonDismissing() : DialogBuilder{
        var submitButton = dialog.findViewById<Button>(R.id.submit_button);
        submitButton.setOnClickListener{
            dialog.dismiss()
        }

        return this;
    }

    fun withSubmitButtonFinishingCurrentActivity() : DialogBuilder{
        var submitButton = dialog.findViewById<Button>(R.id.submit_button);
        submitButton.setOnClickListener{
            dialog.dismiss()
            (this.context as? android.app.Activity)?.finish()
        }

        return this;
    }

    fun withOnDismissFinishingCurrentActivity() : DialogBuilder{
        dialog.setOnDismissListener {
            (this.context as? android.app.Activity)?.finish()
        }

        return this;
    }

    fun create() : Dialog {
        val displayWidth = context.resources.displayMetrics.widthPixels
        dialog.window?.setLayout((displayWidth * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        return dialog;
    }

    fun withSubmitButtonCallback(callback : () -> Unit) : DialogBuilder {
        var submitButton = dialog.findViewById<Button>(R.id.submit_button);
        submitButton.setOnClickListener{
            callback()
        }

        return this;
    }
}