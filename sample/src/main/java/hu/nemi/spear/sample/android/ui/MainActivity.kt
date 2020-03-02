package hu.nemi.spear.sample.android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import dagger.android.AndroidInjection
import hu.nemi.spear.sample.android.R
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: QuoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.quote.observe(this, Observer {
            quote.text = it.value
        })
        loadQuote.setOnClickListener {
            viewModel.loadQuote()
        }
    }

    @Inject
    fun inject(factory: ViewModelProvider.Factory) {
        viewModel = ViewModelProvider(this, factory).get()
    }
}
