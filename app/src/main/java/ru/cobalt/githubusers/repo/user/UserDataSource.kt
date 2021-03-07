package ru.cobalt.githubusers.repo.user

import android.content.Context
import androidx.paging.ItemKeyedDataSource
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.utils.log

class UserDataSource(
    private val appContext: Context,
    private val userRepository: UserRepository,
) : ItemKeyedDataSource<Long, User>() {

    override fun getKey(item: User): Long = item.id

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<User>) {
        (appContext as App).activityComponent?.compositeDisposable?.add(
            userRepository.get(0, params.requestedLoadSize)
                .subscribe(
                    {
                        callback.onResult(it)
                        log("${it.size} initial users were loaded into list")
                    },
                    { log("Can't load initial users: ${it.message}") })
        )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<User>) {
        (appContext as App).activityComponent?.compositeDisposable?.add(
            userRepository.get(params.key, params.requestedLoadSize)
                .subscribe(
                    {
                        callback.onResult(it)
                        log("${it.size} new users were loaded into list")
                    },
                    { log("Can't load new users: ${it.message}") })
        )
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<User>) {}

}