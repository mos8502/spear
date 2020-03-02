package hu.nemi.spear.sample.android.data

interface RemoteDataSource {
    suspend fun quote(language: String): String
}