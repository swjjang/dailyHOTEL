package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.twoheart.dailyhotel.R;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class DailyDeepLinkTest
{
    @Rule
    public ActivityTestRule<com.twoheart.dailyhotel.LauncherActivity> launcherActivityActivityTestRule = new ActivityTestRule(com.twoheart.dailyhotel.LauncherActivity.class);


    @Test
    public void test_DeepLink_더보기() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=m";

        Intent intent = new Intent();
        intent.setData(Uri.parse(deepLink));
        CountDownLatch countDownLatch = new CountDownLatch(1);

        launcherActivityActivityTestRule.launchActivity(intent);

        Completable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("더보기")));
    }

    @Test
    public void test_DeepLink_자주묻는질문() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=faq";

        Intent intent = new Intent();
        intent.setData(Uri.parse(deepLink));
        CountDownLatch countDownLatch = new CountDownLatch(1);

        launcherActivityActivityTestRule.launchActivity(intent);

        Completable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("자주 묻는 질문")));
    }
}