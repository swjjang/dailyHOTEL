package com.twoheart.dailyhotel;

import com.twoheart.dailyhotel.util.Util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by android_sam on 2017. 1. 5..
 */

public class DailyMatcher
{
    public static Matcher<String> isEmpty()
    {
        return new TypeSafeMatcher<String>()
        {
            String stringValue;

            @Override
            protected boolean matchesSafely(String item)
            {
                stringValue = item;
                return Util.isTextEmpty(item);
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(stringValue + " is not empty");
            }
        };
    }

    public static Matcher<String> isNotEmpty()
    {
        return new TypeSafeMatcher<String>()
        {
            @Override
            protected boolean matchesSafely(String item)
            {
                return Util.isTextEmpty(item) == false;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue("input item is empty");
            }
        };
    }

    public static Matcher<Integer> moreThan(final int defaultValue)
    {
        return new TypeSafeMatcher<Integer>()
        {
            int intValue;

            @Override
            protected boolean matchesSafely(Integer item)
            {
                intValue = item;
                return item >= defaultValue;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(intValue + " is not over " + defaultValue);
            }
        };
    }

    public static Matcher<Long> moreThan(final long defaultValue)
    {
        return new TypeSafeMatcher<Long>()
        {
            double longValue;

            @Override
            protected boolean matchesSafely(Long item)
            {
                longValue = item;
                return item >= defaultValue;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(longValue + " is not over " + defaultValue);
            }
        };
    }

    public static Matcher<Double> moreThan(final double defaultValue)
    {
        return new TypeSafeMatcher<Double>()
        {
            double doubleValue;

            @Override
            protected boolean matchesSafely(Double item)
            {
                doubleValue = item;
                return item >= defaultValue;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(doubleValue + " is not over " + defaultValue);
            }
        };
    }
}
