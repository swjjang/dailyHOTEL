package com.daily.dailyhotel.screen.home.stay.inbound.detail;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StayDetailPresenterTest
{
    @Rule
    public ActivityTestRule<StayDetailActivity> mActivityRule;
    private StayDetailPresenter mPresenter;

    @Before
    public void setUp() throws Exception
    {
        mActivityRule = new ActivityTestRule(StayDetailActivity.class);
        mPresenter = new StayDetailPresenter(mActivityRule.getActivity());
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void setStayBookDateTime()
    {
        final String CHECK_IN_DATE_TIME = "2017-01-01T12:00:00T+0900";
        final String CHECK_OUT_DATE_TIME = "2017-01-02T12:00:00T+0900";

        //        mPresenter.setStayBookDateTime(CHECK_IN_DATE_TIME, CHECK_OUT_DATE_TIME);
        //
        //        assertEquals(CHECK_IN_DATE_TIME, mPresenter.mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
        //        assertEquals(CHECK_OUT_DATE_TIME, mPresenter.mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
    }
}