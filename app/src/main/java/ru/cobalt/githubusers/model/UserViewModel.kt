package ru.cobalt.githubusers.model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import ru.cobalt.githubusers.repo.DiffUtilUserCallback
import ru.cobalt.githubusers.repo.UserDataSource
import ru.cobalt.githubusers.repo.UserRepository
import ru.cobalt.githubusers.ui.PagedUserAdapter
import java.util.concurrent.Executors

class UserViewModel(
    private val userRepository: UserRepository,
    dataSource: UserDataSource,
) : ViewModel() {
    private val listConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(50)
        .build()
    private val list = PagedList.Builder(dataSource, listConfig)
        .setFetchExecutor(Executors.newSingleThreadExecutor())
        .setNotifyExecutor { Handler(Looper.getMainLooper()).post(it) }
        .build()
    val adapter = PagedUserAdapter(DiffUtilUserCallback()).apply { submitList(list) }

    fun deleteAll() = userRepository.deleteAll()
}