package hu.nemi.spear.sample.android.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UiModule {
    @get:ContributesAndroidInjector(modules = [MainAcitivtyModule::class])
    abstract val mainActivity: MainActivity
}