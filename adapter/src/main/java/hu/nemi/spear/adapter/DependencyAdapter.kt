package hu.nemi.spear.adapter

abstract class DependencyAdapter<T: Any> {
    var shadow: T? = null
}