package ru.cobalt.githubusers.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import ru.cobalt.githubusers.App
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.model.UserViewModel

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = UserViewModel(App.userRepository)
        listOfUsers.adapter = App.userAdapter
        viewModel.users.observe(this) { App.userAdapter.update(it) }

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