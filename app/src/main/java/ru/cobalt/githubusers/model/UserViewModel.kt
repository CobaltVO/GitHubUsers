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
import ru.cobalt.githubusers.utils.log
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserViewModel : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var adapter: UserAdapter

    private val compositeDisposable by lazy { CompositeDisposable() }

    private lateinit var submitSearchQuery: PublishSubject<String>
    private lateinit var changeSearchQuery: PublishSubject<String>
    val queryListener by lazy { OnQueryTextChangeListener() }

    val viewState: MutableLiveData<ViewState> by lazy { MutableLiveData() }
    var lastViewState: ViewState = ViewState.Empty

    var lastListOfUsers: List<User>? = null

    init {
        App.appComponent.inject(this)
    }

    fun initUsers() {
        compositeDisposable.add(
            userRepository.get(0, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.updateList(it)
                        updateState(ViewState.Loaded(it))
                        log("${it.size} initial users were loaded into list")
                    },
                    { log("Can't load initial users: ${it.message}") })
        )
    }

    fun loadUsers(fromId: Long) {
        compositeDisposable.add(
            userRepository.get(fromId, 100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.updateList(it)
                        updateState(ViewState.Loaded(it))
                        log("${it.size} new users were loaded into list")
                    },
                    { log("Can't load new users: ${it.message}") })
        )
    }

    fun deleteAllUsers() {
        compositeDisposable.add(
            userRepository.deleteAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        adapter.reloadList(listOf())
                        updateState(ViewState.Empty)
                        log("Database was cleared")
                    },
                    { log("Can't clear database: ${it.message}") }
                )
        )
    }

    fun setOnUserClickListener(onUserClickListener: (User) -> Unit) {
        adapter.onUserClickListener = onUserClickListener
    }

    fun startUsersSearch() {
        startEmitters()
        compositeDisposable.add(changeSearchQuery
            .debounce(300, TimeUnit.MILLISECONDS)
            .concatMapSingle { q -> userRepository.search(q) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    log("${it.size} GitHub users were found by user's query")
                    if (lastViewState !is ViewState.Searching)
                        lastListOfUsers = adapter.getCurrentList()

                    adapter.reloadList(it)
                    updateState(ViewState.Searching)
                },
                { log("Can not perform search operation: $it") }
            )
        )
    }

    fun stopUsersSearch() {
        stopEmitters()
        compositeDisposable.clear()
        lastListOfUsers?.let {
            updateState(ViewState.Loaded(it))
            adapter.reloadList(it)
        }
        lastListOfUsers = null
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

    private fun updateState(newViewState: ViewState) {
        lastViewState = newViewState
        viewState.postValue(newViewState)
    }
}