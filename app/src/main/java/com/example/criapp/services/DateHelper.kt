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

        fun formatForDisplay(isoDate: String): String {
            return try {
                val instant = Instant.parse(isoDate)
                val localDate = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
                "${formatNumber(localDate.dayOfMonth)}/${formatNumber(localDate.monthValue)}/${localDate.year}"
            } catch (e: Exception) {
                isoDate
            }
        }

        fun formatForDisplayWithTime(isoDate: String): String {
            return try {
                val instant = Instant.parse(isoDate)
                val dt = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
                "${formatNumber(dt.dayOfMonth)}/${formatNumber(dt.monthValue)}/${dt.year} ${formatNumber(dt.hour)}:${formatNumber(dt.minute)}"
            } catch (e: Exception) {
                isoDate
            }
        }
    }
}