package ru.cobalt.githubusers.ui.user.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.ui.user.adapter.holder.BaseViewHolder
import ru.cobalt.githubusers.ui.user.adapter.holder.LoaderViewHolder
import ru.cobalt.githubusers.ui.user.adapter.holder.UserViewHolder

const val TYPE_USER = 0
const val TYPE_LOADER = 1

class UserAdapter : ListAdapter<User, BaseViewHolder>(DiffUtilUserCallback) {

    var onUserClickListener: (User) -> Unit = {}
    var isLoaderActivated: Boolean = true

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size - 1 && isLoaderActivated) TYPE_LOADER
        else TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            TYPE_USER -> UserViewHolder.getInstance(parent)
            else -> LoaderViewHolder.getInstance(parent)
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val user = currentList[position]
        holder.bind(user) { onUserClickListener.invoke(user) }
    }

    fun getUser(listPosition: Int): User? {
        return try {
            currentList[listPosition]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    private object DiffUtilUserCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}