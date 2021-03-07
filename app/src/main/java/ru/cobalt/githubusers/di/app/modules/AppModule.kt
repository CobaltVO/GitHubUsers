package ru.cobalt.githubusers.di.app.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.OnMenuStateChangeListener
import ru.cobalt.githubusers.repo.UserDataSource
import ru.cobalt.githubusers.repo.UserRepository
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
        UserRepository(appContext, userApi, userDao)

    @Provides
    @Singleton
    fun provideUserDataSource(appContext: Context, userRepository: UserRepository) =
        UserDataSource(appContext, userRepository)

    @Provides
    @Singleton
    fun provideUserViewModel(
        userRepository: UserRepository,
        dataSource: UserDataSource
    ): UserViewModel = UserViewModel(userRepository, dataSource)

    @Provides
    @Singleton
    fun provideOnMenuStateChangeListener(userViewModel: UserViewModel): OnMenuStateChangeListener =
        OnMenuStateChangeListener(userViewModel)
}