package hu.nemi.spear.sample.android

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import hu.nemi.spear.sample.android.di.ApplicationComponent
import hu.nemi.spear.sample.android.di.DaggerApplicationComponent

class QuotesApplication : Application(), HasAndroidInjector {
    private val component: ApplicationComponent = DaggerApplicationComponent.create()

    override fun androidInjector(): AndroidInjector<Any> = component.injector
}