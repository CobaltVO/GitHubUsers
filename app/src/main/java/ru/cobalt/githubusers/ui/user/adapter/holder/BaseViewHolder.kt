package ru.cobalt.githubusers.ui.user.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.cobalt.githubusers.model.User

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(user: User, clickListener: View.OnClickListener)
}