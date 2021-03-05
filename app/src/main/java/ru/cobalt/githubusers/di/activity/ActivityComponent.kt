package ru.cobalt.githubusers.di.activity

import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import ru.cobalt.githubusers.di.app.AppComponent
import ru.cobalt.githubusers.ui.MainActivity

@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
@ActivityScope
interface ActivityComponent {
    val compositeDisposable: CompositeDisposable
    fun inject(mainActivity: MainActivity)
}