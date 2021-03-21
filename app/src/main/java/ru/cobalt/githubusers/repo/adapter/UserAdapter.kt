package ru.cobalt.githubusers.repo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import ru.cobalt.githubusers.R
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.User
import javax.inject.Inject

const val TYPE_USER = 0
const val TYPE_LOADER = 1

class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_USER -> UserViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_user, parent, false)
            )
            else -> LoaderViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_loader, parent, false)
            )
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = differ.currentList[position]
        when (holder) {
            is UserViewHolder -> holder.bind(user) { onUserClickListener.invoke(user) }
            is LoaderViewHolder -> holder.bind(user) { onUserClickListener.invoke(user) }
        }
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