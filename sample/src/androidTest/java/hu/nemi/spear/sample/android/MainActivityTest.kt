package hu.nemi.spear.sample.android

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import hu.nemi.spear.sample.android.data.DataModule
import hu.nemi.spear.sample.android.data.DataModuleAdapter.replaceWith
import hu.nemi.spear.sample.android.data.QuoteOfTheDayRepository
import hu.nemi.spear.sample.android.ui.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.internal.stubbing.answers.CallsRealMethods

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityTestRule = IntentsTestRule(MainActivity::class.java, true, false)

    @Mock
    lateinit var repository: QuoteOfTheDayRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        DataModule replaceWith mock(defaultAnswer = CallsRealMethods())
        doReturn(repository).whenever(DataModule).provideRepository(any())
    }

    @Test
    fun showsQuote() {
        runBlocking {
            whenever(repository.quote(any())).thenReturn("Hello, World!")
        }

        activityTestRule.launchActivity(null)

        onView(withId(R.id.quote)).check(matches(withText("Hello, World!")))
    }

    @Test
    fun loadQuote() {
        runBlocking {
            whenever(repository.quote(any())).thenReturn("Hello, World!")
                .thenReturn("Hello, World ... again!")
        }

        activityTestRule.launchActivity(null)

        onView(withId(R.id.loadQuote)).perform(click())
        onView(withId(R.id.quote)).check(matches(withText("Hello, World ... again!")))
    }
}
