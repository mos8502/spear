package hu.nemi.spear.sample.android.data

import javax.inject.Inject

class RestDataSource @Inject constructor(private val api: QuoteApi): RemoteDataSource {
    override suspend fun quote(language: String): String = with(RestModelMapper) {
        api.qod(language).toModel()
    }
}