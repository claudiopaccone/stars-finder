package it.personal.claudiopaccone.stardfinder.search

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import it.personal.claudiopaccone.stardfinder.mvi.MVIPresenter
import it.personal.claudiopaccone.stardfinder.mvi.reduceViewState

class SearchPresenter(
        private val viewScheduler: Scheduler = AndroidSchedulers.mainThread()
) : MVIPresenter<SearchView, SearchViewState>() {

    override var currentState = SearchViewState(text = "First", progressVisibility = false)

    override fun bind(view: SearchView) {

        val searchIntent: Observable<SearchAction> = view
                .startSearch()
                .flatMap {
                    SearchUseCases.startSearch()
                }

        val clearIntent: Observable<SearchAction> = view
                .clearSearch()
                .flatMap {
                    SearchUseCases.clearSearch()
                }

        Observable.merge(searchIntent, clearIntent)
                .observeOn(viewScheduler)
                .subscribe {
                    val newState = it.reduceViewState(currentState, searchReducer)
                    viewStatePublishSubject.onNext(newState)
                }
    }
}