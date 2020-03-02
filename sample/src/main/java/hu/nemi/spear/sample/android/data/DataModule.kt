package hu.nemi.spear.sample.android.data

import dagger.Module
import dagger.Provides
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider

@Module
object DataModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()

    @Provides
    fun provideRetrofit(okHttpClient: Provider<OkHttpClient>): Retrofit =
        Retrofit.Builder()
            .callFactory(object : Call.Factory {
                private val client by lazy { okHttpClient.get() }

                override fun newCall(request: Request): Call = client.newCall(request)
            })
            .baseUrl("https://quotes.rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideQuoteApi(retrofit: Retrofit): QuoteApi =
        retrofit.create(QuoteApi::class.java)

    @Provides
    fun provideRemoteDataSource(remoteRestDataSource: RestDataSource): RemoteDataSource =
        remoteRestDataSource

    @Provides
    fun provideRepository(repository: QuoteOfTheDayRepositoryImpl): QuoteOfTheDayRepository =
        repository
}