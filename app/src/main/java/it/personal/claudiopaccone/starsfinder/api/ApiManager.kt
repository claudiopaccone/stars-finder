package it.personal.claudiopaccone.starsfinder.api

import com.google.gson.GsonBuilder
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiManager {

    /** OkHttpClient shared between all the APIs. */
    val httpClient: OkHttpClient by lazy {
        with(OkHttpClient.Builder()) {
            connectTimeout(35, TimeUnit.SECONDS)
            readTimeout(35, TimeUnit.SECONDS)
            writeTimeout(35, TimeUnit.SECONDS)
        }.build()
    }

    /** Api Retrofit service. */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                        .registerTypeAdapter(Stargazer::class.java, Stargazer.deserializer)
                        .create()))
                .client(httpClient)
                .build()
                .create(ApiService::class.java)
    }
}