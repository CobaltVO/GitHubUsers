package ru.cobalt.githubusers.ui.user.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.ui.user.adapter.holder.BaseViewHolder
import ru.cobalt.githubusers.ui.user.adapter.holder.LoaderViewHolder
import ru.cobalt.githubusers.ui.user.adapter.holder.UserViewHolder
import javax.inject.Inject

const val TYPE_USER = 0
const val TYPE_LOADER = 1

class UserAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    @Inject
    lateinit var callback: DiffUtilUserCallback

    private val differ: AsyncListDiffer<User> by lazy { AsyncListDiffer(this, callback) }

    private var savedList: List<User> = listOf()

    var onUserClickListener: (User) -> Unit = {}
    var isLoaderActivated: Boolean = true

    init {
        App.appComponent.inject(this)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return if (position == differ.currentList.size - 1 && isLoaderActivated) TYPE_LOADER
        else TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            TYPE_USER -> UserViewHolder.getInstance(parent)
            else -> LoaderViewHolder.getInstance(parent)
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.bind(user) { onUserClickListener.invoke(user) }
    }

    fun getCurrentList(): List<User> = differ.currentList

    fun reloadList(newList: List<User>, commitCallback: Runnable? = null) =
        differ.submitList(newList, commitCallback)

    fun updateList(newSubList: List<User>, commitCallback: Runnable? = null) =
        differ.submitList(differ.currentList + newSubList, commitCallback)

    fun saveList() {
        savedList = getCurrentList()
    }

    fun restoreList(): List<User> = savedList

    fun getUser(listPosition: Int): User? {
        return try {
            differ.currentList[listPosition]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    fun getLastUser(): User? {
        return try {
            differ.currentList[differ.currentList.lastIndex]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }
}