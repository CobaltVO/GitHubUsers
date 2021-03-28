package ru.cobalt.githubusers.ui.user.adapter.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.model.User

class LoaderViewHolder private constructor(itemView: View) : BaseViewHolder(itemView) {

    companion object {
        fun getInstance(parent: ViewGroup): LoaderViewHolder =
            LoaderViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_loader, parent, false)
            )
    }

    override fun bind(user: User, clickListener: View.OnClickListener) {
    }
}