package net.gsantner.markor.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import net.gsantner.markor.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AboutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void aboutTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.nav_more), withContentDescription("More"), childAtPosition(childAtPosition(withId(R.id.bottom_navigation_bar), 0), 3), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction viewPager = onView(allOf(withId(R.id.main__view_pager_container), childAtPosition(allOf(withId(R.id.main_content), childAtPosition(withId(android.R.id.content), 0)), 1), isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        recyclerView.perform(actionOnItemAtPosition(3, click()));

        ViewInteraction textView = onView(allOf(withText("Markdown\nCommonMark is the specification that the markdown parser used in Markor implements.\n\n* CommonMark tutorial\n   Learn Markdown in 10 minutes\n* CommonMark help\n   Quick reference card and interactive tutorial for learning Markdown. \n* CommonMark Spec\n   A strongly defined, highly compatible specification of Markdown\n* Markdown syntax documentation\n   From the Markdown creator\n\nTodo.txt\nTodo.txt is a simple text format for todo. The idea comes from Gina Trapani.\n\n* Todo.txt Homepage\n   Todo.txt's home\n* Todo.txt Format\n   Syntax documentation\n* Todo.txt User Documentation\n   User documentation\n\n"), childAtPosition(allOf(withId(R.id.custom), childAtPosition(withId(R.id.customPanel), 0)), 0), isDisplayed()));
        textView.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
