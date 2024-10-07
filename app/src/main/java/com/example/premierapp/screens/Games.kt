package com.example.premierapp.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.premierapp.ApiService.GameWeekResponseModel
import com.example.premierapp.LoadingScreen
import com.example.premierapp.ApiService.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun GamePage(navController: NavController){
    var expanded by remember { mutableStateOf(false) }
    var clickedWeek by remember { mutableStateOf(1) }
    val lightPurple = Color(0xFF04f5ff)

    val weeks = (1..38).toList()
    Column(){
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { expanded = true }
            .background(lightPurple)
            .padding(16.dp)) {

            // Show the currently selected week
            Text(
                text = "Viikko $clickedWeek",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() // Make text take full width to center it
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .height(LocalConfiguration.current.screenHeightDp.dp / 2)
                    .background(lightPurple),
            ) {
                weeks.forEach { week ->
                    DropdownMenuItem(
                        text = { Text("Viikko $week", textAlign = TextAlign.Center) },
                        onClick = {
                            clickedWeek = week // Set the clicked week
                            expanded = false   // Dismiss the dropdown menu
                        })
                }
            }
        }
        AllGames(navController = navController, weekNumber = clickedWeek)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllGames(navController: NavController, weekNumber: Int) {
    var gameWeekDetails by remember { mutableStateOf<GameWeekResponseModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(weekNumber) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.theSportsDbApiService.getGamesForWeek(week = weekNumber)
                withContext(Dispatchers.Main) {
                    gameWeekDetails = response
                    isLoading = false // Set loading to false after fetching
                }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching Gameweek details: $e")
                isLoading = false
            }
        }
    }
    if (isLoading) {
        LoadingScreen()
    } else {
        Column(modifier = Modifier.padding(16.dp)){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                gameWeekDetails?.events?.forEach { event ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )  {
                                val dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy") // Example: 7.10.2024
                                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm") // Example: 14:30
                                val localDateTimeString = "${event.strTimestamp}"
                                val localDateTime = LocalDateTime.parse(localDateTimeString)
                                val londonZoneId = ZoneId.of("Europe/London")
                                val londonDateTime = ZonedDateTime.of(localDateTime, londonZoneId)
                                val helsinkiZoneId = ZoneId.of("Europe/Helsinki")
                                val helsinkiDateTime = londonDateTime.withZoneSameInstant(helsinkiZoneId)
                                val formattedDate = helsinkiDateTime.format(dateFormatter)
                                val formattedTime = helsinkiDateTime.format(timeFormatter)
                                Text(text = "$formattedDate  $formattedTime", fontSize = 18.sp)
                                Text(text = "${event.strVenue}", fontSize = 18.sp)



                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically // Align images in the center vertically
                            ) {
                                // Check if the home team badge is not null
                                if (!event.strHomeTeamBadge.isNullOrEmpty()) {
                                    Image(
                                        painter = rememberImagePainter(event.strHomeTeamBadge),
                                        contentDescription = "${event.strHomeTeam} Crest",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .align(Alignment.CenterVertically)
                                            .weight(1f)
                                    )
                                } else {
                                    // Placeholder or empty Image when URL is null
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color.Gray), // Placeholder color
                                    )
                                }

                                Text(text = "${event.intHomeScore}", fontSize = 18.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "${event.intAwayScore}", fontSize = 18.sp,)

                                // Check if the away team badge is not null
                                if (!event.strAwayTeamBadge.isNullOrEmpty()) {
                                    Image(
                                        painter = rememberImagePainter(event.strAwayTeamBadge),
                                        contentDescription = "${event.strAwayTeam} Crest",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .align(Alignment.CenterVertically)
                                            .weight(1f)
                                    )
                                } else {
                                    // Placeholder or empty Image when URL is null
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color.Gray), // Placeholder color
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }


                        //{
                        //    Text(text = "${event.strHomeTeam} - ${event.strAwayTeam}", fontSize = 18.sp)
                        //
                       // }
                    }
                }
            }
        }
    }
}