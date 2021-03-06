package ru.cobalt.githubusers.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_activity.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.OnMenuStateChangeListener
import ru.cobalt.githubusers.utils.log
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    @Inject
    lateinit var userViewModel: UserViewModel

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App)
            .createActivityComponent()
            .inject(this@MainActivity)
        listOfUsers.adapter = userViewModel.adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        menu?.let {
            val searchMenu = it.findItem(R.id.menu_item_search)
            searchMenu.setOnActionExpandListener(OnMenuStateChangeListener(userViewModel))
            (searchMenu.actionView as SearchView).apply {
                isIconified = false
                queryHint = getString(R.string.search_hint)
                setOnQueryTextListener(userViewModel.queryListener)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_all -> compositeDisposable.add(
                userViewModel.deleteAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { log("Database was cleared") },
                        { log("Can't clear database: ${it.message}") })
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("disposing")
        compositeDisposable.dispose()
        (applicationContext as App).deleteActivityComponent()
    }
}