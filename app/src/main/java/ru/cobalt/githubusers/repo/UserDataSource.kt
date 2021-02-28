package ru.cobalt.githubusers.repo

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.paging.ItemKeyedDataSource
import ru.cobalt.githubusers.model.User

class UserDataSource(
    private val lifecycleOwner: LifecycleOwner,
    private val userRepository: UserRepository
) : ItemKeyedDataSource<Long, User>() {
    override fun getKey(item: User): Long = item.id

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<User>
    ) = loadPage(0, params.requestedLoadSize, callback)

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<User>) =
        loadPage(params.key, params.requestedLoadSize, callback)

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<User>) =
        loadPage(params.key, params.requestedLoadSize, callback)

    private fun loadPage(lastId: Long, pageSize: Int, callback: LoadCallback<User>) {
        userRepository.load(lastId, pageSize)
        Log.d("wtf", "method thread: ${Thread.currentThread().name}")
        userRepository.users.observe(lifecycleOwner) { Log.d("wtf", "lambda thread: ${Thread.currentThread().name}"); callback.onResult(it) }
    }
}