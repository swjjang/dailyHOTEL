package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.firebase.model.SplashDelegate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    public void testGetVersion()
    {
        String testJson01 = "{\"stores\":{\"play\":{\"versionCode\":{\"optional\":\"2010501\",\"force\":\"2010501\"}},\"one\":{\"versionCode\":{\"optional\":\"2010501\",\"force\":\"2010501\"}}},\"messages\":{\"optional\":{\"title\":\"업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"},\"force\":{\"title\":\"필수 업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"}}}";
        Pair<String, String> pair01 = mRemoteConfig.getVersion(testJson01);
        assertNotNull(pair01);
        assertEquals("2010501", pair01.first);
        assertEquals("2010501", pair01.second);

        mRemoteConfig.

        assertEquals("업데이트 알림", );

        String testJson02 = "{}";
        Pair<String, String> pair02 = mRemoteConfig.getVersion(testJson02);
        assertNotNull(pair02);
        assertNull(pair02.first);
        assertNull(pair02.second);

        String testJson03 = "{\"play\":{\"current\":\"2.1.5\",\"force\":\"2.1.5\"},\"one\":{\"current\":\"2.1.5\",\"force\":\"2.1.5\"},\"versionCode\":{\"play\":{\"current\":\"2020300\",\"force\":\"2010501\"},\"one\":{\"current\":\"2020300\",\"force\":\"2010501\"}}}";
        Pair<String, String> pair03 = mRemoteConfig.getVersion(testJson03);
        assertNotNull(pair03);
        assertEquals("2020300", pair03.first);
        assertEquals("2010501", pair03.second);

        String testJson04 = "";
        Pair<String, String> pair04 = mRemoteConfig.getVersion(testJson04);
        assertNull(pair04);
    }

    @Test
    public void testUpdateImage()
    {
        String testJson01 = "{\"imageUpdate\":{\"updateTime\":\"2018-01-10T10:00:00+09:00\",\"url\":{\"hdpi\":\"http://img.dailyhotel.me/firebase_splash/180103_splash_hdpi.jpg\",\"xhdpi\":\"http://img.dailyhotel.me/firebase_splash/180103_splash_xhdpi.jpg\",\"xxxhdpi\":\"http://img.dailyhotel.me/firebase_splash/180103_splash_xxxhdpi.jpg\"}}}";

        SplashDelegate splashDelegate01 = new SplashDelegate(testJson01);

        assertEquals("2018-01-10T10:00:00+09:00", splashDelegate01.getUpdateTime());
        assertFalse(DailyTextUtils.isTextEmpty(splashDelegate01.getUrl(mContext)));

        ExLog.d("testUpdateImage - getUrl : " + splashDelegate01.getUrl(mContext));

        String testJson02 = "{}";
        SplashDelegate splashDelegate02 = new SplashDelegate(testJson02);
        assertNull(splashDelegate02.getUpdateTime());
        assertTrue(DailyTextUtils.isTextEmpty(splashDelegate02.getUrl(mContext)));

        ExLog.d("testUpdateImage - getUrl : " + splashDelegate02.getUrl(mContext));
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