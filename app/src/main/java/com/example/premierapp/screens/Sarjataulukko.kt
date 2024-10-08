package com.example.premierapp.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.premierapp.ApiService.ResponseModel

@Composable
fun Tulos(dataList: List<ResponseModel>, navController: NavController) {
    LazyColumn(modifier = Modifier.padding(2.dp)) {
        items(dataList) { data ->
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 0.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("team_details/${data.team.id}")
                    }
            ) {
                Text(
                    text = "${data.position}.",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 2.dp, top = 3.dp, end = 2.dp, bottom = 3.dp)
                        .weight(1f)
                )
                Image(
                    painter = rememberImagePainter(data.team.crest),
                    contentDescription = "${data.team.name} Crest",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterVertically)

                )
                Text(
                    text = data.team.tla,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(3.dp)
                        .weight(1.8f)
                )
                Text(
                    text = "${data.playedGames}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 2.dp)
                        .weight(0.8f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "${data.won}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 2.dp)
                        .weight(0.8f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "${data.draw}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 2.dp)
                        .weight(0.8f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "${data.lost}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 2.dp, top = 3.dp, end = 8.dp, bottom = 3.dp)
                        .weight(0.8f),
                    textAlign = TextAlign.End
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
                            .padding(vertical = 3.dp, horizontal = 2.dp)
                            .weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                Text(
                    text = "${data.points}",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 2.dp)
                        .weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
