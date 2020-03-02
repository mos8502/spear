package hu.nemi.spear.sample.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.nemi.spear.sample.android.data.QuoteOfTheDayRepository
import hu.nemi.spear.sample.android.util.Lce
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class QuoteViewModel @Inject constructor(private val repository: QuoteOfTheDayRepository,
                                         private val local: Locale): ViewModel() {
    private val _quote = MutableLiveData<Lce<String>>(Lce.Loading)
    val quote: LiveData<Lce<String>> = _quote


    init {
        loadQuote()
    }

    fun loadQuote() {
        viewModelScope.launch {
            try {
                _quote.value = Lce.Content(repository.quote(local.language))
            } catch (error: Throwable) {
                _quote.value = Lce.Error(error)
            }
        }
    }
}