package ru.cobalt.githubusers.di.app.modules

import dagger.Module
import dagger.Provides
import ru.cobalt.githubusers.ui.user.adapter.DiffUtilUserCallback
import ru.cobalt.githubusers.ui.user.adapter.UserAdapter
import javax.inject.Singleton

@Module
class AdapterModule {

    @Provides
    @Singleton
    fun provideDiffUtilUserCallback(): DiffUtilUserCallback = DiffUtilUserCallback()

    @Provides
    @Singleton
    fun provideUserAdapter(): UserAdapter = UserAdapter()

}