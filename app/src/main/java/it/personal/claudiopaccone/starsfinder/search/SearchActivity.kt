package it.personal.claudiopaccone.starsfinder.search

import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import it.personal.claudiopaccone.starsfinder.api.ApiManager
import it.personal.claudiopaccone.starsfinder.common.PresenterActivity
import it.personal.claudiopaccone.starsfinder.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity :
        PresenterActivity<SearchPresenter, SearchView>(),
        SearchView {

    override val loadNextPageIntent: PublishSubject<String> = PublishSubject.create()

    override val presenterGenerator: () -> SearchPresenter = { SearchPresenter(apiService = ApiManager().apiService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    /* Render */

    override fun render(viewState: SearchViewState) {
        progressBar.visibility = if (viewState.searchingInProgress) View.VISIBLE else View.INVISIBLE
        renderSearchButton(viewState)
    }

    private fun renderSearchButton(viewState: SearchViewState) {
        if (viewState.owner?.isNotEmpty() == true && viewState.repository?.isNotEmpty() == true) {
            searchButton.setEnabled(true)
        } else {
            searchButton.setEnabled(false)
        }
    }

    /* View intents */

    override fun ownerChanged() = RxTextView.textChanges(ownerEditText)

    override fun repositoryChanged() = RxTextView.textChanges(repositoryEditText)

    override fun startSearch(): Observable<Any> = RxView.clicks(searchButton)

    override fun onNextUrl(urlNext: String) {
        loadNextPageIntent.onNext(urlNext)
    }
}