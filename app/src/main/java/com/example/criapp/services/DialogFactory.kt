package com.example.criapp.services

import android.app.Dialog
import android.content.Context
import com.example.criapp.R

class DialogFactory {
    companion object {
        fun createErrorDialog(context : Context, message : String) : Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle(context.getString(R.string.dialog_error_title))
                .withMessage(message)
                .withSubmitButtonMessage(context.getString(R.string.dialog_accept_button))
                .withSubmitButtonDismissing()
                .create()
        }

        fun createSuccessDialog(context : Context, message : String) : Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle(context.getString(R.string.dialog_success_title))
                .withMessage(message)
                .withSubmitButtonMessage(context.getString(R.string.dialog_accept_button))
                .withSubmitButtonFinishingCurrentActivity()
                .create()
        }
    }
}