package ru.cobalt.githubusers.di.app

import android.app.Application
import ru.cobalt.githubusers.di.activity.ActivityComponent
import ru.cobalt.githubusers.di.activity.DaggerActivityComponent
import ru.cobalt.githubusers.di.app.modules.AppModule

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    private var activityComponent: ActivityComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    fun createActivityComponent(): ActivityComponent {
        activityComponent = DaggerActivityComponent.builder()
            .appComponent(appComponent)
            .build()
        return activityComponent!!
    }

    fun deleteActivityComponent() {
        activityComponent = null
    }
}