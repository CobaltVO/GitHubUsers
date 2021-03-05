package ru.cobalt.githubusers.di.activity

import dagger.Component
import ru.cobalt.githubusers.di.app.AppComponent

@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
@ActivityScope
interface ActivityComponent {

}