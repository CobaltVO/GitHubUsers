package ru.cobalt.githubusers.model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.cobalt.githubusers.repo.DiffUtilUserCallback
import ru.cobalt.githubusers.repo.OnQueryTextChangeListener
import ru.cobalt.githubusers.repo.UserDataSource
import ru.cobalt.githubusers.repo.UserRepository
import ru.cobalt.githubusers.ui.PagedUserAdapter
import ru.cobalt.githubusers.utils.log
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

    private val submitSearchQuery = PublishSubject.create<String>()
    private val changeSearchQuery = PublishSubject.create<String>()
    val queryListener = OnQueryTextChangeListener(submitSearchQuery, changeSearchQuery)
    private var disposable: Disposable? = null

    fun startSearch() {
        disposable = changeSearchQuery
            .subscribeOn(Schedulers.io())
            .debounce(300, TimeUnit.MILLISECONDS)
            .concatMapSingle { q -> userRepository.search(q) }
            .subscribe({
                log("success!!: ${it.size}")
            }, {
                log("Can not perform search operation: $it")
            })
    }

    fun stopSearch() = disposable?.dispose()

    fun deleteAll() = userRepository.deleteAll()
}