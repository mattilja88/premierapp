package com.example.premierapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import kotlinx.coroutines.*
import androidx.compose.runtime.*
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import com.example.premierapp.LoadingScreen
import com.example.premierapp.ApiService.RetrofitClient
import com.example.premierapp.ApiService.TeamDetailsModel


@Composable
fun TeamDetailsScreen(navController: NavController, teamId: String) {
    var teamDetails by remember { mutableStateOf<TeamDetailsModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(teamId) {
        delay(1000)

        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.footballApiService.getTeamDetails(teamId)
                withContext(Dispatchers.Main) {
                    teamDetails = response
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
        Column(modifier = Modifier.padding(top=0.dp, start=16.dp, end=16.dp, bottom=0.dp)) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    item {
                        Text(
                            text = "${teamDetails?.name}",
                            fontSize = 32.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                        )
                        Image(
                            painter = rememberImagePainter(teamDetails?.crest),
                            contentDescription = "${teamDetails?.name} Crest",
                            modifier = Modifier
                                .size(160.dp)
                                .padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = {
                                navController.navigate("team_games/${teamDetails?.name}")
                            },
                            modifier = Modifier.padding(bottom = 8.dp).width(300.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF04f5ff),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Joukkueen ottelut")
                        }
                    }
                    item {
                        Text(
                            text = "Perustettu ${teamDetails?.founded}",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "Stadion: ${teamDetails?.venue}",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    teamDetails?.coach?.let { coach ->
                        item {
                            CoachDetails(coach = coach)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    item {
                        Text(
                            text = "Pelaajat:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    teamDetails?.squad?.let { players ->
                        items(players) { player ->
                            PlayerCard(player = player, navController)
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.padding(bottom = 8.dp).width(300.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF04f5ff),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Sarjataulukkoon")
                        }
                    }
                }
            }
        }
    }
}