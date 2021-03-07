package ru.cobalt.githubusers.di.app.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.cobalt.githubusers.repo.room.UserDao
import ru.cobalt.githubusers.repo.room.UserDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(context: Context): UserDatabase =
        Room
            .databaseBuilder(context, UserDatabase::class.java, "users")
            .build()

    @Provides
    @Singleton
    fun provideUserDao(userDatabase: UserDatabase): UserDao = userDatabase.userDao

}