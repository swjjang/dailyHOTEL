package com.twoheart.dailyhotel.util;

import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DailyDeepLinkTest
{
    @Rule
    public ActivityTestRule<com.twoheart.dailyhotel.LauncherActivity> launcherActivityActivityTestRule = new ActivityTestRule(com.twoheart.dailyhotel.LauncherActivity.class);

    final int DELAY_SECONDS = 5;
    final int NIGHTS_RANGE = 4;
    final int DAYS_RANGE = 20;

    public static ViewAction waitFor(final long millis)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return isRoot();
            }

            @Override
            public String getDescription()
            {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view)
            {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    @Test
    public void test_DeepLink_검색홈_국내스테이() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=searchHome&pt=stay";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.stayFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.stayOutboundFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.gourmetFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
    }

    @Test
    public void test_DeepLink_검색홈_해외스테이() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=searchHome&pt=stayOutbound";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.stayFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.stayOutboundFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.gourmetFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
    }

    @Test
    public void test_DeepLink_검색홈_고메() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=searchHome&pt=gourmet";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.stayFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.stayOutboundFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.gourmetFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }

    @Test
    public void test_DeepLink_검색결과_국내스테이() throws Exception
    {
        final String searchKeyword = "강남";
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=hsr&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&w=" + searchKeyword;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=hsr&dp=" + dp + "&n=" + nights + "&w=" + searchKeyword;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=hsr&week=" + String.valueOf(weeks) + "&n=" + nights + "&w=" + searchKeyword;
            StayBookDateTime weekBookDateTime = getStayBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks, nights);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , weekBookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , weekBookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd, s=lp
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=hsr&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&w=" + searchKeyword + "&s=lp";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));

            onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.lowPriceRadioButton)).check(matches(isChecked()));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~, s=hp
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=hsr&dp=" + dp + "&n=" + nights + "&w=" + searchKeyword + "&s=hp";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));

            onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.highPriceRadioButton)).check(matches(isChecked()));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567, s=r
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=hsr&week=" + String.valueOf(weeks) + "&n=" + nights + "&w=" + searchKeyword + "&s=r";
            StayBookDateTime weekBookDateTime = getStayBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks, nights);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", weekBookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , weekBookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));

            onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.satisfactionRadioButton)).check(matches(isChecked()));
        }
    }

    private StayBookDateTime getStayBookDateTime(String currentDateTime, char[] weeks, int nights) throws Exception
    {
        String bookDateTime = DailyCalendar.searchClosedDayOfWeek(currentDateTime, weeks);

        StayBookDateTime stayBookDateTime = new StayBookDateTime();
        stayBookDateTime.setCheckInDateTime(bookDateTime);
        stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        return stayBookDateTime;
    }

    @Test
    public void test_DeepLink_검색결과_해외스테이() throws Exception
    {
        final String searchKeyword = "오사카(및 인근지역)";
        final int index = 179897;
        final String categoryKey = "region";
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        // 해외 검색은 시간이 많이 걸려서 3배로 늘렸다.

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=sosrl&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&i=" + index + "&t=" + searchKeyword + "&ck=" + categoryKey;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 4000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박 돋 성인2, 아동0"//
                , bookDateTime.getCheckInDateTime("M.d(EEE)")//
                , bookDateTime.getCheckOutDateTime("M.d(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=sosrl&dp=" + dp + "&n=" + nights + "&i=" + index + "&t=" + searchKeyword + "&ck=" + categoryKey;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 4000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박 돋 성인2, 아동0"//
                , bookDateTime.getCheckInDateTime("M.d(EEE)")//
                , bookDateTime.getCheckOutDateTime("M.d(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=sosrl&week=" + String.valueOf(weeks) + "&n=" + nights + "&i=" + index + "&t=" + searchKeyword + "&ck=" + categoryKey;
            StayBookDateTime weekBookDateTime = getStayBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks, nights);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 4000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박 돋 성인2, 아동0"//
                , weekBookDateTime.getCheckInDateTime("M.d(EEE)")//
                , weekBookDateTime.getCheckOutDateTime("M.d(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd, s=lp
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=sosrl&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&i=" + index + "&t=" + searchKeyword + "&ck=" + categoryKey + "&s=lp";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 4000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박 돋 성인2, 아동0"//
                , bookDateTime.getCheckInDateTime("M.d(EEE)")//
                , bookDateTime.getCheckOutDateTime("M.d(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~, s=hp
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=sosrl&dp=" + dp + "&n=" + nights + "&i=" + index + "&t=" + searchKeyword + "&ck=" + categoryKey + "&s=hp";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 4000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박 돋 성인2, 아동0"//
                , bookDateTime.getCheckInDateTime("M.d(EEE)")//
                , bookDateTime.getCheckOutDateTime("M.d(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567, s=r
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=sosrl&week=" + String.valueOf(weeks) + "&n=" + nights + "&i=184245&t=" + searchKeyword + "&ck=" + categoryKey + "&s=r";
            StayBookDateTime weekBookDateTime = getStayBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks, nights);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 4000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박 돋 성인2, 아동0"//
                , weekBookDateTime.getCheckInDateTime("M.d(EEE)")//
                , weekBookDateTime.getCheckOutDateTime("M.d(EEE)"), nights))));
        }
    }

    @Test
    public void test_DeepLink_검색결과_고메() throws Exception
    {
        final String searchKeyword = "뷔페";
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final GourmetBookDateTime bookDateTime = new GourmetBookDateTime();
        bookDateTime.setVisitDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=gsr&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&w=" + searchKeyword;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=gsr&dp=" + dp + "&w=" + searchKeyword;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=gsr&week=" + String.valueOf(weeks) + "&w=" + searchKeyword;
            GourmetBookDateTime weekBookDateTeim = getGourmetBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(weekBookDateTeim.getVisitDateTime("MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd, s=lp
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=gsr&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&w=" + searchKeyword + "&s=lp";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));

            onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.lowPriceRadioButton)).check(matches(isChecked()));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~, s=hp
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=gsr&dp=" + dp + "&w=" + searchKeyword + "&s=hp";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));

            onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.highPriceRadioButton)).check(matches(isChecked()));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567, s=r
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=gsr&week=" + String.valueOf(weeks) + "&w=" + searchKeyword + "&s=r";
            GourmetBookDateTime weekBookDateTime = getGourmetBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(searchKeyword)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(weekBookDateTime.getVisitDateTime("MM.dd(EEE)"))));

            onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.satisfactionRadioButton)).check(matches(isChecked()));
        }
    }

    private GourmetBookDateTime getGourmetBookDateTime(String currentDateTime, char[] weeks) throws Exception
    {
        String bookDateTime = DailyCalendar.searchClosedDayOfWeek(currentDateTime, weeks);

        GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
        gourmetBookDateTime.setVisitDateTime(bookDateTime);

        return gourmetBookDateTime;
    }

    @Test
    public void test_DeepLink_검색결과_캠페인태그_국내스테이() throws Exception
    {
        final String campaignTag = "#쿠폰특가";
        final int campaignTagIndex = 111;

        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=stay&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&i=" + campaignTagIndex;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=stay&dp=" + dp + "&n=" + nights + "&i=" + campaignTagIndex;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));
        }
    }

    @Test
    public void test_DeepLink_검색결과_캠페인태그_고메() throws Exception
    {
        final String campaignTag = "#호텔뷔페";
        final int campaignTagIndex = 207;

        final int randomDay = new Random(System.currentTimeMillis()).nextInt(30) + 1;

        final GourmetBookDateTime bookDateTime = new GourmetBookDateTime();
        bookDateTime.setVisitDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=gourmet&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&i=" + campaignTagIndex;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=gourmet&dp=" + dp + "&i=" + campaignTagIndex;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
            onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));
        }
    }

    private class Area
    {
        int provinceIndex;
        int areaIndex;
        int subwayIndex;

        String name;
        String categoryCode; // "hotel", "boutique", "pension", "resort"

        public Area(int provinceIndex, int areaIndex, String name, String categoryCode)
        {
            this.provinceIndex = provinceIndex;
            this.areaIndex = areaIndex;
            this.name = name;
            this.categoryCode = categoryCode;
        }

        public Area(int subwayIndex, String name, String categoryCode)
        {
            this.subwayIndex = subwayIndex;
            this.name = name;
            this.categoryCode = categoryCode;
        }

        public String getCategoryName()
        {
            switch (categoryCode)
            {
                case "hotel":
                    return "국내호텔";
                case "boutique":
                    return "부띠끄";
                case "pension":
                    return "펜션";
                case "resort":
                    return "리조트";
            }

            return null;
        }
    }

    @Test
    public void test_DeepLink_숏컷() throws Exception
    {
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        List<Area> areaList = new ArrayList<>();
        areaList.add(new Area(20, 0, "부산", "hotel"));
        areaList.add(new Area(20, 35, "사상/명지/화명", "hotel"));
        areaList.add(new Area(34, "을지로4가역", "hotel"));

        areaList.add(new Area(45, 0, "제주", "boutique"));
        areaList.add(new Area(45, 159, "서귀포시", "boutique"));
        areaList.add(new Area(121, "수유역", "boutique"));

        areaList.add(new Area(10, 0, "경기", "pension"));
        areaList.add(new Area(10, 163, "안산/대부도/옹진군", "pension"));
        areaList.add(new Area(845, "해운대역", "pension"));

        areaList.add(new Area(25, 0, "경상", "resort"));
        areaList.add(new Area(845, "해운대역", "resort"));


        for (Area area : areaList)
        {
            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd
            {
                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=scl&cc=" + area.categoryCode + "&si=" + area.subwayIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=scl&cc=" + area.categoryCode + "&pi=" + area.provinceIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=scl&cc=" + area.categoryCode + "&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText(area.getCategoryName())));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));
            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            // dp=0~
            {
                final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));

                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=scl&cc=" + area.categoryCode + "&si=" + area.subwayIndex + "&dp=" + dp + "&n=" + nights;
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=scl&cc=" + area.categoryCode + "&pi=" + area.provinceIndex + "&dp=" + dp + "&n=" + nights;
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=scl&cc=" + area.categoryCode + "&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&dp=" + dp + "&n=" + nights;
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText(area.getCategoryName())));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));
            }
        }
    }

    @Test
    public void test_DeepLink_홈_이벤트상세() throws InterruptedException
    {
        final String title = "7박 하면 1박 무료";
        final String description = "클래스가 다른 혜택, 7박 하면 1박 무료! 얼마나 강력한 혜택인지 지금 확인해보세요!";
        final String url = "http://img.dailyhotel.me/resources/images/home_event/7days_sm.jpg";
        final String imageUrl = "http://img.dailyhotel.me/resources/images/home_event/7days_sm.jpg";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hed&t=" + title + "&url=" + url + "&desc=" + description + "&iurl=" + imageUrl;

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText(title)));
    }

    @Test
    public void test_DeepLink_홈_추천업장_국내스테이() throws Exception
    {
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        final int index = 295;
        final String name = "부산 오션뷰 BEST 10";

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {

            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hrpl&i=" + index + "&pt=stay&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(name)));
            onView(withId(R.id.calendarTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), nights))));
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hrpl&i=" + index + "&pt=stay&dp=" + dp + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(name)));
            onView(withId(R.id.calendarTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), nights))));
        }
    }

    @Test
    public void test_DeepLink_홈_추천업장_고메() throws Exception
    {
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final GourmetBookDateTime bookDateTime = new GourmetBookDateTime();
        bookDateTime.setVisitDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);

        final int index = 300;
        final String name = "벚꽃 옆 레스토랑";

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {

            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hrpl&i=" + index + "&pt=gourmet&d=" + bookDateTime.getVisitDateTime("yyyyMMdd");

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(withId(R.id.titleTextView)).check(matches(withText(name)));
            onView(withId(R.id.calendarTextView)).check(matches(withText(bookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"))));
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hrpl&i=" + index + "&pt=gourmet&dp=" + dp;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(withId(R.id.titleTextView)).check(matches(withText(name)));
            onView(withId(R.id.calendarTextView)).check(matches(withText(bookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"))));
        }
    }

    @Test
    public void test_DeepLink_국내스테이목록() throws Exception
    {
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        List<Area> areaList = new ArrayList<>();
        areaList.add(new Area(20, 0, "부산", null));
        areaList.add(new Area(20, 583, "사상/명지/화명/하단", null));
        areaList.add(new Area(34, "을지로4가역", null));

        for (Area area : areaList)
        {
            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd
            {
                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=hl&si=" + area.subwayIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&pi=" + area.provinceIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리호텔")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));
            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            // dp=0~
            {
                final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));

                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=hl&si=" + area.subwayIndex + "&dp=" + dp + "&n=" + nights;
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&pi=" + area.provinceIndex + "&dp=" + dp + "&n=" + nights;
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&dp=" + dp + "&n=" + nights;
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리호텔")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));
            }

            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd, s=lp
            {
                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=hl&si=" + area.subwayIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&s=lp";
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&pi=" + area.provinceIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&s=lp";
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&s=lp";
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리호텔")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));

                onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.lowPriceRadioButton)).check(matches(isChecked()));
            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            // dp=0~, s=hp
            {
                final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));

                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=hl&si=" + area.subwayIndex + "&dp=" + dp + "&n=" + nights + "&s=hp";
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=hl&pi=" + area.provinceIndex + "&dp=" + dp + "&n=" + nights + "&s=hp";
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=hl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&dp=" + dp + "&n=" + nights + "&s=hp";
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리호텔")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));

                onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.highPriceRadioButton)).check(matches(isChecked()));
            }

            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd, s=r
            {
                final String deepLink;

                if (area.subwayIndex > 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=hl&si=" + area.subwayIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&s=r";
                } else if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=hl&pi=" + area.provinceIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&s=r";
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=hl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&s=r";
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리호텔")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s", bookDateTime.getCheckInDateTime("M.d(EEE)")//
                    , bookDateTime.getCheckOutDateTime("M.d(EEE)")))));

                onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.satisfactionRadioButton)).check(matches(isChecked()));
            }
        }
    }

    @Test
    public void test_DeepLink_국내스테이상세() throws Exception
    {
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 2;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        final int index = 15731;
        final String name = "코트야드 메리어트 서울 남대문";

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=hd&i=" + index + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"))));
            onView(withId(R.id.date2TextView)).check(matches(withText(bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"))));
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=hd&i=" + index + "&dp=" + dp + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"))));
            onView(withId(R.id.date2TextView)).check(matches(withText(bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=hd&i=" + index + "&week=" + String.valueOf(weeks) + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));
            StayBookDateTime weekBookDateTime = getStayBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks, nights);

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(weekBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"))));
            onView(withId(R.id.date2TextView)).check(matches(withText(weekBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd showCalendar
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=hd&i=" + index + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&cal=1";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(withId(R.id.titleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), nights))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd, showVR
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=hd&i=" + index + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&vr=1";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            if (DailyPreference.getInstance(getInstrumentation().getContext()).isTrueVRCheckDataGuide() == false)
            {
                onView(withId(R.id.positiveTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(5000));
            }

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            } catch (NoMatchingViewException e)
            {
                onView(withId(R.id.backView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            }
        }
    }

    @Test
    public void test_DeepLink_고메목록() throws Exception
    {
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(30) + 1;

        final GourmetBookDateTime bookDateTime = new GourmetBookDateTime();
        bookDateTime.setVisitDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);

        List<Area> areaList = new ArrayList<>();
        areaList.add(new Area(5, 0, "서울", null));
        areaList.add(new Area(5, 3, "종로/광화문/삼청/종각", null));

        for (Area area : areaList)
        {
            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd
            {
                final String deepLink;

                if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=gl&pi=" + area.provinceIndex + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd");
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=gl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd");
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리고메")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(bookDateTime.getVisitDateTime("M.d(EEE)"))));
            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            // dp=0~
            {
                final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));

                final String deepLink;

                if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=gl&pi=" + area.provinceIndex + "&dp=" + dp;
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=gl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&dp=" + dp;
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리고메")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(bookDateTime.getVisitDateTime("M.d(EEE)"))));
            }

            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd, s=lp
            {
                final String deepLink;

                if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=gl&pi=" + area.provinceIndex + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&s=lp";
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=gl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&s=lp";
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리고메")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(bookDateTime.getVisitDateTime("M.d(EEE)"))));

                onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.lowPriceRadioButton)).check(matches(isChecked()));
            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            // dp=0~, s=hp
            {
                final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));

                final String deepLink;

                if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=gl&pi=" + area.provinceIndex + "&dp=" + dp + "&s=hp";
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=gl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&dp=" + dp + "&s=hp";
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리고메")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(bookDateTime.getVisitDateTime("M.d(EEE)"))));

                onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.highPriceRadioButton)).check(matches(isChecked()));
            }

            ////////////////////////////////////////////////////////////////////////////////////////////
            // d=yyyyMMMdd, s=r
            {
                final String deepLink;

                if (area.areaIndex == 0)
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=gl&pi=" + area.provinceIndex + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&s=r";
                } else
                {
                    deepLink = "dailyhotel://dailyhotel.co.kr?vc=16&v=gl&pi=" + area.provinceIndex + "&ai=" + area.areaIndex + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&s=r";
                }

                launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

                onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
                onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리고메")));
                onView(withId(R.id.regionTextView)).check(matches(withText(area.name)));
                onView(withId(R.id.dateTextView)).check(matches(withText(bookDateTime.getVisitDateTime("M.d(EEE)"))));

                onView(withId(R.id.filterActionTextView)).perform(ViewActions.click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.satisfactionRadioButton)).check(matches(isChecked()));
            }
        }
    }

    @Test
    public void test_DeepLink_고메상세() throws Exception
    {
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 2;

        final GourmetBookDateTime bookDateTime = new GourmetBookDateTime();
        bookDateTime.setVisitDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);

        final int index = 51597;
        final String name = "도쿄등심 압구정점";

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=gd&i=" + index + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd");

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(bookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"))));
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=gd&i=" + index + "&dp=" + dp;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(bookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // week=1234567
        {
            final char[] weeks = {'2', '4', '6'};
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=gd&i=" + index + "&week=" + String.valueOf(weeks);
            GourmetBookDateTime weekBookDateTime = getGourmetBookDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), weeks);

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {

            }

            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(weekBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"))));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd showCalendar
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=15&v=gd&i=" + index + "&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&cal=1";

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

            try
            {
                onView(withId(R.id.messageTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                onView(isRoot()).perform(ViewActions.pressBack());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e)
            {
                onView(withId(R.id.titleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"))));
            }
        }
    }

    @Test
    public void test_DeepLink_해외스테이상세() throws Exception
    {
        final int nights = new Random(System.currentTimeMillis()).nextInt(NIGHTS_RANGE) + 1;
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(DAYS_RANGE) + 1;

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        final int index = 251391;
        final String name = "호텔 WBF 남바 모토마치";

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        {
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=20&v=pd&pt=stayOutbound&i=" + index + "&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , bookDateTime.getCheckInDateTime("M.d(EEE)")//
                , bookDateTime.getCheckOutDateTime("M.d(EEE)")//
                , bookDateTime.getNights()))));
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~
        {
            final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
            final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=20&v=pd&pt=stayOutbound&i=" + index + "&dp=" + dp + "&n=" + nights;

            launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

            onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
            onView(Matchers.allOf(withId(R.id.nameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.titleInformationView))))).check(matches(withText(name)));
            onView(withId(R.id.date1TextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , bookDateTime.getCheckInDateTime("M.d(EEE)")//
                , bookDateTime.getCheckOutDateTime("M.d(EEE)")//
                , bookDateTime.getNights()))));
        }
    }

    @Test
    public void test_DeepLink_예약내역() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=bl";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));
    }

    @Test
    public void test_DeepLink_예약상세_국내스테이() throws InterruptedException
    {
        final String aggregationId = "272b7cb5-a587-4d5b-9d45-a14f1f23f573";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=25&v=bd&agi=" + aggregationId + "&pt=stay";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));

        if (DailyHotel.isLogin() == true)
        {
            onView(Matchers.allOf(withId(R.id.placeNameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.placeInformationLayout))))).check(matches(withText("삼성 캘리포니아")));
        }
    }

    @Test
    public void test_DeepLink_예약상세_해외스테이() throws InterruptedException
    {
        final String aggregationId = "d6cb3662-9e75-4100-af04-9b92226d290b";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=25&v=bd&agi=" + aggregationId + "&pt=stayOutbound";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.placeNameTextView)).check(matches(withText("호텔 WBF 남바 모토마치")));
        }
    }

    @Test
    public void test_DeepLink_예약상세_고메() throws InterruptedException
    {
        final String aggregationId = "a28a351a-5a73-46f7-8e50-7797dd3e9e63";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=25&v=bd&agi=" + aggregationId + "&pt=gourmet";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));

        if (DailyHotel.isLogin() == true)
        {
            onView(Matchers.allOf(withId(R.id.placeNameTextView), isDescendantOfA(Matchers.allOf(withId(R.id.placeInformationLayout))))).check(matches(withText("탑클라우드 23")));
        }
    }

    @Test
    public void test_DeepLink_마이데일리() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=md";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("마이데일리")));
    }

    @Test
    public void test_DeepLink_적립금() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=b";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("적립금")));
        } else
        {
            onView(withId(R.id.messageTextView)).check(matches(withText("로그인 후 적립금을 확인할 수 있습니다.\n로그인 할까요?")));
        }
    }

    @Test
    public void test_DeepLink_로그인() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=login";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("마이데일리")));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("로그인")));
        }
    }

    @Test
    public void test_DeepLink_회원가입() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=su";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.homeContentLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("회원가입(1/2)")));
        }
    }

    @Test
    public void test_DeepLink_할인쿠폰함() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=cl";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("할인쿠폰함")));
        } else
        {
            onView(withId(R.id.messageTextView)).check(matches(withText("로그인 후 할인 쿠폰함을 확인할 수 있습니다.\n로그인 할까요?")));
        }
    }

    @Test
    public void test_DeepLink_할인쿠폰코드등록() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=cr";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("할인쿠폰 코드 등록")));
        } else
        {
            onView(withId(R.id.messageTextView)).check(matches(withText("로그인 후 할인쿠폰 등록을 확인할 수 있습니다.\n로그인 할까요?")));
        }
    }

    @Test
    public void test_DeepLink_내정보() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=pr";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("내정보")));
        } else
        {
            onView(withId(R.id.messageTextView)).check(matches(withText("로그인 후 내정보를 확인할 수 있습니다.\n로그인 할까요?")));
        }
    }

    @Test
    public void test_DeepLink_내정보_생일정보수정() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=prbd";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.titleTextView)).check(matches(withText("생일 선택")));
        } else
        {
            onView(withId(R.id.messageTextView)).check(matches(withText("로그인 후 생일정보를 확인할 수 있습니다.\n로그인 할까요?")));
        }
    }

    @Test
    public void test_DeepLink_데일리리워드() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=21&v=reward";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리 리워드")));
    }

    @Test
    public void test_DeepLink_더보기() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=m";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("더보기")));
    }

    @Test
    public void test_DeepLink_이벤트목록() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=el";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("이벤트")));
    }

    @Test
    public void test_DeepLink_이벤트상세() throws InterruptedException
    {
        final String title = "7박 하면 1박 무료";
        final String description = "클래스가 다른 혜택, 7박 하면 1박 무료! 얼마나 강력한 혜택인지 지금 확인해보세요!";
        final String url = "http://img.dailyhotel.me/resources/images/home_event/7days_sm.jpg";
        final String imageUrl = "http://img.dailyhotel.me/resources/images/home_event/7days_sm.jpg";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=ed&t=" + title + "&url=" + url + "&desc=" + description + "&iurl=" + imageUrl;

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText(title)));
    }

    @Test
    public void test_DeepLink_공지사항상세() throws InterruptedException
    {
        final String title = "[공지] 친구추천 적립금 이벤트 종료 안내";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=nd&url=http://m.dailyhotel.co.kr/banner/171120savedmoney/&t=" + title + "&ni=46";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText(title)));
    }

    @Test
    public void test_DeepLink_자주묻는질문() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=faq";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("자주 묻는 질문")));
    }

    @Test
    public void test_DeepLink_약관및정책() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=tnp";

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink)));

        onView(isRoot()).perform(waitFor(DELAY_SECONDS * 1000));
        onView(withId(R.id.daily_titleTextView)).check(matches(withText("약관 및 정책")));
    }
}