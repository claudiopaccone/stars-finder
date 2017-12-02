package it.personal.claudiopaccone.starsfinder.common

import android.content.Context
import android.support.v4.content.Loader
import it.personal.claudiopaccone.starsfinder.mvi.Presenter
import it.personal.claudiopaccone.starsfinder.mvi.View

class PresenterLoader<P : Presenter<V>, V : View>(
        context: Context,
        private val presenterGenerator: () -> P
) : Loader<P>(context) {

    private var presenter: P? = null

    override fun onStartLoading() {

        // If we already own an instance, simply deliver it.
        if (presenter != null) {
            deliverResult(presenter)
            return
        }
        // Otherwise, force a load
        forceLoad()
    }

    override fun onForceLoad() {
        // Create the Presenter using the Factory
        presenter = presenterGenerator.invoke()

        // Deliver the result
        deliverResult(presenter)
    }

    override fun onReset() {
        presenter = null
    }

}