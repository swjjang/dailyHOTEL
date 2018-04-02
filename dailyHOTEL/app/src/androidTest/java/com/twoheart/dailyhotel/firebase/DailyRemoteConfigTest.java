package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DailyRemoteConfigTest
{
    private DailyRemoteConfig mRemoteConfig;
    private Context mContext;
    private DailyRemoteConfigPreference mPreference;

    @Before
    public void setUp() throws Exception
    {
        mContext = InstrumentationRegistry.getContext();
        mRemoteConfig = new DailyRemoteConfig(mContext);
        mPreference = DailyRemoteConfigPreference.getInstance(mContext);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void writeDetailTrueView()
    {
        String testJson = "{\"productNameVisible\":{\"stay\":true,\"stayOutbound\":true,\"gourmet\":true}}";

        mRemoteConfig.writeTrueReview(mContext, testJson);

        assertTrue(mPreference.isKeyRemoteConfigStayDetailTrueReviewProductVisible());
        assertTrue(mPreference.isKeyRemoteConfigStayOutboundDetailTrueReviewProductVisible());
        assertTrue(mPreference.isKeyRemoteConfigGourmetDetailTrueReviewProductVisible());

        String testJson01 = "{}";

        mRemoteConfig.writeTrueReview(mContext, testJson01);

        assertTrue(mPreference.isKeyRemoteConfigStayDetailTrueReviewProductVisible());
        assertTrue(mPreference.isKeyRemoteConfigStayOutboundDetailTrueReviewProductVisible());
        assertTrue(mPreference.isKeyRemoteConfigGourmetDetailTrueReviewProductVisible());

        String testJson02 = "{\"productNameVisible\":{\"stay\":false,\"stayOutbound\":false,\"gourmet\":false}}";

        mRemoteConfig.writeTrueReview(mContext, testJson02);

        assertFalse(mPreference.isKeyRemoteConfigStayDetailTrueReviewProductVisible());
        assertFalse(mPreference.isKeyRemoteConfigStayOutboundDetailTrueReviewProductVisible());
        assertFalse(mPreference.isKeyRemoteConfigGourmetDetailTrueReviewProductVisible());
    }
}