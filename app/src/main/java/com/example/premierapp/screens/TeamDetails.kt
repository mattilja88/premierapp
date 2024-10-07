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

    // Fetch team details based on the teamId
    LaunchedEffect(teamId) {
        // Simulate a delay for loading state
        delay(1000)

        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.footballApiService.getTeamDetails(teamId)
                withContext(Dispatchers.Main) {
                    teamDetails = response
                    isLoading = false // Set loading to false after fetching
                }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching team details: $e")
                isLoading = false // Set loading to false in case of error
            }
        }
    }

    // Show loading state until data is fetched
    if (isLoading) {
        LoadingScreen()
    } else {
        // Main column for displaying team details
        Column(modifier = Modifier.padding(16.dp)) {
            // Display team name and crest outside of LazyColumn
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
                    .align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = {
                    navController.navigate("team_games/${teamDetails?.name}")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp).width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF04f5ff), // Background color
                    contentColor = Color.White   // Text color
                )
            ) {
                Text(text = "Joukkueen ottelut")
            }

            Box(
                modifier = Modifier.fillMaxSize(), // Make Box take the whole screen
                contentAlignment = Alignment.Center // Center the content inside the Box
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally, // Center items horizontally inside LazyColumn
                ){
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

                    // Coach details
                    teamDetails?.coach?.let { coach ->
                        item {
                            CoachDetails(coach = coach) // Insert CoachDetails here
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Header for players
                    item {
                        Text(
                            text = "Pelaajat:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    // Player list
                    teamDetails?.squad?.let { players ->
                        items(players) { player ->
                            PlayerCard(player = player, navController)
                        }
                    }

                    // Button to go back to the previous screen
                    item {
                        androidx.compose.material3.Button(
                            onClick = {
                                navController.popBackStack() // Navigate back to the previous screen
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Sarjataulukkoon")
                        }
                    }
                }
            }
        }
    }
}