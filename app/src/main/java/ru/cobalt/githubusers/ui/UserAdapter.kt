package ru.cobalt.githubusers.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user.view.*
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.model.User

class UserAdapter() : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var list: List<User> = listOf()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            itemView.userName.text = user.login
            itemView.userId.text = "id: ${user.id}"
            Glide.with(itemView).load(user.photoUrl).into(itemView.userPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun update(newList: List<User>) {
        list = newList
    }
}