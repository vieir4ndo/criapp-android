package com.example.criapp.helpers

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
    }
}