package com.example.criapp.services

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale

class DateHelper {

    companion object {
        fun formatDateYYYYMMDD(ano: Int, mes: Int, dia: Int): String {
            return "$ano-${formatNumber(mes)}-${formatNumber(dia)}"
        }

        private fun formatNumber(numero: Int): String {
            return if (numero < 10) "0$numero" else numero.toString()
        }

        fun formatDateDDMMYYYY(ano: Int, mes: Int, dia: Int): String {
            return "${formatNumber(dia)}/${formatNumber(mes)}/$ano"
        }

        fun getDayMonthAndYearFromFormattedDate(date: String): Triple<Int, Int, Int> {
            val localDateTime = Instant.parse(date).atOffset(ZoneOffset.UTC).toLocalDateTime()
            return Triple(localDateTime.dayOfMonth, localDateTime.monthValue, localDateTime.year)
        }

        fun convertDateFromMMDDYYYYtoYYYYMMDD(inputDate: String): String {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date!!)
        }
    }
}