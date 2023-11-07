package com.example.diaryapp

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Helper {
    fun getDiaryKey(title : String, date : String, minute : String) : String{
        return "${title[0]}${title[1]}_${date}_${minute}"
    }

    fun getCurrentMinute(): String {
        val currentTime = LocalTime.now()
        val minute = currentTime.minute
        return minute.toString()
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.UK)
        return currentDate.format(formatter)
    }
}