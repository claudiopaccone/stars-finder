package it.personal.claudiopaccone.stardfinder.search

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import it.personal.claudiopaccone.stardfinder.mvi.MVIPresenter
import it.personal.claudiopaccone.stardfinder.mvi.reduceViewState

class SearchPresenter(
        private val viewScheduler: Scheduler = AndroidSchedulers.mainThread()
) : MVIPresenter<SearchView, SearchViewState>() {

    override var currentState = SearchViewState(owner = null, repository = null, searchingInProgress = false)

    override fun bind(view: SearchView) {

        val searchIntent: Observable<SearchAction> = view
                .startSearch()
                .flatMap {
                    SearchUseCases.startSearch()
                }

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

        Observable.merge(searchIntent, ownerChangedIntent, repositoryChangedIntent)
                .observeOn(viewScheduler)
                .subscribe {
                    val newState = it.reduceViewState(currentState, searchReducer)
                    viewStatePublishSubject.onNext(newState)
                }
    }
}