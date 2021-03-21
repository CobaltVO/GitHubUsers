package ru.cobalt.githubusers.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.repo.adapter.OnQueryTextChangeListener
import ru.cobalt.githubusers.repo.adapter.UserAdapter
import ru.cobalt.githubusers.repo.user.UserRepository
import ru.cobalt.githubusers.ui.ViewState
import ru.cobalt.githubusers.ui.ViewState.*
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
    private var currentViewState: ViewState = Empty

    private var currentListOfUsers: List<User> = listOf()

    init {
        App.appComponent.inject(this)
    }

    fun setOnUserClickListener(onUserClickListener: (User) -> Unit) {
        adapter.onUserClickListener = onUserClickListener
    }

    fun initUsers() {
        updateState(Loading)
        compositeDisposable.add(
            userRepository.get(0, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        currentListOfUsers = adapter.updateList(it) { updateState(Loaded(it)) }
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
        compositeDisposable.add(
            userRepository.get(fromId, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        currentListOfUsers = adapter.updateList(it) { updateState(Loaded(it)) }
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
                    { updateState(DatabaseError("Can't clear database: $it")) }
                )
        )
    }

    fun startUsersSearch() {
        startEmitters()
        compositeDisposable.add(changeSearchQuery
            .debounce(300, TimeUnit.MILLISECONDS)
            .doOnNext { updateState(Searching) }
            .concatMapSingle { q -> userRepository.search(q) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    adapter.reloadList(it) { updateState(Searched(it)) }
                    log("${it.size} GitHub users were found by user's query")
                },
                { updateState(SearchError("Unable to search users: $it")) }
            )
        )
    }

    fun stopUsersSearch() {
        stopEmitters()
        compositeDisposable.clear()
        adapter.reloadList(currentListOfUsers)
        updateState(Loaded(currentListOfUsers))
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
        currentViewState = newViewState
        viewState.postValue(newViewState)
    }

}