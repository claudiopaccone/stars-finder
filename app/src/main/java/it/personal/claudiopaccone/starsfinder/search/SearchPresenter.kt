package it.personal.claudiopaccone.starsfinder.search

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.personal.claudiopaccone.starsfinder.api.ApiService
import it.personal.claudiopaccone.starsfinder.mvi.MVIPresenter

class SearchPresenter(
        val apiService: ApiService,
        private val viewScheduler: Scheduler = AndroidSchedulers.mainThread(),
        private val jobScheduler: Scheduler = Schedulers.io()
) : MVIPresenter<SearchView, SearchViewState>() {

    override var currentState = SearchViewState(
            owner = "",
            repository = "",
            searchingInProgress = false,
            list = emptyList(),
            next = null)

    override fun bind(view: SearchView) {

        val searchIntent: Observable<SearchAction> = view
                .startSearch()
                .flatMap {
                    SearchUseCases.startSearch(apiService, currentState.owner!!, currentState.repository!!, jobScheduler)
                }

        val loadNextPageIntent: Observable<SearchAction> = view
                .loadNextPageIntent
                .flatMap { SearchUseCases.loadNextPage(apiService, it, jobScheduler) }


        val ownerChangedIntent: Observable<SearchAction> = view
                .ownerChanged()
                .flatMap {
                    Observable.just(OwnerChanged(it.toString()))
                }

        val repositoryChangedIntent: Observable<SearchAction> = view
                .repositoryChanged()
                .flatMap {
                    Observable.just(RepositoryChanged(it.toString()))
                }

        Observable.merge(searchIntent, ownerChangedIntent, repositoryChangedIntent, loadNextPageIntent)
                .observeOn(viewScheduler)
                .subscribe {
                    Log.d("SearchPresenter", "Action -> " + it.toString())
                    val newState = it.reduceViewState(currentState, searchReducer)
                    viewStatePublishSubject.onNext(newState)
                }
    }
}