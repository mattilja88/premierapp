package com.example.premierapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.premierapp.ui.theme.PremierAppTheme
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import kotlinx.coroutines.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.example.premierapp.ApiService.ResponseModel
import com.example.premierapp.ApiService.RetrofitClient
import com.example.premierapp.screens.AllGames
import com.example.premierapp.screens.EveryGame
import com.example.premierapp.screens.GamePage
import com.example.premierapp.screens.PlayerDetailsScreen
import com.example.premierapp.screens.TeamDetailsScreen
import com.example.premierapp.screens.TopTen
import com.example.premierapp.screens.Tulos
import retrofit2.HttpException


class MainActivity : ComponentActivity() {
    private val apiRateLimiter = ApiRateLimiter(10)
    private var debounceJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PremierAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PremierApp(fetchData = { onResult -> fetchData(this, onResult) })
                }
            }
        }
    }
    private fun fetchData(context: Context, onResult: (List<ResponseModel>) -> Unit) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.IO).launch {
            if (apiRateLimiter.canMakeCall()) {
                try {
                    val response = RetrofitClient.footballApiService.getData()
                    apiRateLimiter.recordCall()

                    withContext(Dispatchers.Main) {
                        onResult(response.standings[0].table)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        when (e) {
                            is HttpException -> {
                                if (e.code() == 429) {
                                    Log.w("Warning", "Rate limit exceeded. Please wait.")
                                    Toast.makeText(
                                        context,
                                        "Liian monta API-kutsua peräkkäin, odota hetki.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    // Muu HTTP-virhe
                                    Log.e("Error", "HTTP error: ${e.code()}")
                                    Toast.makeText(
                                        context,
                                        "HTTP-virhe: ${e.code()}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        onResult(emptyList()) // Palautetaan tyhjä lista virheen sattuessa
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.w("Warning", "Rate limit exceeded. Please wait.")
                    Toast.makeText(context, "Liikaa pyyntöjä, odota hetki.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremierApp(fetchData: (onResult: (List<ResponseModel>) -> Unit) -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        TabItem("Sarjataulukko", Icons.Filled.Home, "team_list"),
        TabItem("Top 10", Icons.Filled.Person, "top_ten"),
        TabItem("Kaikki pelit", Icons.Filled.Search,"all_games")
    )
    val lightPurple = Color(0xFF04f5ff)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    AsyncImage(
                        model = "https://fifplay.com/img/public/premier-league-3-logo.png",
                        contentDescription = "Premier League Logo",
                        modifier = Modifier.size(180.dp),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = lightPurple
                )
            )
        },
        bottomBar = {
            BottomNavigation(items, navController)

        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "team_list", Modifier.padding(paddingValues)) {
            composable("team_list") {
                TeamListScreen(navController, fetchData)
            }
            composable("top_ten") {
                TopTen(navController)
            }
            composable("all_games") {
                GamePage(navController)
            }
            composable("team_details/{teamId}") { backStackEntry ->
                val teamId = backStackEntry.arguments?.getString("teamId") ?: return@composable
                TeamDetailsScreen(navController, teamId = teamId)
            }
            composable("player_details/{fname}") { backStackEntry ->
                val fname = backStackEntry.arguments?.getString("fname") ?: return@composable
                PlayerDetailsScreen(navController, fname = fname)
            }
            composable("team_games/{teamName}") { backStackEntry ->
                val teamName = backStackEntry.arguments?.getString("teamName") ?: return@composable
                EveryGame(teamName = teamName)
            }
        }
    }
}

@Composable
fun BottomNavigation(items: List<TabItem>, navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val lightPurple = Color(0xFF04f5ff)

    NavigationBar(
        containerColor = lightPurple,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route)
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}


@Composable
fun TeamListScreen(navController: NavController, fetchData: (onResult: (List<ResponseModel>) -> Unit) -> Unit) {
    var dataList by remember { mutableStateOf<List<ResponseModel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchData { responseList ->
            dataList = responseList
            loading = false
        }
    }

    Column(
        modifier = Modifier.padding(top=0.dp, start=16.dp, end=16.dp, bottom=0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (loading) {
            LoadingScreen()
        } else {
            Tulos(dataList, navController)
        }
    }
}


@Composable
fun LoadingScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading data...")
            }
        }
}

class ApiRateLimiter(private val maxCallsPerMinute: Int) {
    private val callTimestamps = mutableListOf<Long>()
    private var lastResetTime = System.currentTimeMillis()

    fun canMakeCall(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastResetTime > 60000) {
            callTimestamps.clear()
            lastResetTime = currentTime
        }
        return callTimestamps.size < maxCallsPerMinute
    }
    fun recordCall() {
        callTimestamps.add(System.currentTimeMillis())
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PremierApp(fetchData = { onResult -> onResult(emptyList()) })
}