package ru.cobalt.githubusers.ui.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.search_progress_bar.view.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.utils.toUserViewType
import ru.cobalt.githubusers.ui.user.ViewState.*
import ru.cobalt.githubusers.ui.user.adapter.UserAdapter
import ru.cobalt.githubusers.ui.user.adapter.ViewType
import ru.cobalt.githubusers.ui.user.listener.OnMenuStateChangeListener
import ru.cobalt.githubusers.ui.user.listener.RecyclerViewScrollListener
import ru.cobalt.githubusers.ui.user.utils.SearchViews
import ru.cobalt.githubusers.ui.user.utils.hideSearchLoader
import ru.cobalt.githubusers.ui.user.utils.showSearchLoader
import ru.cobalt.githubusers.ui.user.utils.snack
import ru.cobalt.githubusers.utils.log
import ru.cobalt.githubusers.utils.logError
import javax.inject.Inject

private const val SEARCH_QUERY = "SEARCH_QUERY"

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var userAdapter: UserAdapter

    private lateinit var userViewModel: UserViewModel
    private lateinit var recyclerViewScrollListener: RecyclerViewScrollListener

    private var searchView: SearchView? = null
    private var searchMenu: MenuItem? = null
    private var searchQuery: CharSequence = ""

    private var searchIconViews: SearchViews? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App)
            .createActivityComponent()
            .inject(this@MainActivity)

        setSupportActionBar(toolbar)

        initViewModel(savedInstanceState)
        initRecyclerView()

        searchQuery = savedInstanceState?.getCharSequence(SEARCH_QUERY) ?: ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.let {
            searchMenu = it.findItem(R.id.menu_item_search)
            setupSearchMenu()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_all -> mainActivityContainer.snack(
                R.string.delete_all_users_confirmation_message,
                R.string.delete_all_users_confirmation_action_button,
                Snackbar.LENGTH_LONG
            ) { userViewModel.deleteAllUsers() }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val menu = searchMenu ?: return
        val view = searchView ?: return
        if (menu.isActionViewExpanded) {
            outState.putCharSequence(SEARCH_QUERY, view.query)
            userViewModel.stopUsersSearch()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        (applicationContext as App).deleteActivityComponent()
    }

    private fun render(state: ViewState) {
        log("State: $state")
        when (state) {
            is Initialization -> {
                progressBar.visibility = View.VISIBLE
                listOfUsers.visibility = View.INVISIBLE

                recyclerViewScrollListener.isActivated = false
            }
            is Cleared -> {
                userAdapter.submitList(listOf()) {
                    progressBar.visibility = View.GONE
                    listOfUsers.visibility = View.INVISIBLE

                    mainActivityContainer.snack(
                        R.string.delete_all_users_successful_message,
                        R.string.delete_all_users_successful_action_button
                    ) {
                        log("Users reloading was started")
                        userViewModel.loadUsers()
                    }
                }
            }
            is Loading -> {
                userAdapter.submitList(state.users.toUserViewType() + ViewType.LoaderViewType)
            }
            is Reloading -> {
                listReloadingProgressBar.visibility = View.VISIBLE
                userViewModel.reloadUsers(state.users)
            }
            is Loaded -> {
                userAdapter.submitList(state.users.toUserViewType()) {
                    progressBar.visibility = View.GONE
                    listReloadingProgressBar.visibility = View.GONE
                    listOfUsers.visibility = View.VISIBLE

                    recyclerViewScrollListener.isActivated = true
                    recyclerViewScrollListener.onDataLoaded()

                    hideSearchLoader()
                }
            }
            is Searching -> {
                recyclerViewScrollListener.isActivated = false
                showSearchLoader()
            }
            is Searched -> {
                recyclerViewScrollListener.isActivated = false
                userAdapter.submitList(state.searchedUsers.toUserViewType()) { hideSearchLoader() }
            }
            is NetworkError -> {
                logError("network: ${state.errorMessage}")
                userAdapter.submitList(state.users.toUserViewType())
                progressBar.visibility = View.GONE

                mainActivityContainer.snack(
                    R.string.load_users_error_message,
                    R.string.load_users_error_action_button
                ) {
                    log("Trying to load users again")
                    userViewModel.loadUsers(state.lastUserId)
                }
            }
            is ApiLimitError -> {
                logError("api limitation: ${state.errorMessage}, docUrl=${state.docUrl}")
                userAdapter.submitList(state.users.toUserViewType())
                progressBar.visibility = View.GONE

                mainActivityContainer.snack(
                    R.string.load_users_api_limit_error_message,
                    R.string.load_users_api_limit_error_action_button
                ) {
                    log("Going to GitHub to get details about API limit")
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(state.docUrl)))
                }
            }
            is DatabaseError -> {
                logError("database: ${state.errorMessage}")
                mainActivityContainer.snack(R.string.delete_all_users_error_message)
            }
            is SearchError -> {
                logError("search: ${state.errorMessage}")
                hideSearchLoader()
                mainActivityContainer.snack(R.string.search_user_error_message)
            }
        }
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        userAdapter.onViewClickListener = {
            if (it is ViewType.UserViewType) openUserProfile(it.user.userPageUrl)
        }
        userViewModel.viewState.observe(this) { if (it != null) render(it) }

        if (savedInstanceState == null) userViewModel.loadUsers()
    }

    private fun initRecyclerView() {
        listOfUsers.adapter = userAdapter

        recyclerViewScrollListener =
            RecyclerViewScrollListener(listOfUsers.layoutManager!!) { lastPosition ->
                val viewType =
                    userAdapter.getViewType(lastPosition) ?: return@RecyclerViewScrollListener
                if (viewType is ViewType.UserViewType)
                    userViewModel.loadUsers(viewType.user.id)
            }
        listOfUsers.addOnScrollListener(recyclerViewScrollListener)
    }

    private fun setupSearchMenu() {
        val menu = searchMenu ?: return
        menu.setOnActionExpandListener(OnMenuStateChangeListener(this, userViewModel))

        searchView = (menu.actionView as SearchView).apply {
            isIconified = false
            queryHint = getString(R.string.search_hint)
            setOnQueryTextListener(userViewModel.queryListener)
            if (searchQuery.isNotEmpty()) {
                menu.expandActionView()
                setQuery(searchQuery, false)
                searchQuery = ""
            }
        }
    }

    private fun showSearchLoader() {
        if (searchIconViews == null) {
            searchIconViews = searchView?.showSearchLoader(
                this,
                R.layout.search_progress_bar
            )
        }
    }

    private fun hideSearchLoader() {
        searchView?.hideSearchLoader(searchIconViews ?: return)
        searchIconViews = null
    }

    private fun openUserProfile(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        makeTouchVibrationFeedback()
        startActivity(browserIntent)
    }

    private fun makeTouchVibrationFeedback() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50, 255))
        }
    }
}