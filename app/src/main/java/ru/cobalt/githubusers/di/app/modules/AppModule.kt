package ru.cobalt.githubusers.di.app.modules

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.domain.user.UserInteractor
import ru.cobalt.githubusers.repo.room.UserDao
import javax.inject.Singleton

@Module
class AppModule(var appContext: Context) {

    @Provides
    @Singleton
    fun provideAppContext(): Context = appContext

    @Provides
    @Singleton
    fun provideUserRepository(appContext: Context, userApi: UserApi, userDao: UserDao) =
        UserInteractor(userApi, userDao)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}