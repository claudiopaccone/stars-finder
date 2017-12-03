package it.personal.claudiopaccone.starsfinder

import io.kotlintest.matchers.shouldBe
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.kotlintest.specs.ShouldSpec
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import it.personal.claudiopaccone.starsfinder.api.ApiService
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import it.personal.claudiopaccone.starsfinder.search.SearchError
import it.personal.claudiopaccone.starsfinder.search.SearchResult
import it.personal.claudiopaccone.starsfinder.search.SearchUseCases
import it.personal.claudiopaccone.starsfinder.search.StartSearch
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result

class SearchUseCasesTest : ShouldSpec() {

    init {
        val ignored = "_ignored"
        val apiServiceMock: ApiService = mock()

        "SeachUseCase.startSearch"{
            should("emit StartSearch action first") {
                `when`(apiServiceMock.getStargazers(ignored, ignored))
                        .thenReturn(Observable.just(Result.response(Response.success(emptyList()))))

                val actions = SearchUseCases.startSearch(apiServiceMock, ignored, ignored, Schedulers.trampoline()).test().values()

                actions.first() shouldBe StartSearch
            }

            should("emit SearchResult action with correct list of stargazers") {
                val expectedList = listOf(Stargazer(username = "name1", avatarUrl = "avatar1"),
                        Stargazer(username = "name1", avatarUrl = "avatar1"))
                `when`(apiServiceMock.getStargazers(ignored, ignored))
                        .thenReturn(Observable.just(Result.response(Response.success(expectedList))))

                val actions = SearchUseCases.startSearch(apiServiceMock, ignored, ignored, Schedulers.trampoline()).test().values()

                actions[0] shouldBe StartSearch
                actions[1] shouldBe SearchResult(expectedList, null)
            }

            should("emit SearchResult action with correct next value") {
                val ignoredList = listOf(Stargazer(username = "name1", avatarUrl = "avatar1"),
                        Stargazer(username = "name1", avatarUrl = "avatar1"))
                val expectedUrl = "https://api.github.com/resource?page=2"
                val header = okhttp3.Headers.Builder().add("Link", "<$expectedUrl>; rel=\"next\"").build()
                `when`(apiServiceMock.getStargazers(ignored, ignored))
                        .thenReturn(Observable.just(Result.response(Response.success(ignoredList, header))))

                val actions = SearchUseCases.startSearch(apiServiceMock, ignored, ignored, Schedulers.trampoline()).test().values()

                actions[0] shouldBe StartSearch
                actions[1] shouldBe SearchResult(ignoredList, expectedUrl)
            }

            should("emit SearchError(isNotFound = true) action if response return 404 code") {
                `when`(apiServiceMock.getStargazers(ignored, ignored))
                        .thenReturn(Observable.just(Result.response(Response.error(404, ResponseBody.create(MediaType.parse("json"), "")))))

                val actions = SearchUseCases.startSearch(apiServiceMock, ignored, ignored, Schedulers.trampoline()).test().values()

                actions[0] shouldBe StartSearch
                actions[1] shouldBe SearchError(isNotFound = true)
            }
        }

    }
}