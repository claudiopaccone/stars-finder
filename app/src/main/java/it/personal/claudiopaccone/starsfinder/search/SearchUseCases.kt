package it.personal.claudiopaccone.starsfinder.search

import io.reactivex.Observable
import io.reactivex.Scheduler
import it.personal.claudiopaccone.starsfinder.api.ApiService
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.common.NotFoundException
import it.personal.claudiopaccone.starsfinder.common.getNextUrl
import it.personal.claudiopaccone.starsfinder.common.handleErrorResponseCode

typealias StargazersResponseInfo = Pair<String?, List<Stargazer>>

object SearchUseCases {

    fun startSearch(apiService: ApiService, owner: String, repository: String, jobScheduler: Scheduler): Observable<SearchAction> = apiService
            .getStargazers(owner, repository)
            .flatMap {
                if (it.isError) {
                    Observable.error(Exception("Generic error"))
                } else {
                    val response = it.response()
                    response.handleErrorResponseCode()

                    val next = response.headers().get("Link")?.getNextUrl()
                    Observable.just(StargazersResponseInfo(next, response.body()))
                }
            }
            .flatMap<SearchAction> { (next, list) ->
                if (next == null)
                    Observable.just(SearchResult(list), SearchCompleted)
                else
                    Observable.just(SearchResult(list, next))
            }
            .startWith(StartSearch)
            .onErrorReturn {
                if (it is NotFoundException)
                    SearchError(isNotFound = true)
                else
                    SearchError(isNotFound = false)
            }
            .subscribeOn(jobScheduler)


    fun loadNextPage(apiService: ApiService, nextUrl: String, jobScheduler: Scheduler): Observable<SearchAction> = apiService
            .getMoreStargazers(nextUrl)
            .flatMap {
                if (it.isError) {
                    Observable.error(Exception("error"))
                } else {
                    val response = it.response()
                    val next = response.headers().get("Link")?.getNextUrl()
                    Observable.just(StargazersResponseInfo(next, response.body()))
                }
            }
            .flatMap<SearchAction> { (next, list) ->
                if (next == null)
                    Observable.just(SearchResult(list), SearchCompleted)
                else
                    Observable.just(SearchResult(list, next))
            }
            .doOnError { SearchError(isNotFound = false) }
            .subscribeOn(jobScheduler)

}