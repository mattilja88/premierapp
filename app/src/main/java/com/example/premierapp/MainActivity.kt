package com.example.premierapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.premierapp.ui.theme.PremierAppTheme
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import com.example.premierapp.RetrofitClient
import kotlinx.coroutines.*
import androidx.compose.runtime.*
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.unit.dp

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
                    PremierApp(fetchData = { onResult -> fetchData(onResult) })
                }
            }
        }
    }
    fun fetchData(onResult: (List<ResponseModel>) -> Unit) {
        debounceJob?.cancel() // Cancel previous job
        debounceJob = CoroutineScope(Dispatchers.IO).launch {
            if (apiRateLimiter.canMakeCall()) {
                try {
                    val response = RetrofitClient.footballApiService.getData()
                    apiRateLimiter.recordCall() // Record the call
                    withContext(Dispatchers.Main) {
                        onResult(response.standings[0].table)
                    }
                } catch (e: Exception) {
                    Log.e("Error", "Error fetching data: $e")
                    withContext(Dispatchers.Main) {
                        onResult(emptyList())
                    }
                }
            } else {
                Log.w("Warning", "Rate limit exceeded. Please wait.")
            }
        }
    }
}

@Composable
fun PremierApp(fetchData: (onResult: (List<ResponseModel>) -> Unit) -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "team_list") {
        composable("team_list") {
            TeamListScreen(navController, fetchData)
        }
        composable("team_details/{teamId}") { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: return@composable
            TeamDetailsScreen(navController, teamId = teamId)
        }
        composable("player_details/{fname}") { backStackEntry ->
            val fname = backStackEntry.arguments?.getString("fname") ?: return@composable
            PlayerDetailsScreen(navController, fname=fname)
        }
    }
}

@Composable
fun TeamListScreen(navController: NavController, fetchData: (onResult: (List<ResponseModel>) -> Unit) -> Unit) {
    var dataList by remember { mutableStateOf<List<ResponseModel>>(emptyList()) }
    var taulukko by remember { mutableStateOf(true)}
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Otsikko("PremierApp")
        androidx.compose.material3.Button(
            onClick = {
                taulukko = !taulukko
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            if (taulukko) {
                Text("Top10")
            } else {
                Text("Sarjataulukko")
            }

        }
        fetchData { responseList ->
            dataList = responseList
        }
        if (taulukko){
            Tulos(dataList, navController)
        } else {
            topTen()
        }

    }
}

@Composable
fun Otsikko(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
    )
}

@Composable
fun topTen(){
    Column(
        modifier = Modifier
            .padding(32.dp)
    ){
        Text(
            text = "Tähän tulee top10",
            fontSize = 20.sp,
        )
    }
}

@Composable
fun Tulos(dataList: List<ResponseModel>, navController: NavController) {
    Column(modifier = Modifier.padding(4.dp)) {
        dataList.forEach { data ->
            Row(
                modifier = Modifier
                    .padding(vertical = 3.dp, horizontal = 0.dp) // Set vertical space to 3.dp
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${data.position}.",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            start = 2.dp,
                            top = 3.dp,
                            end = 2.dp,
                            bottom = 3.dp
                        ) // Horizontal padding set to 2.dp and vertical to 3.dp
                        .weight(1f)
                )
                Image(
                    painter = rememberImagePainter(data.team.crest),
                    contentDescription = "${data.team.name} Crest",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            navController.navigate("team_details/${data.team.id}")
                        } // Navigate on click
                )
                Text(
                    text = data.team.tla,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(3.dp)
                        .weight(1.8f)
                        .clickable {
                            navController.navigate("team_details/${data.team.id}")
                        } // Navigate on click
                )
                Text(
                    text = "${data.playedGames}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            vertical = 3.dp,
                            horizontal = 2.dp
                        ) // Horizontal padding set to 2.dp and vertical to 3.dp
                        .weight(0.8f),
                    textAlign = TextAlign.End // Right align the text
                )
                Text(
                    text = "${data.won}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            vertical = 3.dp,
                            horizontal = 2.dp
                        ) // Horizontal padding set to 2.dp and vertical to 3.dp
                        .weight(0.8f),
                    textAlign = TextAlign.End // Right align the text
                )
                Text(
                    text = "${data.draw}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            vertical = 3.dp,
                            horizontal = 2.dp
                        ) // Horizontal padding set to 2.dp and vertical to 3.dp
                        .weight(0.8f),
                    textAlign = TextAlign.End // Right align the text
                )
                Text(
                    text = "${data.lost}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            start = 2.dp,
                            top = 3.dp,
                            end = 8.dp,
                            bottom = 3.dp
                        ) // Horizontal padding set to 2.dp and vertical to 3.dp
                        .weight(0.8f),
                    textAlign = TextAlign.End // Right align the text
                )
                Row(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${data.goalsFor}  - ${if (data.goalsAgainst < 10) "  " else ""}${data.goalsAgainst}",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(
                                vertical = 3.dp,
                                horizontal = 2.dp
                            ) // Horizontal padding set to 2.dp and vertical to 3.dp
                            .weight(1f),
                        textAlign = TextAlign.End // Right align the text
                    )
                }
                Text(
                    text = "${data.points}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            vertical = 3.dp,
                            horizontal = 2.dp
                        ) // Horizontal padding set to 2.dp and vertical to 3.dp
                        .weight(1f),
                    textAlign = TextAlign.End // Right align the text
                )
            }
        }
    }
}
@Composable
fun TeamDetailsScreen(navController: NavController, teamId: String) {
    var teamDetails by remember { mutableStateOf<TeamDetailsModel?>(null) }
    var isLoading by remember { mutableStateOf(true) } // State to track loading status

    // Fetch team details based on the teamId
    LaunchedEffect(teamId) {
        // Simulate a delay for loading state
        delay(2000)

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
                    .padding(vertical = 32.dp)
                    .fillMaxWidth()
            )
            Image(
                painter = rememberImagePainter(teamDetails?.crest),
                contentDescription = "${teamDetails?.name} Crest",
                modifier = Modifier
                    .size(160.dp)
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

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

@Composable
fun PlayerDetailsScreen(navController: NavController, fname: String){
    var playerDetails by remember { mutableStateOf<PlayerResponseModel?>(null) }
    var isLoading by remember { mutableStateOf(true) } // State to track loading status

    // Fetch team details based on the teamId
    LaunchedEffect(fname) {
        // Simulate a delay for loading state
        delay(2000)

        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.theSportsDbApiService.getPlayerDetails(fname)
                withContext(Dispatchers.Main) {
                    playerDetails = response
                    isLoading = false // Set loading to false after fetching
                }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching team details: $e")
                isLoading = false // Set loading to false in case of error
            }
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        // Main column for displaying team details
        Column(modifier = Modifier.padding(16.dp)) {
            // Display player name and details
            playerDetails?.player?.firstOrNull()?.let { player ->
                Text(
                    text = player.strPlayer,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .fillMaxWidth()
                )
                // Add more player details display here as needed
                Text(text = "Pelipaikka: ${player.strPosition}")
                Text(text = "Kansalaisuus: ${player.strNationality}")
                Text(text = "Syntymäaika: ${player.dateBorn}")
                Text(text = "Pelinumero: ${player.strNumber}")
                Text(text = "Palkka: ${player.strWage}")
                Text(text = "Pituus: ${player.strHeight}")
                Text(text = "Paino: ${player.strWeight}")
                // Add more player fields as needed
                Box(
                    modifier = Modifier.fillMaxSize(), // Make Box take the whole screen
                    contentAlignment = Alignment.Center // Center the content inside the Box
                ) {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally, // Center items horizontally inside LazyColumn
                    ) {
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
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
                            // Jos ei haluta näyttää mitään, voidaan jättää tämä else-blokki tyhjäksi
                        }
                    }
                }
                    }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
        Box(
            modifier = Modifier.fillMaxSize(), // täyttää koko näytön
            contentAlignment = Alignment.Center // keskittää sisältö ruudun keskelle
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // keskittää tekstin ja kuvakkeen
            ) {
                CircularProgressIndicator() // Latausikoni (pyörivä ympyrä)
                Spacer(modifier = Modifier.height(16.dp)) // Lisää väliä ikonin ja tekstin väliin
                Text("Loading data...") // Näyttää lataustekstin
            }
        }
}

class ApiRateLimiter(private val maxCallsPerMinute: Int) {
    private val callTimestamps = mutableListOf<Long>()
    private var lastResetTime = System.currentTimeMillis()

    fun canMakeCall(): Boolean {
        val currentTime = System.currentTimeMillis()
        // Reset timestamps if a minute has passed
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

@Composable
fun CoachDetails(coach: Coach) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Valmentaja: ${coach.name}", fontWeight = FontWeight.Bold)

        Text(text = "Syntymäaika: ${coach.dateOfBirth}", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Kansalaisuus: ${coach.nationality}", modifier = Modifier.padding(vertical = 4.dp))

        Row(modifier = Modifier.padding(vertical = 4.dp)){
            coach.contract?.let {
                Text(text = "Sopimus: ${it.start ?: "N/A"} - ")
                Text(text = "${it.until ?: "N/A"}")
            } ?: Text(text = "Contract: Not Available")
        }
    }
}

@Composable
fun PlayerCard(player: Player, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, shape = CutCornerShape(8.dp))
            .padding(8.dp)
            .clickable {
                navController.navigate("player_details/${player.name}")
            }
    ) {
        Text(
            text = player.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Pelipaikka: ${player.position}")
        Text(text = "Syntymäpäivä: ${player.dateOfBirth}")
        Text(text = "Kansallisuus: ${player.nationality}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PremierApp(fetchData = { onResult -> onResult(emptyList()) }) // Mock fetchData for preview
}