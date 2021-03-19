package ru.cobalt.githubusers.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.search_progress_bar.view.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.adapter.RecyclerViewScrollListener
import ru.cobalt.githubusers.ui.ViewState.*
import ru.cobalt.githubusers.ui.utils.SearchViews
import ru.cobalt.githubusers.ui.utils.hideSearchLoader
import ru.cobalt.githubusers.ui.utils.showSearchLoader
import ru.cobalt.githubusers.ui.utils.snack
import ru.cobalt.githubusers.utils.log
import javax.inject.Inject

private const val SEARCH_QUERY = "SEARCH_QUERY"

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

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

        initViewModel(savedInstanceState)
        listOfUsers.adapter = userViewModel.adapter

        recyclerViewScrollListener = RecyclerViewScrollListener(listOfUsers.layoutManager!!) {
            log("threshold reached! id: $it")
            val userId = userViewModel.adapter.getUser(it)?.id ?: return@RecyclerViewScrollListener
            log("userId: $userId")
            userViewModel.loadUsers(userId)
        }
        listOfUsers.addOnScrollListener(recyclerViewScrollListener)

        searchQuery = savedInstanceState?.getCharSequence(SEARCH_QUERY) ?: ""

        setSupportActionBar(toolbar)
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
            R.id.menu_item_delete_all -> {
                userViewModel.deleteAllUsers()
                mainActivityContainer.snack(
                    R.string.delete_all_users_successful_message,
                    R.string.delete_all_users_successful_action_button
                ) {
                    log("Users reloading was started")
                    userViewModel.initUsers()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val menu = searchMenu ?: return
        val view = searchView ?: return
        if (menu.isActionViewExpanded) outState.putCharSequence(SEARCH_QUERY, view.query)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        (applicationContext as App).deleteActivityComponent()
    }

    private fun setupSearchMenu() {
        val menu = searchMenu ?: return
        menu.setOnActionExpandListener(OnMenuStateChangeListener(userViewModel))

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
        val resolveInfo = packageManager
            .resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo != null) startActivity(browserIntent)
    }

    private fun render(state: ViewState) {
        log("State: $state")
        when (state) {
            is Empty -> {
                progressBar.visibility = View.GONE
                listOfUsers.visibility = View.INVISIBLE
            }
            is Loading -> {
                progressBar.visibility = View.VISIBLE
                listOfUsers.visibility = View.INVISIBLE
            }
            is Loaded -> {
                progressBar.visibility = View.GONE
                listOfUsers.visibility = View.VISIBLE

                recyclerViewScrollListener.isActivated = true
                recyclerViewScrollListener.onDataLoaded()
            }
            is Searching -> {
                recyclerViewScrollListener.isActivated = false
                showSearchLoader()
            }
            is Searched -> {
                recyclerViewScrollListener.isActivated = false
                hideSearchLoader()
            }
        }
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        userViewModel.setOnUserClickListener { openUserProfile(it.userPageUrl) }
        userViewModel.viewState.observe(this) { if (it != null) render(it) }

        if (savedInstanceState == null) userViewModel.initUsers()
    }
}