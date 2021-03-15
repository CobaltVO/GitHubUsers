package ru.cobalt.githubusers.di.app.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.repo.room.UserDao
import ru.cobalt.githubusers.repo.user.UserRepository
import javax.inject.Singleton

@Module
class AppModule(var appContext: Context) {

    @Provides
    @Singleton
    fun provideAppContext(): Context = appContext

    @Provides
    @Singleton
    fun provideUserRepository(appContext: Context, userApi: UserApi, userDao: UserDao) =
        UserRepository(userApi, userDao)

}