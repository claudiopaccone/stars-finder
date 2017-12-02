package it.personal.claudiopaccone.stardfinder.search

import io.reactivex.Observable
import it.personal.claudiopaccone.stardfinder.mvi.Action
import it.personal.claudiopaccone.stardfinder.mvi.MVIView
import it.personal.claudiopaccone.stardfinder.mvi.ViewState

data class SearchViewState(val text: String, val progressVisibility: Boolean) : ViewState

sealed class SearchAction : Action

object Search : SearchAction()
object Clear : SearchAction()
object CharChanged : SearchAction()

val searchReducer: (SearchAction, SearchViewState) -> SearchViewState = { action, viewState ->
    when (action) {
        Search -> viewState.copy(text = "Start Search", progressVisibility = true)
        Clear -> viewState.copy(progressVisibility = false)
        CharChanged -> viewState
    }
}

interface SearchView : MVIView<SearchViewState> {
    fun startSearch(): Observable<Any>
    fun clearSearch(): Observable<Any>
}