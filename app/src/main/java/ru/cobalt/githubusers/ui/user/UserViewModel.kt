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
import ru.cobalt.githubusers.ui.user.listener.OnQueryTextChangeListener
import ru.cobalt.githubusers.utils.log
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserViewModel : ViewModel() {

    @Inject
    lateinit var userInteractor: UserInteractor

    @Inject
    lateinit var errorInterceptor: ErrorInterceptor

    private val compositeDisposable = CompositeDisposable()

    private lateinit var changeSearchQuery: PublishSubject<String>
    val queryListener = OnQueryTextChangeListener()

    val viewState: MutableLiveData<ViewState> = MutableLiveData()

    init {
        App.appComponent.inject(this)
        errorInterceptor.errorListener =
            { updateState(ApiLimitError(getCurrentUsers(), it.message, it.docUrl)) }
    }

    fun loadUsers(fromId: Long = 0, count: Int = 100) {
        if (fromId == 0L) updateState(Initialization)
        else updateState(Loading(getCurrentUsers()))

        compositeDisposable.add(
            userInteractor.get(fromId, count)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updateState(Loaded(getCurrentUsers() + it))
                        log("${it.size} users were loaded into list")
                    },
                    {
                        showNetworkError(
                            getCurrentUsers().lastOrNull()?.id ?: 0,
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
                        updateState(Cleared)
                        log("Database was cleared")
                    },
                    {
                        updateState(
                            DatabaseError(getCurrentUsers(), "Can't clear database: $it")
                        )
                    }
                )
        )
    }

    fun startUsersSearch() {
        startEmitters()
        compositeDisposable.add(changeSearchQuery
            .debounce(300, TimeUnit.MILLISECONDS)
            .doOnNext { updateState(Searching(getCurrentUsers())) }
            .concatMapMaybe { q -> userInteractor.search(q) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    updateState(Searched(getCurrentUsers(), it))
                    log("${it.size} GitHub users were found by user's query")
                },
                {
                    updateState(SearchError(getCurrentUsers(), "Unable to search users: $it"))
                }
            )
        )
    }

    fun stopUsersSearch() {
        stopEmitters()
        compositeDisposable.clear()
        updateState(Reloading(getCurrentUsers()))
    }

    fun reloadUsers(users: List<User>) {
        updateState(Loaded(users))
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

    private fun getCurrentUsers(): List<User> = viewState.value?.users ?: listOf()

    private fun showNetworkError(lastUserId: Long, errorMessage: String) {
        val state = viewState.value
        if (state != null && state !is ApiLimitError)
            updateState(NetworkError(getCurrentUsers(), errorMessage, lastUserId))
    }
}