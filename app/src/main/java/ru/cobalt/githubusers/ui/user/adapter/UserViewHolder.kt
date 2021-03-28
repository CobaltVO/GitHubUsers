package ru.cobalt.githubusers.ui.user.adapter

import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user.view.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.model.User

class UserViewHolder(itemView: View) : BaseViewHolder(itemView) {
    override fun bind(user: User, clickListener: View.OnClickListener) {
        itemView.userName.text = user.login
        itemView.userId.text = itemView.context.getString(R.string.user_id, user.id)
        itemView.setOnClickListener(clickListener)
        Glide.with(itemView).load(user.photoUrl).into(itemView.userPhoto)
    }
}