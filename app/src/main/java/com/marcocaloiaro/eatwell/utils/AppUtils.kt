package com.marcocaloiaro.eatwell.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AppUtils private constructor() {

    companion object {

        private const val FORMAT = "yyyyMMdd"

        fun formatCurrentDate() : String {
            val dateFormat: DateFormat = SimpleDateFormat(FORMAT, Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun isListEmpty(list: List<Any>?) : Boolean {
            return list.isNullOrEmpty()
        }
    }
}