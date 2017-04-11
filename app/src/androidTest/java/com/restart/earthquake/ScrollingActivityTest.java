package com.restart.earthquake;

import android.content.res.Resources;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ScrollingActivityTest {

    @Rule
    public ActivityTestRule<ScrollingActivity> mActivityRule = new ActivityTestRule<>(ScrollingActivity.class);

    @Test
    public void checkIfBasicViewsExists() throws Exception {
        onView(withId(R.id.quakesRecyclerList)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfProgressSpinnerWentAway() throws Exception {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.httpWait)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void checkIfRecyclerViewIsClickable() throws Exception {
        onView(withId(R.id.quakesRecyclerList)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
    }

    @Test
    public void checkIfRecyclerViewIsFilledWithData() throws Exception {
        onView(withRecyclerView(R.id.quakesRecyclerList).atPositionOnView(0, R.id.date)).check(matches(withText("6 years ago")));
    }

    @Test
    public void checkIfRecyclerISScrollableAndIsFilledWithData() throws Exception {
        onView(withId(R.id.quakesRecyclerList)).perform(RecyclerViewActions.scrollToPosition(9));
        onView(withRecyclerView(R.id.quakesRecyclerList).atPosition(9)).perform(click());
        onView(withRecyclerView(R.id.quakesRecyclerList).atPositionOnView(9, R.id.date)).check(matches(withText("1 year ago")));
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    /**
     * Fork of the RecyclerViewMatcher from https://github.com/dannyroa/espresso-samples
     * More at: https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/
     */
    public static class RecyclerViewMatcher {

        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)", recyclerViewId);
                        }
                    }

                    description.appendText("RecyclerView with id: " + idDescription + " at position: " + position);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                            if (viewHolder != null) {
                                childView = viewHolder.itemView;
                            }
                        } else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }
                }
            };
        }
    }
}
