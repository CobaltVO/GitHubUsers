package ru.cobalt.githubusers.di.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.UserDataSource
import ru.cobalt.githubusers.repo.UserRepository
import ru.cobalt.githubusers.repo.room.UserDao
import ru.cobalt.githubusers.repo.room.UserDatabase
import javax.inject.Singleton

@Module
class AppModule(var appContext: Context) {

    @Provides
    @Singleton
    fun provideAppContext(): Context = appContext

    @Provides
    @Singleton
    fun provideUserDatabase(context: Context): UserDatabase =
        Room
            .databaseBuilder(context, UserDatabase::class.java, "users")
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideUserDao(userDatabase: UserDatabase): UserDao = userDatabase.userDao

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi, userDao: UserDao) = UserRepository(userApi, userDao)

    @Provides
    @Singleton
    fun provideUserDataSource(userRepository: UserRepository) = UserDataSource(userRepository)

    @Provides
    @Singleton
    fun provideUserViewModel(
        userRepository: UserRepository,
        dataSource: UserDataSource
    ): UserViewModel = UserViewModel(userRepository, dataSource)
}