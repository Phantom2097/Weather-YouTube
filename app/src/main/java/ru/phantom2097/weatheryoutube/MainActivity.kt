package ru.phantom2097.weatheryoutube

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Switch
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import ru.phantom2097.weatheryoutube.data.WeatherModel
import ru.phantom2097.weatheryoutube.screens.DialogSearch
import ru.phantom2097.weatheryoutube.screens.MainCard
import ru.phantom2097.weatheryoutube.screens.TabLayout
import ru.phantom2097.weatheryoutube.ui.theme.WeatherYouTubeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherYouTubeTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }
                val city = rememberSaveable() {
                    mutableStateOf("Moscow")
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherModel(
                            "",
                            "",
                            "23.0",
                            "",
                            "",
                            "23.0",
                            "15.0",
                            "",
                        )
                    )
                }
                if (dialogState.value) {
                    DialogSearch(dialogState, onSubmit = {
                        city.value = it
                    })
                }

                getData(city.value, this, daysList, currentDay)
//                val darkTheme: Boolean = isSystemInDarkTheme()
                val backgroundImageId =
                    if (isSystemInDarkTheme()) R.drawable.weather_background_dark
                    else R.drawable.weather_background_light
                Image(
                    painter = painterResource(id = backgroundImageId),
                    contentDescription = "background image",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(1f),
                    contentScale = ContentScale.FillBounds
                )

                val layoutDirection = LocalLayoutDirection.current
                val displayCutout = WindowInsets.displayCutout.asPaddingValues()
                val startPadding = displayCutout.calculateStartPadding(layoutDirection)
                val endPadding = displayCutout.calculateEndPadding(layoutDirection)
                val layoutConfiguration = LocalConfiguration.current
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .statusBarsPadding()
                        .padding(
                            PaddingValues(
                                start = startPadding,
                                end = endPadding
                            )
                        )
                ) {
                    if (layoutConfiguration.orientation != Configuration.ORIENTATION_PORTRAIT) {
                        Row(
//                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .fillMaxHeight()

                            WeatherMainScreen(modifier, currentDay, city.value, this@MainActivity, daysList, dialogState)
                        }
                    } else {
                        Column(

                        ) {
                            val modifier = Modifier
                                .fillMaxWidth()

                            WeatherMainScreen(modifier, currentDay, city.value, this@MainActivity, daysList, dialogState)
                        }
                    }

                }

            }
        }
    }
}

@Composable
fun WeatherMainScreen(
    modifier: Modifier,
    currentDay: MutableState<WeatherModel>,
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    dialogState: MutableState<Boolean>
) {
    MainCard(
        modifier = modifier,
        currentDay = currentDay,
        onClickSync = {
            getData(city, context, daysList, currentDay)
        }, onClickSearch = {
            dialogState.value = true
        }
    )
    TabLayout(daysList, currentDay)
}

const val API_KEY = "e98ddd4851de419ab5e172407243008"
private fun getData(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {
    val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
            API_KEY +
            "&q=$city" +
            "&days=3" +
            "&aqi=no" +
            "&alerts=no"

    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)

    val city = mainObject.getJSONObject("location").getString("name")

    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0..<days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day")
                    .getJSONObject("condition")
                    .getString("text"),
                item.getJSONObject("day")
                    .getJSONObject("condition")
                    .getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}

