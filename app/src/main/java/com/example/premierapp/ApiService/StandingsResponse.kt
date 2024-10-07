package com.example.premierapp.ApiService

data class StandingsResponse(
    val standings: List<Standings>
)

data class Standings(
    val table: List<ResponseModel>
)
