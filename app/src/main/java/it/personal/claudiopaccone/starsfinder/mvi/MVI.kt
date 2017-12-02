package it.personal.claudiopaccone.starsfinder.mvi

import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.lang.ref.WeakReference

interface View
interface Action
interface NavigationAction : Action
interface ViewState
typealias Reducer<A, VS> = (A, VS) -> VS
typealias Navigator<NA, V> = (NA) -> ((V) -> Unit)

/* VIEW */

interface MVIView<in VS> : View {
    fun render(viewState: VS)
}


/* PRESENTER */

interface Presenter<in V : View> {
    fun onAttach(view: V)
    fun onDetach()
    fun bind(view: V)
}

abstract class MVIPresenter<V : MVIView<VS>, VS : ViewState> : Presenter<V> {

    private var viewReference: WeakReference<V>? = null

    abstract var currentState: VS
    protected val viewStatePublishSubject = BehaviorSubject.create<VS>()
    private lateinit var viewStateCompositeDisposable: Disposable

    override fun onAttach(view: V) {
        viewReference = WeakReference(view)

        viewStateCompositeDisposable = viewStatePublishSubject.subscribe {
            currentState = it
            view.render(it)
        }
    }


    override fun onDetach() {
        viewReference?.clear()
        viewReference = null

        viewStateCompositeDisposable.dispose()
    }


    inline fun <VS, reified VA : Action> VA.reduceViewState(previousState: VS, reducer: Reducer<VA, VS>): VS {
        return reducer(this, previousState)
    }
}

