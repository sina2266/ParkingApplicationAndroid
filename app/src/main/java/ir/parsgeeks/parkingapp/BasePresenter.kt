package ir.parsgeeks.parkingapp

abstract class BasePresenter<T> {
    protected var view: T? = null
    fun attachView(view: T) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }
}