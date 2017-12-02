package it.personal.claudiopaccone.stardfinder.search

import com.jakewharton.rxbinding2.InitialValueObservable
import io.reactivex.Observable
import it.personal.claudiopaccone.stardfinder.mvi.Action
import it.personal.claudiopaccone.stardfinder.mvi.MVIView
import it.personal.claudiopaccone.stardfinder.mvi.ViewState

data class SearchViewState(val owner: String?, val repository: String?, val searchingInProgress: Boolean) : ViewState

sealed class SearchAction : Action

object Search : SearchAction()
data class OwnerChanged(val owner: String?) : SearchAction()
data class RepositoryChanged(val repository: String?) : SearchAction()

val searchReducer: (SearchAction, SearchViewState) -> SearchViewState = { action, viewState ->
    when (action) {
        Search -> viewState.copy(searchingInProgress = true)
        is OwnerChanged -> viewState.copy(owner = action.owner)
        is RepositoryChanged -> viewState.copy(repository = action.repository)
    }
}

interface SearchView : MVIView<SearchViewState> {
    fun ownerChanged(): InitialValueObservable<CharSequence>
    fun repositoryChanged(): InitialValueObservable<CharSequence>
    fun startSearch(): Observable<Any>
}