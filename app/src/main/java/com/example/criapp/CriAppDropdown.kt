package com.example.criapp

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

class CriAppDropdown @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var selectedPosition: Int = 0
        private set

    private var options: List<String> = emptyList()
    private var popup: PopupWindow? = null

    init {
        isFocusable = false
        isFocusableInTouchMode = false

        isSingleLine = true
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END

        setHorizontallyScrolling(false)
        setOnClickListener { showPopup() }
    }

    fun setup(opts: List<String>) {
        options = opts
        if (opts.isNotEmpty()) {
            text = opts[0]
            applySelectionStyle(0)
        }
        popup?.dismiss()
        popup = null
    }

    fun selectItem(position: Int) {
        selectedPosition = position
        text = options.getOrElse(position) { "" }
        applySelectionStyle(position)
    }

    private fun showPopup() {
        if (options.isEmpty()) return

        if (popup?.isShowing == true) return
        popup?.dismiss()
        popup = null

        val popupWidth = if (width > 0) {
            width
        } else {
            (resources.displayMetrics.widthPixels * 0.7f).toInt()
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            background = ContextCompat.getDrawable(
                context, R.drawable.spinner_popup_background
            )
        }

        options.forEachIndexed { index, optionText ->
            container.addView(buildItemView(optionText, index, popupWidth))
        }

        popup = PopupWindow(
            container,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable(0))
            elevation = resources.displayMetrics.density * 8f
            isOutsideTouchable = true
        }

        showPopupSmart(container, popupWidth)
    }

    private fun showPopupSmart(content: View, popupWidth: Int) {
        val pw = popup ?: return

        content.measure(
            View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupHeight = content.measuredHeight

        val location = IntArray(2)
        getLocationOnScreen(location)
        val anchorTop = location[1]
        val anchorBottom = anchorTop + height
        val screenHeight = resources.displayMetrics.heightPixels

        val spaceBelow = screenHeight - anchorBottom
        val spaceAbove = anchorTop

        val openUpward = popupHeight > spaceBelow && spaceAbove > spaceBelow

        if (openUpward) {
            pw.showAsDropDown(this, 0, -(height + popupHeight))
        } else {
            pw.showAsDropDown(this)
        }
    }

    private fun buildItemView(itemText: String, index: Int, popupWidth: Int): TextView {
        val tv = LayoutInflater.from(context)
            .inflate(R.layout.criapp_spinner_dropdown_item, null, false) as TextView

        tv.text = itemText
        tv.isSingleLine = false
        tv.maxLines = 4
        tv.ellipsize = null
        tv.setHorizontallyScrolling(false)
        tv.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        tv.layoutParams = LinearLayout.LayoutParams(
            popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val outValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackground, outValue, true
        )
        tv.setBackgroundResource(outValue.resourceId)
        tv.isClickable = true
        tv.isFocusable = true

        tv.setOnClickListener {
            selectedPosition = index
            text = itemText
            applySelectionStyle(index)
            popup?.dismiss()
        }

        return tv
    }

    private fun applySelectionStyle(position: Int) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val primaryColor = ContextCompat.getColor(context, typedValue.resourceId)
        val placeholderColor = ContextCompat.getColor(context, R.color.hint_text)
        val color = if (position == 0) placeholderColor else primaryColor
        setTextColor(color)
        compoundDrawableTintList = android.content.res.ColorStateList.valueOf(color)
    }
}