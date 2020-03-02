package hu.nemi.spear.sample.android.data

interface QuoteOfTheDayRepository {
    suspend fun quote(language: String): String
}