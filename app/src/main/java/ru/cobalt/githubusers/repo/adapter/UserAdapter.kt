package ru.cobalt.githubusers.repo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.User
import javax.inject.Inject

class UserAdapter : RecyclerView.Adapter<UserViewHolder>() {

    @Inject
    lateinit var callback: DiffUtilUserCallback

    private val differ: AsyncListDiffer<User> by lazy { AsyncListDiffer(this, callback) }

    var onUserClickListener: (User) -> Unit = {}

    init {
        App.appComponent.inject(this)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.bind(user) { onUserClickListener.invoke(user) }
    }

    fun getCurrentList(): MutableList<User> = differ.currentList
    fun updateList(newSubList: List<User>) = differ.submitList(differ.currentList + newSubList)
    fun reloadList(newList: List<User>) = differ.submitList(newList)

    fun getUser(listPosition: Int): User? {
        return try {
            differ.currentList[listPosition]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

}