package ru.cobalt.githubusers.di.activity

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ActivityModule {
    @Provides
    @ActivityScope
    fun provideCompositeDisposable() = CompositeDisposable()
}