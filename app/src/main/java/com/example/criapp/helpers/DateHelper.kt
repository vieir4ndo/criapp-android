package com.example.criapp.helpers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        fun getDayMonthAndYearFromFormattedDate(date : String) : Triple<Int, Int, Int> {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val date = LocalDateTime.parse(date, formatter)

            return Triple(date.dayOfMonth, date.monthValue, date.year)
        }
    }
}