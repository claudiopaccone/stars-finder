package it.personal.claudiopaccone.starsfinder.common

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import it.personal.claudiopaccone.starsfinder.mvi.Presenter
import it.personal.claudiopaccone.starsfinder.mvi.View

abstract class PresenterActivity<P : Presenter<V>, V : View> : AppCompatActivity() {

    private val LOADER_ID = 101
    protected var presenter: P? = null

    abstract val presenterGenerator: () -> P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportLoaderManager.initLoader(LOADER_ID, null, object : LoaderManager.LoaderCallbacks<P> {

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<P> {
                return PresenterLoader(this@PresenterActivity, presenterGenerator)
            }

            override fun onLoadFinished(loader: Loader<P>, data: P) {
                presenter = data
            }

            override fun onLoaderReset(loader: Loader<P>) {
                presenter = null
            }

        })
    }

    override fun onStart() {
        super.onStart()
        presenter?.onAttach(this as V)
        presenter?.bind(this as V)
    }

    override fun onStop() {
        presenter?.onDetach()
        super.onStop()
    }
}