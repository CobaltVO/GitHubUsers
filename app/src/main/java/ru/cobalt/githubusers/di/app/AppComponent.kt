package ru.cobalt.githubusers.di.app

import dagger.Component
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.UserRepository
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    val userRepository: UserRepository
    val userViewModel: UserViewModel
}