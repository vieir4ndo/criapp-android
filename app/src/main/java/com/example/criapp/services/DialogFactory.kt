package com.example.criapp.services

import android.app.Dialog
import android.content.Context
import com.example.criapp.R

class DialogFactory {
    companion object {
        fun createErrorDialog(context: Context, message: String): Dialog {
            return createErrorDialog(context, message, onDismiss = null)
        }

        fun createErrorDialog(context: Context, message: String, onDismiss: (() -> Unit)?): Dialog {
            val builder = DialogBuilder(context)
                .withCancelable()
                .withTitle(context.getString(R.string.dialog_error_title))
                .withMessage(message)
                .withSubmitButtonMessage(context.getString(R.string.dialog_accept_button))
                .withSubmitButtonDismissing()
            if (onDismiss != null) builder.withOnDismissCallback(onDismiss)
            return builder.create()
        }

        fun createSuccessDialog(context : Context, message : String) : Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle(context.getString(R.string.dialog_success_title))
                .withMessage(message)
                .withSubmitButtonMessage(context.getString(R.string.dialog_accept_button))
                .withSubmitButtonFinishingCurrentActivity()
                .withOnDismissFinishingCurrentActivity()
                .create()
        }

        fun createSuccessDialog(context: Context, message: String, onDismiss: () -> Unit): Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle(context.getString(R.string.dialog_success_title))
                .withMessage(message)
                .withSubmitButtonMessage(context.getString(R.string.dialog_accept_button))
                .withSubmitButtonDismissing()
                .withOnDismissCallback(onDismiss)
                .create()
        }

        fun createConfirmationDialog(context : Context, title : String, message : String, callback : () -> Unit) : Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle(title)
                .withMessage(message)
                .withSubmitButtonMessage(context.getString(R.string.dialog_accept_button))
                .withSubmitButtonCallback(callback)
                .create();
        }
    }
}