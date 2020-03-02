package hu.nemi.spear.sample.android.util

sealed class Lce<out T: Any> {
    open val value: T? = null

    object Loading: Lce<Nothing>()

    data class Content<out T: Any>(override val value: T): Lce<T>()

    data class Error(val cause: Throwable): Lce<Nothing>()

}