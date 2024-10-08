package com.example.premierapp.ApiService

import com.example.premierapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Auth-Token", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}

object RetrofitClient {
    private const val FOOTBALL_API_BASE_URL = "https://api.football-data.org/"
    private const val THESPORTSDB_API_BASE_URL = "https://www.thesportsdb.com/"

    private val footballHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .build()

    val footballApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FOOTBALL_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(footballHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    val theSportsDbApiService: TheSportsDbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(THESPORTSDB_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheSportsDbApiService::class.java)
    }
}
