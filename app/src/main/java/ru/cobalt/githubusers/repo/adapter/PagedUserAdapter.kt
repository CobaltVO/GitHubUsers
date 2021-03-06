package ru.cobalt.githubusers.repo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.model.User

class PagedUserAdapter(callback: DiffUtil.ItemCallback<User>) :
    PagedListAdapter<User, UserViewHolder>(callback) {

    var onUserClickListener: (User) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { user -> holder.bind(user) { onUserClickListener.invoke(user) } }
    }

}