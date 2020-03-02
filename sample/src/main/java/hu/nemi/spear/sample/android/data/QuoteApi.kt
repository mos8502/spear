package hu.nemi.spear.sample.android.data

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface QuoteApi {
    @GET("/qod")
    @Headers("accept: application/json")
    suspend fun qod(@Query("language") language: String): RestQuoteOfTheDay
}