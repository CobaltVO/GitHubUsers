package ru.cobalt.githubusers.ui.user.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.cobalt.githubusers.ui.user.adapter.ViewType.LoaderViewType
import ru.cobalt.githubusers.ui.user.adapter.ViewType.UserViewType
import ru.cobalt.githubusers.ui.user.adapter.holder.BaseViewHolder
import ru.cobalt.githubusers.ui.user.adapter.holder.LoaderViewHolder
import ru.cobalt.githubusers.ui.user.adapter.holder.UserViewHolder

class UserAdapter : ListAdapter<ViewType, BaseViewHolder>(DiffUtilViewTypeCallback) {

    var onViewClickListener: (ViewType) -> Unit = {}

    override fun getItemViewType(position: Int): Int = currentList[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            TYPE_USER -> UserViewHolder.getInstance(parent)
            else -> LoaderViewHolder.getInstance(parent)
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (val viewType = currentList[position]) {
            is UserViewType -> holder.bind(viewType.user) { onViewClickListener.invoke(viewType) }
        }
    }

    fun getViewType(listPosition: Int): ViewType? {
        return try {
            currentList[listPosition]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    private object DiffUtilViewTypeCallback : DiffUtil.ItemCallback<ViewType>() {
        override fun areItemsTheSame(oldItem: ViewType, newItem: ViewType): Boolean {
            if (oldItem.javaClass != newItem.javaClass) return false
            return when (oldItem) {
                is LoaderViewType -> oldItem == newItem
                is UserViewType -> oldItem.user.id == (newItem as UserViewType).user.id
            }
        }

        override fun areContentsTheSame(oldItem: ViewType, newItem: ViewType): Boolean {
            if (oldItem.javaClass != newItem.javaClass) return false
            return when (oldItem) {
                is LoaderViewType -> true
                is UserViewType -> oldItem == newItem
            }
        }
    }
}