package ru.cobalt.githubusers.di.app.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.interceptor.ErrorInterceptor
import javax.inject.Singleton

const val API_BASE_URL = "https://api.github.com/"

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideErrorInterceptor(gson: Gson): ErrorInterceptor =
        ErrorInterceptor(gson)

    @Provides
    @Singleton
    fun provideOkHttpClient(errorInterceptor: ErrorInterceptor): OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(errorInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

}