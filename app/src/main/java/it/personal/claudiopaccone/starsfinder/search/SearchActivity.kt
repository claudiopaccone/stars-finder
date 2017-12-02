package it.personal.claudiopaccone.starsfinder.search

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import it.personal.claudiopaccone.starsfinder.R
import it.personal.claudiopaccone.starsfinder.api.ApiManager
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.common.PresenterActivity
import it.personal.claudiopaccone.starsfinder.common.closeKeyboard
import it.personal.claudiopaccone.starsfinder.search.list.StargazerAdapter
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity :
        PresenterActivity<SearchPresenter, SearchView>(),
        SearchView {

    private var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
    private var listAdapter = StargazerAdapter(emptyList())

    override val presenterGenerator: () -> SearchPresenter = { SearchPresenter(apiService = ApiManager().apiService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        stargazersRecyclerView.setHasFixedSize(true)
        stargazersRecyclerView.adapter = listAdapter
        stargazersRecyclerView.layoutManager = linearLayoutManager
        stargazersRecyclerView.itemAnimator = DefaultItemAnimator()

    }

    /* Render */

    override fun render(viewState: SearchViewState) {
        renderSearchButton(viewState)
        renderList(viewState.list)
        renderState(viewState.resultState)
    }

    private fun renderState(resultState: ResultState) {
        when (resultState) {
            is ErrorState -> {
                stargazersRecyclerView.visibility = View.INVISIBLE
                errorTextView.visibility = View.VISIBLE
                loadingProgressBar.visibility = View.INVISIBLE
                if (resultState.isNotFound) {
                    errorTextView.setText(R.string.notFoundErrorMessage)
                } else {
                    errorTextView.setText(R.string.genericErrorMessage)
                }
            }
            SuccessState -> {
                stargazersRecyclerView.visibility = View.VISIBLE
                errorTextView.visibility = View.INVISIBLE
                loadingProgressBar.visibility = View.INVISIBLE
            }
            LoadingState -> {
                closeKeyboard()
                stargazersRecyclerView.visibility = View.INVISIBLE
                errorTextView.visibility = View.INVISIBLE
                loadingProgressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun renderList(list: List<Stargazer>) {
        if (list.isEmpty()) listAdapter.deleteAllItems()
        else
            listAdapter.addItems(list)
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

    override fun loadMoreStargazers(): Observable<Boolean> = RxRecyclerView.scrollStateChanges(stargazersRecyclerView)
            .filter { it == RecyclerView.SCROLL_STATE_IDLE }
            .flatMap { Observable.just(linearLayoutManager.findLastVisibleItemPosition() == stargazersRecyclerView.adapter.itemCount - 1) }

}
