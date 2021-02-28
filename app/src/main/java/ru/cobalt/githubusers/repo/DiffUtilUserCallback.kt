package ru.cobalt.githubusers.repo

import androidx.recyclerview.widget.DiffUtil
import ru.cobalt.githubusers.model.User

class DiffUtilUserCallback: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}