package hu.nemi.spear.adapter

interface ModelAdapter<T: Any> {
    infix fun T.replaceWith(with: T)

    fun T.reset()
}