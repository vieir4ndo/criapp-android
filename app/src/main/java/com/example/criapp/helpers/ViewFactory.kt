package com.example.criapp.helpers

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.criapp.R

class ViewFactory {

    companion object {
        fun <T> generateButton(context : Context, resources : Resources, text: String, content : String, redirectTo : Class<T>) : Button {
            val button = Button(context)
            button.gravity = Gravity.CENTER_HORIZONTAL
            button.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            button.textSize = 20f
            button.setTypeface(null, Typeface.BOLD)
            button.setTextColor(ContextCompat.getColor(context, R.color.dark_blue))

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = resources.getDimension(R.dimen.border_radius)
            shape.setColor(ContextCompat.getColor(context, R.color.white))

            button.background = shape

            button.text = text

            button.setOnClickListener { view ->
                val intent = Intent(context, redirectTo);
                intent.putExtra("child", content)
                context.startActivity(intent);
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.topMargin = resources.getDimensionPixelSize(R.dimen.margin_top)
            params.marginEnd = resources.getDimensionPixelSize(R.dimen.margin_top)
            params.marginStart = resources.getDimensionPixelSize(R.dimen.margin_top)

            button.layoutParams = params

            return button;
        }

        fun generateEmptyTextMessage(context: Context, resources: Resources, message : String = "Aún no hay nada por aquí") : TextView {
            val text = TextView(context);

            text.text = message;
            text.gravity = Gravity.CENTER_HORIZONTAL;
            text.textSize = 20f
            text.setTextColor(ContextCompat.getColor(context, R.color.white))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.topMargin = resources.getDimensionPixelSize(R.dimen.margin_top)

            text.layoutParams = params

            return text;
        }

        fun <T> generateAddButton(context: Context, resources: Resources, message : String, redirectTo : Class<T>) : Button {
            val button = Button(context)
            button.gravity = Gravity.CENTER_HORIZONTAL
            button.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            button.textSize = 20f
            button.setTypeface(null, Typeface.BOLD)
            button.setTextColor(ContextCompat.getColor(context, R.color.white))

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = resources.getDimension(R.dimen.border_radius)
            shape.setColor(ContextCompat.getColor(context, R.color.dark_blue))

            button.background = shape

            button.text = message

            button.setOnClickListener { view ->
                val intent = Intent(context, redirectTo);
                context.startActivity(intent);
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.topMargin = resources.getDimensionPixelSize(R.dimen.margin_top)
            params.marginEnd = resources.getDimensionPixelSize(R.dimen.margin_top)
            params.marginStart = resources.getDimensionPixelSize(R.dimen.margin_top)

            button.layoutParams = params

            return button;
        }
    }

}