package ru.cobalt.githubusers.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_activity.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.UserRepository
import ru.cobalt.githubusers.utils.log
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private lateinit var viewModel: UserViewModel

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App).appComponent.inject(this)

        viewModel = UserViewModel(userRepository)
        listOfUsers.adapter = viewModel.adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_all -> viewModel.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { log("Database was cleared") },
                    { log("Can't clear database: ${it.message}") })
        }
        return super.onOptionsItemSelected(item)
    }
}