package com.example.premierapp.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import com.example.premierapp.ApiService.Coach
import com.example.premierapp.ApiService.Player
import com.example.premierapp.ApiService.Scorer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
            .clickable {
                navController.navigate("player_details/${player.name}")
            },
                horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = player.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        val dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        val localDateTimeString = "${player.dateOfBirth}T00:00"
        val localDateTime = LocalDateTime.parse(localDateTimeString)
        val londonZoneId = ZoneId.of("Europe/London")
        val londonDateTime = ZonedDateTime.of(localDateTime, londonZoneId)
        val helsinkiZoneId = ZoneId.of("Europe/Helsinki")
        val helsinkiDateTime = londonDateTime.withZoneSameInstant(helsinkiZoneId)
        val formattedDate = helsinkiDateTime.format(dateFormatter)
        Text(text = "Pelipaikka: ${player.position}")
        Text(text = "Syntymäpäivä: $formattedDate")
        Text(text = "Kansallisuus: ${player.nationality}")
    }
}

@Composable
fun TopTenScorers(scorer: Scorer, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(4.dp)
            .clickable {
                navController.navigate("player_details/${scorer.player.name}")
            },

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = scorer.player.name,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = scorer.team.name, fontWeight = FontWeight.Bold) // Use scorer.team.name
        Text(text = "Maalit: ${scorer.goals}")
        Text(text = "Syötöt: ${scorer.assists ?: 0}") // Handle null assists
        Text(text = "Pilkuista: ${scorer.penalties ?: 0}") // Handle null penalties
        Text(text = "Ottelut: ${scorer.playedMatches}")
    }
}