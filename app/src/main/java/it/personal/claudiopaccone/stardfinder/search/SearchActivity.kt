package it.personal.claudiopaccone.stardfinder.search

import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import it.personal.claudiopaccone.stardfinder.common.PresenterActivity
import it.personal.claudiopaccone.starsfinder.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity :
        PresenterActivity<SearchPresenter, SearchView>(),
        SearchView {

    override val presenterGenerator: () -> SearchPresenter = { SearchPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun render(viewState: SearchViewState) {
        textView.setText(viewState.text)
        progressBar.visibility = if (viewState.progressVisibility) View.VISIBLE else View.INVISIBLE
    }

    override fun startSearch(): Observable<Any> = RxView.clicks(searchButton)

    override fun clearSearch(): Observable<Any> = RxView.clicks(clearButton)


}
