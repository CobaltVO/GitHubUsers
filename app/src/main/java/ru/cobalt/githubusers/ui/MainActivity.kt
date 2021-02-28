package ru.cobalt.githubusers.ui

import android.os.Bundle
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
        viewModel.users.observe(this) { adapter.update(it) }

//        val dataSource = UserDataSource(this, App.userRepository)
//        val config = PagedList.Config.Builder()
//            .setEnablePlaceholders(false)
//            .setPageSize(10)
//            .build()
//        val list = PagedList.Builder(dataSource, config)
//            .setFetchExecutor(Executors.newSingleThreadExecutor())
//            .setNotifyExecutor { Handler(Looper.getMainLooper()).post(it) }
//            .build()
//        listOfUsers.adapter = UserAdapter(DiffUtilUserCallback()).apply { submitList(list) }

    }

}