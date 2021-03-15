package ru.cobalt.githubusers.di.app

import dagger.Component
import ru.cobalt.githubusers.di.app.modules.AdapterModule
import ru.cobalt.githubusers.di.app.modules.AppModule
import ru.cobalt.githubusers.di.app.modules.DatabaseModule
import ru.cobalt.githubusers.di.app.modules.NetworkModule
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.adapter.UserAdapter
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        AdapterModule::class,
    ]
)
@Singleton
interface AppComponent {
    fun inject(userViewModel: UserViewModel)
    fun inject(userAdapter: UserAdapter)
}