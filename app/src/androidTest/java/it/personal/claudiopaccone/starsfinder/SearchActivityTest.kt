package it.personal.claudiopaccone.starsfinder

import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import it.personal.claudiopaccone.starsfinder.search.SearchActivity
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchActivityTest {

    @Rule
    @JvmField
    val searchActivity = ActivityTestRule(SearchActivity::class.java, true, false)

    @Test
    fun recyclerView_shouldBeShown_whenInputsAreCorrect() {
        searchActivity.launchActivity(Intent())
        waitMillis(1000)

        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.ownerEditText)))
                .perform(ViewActions.replaceText("SchibstedSpain"), ViewActions.closeSoftKeyboard())

        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.repositoryEditText)))
                .perform(ViewActions.replaceText("Barista"), ViewActions.closeSoftKeyboard())

        waitMillis(1000)

        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.searchButton)))
                .perform(ViewActions.click())

        waitMillis(2000)

        val recyclerView = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.stargazersRecyclerView)))
        recyclerView.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun errorView_shouldBeShown_whenRepositoryIsIncorrect() {
        searchActivity.launchActivity(Intent())
        waitMillis(1000)

        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.ownerEditText)))
                .perform(ViewActions.replaceText("SchibstedSpain_error"), ViewActions.closeSoftKeyboard())

        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.repositoryEditText)))
                .perform(ViewActions.replaceText("Barista_error"), ViewActions.closeSoftKeyboard())

        waitMillis(1000)

        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.searchButton)))
                .perform(ViewActions.click())

        waitMillis(2000)

        val recyclerView = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.errorTextView)))
        recyclerView.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }
}


fun waitMillis(millis: Long) {
    try {
        Thread.sleep(millis)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}
