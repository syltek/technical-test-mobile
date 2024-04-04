package com.playtomic.foundation.observable

class Disposable(val dispose: (() -> Unit)) {

    fun add(to: IDisposal) {
        to.add(disposable = this)
    }
}

class Disposal : IDisposal {
    private val disposables: MutableList<Disposable> = mutableListOf()

    override fun add(disposable: Disposable) {
        disposables.add(disposable)
    }

    override fun dispose() {
        disposables.forEach { it.dispose() }
        disposables.clear()
    }

    override fun getDisposableSize() = disposables.size
}

interface IDisposal {
    fun add(disposable: Disposable)
    fun dispose()
    fun getDisposableSize(): Int
}
