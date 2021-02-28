package ru.cobalt.githubusers

import android.app.Application
import android.content.Context
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.repo.UserRepository
import ru.cobalt.githubusers.room.UserDatabase
import ru.cobalt.githubusers.ui.UserAdapter

class App : Application() {

    companion object {
        lateinit var appContext: Context
        lateinit var userDb: UserDatabase
        lateinit var retrofit: Retrofit
        lateinit var userApi: UserApi
        lateinit var userRepository: UserRepository
        lateinit var userAdapter: UserAdapter
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        userDb = Room
            .databaseBuilder(appContext, UserDatabase::class.java, "users")
            .build()

        retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        userApi = retrofit.create(UserApi::class.java)
        userRepository = UserRepository(userApi, userDb.userDao)
        userAdapter = UserAdapter()
    }

}