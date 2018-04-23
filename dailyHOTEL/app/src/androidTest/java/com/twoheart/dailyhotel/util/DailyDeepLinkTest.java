package com.twoheart.dailyhotel.util;

import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;

import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class DailyDeepLinkTest
{
    @Rule
    public ActivityTestRule<com.twoheart.dailyhotel.LauncherActivity> launcherActivityActivityTestRule = new ActivityTestRule(com.twoheart.dailyhotel.LauncherActivity.class);


    @Test
    public void test_DeepLink_검색홈_국내스테이() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=searchHome&pt=stay";

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

        onView(withId(R.id.stayFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.stayOutboundFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.gourmetFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
    }

    @Test
    public void test_DeepLink_검색홈_해외스테이() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=searchHome&pt=stayOutbound";

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

        onView(withId(R.id.stayFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.stayOutboundFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.gourmetFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
    }

    @Test
    public void test_DeepLink_검색홈_고메() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=23&v=searchHome&pt=gourmet";

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

        onView(withId(R.id.stayFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.stayOutboundFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.gourmetFilterView)).check(matches((withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }

    @Test
    public void test_DeepLink_캠페인태그_국내스테이() throws Exception
    {
        final String campaignTag = "#부산 부띠끄호텔";
        final int campaignTagIndex = 210;

        final int nights = new Random(System.currentTimeMillis()).nextInt(7);
        final int randomDay = new Random(System.currentTimeMillis()).nextInt(60);

        final StayBookDateTime bookDateTime = new StayBookDateTime();
        bookDateTime.setCheckInDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);
        bookDateTime.setCheckOutDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        final String deepLink01 = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=stay&d=" + bookDateTime.getCheckInDateTime("yyyyMMdd") + "&n=" + nights + "&i=" + campaignTagIndex;

        CountDownLatch countDownLatch01 = new CountDownLatch(1);

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink01)));

        Completable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                countDownLatch01.countDown();
            }
        });

        countDownLatch01.await();

        onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
        onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
            , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~

        final int dp = DailyCalendar.compareDateDay(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
        final String deepLink02 = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=stay&dp=" + dp + "&n=" + nights + "&i=" + campaignTagIndex;

        CountDownLatch countDownLatch02 = new CountDownLatch(1);

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink02)));

        Completable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                countDownLatch02.countDown();
            }
        });

        countDownLatch02.await();

        onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
        onView(withId(R.id.subTitleTextView)).check(matches(withText(String.format(Locale.KOREA, "%s - %s, %d박", bookDateTime.getCheckInDateTime("MM.dd(EEE)")//
            , bookDateTime.getCheckOutDateTime("MM.dd(EEE)"), nights))));
    }

    @Test
    public void test_DeepLink_캠페인태그_고메() throws Exception
    {
        final String campaignTag = "#호텔뷔페";
        final int campaignTagIndex = 210;

        final int randomDay = new Random(System.currentTimeMillis()).nextInt(30);

        final GourmetBookDateTime bookDateTime = new GourmetBookDateTime();
        bookDateTime.setVisitDateTime(DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT), randomDay);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // d=yyyyMMMdd
        final String deepLink01 = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=gourmet&d=" + bookDateTime.getVisitDateTime("yyyyMMdd") + "&i=" + campaignTagIndex;

        CountDownLatch countDownLatch01 = new CountDownLatch(1);

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink01)));

        Completable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                countDownLatch01.countDown();
            }
        });

        countDownLatch01.await();

        onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
        onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dp=0~

        final int dp = DailyCalendar.compareDateDay(bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(new Date(), DailyCalendar.ISO_8601_FORMAT));
        final String deepLink02 = "dailyhotel://dailyhotel.co.kr?vc=23&v=ctl&pt=gourmet&dp=" + dp + "&i=" + campaignTagIndex;

        CountDownLatch countDownLatch02 = new CountDownLatch(1);

        launcherActivityActivityTestRule.launchActivity(new Intent().setData(Uri.parse(deepLink02)));

        Completable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                countDownLatch02.countDown();
            }
        });

        countDownLatch02.await();

        onView(withId(R.id.titleTextView)).check(matches(withText(campaignTag)));
        onView(withId(R.id.subTitleTextView)).check(matches(withText(bookDateTime.getVisitDateTime("MM.dd(EEE)"))));
    }

    @Test
    public void test_DeepLink_예약내역() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=bl";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));
    }

    @Test
    public void test_DeepLink_예약상세_국내스테이() throws InterruptedException
    {
        final String aggregationId = "";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=20&v=bd&agi=" + aggregationId + "&pt=stay";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));
    }

    @Test
    public void test_DeepLink_예약상세_해외스테이() throws InterruptedException
    {
        final String aggregationId = "";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=20&v=bd&agi=" + aggregationId + "&pt=stayOutbound";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));
    }

    @Test
    public void test_DeepLink_예약상세_고메() throws InterruptedException
    {
        final String aggregationId = "";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=20&v=bd&agi=" + aggregationId + "&pt=gourmet";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("예약내역")));
    }

    @Test
    public void test_DeepLink_마이데일리() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=md";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("마이데일리")));
    }

    @Test
    public void test_DeepLink_적립금() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=login";

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

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("적립금")));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("로그인")));
        }
    }

    @Test
    public void test_DeepLink_로그인() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=24&v=login";

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

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("마이데일리")));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("회원가입(1/2)")));
        }
    }

    @Test
    public void test_DeepLink_할인쿠폰함() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=cl";

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

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("로그인")));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("할인쿠폰함")));
        }
    }

    @Test
    public void test_DeepLink_할인쿠폰코드등록() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=cr";

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

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("로그인")));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("할인쿠폰 코드 등록")));
        }
    }

    @Test
    public void test_DeepLink_내정보() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=pr";

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

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("로그인")));
        } else
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("내정보")));
        }
    }

    @Test
    public void test_DeepLink_내정보_생일정보수정() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=prbd";

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

        if (DailyHotel.isLogin() == true)
        {
            onView(withId(R.id.daily_titleTextView)).check(matches(withText("로그인")));
        } else
        {
            onView(withId(R.id.titleTextView)).check(matches(withText("생일 선택")));
        }
    }

    @Test
    public void test_DeepLink_데일리리워드() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=21&v=reward";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("데일리 리워드")));
    }

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
    public void test_DeepLink_이벤트목록() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=el";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText(title)));
    }

    @Test
    public void test_DeepLink_공지사항상세() throws InterruptedException
    {
        final String title = "[공지] 친구추천 적립금 이벤트 종료 안내";
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=nd&url=http://m.dailyhotel.co.kr/banner/171120savedmoney/&t=" + title + "&ni=46";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText(title)));
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

    @Test
    public void test_DeepLink_약관및정책() throws InterruptedException
    {
        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=tnp";

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

        onView(withId(R.id.daily_titleTextView)).check(matches(withText("약관 및 정책")));
    }
}