package it.personal.claudiopaccone.starsfinder.api

import io.reactivex.Observable
import it.personal.claudiopaccone.starsfinder.api.models.Stargazer
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {

    @GET("/repos/{owner}/{repo}/stargazers")
    fun getStargazers(@Path("owner") owner: String, @Path("repo") repo: String): Observable<Result<List<Stargazer>>>

    @GET
    fun getMoreStargazers(@Url url: String): Observable<Result<List<Stargazer>>>


}