package ru.cobalt.githubusers.di.app

import dagger.Component
import ru.cobalt.githubusers.di.app.modules.AppModule
import ru.cobalt.githubusers.di.app.modules.DatabaseModule
import ru.cobalt.githubusers.di.app.modules.NetworkModule
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.user.UserRepository
import ru.cobalt.githubusers.ui.OnMenuStateChangeListener
import javax.inject.Singleton

@Component(modules = [AppModule::class, DatabaseModule::class, NetworkModule::class])
@Singleton
interface AppComponent {
    val userRepository: UserRepository
    val userViewModel: UserViewModel
    val onMenuStateChangeListener: OnMenuStateChangeListener
}