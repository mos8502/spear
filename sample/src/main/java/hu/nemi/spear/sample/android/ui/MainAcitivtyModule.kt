package hu.nemi.spear.sample.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import hu.nemi.spear.sample.android.di.ViewModelFactory
import hu.nemi.spear.sample.android.di.ViewModelKey
import java.util.*

@Module
object MainAcitivtyModule {
    @Provides
    fun provideLocale(activity: MainActivity): Locale =
        activity.resources.configuration.locale

    @Provides
    @IntoMap
    @ViewModelKey(QuoteViewModel::class)
    fun provideQuiteViewModel(viewModel: QuoteViewModel): ViewModel = viewModel

    @Provides
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory = factory
}