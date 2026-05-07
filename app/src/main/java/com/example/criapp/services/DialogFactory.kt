package com.example.criapp.services

import android.app.Dialog
import android.content.Context

class DialogFactory {
    companion object {
        fun createErrorDialog(context : Context, message : String) : Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle("Oops")
                .withMessage(message)
                .withSubmitButtonMessage("OK")
                .withSubmitButtonDismissing()
                .create()
        }

        fun createSuccessDialog(context : Context, message : String) : Dialog {
            return DialogBuilder(context)
                .withCancelable()
                .withTitle("Enhorabuena")
                .withMessage(message)
                .withSubmitButtonMessage("OK")
                .withSubmitButtonFinishingCurrentActivity()
                .create()
        }
    }
}