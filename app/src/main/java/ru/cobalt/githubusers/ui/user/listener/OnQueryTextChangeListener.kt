package ru.cobalt.githubusers.ui.user.listener

import android.widget.SearchView
import io.reactivex.Observer

class OnQueryTextChangeListener : SearchView.OnQueryTextListener {

    var submitEmitter: Observer<String>? = null
    var changeEmitter: Observer<String>? = null

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { if (it.isNotEmpty()) submitEmitter?.onNext(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { if (it.isNotEmpty()) changeEmitter?.onNext(it) }
        return false
    }

}