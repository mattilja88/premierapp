package com.example.premierapp

import java.util.Objects

data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String
)

data class ResponseModel(
    val position: Int,
    val team: Team, // Use the new Team class here
    val playedGames: Int,
    val form: String?, // form can be null, so we make it nullable
    val won: Int,
    val draw: Int,
    val lost: Int,
    val points: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int
)

data class TeamDetailsModel(
    val area: Area,
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String,
    val address: String,
    val website: String,
    val founded: Int,
    val clubColors: String,
    val venue: String,
    val runningCompetitions: List<Competition>,
    val coach: Coach,
    val squad: List<Player>,
    val lastUpdated: String
)

data class Area(
    val id: Int,
    val name: String,
    val code: String,
    val flag: String
)

data class Competition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String
)

data class Coach(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val name: String,
    val dateOfBirth: String,
    val nationality: String,
    val contract: Contract?
)

data class Contract(
    val start: String?,
    val until: String?
)

data class Player(
    val id: Int,
    val name: String,
    val position: String,
    val dateOfBirth: String,
    val nationality: String
)

data class PlayerResponseModel(
    val player: List<TeamPlayer>
)

data class TeamPlayer(
    val strNationality: String,
    val strPlayer: String,
    val strTeam: String, // Changed from strTeam1 to strTeam for clarity
    val strTeam2: String,
    val dateBorn: String,
    val strNumber: String,
    val strSigning: String,
    val strWage: String,
    val strDescriptionEN: String,
    val strPosition: String,
    val strHeight: String,
    val strWeight: String,
    val strThumb: String,
    val strCutout: String,
    val strRender: String,
    val strBanner: String?, // Nullable fields
    val strFanart1: String?, // Nullable fields
    val strFanart2: String?, // Nullable fields
    val strFanart3: String?, // Nullable fields
    val strFanart4: String?  // Nullable fields
)

data class TopScorers(
    val count: Int,
    val filters: Filters,
    val competition: TopCompetition,
    val season: Season,
    val scorers: List<Scorer>
)

data class Filters(
    val season: String,
    val limit: Int
)

data class TopCompetition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String
)

data class Season(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int,
    val winner: String? // Can be null
)

data class Scorer(
    val player: TopPlayer,
    val team: TopTeam,
    val playedMatches: Int,
    val goals: Int,
    val assists: Int?,
    val penalties: Int?
)

data class TopPlayer(
    val id: Int,
    val name: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val nationality: String,
    val section: String,
    val position: String?,
    val shirtNumber: Int?,
    val lastUpdated: String
)

data class TopTeam(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String,
    val address: String,
    val website: String,
    val founded: Int,
    val clubColors: String,
    val venue: String,
    val lastUpdated: String
)

