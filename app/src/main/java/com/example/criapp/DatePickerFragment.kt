package com.example.criapp

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.TimeZone

class DatePickerFragment(private val listener: (day: Int, month: Int, year: Int) -> Unit) {

    fun show(fragmentManager: FragmentManager, tag: String) {
        if (fragmentManager.findFragmentByTag(tag) != null) return
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.birth_date)
            .setTheme(R.style.CRIApp_MaterialDatePicker)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            listener(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
            )
        }

        picker.show(fragmentManager, tag)
    }
}