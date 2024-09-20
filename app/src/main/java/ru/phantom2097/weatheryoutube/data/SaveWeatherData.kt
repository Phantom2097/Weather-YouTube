package ru.phantom2097.weatheryoutube.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
fun saveWeatherData(context: Context, weatherModel: WeatherModel) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.putString("city", weatherModel.city)
    editor.putString("time", weatherModel.time)
    editor.putString("currentTemp", weatherModel.currentTemp)
    editor.putString("conditionText", weatherModel.conditionText)
    editor.putString("icon", weatherModel.icon)
    editor.putString("maxTemp", weatherModel.maxTemp)
    editor.putString("minTemp", weatherModel.minTemp)
    editor.putString("hours", weatherModel.hours)

    editor.apply()
}

fun loadWeatherData(context: Context): WeatherModel? {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)

    val city = sharedPreferences.getString("city", null)
    val time = sharedPreferences.getString("time", null)
    val currentTemp = sharedPreferences.getString("currentTemp", null)
    val conditionText = sharedPreferences.getString("conditionText", null)
    val icon = sharedPreferences.getString("icon", null)
    val maxTemp = sharedPreferences.getString("maxTemp", null)
    val minTemp = sharedPreferences.getString("minTemp", null)
    val hours = sharedPreferences.getString("hours", null)

    return if (
        city != null &&
        time != null &&
        currentTemp != null &&
        conditionText != null &&
        icon != null &&
        maxTemp != null &&
        minTemp != null &&
        hours != null
    ) {
        WeatherModel(city, time, currentTemp, conditionText, icon, maxTemp, minTemp, hours)
    } else {
        null
    }
}