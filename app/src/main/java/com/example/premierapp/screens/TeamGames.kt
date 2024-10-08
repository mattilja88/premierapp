package com.example.premierapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.premierapp.ApiService.Events
import com.example.premierapp.LoadingScreen
import com.example.premierapp.ApiService.RetrofitClient
import com.example.premierapp.BottomNavigation
import com.example.premierapp.MainTopBar
import com.example.premierapp.ScreenTopBar
import com.example.premierapp.TabItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EveryGame(teamName: String, items: List<TabItem>, lightPurple: Color, navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    val everyGameList = remember { mutableListOf<List<Events>>() }

    Scaffold(
        topBar = {
            ScreenTopBar(color = lightPurple, navController = navController)
        },
        bottomBar = {
            BottomNavigation(items, navController)
        }
    ) { paddingValues ->
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                for (i in 1..38) {
                    val response = RetrofitClient.theSportsDbApiService.getGamesForWeek(week = i)
                    everyGameList.add(response.events)
                }
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
        Column(
            modifier = Modifier.padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                everyGameList.flatten().forEach { game ->
                    item {

                        if (isMatchingTeam(teamName, game) ||
                            (teamName != "Manchester City FC" && teamName != "Manchester United FC" && isMatchingTeamWithShortName(
                                teamName,
                                game
                            ))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Kierros: ${game.intRound}  ${game.dateEvent}",
                                    fontSize = 18.sp
                                )
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
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

fun isMatchingTeam(teamName: String, game: Events): Boolean {
    val normalizedTeamName = teamName.take(15)
    val teamNames = listOf("Manchester City", "Manchester United")


    return teamNames.any { game.strHomeTeam.startsWith(it, ignoreCase = true) || game.strAwayTeam.startsWith(it, ignoreCase = true) } &&
            (game.strHomeTeam.startsWith(normalizedTeamName, ignoreCase = true) ||
                    game.strAwayTeam.startsWith(normalizedTeamName, ignoreCase = true))
}

fun isMatchingTeamWithShortName(teamName: String, game: Events): Boolean {
    val normalizedTeamName = teamName.take(3)

    return (game.strHomeTeam.startsWith(normalizedTeamName, ignoreCase = true) ||
            game.strAwayTeam.startsWith(normalizedTeamName, ignoreCase = true))
}