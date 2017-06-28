package fr.wayofcode.jeremy.bakingapp.activity;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import fr.wayofcode.jeremy.bakingapp.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created Test on MainActivity.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }

  @Test public void mainActivityWithPlayerTest() {
    ViewInteraction textView = onView(allOf(withId(R.id.tv_recipe_name), withText("Nutella Pie"),
        childAtPosition(childAtPosition(withId(R.id.rv_recipe), 0), 1), isDisplayed()));
    textView.check(matches(withText("Nutella Pie")));

    ViewInteraction recyclerView = onView(allOf(withId(R.id.rv_recipe), isDisplayed()));
    recyclerView.perform(actionOnItemAtPosition(0, click()));

    ViewInteraction textView2 = onView(
        allOf(withId(R.id.tv_ingredient_step), withText("Recipe Introduction"),
            childAtPosition(childAtPosition(withId(R.id.master_list_fragment), 1), 0),
            isDisplayed()));
    textView2.check(matches(withText("Recipe Introduction")));

    ViewInteraction recyclerView2 = onView(allOf(withId(R.id.master_list_fragment), withParent(
        allOf(withId(android.R.id.content), withParent(withId(R.id.decor_content_parent)))),
        isDisplayed()));
    recyclerView2.perform(actionOnItemAtPosition(1, click()));

    ViewInteraction appCompatButton = onView(allOf(withId(R.id.bt_next), withText("Next")));
    appCompatButton.perform(scrollTo(), click());


  }


}
