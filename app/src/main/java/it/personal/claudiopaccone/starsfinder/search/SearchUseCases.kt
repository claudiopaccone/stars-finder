package it.personal.claudiopaccone.starsfinder.search

import io.reactivex.Observable
import io.reactivex.Scheduler
import it.personal.claudiopaccone.starsfinder.api.ApiService
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.common.getNextUrl

typealias StargazersResponseInfo = Pair<String?, List<Stargazer>>

object SearchUseCases {

    fun startSearch(apiService: ApiService, owner: String, repository: String, jobScheduler: Scheduler): Observable<SearchAction> = apiService
            .getStargazers(owner, repository)
            .flatMap {
                if (it.isError) {
                    Observable.error(Exception("error"))
                } else {
                    val response = it.response()
                    val next = response.headers().get("Link").getNextUrl()
                    Observable.just(StargazersResponseInfo(next, response.body()))
                }
            }
            .flatMap<SearchAction> { (next, list) ->
                if (next == null)
                    Observable.just(SearchResult(list))
                else
                    Observable.just(SearchResult(list), SearchNextPage(next))
            }
            .doOnError { SearchError }
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
                Observable.create { observable ->
                    observable.onNext(SearchResult(list))
                    if (next != null) {
                        observable.onNext(SearchNextPage(next))
                    } else {
                        observable.onNext(SearchCompleted)
                    }

                    observable.onComplete()
                }
            }
            .doOnError { SearchError }
            .subscribeOn(jobScheduler)

}