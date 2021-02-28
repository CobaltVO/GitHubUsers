package ru.cobalt.githubusers.di.app

import dagger.Component
import ru.cobalt.githubusers.ui.MainActivity
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}