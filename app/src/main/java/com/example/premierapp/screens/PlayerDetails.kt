package com.example.premierapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.premierapp.LoadingScreen
import com.example.premierapp.ApiService.PlayerResponseModel
import com.example.premierapp.ApiService.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun PlayerDetailsScreen(navController: NavController, fname: String){
    var playerDetails by remember { mutableStateOf<PlayerResponseModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(fname) {
        delay(2000)

        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.theSportsDbApiService.getPlayerDetails(fname)
                withContext(Dispatchers.Main) {
                    playerDetails = response
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching team details: $e")
                isLoading = false
            }
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        Column(modifier = Modifier.padding(top=0.dp, end=16.dp, start=16.dp)) {
            playerDetails?.player?.firstOrNull()?.let { player ->

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        item {
                            Text(
                                text = player.strPlayer,
                                fontSize = 32.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth()
                            )
                            Text(text = "Pelipaikka: ${player.strPosition}")
                            Text(text = "Kansalaisuus: ${player.strNationality}")
                            val dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
                            val localDateTimeString = "${player.dateBorn}T00:00"
                            val localDateTime = LocalDateTime.parse(localDateTimeString)
                            val londonZoneId = ZoneId.of("Europe/London")
                            val londonDateTime = ZonedDateTime.of(localDateTime, londonZoneId)
                            val helsinkiZoneId = ZoneId.of("Europe/Helsinki")
                            val helsinkiDateTime = londonDateTime.withZoneSameInstant(helsinkiZoneId)
                            val formattedDate = helsinkiDateTime.format(dateFormatter)
                            Text(text = "Syntymäaika: $formattedDate")
                            Text(text = "Pelinumero: ${player.strNumber}")
                            Text(text = "Palkka: ${player.strWage}")
                            Text(text = "Pituus: ${player.strHeight}")
                            Text(text = "Paino: ${player.strWeight}")
                        }
                        item {
                            if (player?.strThumb != null && player.strThumb != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strThumb),
                                    contentDescription = "${player.strThumb} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strCutout != null && player.strCutout != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strCutout),
                                    contentDescription = "${player.strCutout} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strRender != null && player.strRender != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strRender),
                                    contentDescription = "${player.strRender} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strBanner != null && player.strBanner != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strBanner),
                                    contentDescription = "${player.strBanner} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strFanart1 != null && player.strFanart1 != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strFanart1),
                                    contentDescription = "${player.strFanart1} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strFanart2 != null && player.strFanart2 != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strFanart2),
                                    contentDescription = "${player.strFanart2} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strFanart3 != null && player.strFanart3 != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strFanart3),
                                    contentDescription = "${player.strFanart3} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                            }
                        }
                        item {
                            if (player?.strFanart4 != null && player.strFanart4 != "null") {
                                Image(
                                    painter = rememberImagePainter(player.strFanart4),
                                    contentDescription = "${player.strFanart4} Crest",
                                    modifier = Modifier
                                        .size(400.dp)
                                        .padding(vertical = 16.dp)

                                )
                            } else {
                            }
                        }
                    }
                }
            }
        }
    }
}