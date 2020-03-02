package hu.nemi.spear.sample.android.data

import javax.inject.Inject

class QuoteOfTheDayRepositoryImpl @Inject constructor(private val remote: RemoteDataSource): QuoteOfTheDayRepository {
    override suspend fun quote(language: String): String =
        remote.quote(language)
}