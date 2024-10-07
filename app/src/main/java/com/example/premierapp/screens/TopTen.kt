package com.example.premierapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.premierapp.ApiService.RetrofitClient
import com.example.premierapp.ApiService.TopScorers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
import com.example.premierapp.LoadingScreen
import retrofit2.HttpException


@Composable
fun TopTen(navController: NavController) {
    var topScorers by remember { mutableStateOf<TopScorers?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.footballApiService.getTopScorers()
            }

            topScorers = response
        } catch (e: Exception) {
            Log.e("Error", "Error fetching top scorers: $e")

            if (e is HttpException) {
                when (e.code()) {
                    429 -> {
                        errorMessage = "Liian paljon tietojen hakua. Tietokannan ilmaisversiossa hakujen määrä on rajoitettu. Odota hetki."
                    }
                    else -> {
                        errorMessage = "HTTP-virhe: ${e.code()}"
                    }
                }
            } else {
                errorMessage = "Verkkovirhe, tarkista yhteys."
            }
        }
    }

    // Using Box to center content
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            topScorers?.scorers?.let { scorers ->
                items(scorers) { scorer ->
                    TopTenScorers(scorer = scorer, navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Centering the error message or loading screen
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = Color.Red
            )
        } else if (topScorers == null) {
            LoadingScreen()
        }
    }
}

