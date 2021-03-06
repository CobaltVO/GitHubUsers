package ru.cobalt.githubusers.repo

import androidx.paging.ItemKeyedDataSource
import ru.cobalt.githubusers.model.User

class SearchDataSource(private val list: List<User>) : ItemKeyedDataSource<Long, User>() {

    override fun getKey(item: User): Long = item.id

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<User>) =
        callback.onResult(list)

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<User>) {}
    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<User>) {}

}