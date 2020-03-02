package hu.nemi.spear.sample.android.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class ViewModelKey(val type: KClass<out ViewModel>)