package ru.cobalt.githubusers.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_activity.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.OnMenuStateChangeListener
import javax.inject.Inject

const val SEARCH_QUERY = "SEARCH_QUERY"

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    @Inject
    lateinit var userViewModel: UserViewModel

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private var searchView: SearchView? = null
    private var searchQuery: CharSequence = ""

    private fun setupSearchMenu(searchMenu: MenuItem) {
        searchMenu.setOnActionExpandListener(OnMenuStateChangeListener(userViewModel))
        searchView = (searchMenu.actionView as SearchView).apply {
            isIconified = false
            queryHint = getString(R.string.search_hint)
            setOnQueryTextListener(userViewModel.queryListener)
            if (searchQuery.isNotEmpty()) {
                searchMenu.expandActionView()
                setQuery(searchQuery, false)
                searchQuery = ""
            }
        }
    }

    private fun openUserProfile(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val resolveInfo = packageManager
            .resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo != null) startActivity(browserIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App)
            .createActivityComponent()
            .inject(this@MainActivity)
        listOfUsers.adapter = userViewModel.adapter

        userViewModel.setOnUserClickListener { openUserProfile(it.userPageUrl) }

        searchQuery = savedInstanceState?.getCharSequence(SEARCH_QUERY) ?: ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.let { setupSearchMenu(it.findItem(R.id.menu_item_search)) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_all -> userViewModel.deleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchView?.let { outState.putCharSequence(SEARCH_QUERY, it.query) }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        (applicationContext as App).deleteActivityComponent()
    }
}