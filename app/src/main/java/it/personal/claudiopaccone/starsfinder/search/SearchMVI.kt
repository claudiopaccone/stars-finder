package it.personal.claudiopaccone.starsfinder.search

import com.jakewharton.rxbinding2.InitialValueObservable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.mvi.*

data class SearchViewState(
        val owner: String?,
        val repository: String?,
        val searchingInProgress: Boolean,
        val next: String?,
        val list: List<Stargazer>) : ViewState

sealed class SearchAction : Action

object Search : SearchAction()
data class OwnerChanged(val owner: String?) : SearchAction()
data class RepositoryChanged(val repository: String?) : SearchAction()
data class SearchResult(val list: List<Stargazer>) : SearchAction()
object SearchError : SearchAction()
object SearchCompleted : SearchAction()

sealed class SearchNavigationAction : NavigationAction, SearchAction()
data class SearchNextPage(val urlNext: String) : SearchNavigationAction()

val searchReducer: Reducer<SearchAction, SearchViewState> = { action, viewState ->
    when (action) {
        Search -> viewState.copy(searchingInProgress = true)
        is OwnerChanged -> viewState.copy(owner = action.owner)
        is RepositoryChanged -> viewState.copy(repository = action.repository)
        is SearchResult -> viewState.copy(list = action.list)
        SearchError -> viewState.copy()
        is SearchNextPage -> viewState.copy()
        SearchCompleted -> viewState.copy(next = null)
    }
}

val searchNavigator: (SearchNavigationAction) -> ((SearchView) -> Unit) = { searchNavigationAction ->
    when (searchNavigationAction) {
        is SearchNextPage -> { searchView -> searchView.onNextUrl(searchNavigationAction.urlNext) }
    }
}

interface SearchView : MVIView<SearchViewState> {
    fun ownerChanged(): InitialValueObservable<CharSequence>
    fun repositoryChanged(): InitialValueObservable<CharSequence>
    fun startSearch(): Observable<Any>
    val loadNextPageIntent: PublishSubject<String>

    fun onNextUrl(urlNext: String)
}