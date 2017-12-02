package it.personal.claudiopaccone.starsfinder.search

import io.reactivex.Observable
import io.reactivex.Scheduler
import it.personal.claudiopaccone.starsfinder.api.ApiService
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.common.NotFoundException
import it.personal.claudiopaccone.starsfinder.common.getNextUrl
import it.personal.claudiopaccone.starsfinder.common.handleErrorResponseCode
import retrofit2.adapter.rxjava2.Result

typealias StargazersResponseInfo = Pair<String?, List<Stargazer>>

object SearchUseCases {

    fun startSearch(apiService: ApiService, owner: String, repository: String, jobScheduler: Scheduler): Observable<SearchAction> = apiService
            .getStargazers(owner, repository)
            .flatMap { getResponseInfo(it) }
            .flatMap<SearchAction> { Observable.just(SearchResult(it.second, it.first)) }
            .startWith(StartSearch)
            .catchException()
            .subscribeOn(jobScheduler)

    fun loadNextPage(apiService: ApiService, nextUrl: String, jobScheduler: Scheduler): Observable<SearchAction> = apiService
            .getMoreStargazers(nextUrl)
            .flatMap { getResponseInfo(it) }
            .flatMap<SearchAction> { Observable.just(SearchResult(it.second, it.first)) }
            .startWith(LoadMore)
            .catchException()
            .subscribeOn(jobScheduler)

    private fun getResponseInfo(it: Result<List<Stargazer>>): Observable<StargazersResponseInfo>? {
        return if (it.isError) {
            Observable.error(Exception("Generic error"))
        } else {
            val response = it.response()
            response.handleErrorResponseCode()

            val next = response.headers().get("Link")?.getNextUrl()
            Observable.just(StargazersResponseInfo(next, response.body()))
        }
    }

    private fun Observable<SearchAction>.catchException() = onErrorReturn {
        if (it is NotFoundException)
            SearchError(isNotFound = true)
        else
            SearchError(isNotFound = false)

    }

}