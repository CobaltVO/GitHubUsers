package ru.cobalt.githubusers.repo

import android.widget.SearchView
import io.reactivex.Observer

class OnQueryTextChangeListener(
    private val submitEmitter: Observer<String>,
    private val changeEmitter: Observer<String>
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