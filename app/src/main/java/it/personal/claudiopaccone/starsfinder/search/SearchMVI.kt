package it.personal.claudiopaccone.starsfinder.search

import com.jakewharton.rxbinding2.InitialValueObservable
import io.reactivex.Observable
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.mvi.Action
import it.personal.claudiopaccone.starsfinder.mvi.MVIView
import it.personal.claudiopaccone.starsfinder.mvi.Reducer
import it.personal.claudiopaccone.starsfinder.mvi.ViewState

data class SearchViewState(
        val resultState: ResultState,
        val owner: String,
        val repository: String,
        val next: String?,
        val list: List<Stargazer>) : ViewState

sealed class ResultState
object SuccessState : ResultState()
data class ErrorState(val isNotFound: Boolean) : ResultState()
object LoadingState : ResultState()
object NoneState : ResultState()

sealed class SearchAction : Action

data class OwnerChanged(val owner: String) : SearchAction()
data class RepositoryChanged(val repository: String) : SearchAction()
data class SearchResult(val list: List<Stargazer>, val urlNext: String? = null) : SearchAction()
object StartSearch : SearchAction()
data class SearchError(val isNotFound: Boolean) : SearchAction()
object SearchCompleted : SearchAction()

val searchReducer: Reducer<SearchAction, SearchViewState> = { action, viewState ->
    when (action) {
        is OwnerChanged -> viewState.copy(owner = action.owner)
        is RepositoryChanged -> viewState.copy(repository = action.repository)
        is SearchResult -> viewState.copy(resultState = SuccessState, list = action.list, next = action.urlNext)
        StartSearch -> viewState.copy(resultState = LoadingState, next = null, list = emptyList())
        is SearchError -> viewState.copy(resultState = ErrorState(isNotFound = action.isNotFound))
        SearchCompleted -> viewState.copy(next = null)
    }
}

interface SearchView : MVIView<SearchViewState> {
    fun ownerChanged(): InitialValueObservable<CharSequence>
    fun repositoryChanged(): InitialValueObservable<CharSequence>
    fun startSearch(): Observable<Any>
    fun loadMoreStargazers(): Observable<Boolean>
}