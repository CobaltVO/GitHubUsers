package ru.cobalt.githubusers.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.domain.user.UserInteractor
import ru.cobalt.githubusers.interceptor.ErrorInterceptor
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.ui.user.ViewState.*
import ru.cobalt.githubusers.ui.user.adapter.UserAdapter
import ru.cobalt.githubusers.ui.user.listener.OnQueryTextChangeListener
import ru.cobalt.githubusers.utils.log
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserViewModel : ViewModel() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var adapter: UserAdapter

    @Inject
    lateinit var errorInterceptor: ErrorInterceptor

    private val compositeDisposable = CompositeDisposable()

    private lateinit var changeSearchQuery: PublishSubject<String>
    val queryListener = OnQueryTextChangeListener()

    val viewState: MutableLiveData<ViewState> = MutableLiveData()

    init {
        App.appComponent.inject(this)
        errorInterceptor.errorListener = { updateState(ApiLimitError(it.docUrl, it.message)) }
    }

    fun setOnUserClickListener(onUserClickListener: (User) -> Unit) {
        adapter.onUserClickListener = onUserClickListener
    }

    fun initUsers() {
        updateState(Initialization)
        compositeDisposable.add(
            userInteractor.get()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.reloadList(it) { updateState(Loaded) }
                        log("${it.size} initial users were loaded into list")
                    },
                    {
                        showNetworkError(0, "Can't load initial users: $it")
                    })
        )
    }

    fun loadUsers(fromId: Long) {
        updateState(Loading)
        compositeDisposable.add(
            userInteractor.get(fromId, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.updateList(it) { updateState(Loaded) }
                        log("${it.size} new users were loaded into list")
                    },
                    {
                        showNetworkError(
                            adapter.getLastUser()?.id ?: 0,
                            "Can't load new users: $it"
                        )
                    })
        )
    }

    fun deleteAllUsers() {
        compositeDisposable.add(
            userInteractor.deleteAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        adapter.reloadList(listOf()) { updateState(Empty) }
                        log("Database was cleared")
                    },
                    {
                        updateState(
                            DatabaseError("Can't clear database: $it")
                        )
                    }
                )
        )
    }

    fun startUsersSearch() {
        startEmitters()
        adapter.saveList()
        compositeDisposable.add(changeSearchQuery
            .debounce(300, TimeUnit.MILLISECONDS)
            .doOnNext { updateState(Searching) }
            .concatMapMaybe { q -> userInteractor.search(q) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    adapter.reloadList(it) { updateState(Searched) }
                    log("${it.size} GitHub users were found by user's query")
                },
                {
                    updateState(SearchError("Unable to search users: $it"))
                }
            )
        )
    }

    fun stopUsersSearch() {
        stopEmitters()
        compositeDisposable.clear()
        updateState(Reloading)
        adapter.reloadList(adapter.restoreList()) { updateState(Loaded) }
    }

    fun showUsersLoader() {
        adapter.isLoaderActivated = true
    }

    fun hideUsersLoader() {
        adapter.isLoaderActivated = false
    }

    private fun startEmitters() {
        changeSearchQuery = PublishSubject.create()
        queryListener.changeEmitter = changeSearchQuery
    }

    private fun stopEmitters() {
        changeSearchQuery.onComplete()
    }

    private fun updateState(newViewState: ViewState) {
        viewState.postValue(newViewState)
    }

    private fun showNetworkError(lastUserId: Long, errorMessage: String) {
        val state = viewState.value
        if (state != null && state !is ApiLimitError)
            updateState(NetworkError(lastUserId, errorMessage))
    }
}