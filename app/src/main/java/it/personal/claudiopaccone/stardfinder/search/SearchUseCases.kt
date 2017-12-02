package it.personal.claudiopaccone.stardfinder.search

import io.reactivex.Observable

object SearchUseCases {

    fun startSearch() =
            Observable.just(Search)

    fun clearSearch() =
            Observable.just(Clear)

}