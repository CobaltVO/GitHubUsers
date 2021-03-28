package ru.cobalt.githubusers.di.app

import android.content.Context
import dagger.Component
import ru.cobalt.githubusers.di.app.modules.AdapterModule
import ru.cobalt.githubusers.di.app.modules.AppModule
import ru.cobalt.githubusers.di.app.modules.DatabaseModule
import ru.cobalt.githubusers.di.app.modules.NetworkModule
import ru.cobalt.githubusers.ui.user.UserViewModel
import ru.cobalt.githubusers.ui.user.adapter.UserAdapter
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
    val context: Context
    val userAdapter: UserAdapter
    fun inject(userViewModel: UserViewModel)
}