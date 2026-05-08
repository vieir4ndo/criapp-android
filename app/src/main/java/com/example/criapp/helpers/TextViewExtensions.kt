package com.example.criapp.helpers

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun TextView.markRequired() {
    val original = text.toString()
    val star = " *"
    val spannable = SpannableString(original + star)
    spannable.setSpan(
        ForegroundColorSpan(Color.parseColor("#CC0000")),
        original.length,
        spannable.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = spannable
}
