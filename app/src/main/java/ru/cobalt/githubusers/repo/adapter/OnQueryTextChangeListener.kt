package ru.cobalt.githubusers.repo.adapter

import android.widget.SearchView
import io.reactivex.Observer

class OnQueryTextChangeListener : SearchView.OnQueryTextListener {

    lateinit var submitEmitter: Observer<String>
    lateinit var changeEmitter: Observer<String>

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { if (it.isNotEmpty()) submitEmitter.onNext(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { if (it.isNotEmpty()) changeEmitter.onNext(it) }
        return false
    }

}