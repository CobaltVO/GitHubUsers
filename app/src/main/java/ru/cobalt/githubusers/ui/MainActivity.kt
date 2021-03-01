package ru.cobalt.githubusers.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.repo.UserRepository
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private lateinit var viewModel: UserViewModel

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App).appComponent.inject(this)

        viewModel = UserViewModel(userRepository)
        val adapter = UserAdapter()
        listOfUsers.adapter = adapter
        viewModel.users.observe(this) {
            adapter.update(it)
            Toast.makeText(this, "Updated: ${viewModel.users.value?.size}", Toast.LENGTH_SHORT)
                .show()
        }
        viewModel.update()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_all -> viewModel.deleteAll()
        }
        return super.onOptionsItemSelected(item)
    }
}