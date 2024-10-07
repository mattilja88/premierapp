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
import com.example.premierapp.ApiService.Events
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
fun EveryGame(teamName: String) {
    var isLoading by remember { mutableStateOf(true) }
    val everyGameList = remember { mutableListOf<List<Events>>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                for (i in 1..38) {
                    val response = RetrofitClient.theSportsDbApiService.getGamesForWeek(week = i)
                    everyGameList.add(response.events)
                }
                // Kun kaikki on ladattu, aseta lataaminen valmiiksi
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching Gameweek details: $e")
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                everyGameList.flatten().forEach { game ->
                    item {

                            if (isMatchingTeam(teamName, game) ||
                                (teamName != "Manchester City FC" && teamName != "Manchester United FC" && isMatchingTeamWithShortName(teamName, game))) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "Kierros: ${game.intRound}  ${game.dateEvent}", fontSize = 18.sp)
                                    Image(
                                        painter = rememberImagePainter(game.strBanner),
                                        contentDescription = "${game.strEvent} Crest",
                                        modifier = Modifier
                                            .size(width = 360.dp, height = 60.dp)
                                            .padding(0.dp)
                                    )
                                    Text(
                                        text = if (game.intHomeScore == null || game.intAwayScore == null) {
                                            "${game.strHomeTeam} - ${game.strAwayTeam} (tulossa)"
                                        } else {
                                            "${game.strHomeTeam} - ${game.strAwayTeam} ${game.intHomeScore} - ${game.intAwayScore}"
                                        },
                                        fontSize = 18.sp
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isMatchingTeam(teamName: String, game: Events): Boolean {
    val normalizedTeamName = teamName.take(15) // Take up to 15 characters
    val teamNames = listOf("Manchester City", "Manchester United")

    // Check if the teamName matches either full team name or its substring
    return teamNames.any { game.strHomeTeam.startsWith(it, ignoreCase = true) || game.strAwayTeam.startsWith(it, ignoreCase = true) } &&
            (game.strHomeTeam.startsWith(normalizedTeamName, ignoreCase = true) ||
                    game.strAwayTeam.startsWith(normalizedTeamName, ignoreCase = true))
}

fun isMatchingTeamWithShortName(teamName: String, game: Events): Boolean {
    val normalizedTeamName = teamName.take(3) // Take the first 3 characters

    return (game.strHomeTeam.startsWith(normalizedTeamName, ignoreCase = true) ||
            game.strAwayTeam.startsWith(normalizedTeamName, ignoreCase = true))
}