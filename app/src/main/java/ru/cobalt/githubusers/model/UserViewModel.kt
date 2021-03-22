package ru.cobalt.githubusers.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.repo.adapter.UserAdapter
import ru.cobalt.githubusers.repo.user.UserRepository
import ru.cobalt.githubusers.ui.ViewState
import ru.cobalt.githubusers.ui.ViewState.*
import ru.cobalt.githubusers.ui.listener.OnQueryTextChangeListener
import ru.cobalt.githubusers.utils.log
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserViewModel : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var adapter: UserAdapter

    private val compositeDisposable by lazy { CompositeDisposable() }

    private lateinit var changeSearchQuery: PublishSubject<String>
    val queryListener by lazy { OnQueryTextChangeListener() }

    val viewState: MutableLiveData<ViewState> by lazy { MutableLiveData() }

    init {
        App.appComponent.inject(this)
    }

    fun setOnUserClickListener(onUserClickListener: (User) -> Unit) {
        adapter.onUserClickListener = onUserClickListener
    }

    fun initUsers() {
        updateState(Initialization)
        compositeDisposable.add(
            userRepository.get(0, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.reloadList(it) { updateState(Loaded) }
                        log("${it.size} initial users were loaded into list")
                    },
                    {
                        updateState(
                            NetworkError(0, "Can't load initial users: $it")
                        )
                    })
        )
    }

    fun loadUsers(fromId: Long) {
        updateState(Loading)
        compositeDisposable.add(
            userRepository.get(fromId, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.updateList(it) { updateState(Loaded) }
                        log("${it.size} new users were loaded into list")
                    },
                    {
                        updateState(
                            NetworkError(
                                adapter.getLastUser()?.id ?: 0,
                                "Can't load new users: $it"
                            )
                        )
                    })
        )
    }

    fun deleteAllUsers() {
        compositeDisposable.add(
            userRepository.deleteAll()
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
            .concatMapSingle { q -> userRepository.search(q) }
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

}