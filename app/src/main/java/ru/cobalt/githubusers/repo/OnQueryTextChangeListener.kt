package ru.cobalt.githubusers.repo

import android.widget.SearchView
import io.reactivex.Observer

class OnQueryTextChangeListener(
    var submitEmitter: Observer<String>,
    var changeEmitter: Observer<String>
) : SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { if (it.isNotEmpty()) submitEmitter.onNext(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { if (it.isNotEmpty()) changeEmitter.onNext(it) }
        return false
    }

}