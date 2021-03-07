package ru.cobalt.githubusers.model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.cobalt.githubusers.repo.adapter.DiffUtilUserCallback
import ru.cobalt.githubusers.repo.adapter.OnQueryTextChangeListener
import ru.cobalt.githubusers.repo.adapter.PagedUserAdapter
import ru.cobalt.githubusers.repo.user.SearchDataSource
import ru.cobalt.githubusers.repo.user.UserDataSource
import ru.cobalt.githubusers.repo.user.UserRepository
import ru.cobalt.githubusers.utils.log
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UserViewModel(
    private val userRepository: UserRepository,
    private val dataSource: UserDataSource,
) : ViewModel() {

    private val listConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(50)
        .build()
    private var list = PagedList.Builder(dataSource, listConfig)
        .setFetchExecutor(Executors.newSingleThreadExecutor())
        .setNotifyExecutor { Handler(Looper.getMainLooper()).post(it) }
        .build()
    val adapter = PagedUserAdapter(DiffUtilUserCallback()).apply { submitList(list) }

    private lateinit var submitSearchQuery: PublishSubject<String>
    private lateinit var changeSearchQuery: PublishSubject<String>
    val queryListener by lazy { OnQueryTextChangeListener() }

    private val disposable by lazy { CompositeDisposable() }

    private fun showQueryResults(newList: List<User>, reassign: Boolean = false) {
        val searchDataSource = SearchDataSource(newList)
        val queryList = PagedList.Builder(searchDataSource, listConfig)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor { Handler(Looper.getMainLooper()).post(it) }
            .build()
        if (reassign) list = queryList
        adapter.submitList(queryList)
    }

    private fun startEmitters() {
        submitSearchQuery = PublishSubject.create()
        changeSearchQuery = PublishSubject.create()
        queryListener.submitEmitter = submitSearchQuery
        queryListener.changeEmitter = changeSearchQuery
    }

    private fun stopEmitters() {
        changeSearchQuery.onComplete()
        submitSearchQuery.onComplete()
    }

    fun setOnUserClickListener(onUserClickListener: (User) -> Unit) {
        adapter.onUserClickListener = onUserClickListener
    }

    fun startSearch() {
        startEmitters()
        disposable.add(changeSearchQuery
            .subscribeOn(Schedulers.io())
            .debounce(300, TimeUnit.MILLISECONDS)
            .concatMapSingle { q -> userRepository.search(q) }
            .subscribe(
                {
                    log("${it.size} GitHub users were found by user's query")
                    showQueryResults(it)
                },
                { log("Can not perform search operation: $it") }
            )
        )
    }

    fun stopSearch() {
        stopEmitters()
        // remove the query list and show original
        adapter.submitList(list)
        disposable.clear()
    }

    fun reload() {
        list = PagedList.Builder(dataSource, listConfig)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor { Handler(Looper.getMainLooper()).post(it) }
            .build()
        adapter.submitList(list)
    }

    fun deleteAll() {
        disposable.add(
            userRepository.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        showQueryResults(listOf(), true)
                        log("Database was cleared")
                    },
                    { log("Can't clear database: ${it.message}") }
                )
        )
    }

}