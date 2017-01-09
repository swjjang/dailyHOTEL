package com.twoheart.dailyhotel;

import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<DailyHotel>
{
    protected DailyHotel application;

    public ApplicationTest()
    {
        super(DailyHotel.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        createApplication();

        application = getApplication();
    }
}