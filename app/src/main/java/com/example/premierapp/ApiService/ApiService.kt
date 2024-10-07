package com.example.premierapp.ApiService

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Fetches standings of all teams in the Premier League
    @GET("v4/competitions/PL/standings")
    suspend fun getData(): StandingsResponse

    // Fetches specific team details by ID
    @GET("v4/teams/{id}") // Adjust the endpoint based on actual API requirements
    suspend fun getTeamDetails(@Path("id") teamId: String): TeamDetailsModel

    // Fetches the top scorers in the Premier League
    @GET("v4/competitions/PL/scorers") // Adjust the endpoint based on actual API requirements
    suspend fun getTopScorers(): TopScorers
}

interface TheSportsDbApiService {
    @GET("api/v1/json/3/searchplayers.php")
    suspend fun getPlayerDetails(@Query("p") fname: String): PlayerResponseModel

    @GET("api/v1/json/3/eventsround.php")
    suspend fun getGamesForWeek(
        @Query("id") id: Int = 4328,   // Set default ID
        @Query("r") week: Int,         // Use @Query to pass the week
        @Query("s") season: String = "2024-2025" // Use @Query to pass the season
    ): GameWeekResponseModel
}
