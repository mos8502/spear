package hu.nemi.spear.sample.android.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.DispatchingAndroidInjector
import hu.nemi.spear.sample.android.data.DataModule
import hu.nemi.spear.sample.android.ui.UiModule

@Component(modules = [
    DataModule::class,
    UiModule::class,
    AndroidInjectionModule::class
])
interface ApplicationComponent {
    val injector: DispatchingAndroidInjector<Any>
}