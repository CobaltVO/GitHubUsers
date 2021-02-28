package ru.cobalt.githubusers.di.app

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        lateinit var appContext: Context
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appContext = this
        appComponent = DaggerAppComponent.builder().appModule(AppModule(appContext)).build()
    }

}