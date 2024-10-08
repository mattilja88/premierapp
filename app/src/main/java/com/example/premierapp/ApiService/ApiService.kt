package com.example.premierapp.ApiService

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("v4/competitions/PL/standings")
    suspend fun getData(): StandingsResponse

    @GET("v4/teams/{id}")
    suspend fun getTeamDetails(@Path("id") teamId: String): TeamDetailsModel

    @GET("v4/competitions/PL/scorers")
    suspend fun getTopScorers(): TopScorers
}

interface TheSportsDbApiService {
    @GET("api/v1/json/3/searchplayers.php")
    suspend fun getPlayerDetails(@Query("p") fname: String): PlayerResponseModel

    @GET("api/v1/json/3/eventsround.php")
    suspend fun getGamesForWeek(
        @Query("id") id: Int = 4328,
        @Query("r") week: Int,
        @Query("s") season: String = "2024-2025"
    ): GameWeekResponseModel
}
