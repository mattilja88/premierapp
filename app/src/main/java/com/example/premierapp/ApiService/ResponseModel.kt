package com.example.premierapp.ApiService

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

data class GameWeekResponseModel(
    val events: List<Events>,
)

data class Events(
    val strEvent: String,             // Event name
    val strLeague: String,            // League name
    val strLeagueBadge: String,       // URL of the league badge
    val strHomeTeam: String,          // Home team name
    val strAwayTeam: String,          // Away team name
    val intHomeScore: String?,        // Home team score (nullable)
    val intAwayScore: String?,        // Away team score (nullable)
    val intRound: String?,            // Round number (nullable)
    val intSpectators: String?,       // Number of spectators (nullable)
    val strOfficial: String?,         // Official(s) (nullable)
    val strTimestamp: String,         // Event timestamp (UTC time)
    val dateEvent: String,            // Event date
    val dateEventLocal: String,       // Local event date
    val strTime: String,              // Event time (UTC)
    val strTimeLocal: String,         // Local event time
    val strHomeTeamBadge: String,     // URL of the home team's badge
    val idAwayTeam: String,           // ID of the away team
    val strAwayTeamBadge: String,     // URL of the away team's badge
    val strVenue: String?,            // Venue name (nullable)
    val strCountry: String?,          // Country name (nullable)
    val strCity: String?,             // City name (nullable)
    val strPoster: String?,           // URL of the event poster
    val strSquare: String?,           // URL of the square image of the event
    val strFanart: String?,           // URL of the fanart image (nullable)
    val strThumb: String?,            // URL of the thumbnail image
    val strBanner: String?,           // URL of the banner image
    val strMap: String?,              // Map URL (nullable)
    val strVideo: String?,            // URL of the video highlights
    val strStatus: String?,           // Event status (nullable)
    val strPostponed: String?,        // If the match is postponed (nullable)
    val strLocked: String?            // If the event data is locked (nullable)
)


