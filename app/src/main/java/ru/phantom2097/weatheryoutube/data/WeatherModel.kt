package ru.phantom2097.weatheryoutube.data

data class WeatherModel(
    val city: String,
    val time: String,
    val currentTemp: String,
    val conditionText: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String,
)