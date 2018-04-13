package com.twoheart.dailyhotel.firebase

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.twoheart.dailyhotel.firebase.model.SplashDelegate
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyRemoteConfigTest {
    private lateinit var remoteConfig: DailyRemoteConfig
    private lateinit var context: Context
    private lateinit var preference: DailyRemoteConfigPreference

    @Before
    @Throws(Exception::class)
    fun setUp() {
        context = InstrumentationRegistry.getContext()
        remoteConfig = DailyRemoteConfig(context)
        preference = DailyRemoteConfigPreference.getInstance(context)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    fun testGetVersion() {
        val testJson01 = "{\"stores\":{\"play\":{\"versionCode\":{\"optional\":\"2010501\",\"force\":\"2010501\"}},\"one\":{\"versionCode\":{\"optional\":\"2010501\",\"force\":\"2010501\"}}},\"messages\":{\"optional\":{\"title\":\"업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"},\"force\":{\"title\":\"필수 업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"}}}"
        val pair01 = remoteConfig.getVersionNsetMessages(context, testJson01)
        assertNotNull(pair01)
        assertEquals("2010501", pair01?.first)
        assertEquals("2010501", pair01?.second)

        assertEquals("{\"title\":\"업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"}", preference.remoteConfigUpdateOptional)
        assertEquals("{\"title\":\"필수 업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"}", preference.remoteConfigUpdateForce)

        val testJson02 = "{}"
        val pair02 = remoteConfig.getVersionNsetMessages(context, testJson02)
        assertNotNull(pair02)
        assertNull(pair02?.first)
        assertNull(pair02?.second)

        val testJson03 = "{\"stores\":{\"play\":{\"versionCode\":{\"optional\":\"2020300\",\"force\":\"2010501\"}},\"one\":{\"versionCode\":{\"optional\":\"2010501\",\"force\":\"2010501\"}}},\"messages\":{\"optional\":{\"title\":\"업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"},\"force\":{\"title\":\"필수 업데이트 알림\",\"message\":\"지금 업데이트하여\\n더욱 편리해진 데일리호텔을 경험해보세요!\"}}}"
        val pair03 = remoteConfig.getVersionNsetMessages(context, testJson03)
        assertNotNull(pair03)
        assertEquals("2020300", pair03?.first)
        assertEquals("2010501", pair03?.second)

        val testJson04 = ""
        val pair04 = remoteConfig.getVersionNsetMessages(context, testJson04)
        assertNull(pair04)
    }

    @Test
    fun testUpdateImage() {
        val testJson01 = "{\"imageUpdate\":{\"updateTime\":\"2018-01-10T10:00:00+09:00\",\"url\":{\"hdpi\":\"http://img.dailyhotel.me/firebase_splash/180103_splash_hdpi.jpg\",\"xhdpi\":\"http://img.dailyhotel.me/firebase_splash/180103_splash_xhdpi.jpg\",\"xxxhdpi\":\"http://img.dailyhotel.me/firebase_splash/180103_splash_xxxhdpi.jpg\"}}}"

        val splashDelegate01 = SplashDelegate(testJson01)

        assertEquals("2018-01-10T10:00:00+09:00", splashDelegate01.updateTime)
        assertFalse(DailyTextUtils.isTextEmpty(splashDelegate01.getUrl(context)))

        ExLog.d("testUpdateImage - getUrl : " + splashDelegate01.getUrl(context))

        val testJson02 = "{}"
        val splashDelegate02 = SplashDelegate(testJson02)
        assertNull(splashDelegate02.updateTime)
        assertTrue(DailyTextUtils.isTextEmpty(splashDelegate02.getUrl(context)))

        ExLog.d("testUpdateImage - getUrl : " + splashDelegate02.getUrl(context))
    }

    @Test
    fun testSetCompany() {
        val testJson01 = "{\"name\":\"(주)데일리\",\"ceo\":\"신인식\",\"bizRegNumber\":\"144-81-15781\",\"itcRegNumber\":\"2016-서울강남-00726\",\"address1\":\"서울시 강남구 테헤란로 20길 20 삼정빌딩 10,11층\",\"phoneNumber1\":\"1800-9120\",\"fax1\":\"02-6455-9331\",\"privacyManager\":\"privacy.korea@dailyhotel.com\"}"

        remoteConfig.setCompany(context, testJson01)

        assertEquals("(주)데일리", preference.remoteConfigCompanyName)
        assertEquals("신인식", preference.remoteConfigCompanyCEO)
        assertEquals("144-81-15781", preference.remoteConfigCompanyBizRegNumber)
        assertEquals("2016-서울강남-00726", preference.remoteConfigCompanyItcRegNumber)
        assertEquals("서울시 강남구 테헤란로 20길 20 삼정빌딩 10,11층", preference.remoteConfigCompanyAddress)
        assertEquals("1800-9120", preference.remoteConfigCompanyPhoneNumber)
        assertEquals("02-6455-9331", preference.remoteConfigCompanyFax)
        assertEquals("privacy.korea@dailyhotel.com", preference.remoteConfigCompanyPrivacyEmail)
    }

    @Test
    fun testSetPayment() {
        val testJson01 = "{\"paymentTypeEnabled\":{\"stay\":{\"easyCard\":true,\"card\":true,\"phoneBill\":true,\"virtualAccount\":true},\"gourmet\":{\"easyCard\":true,\"card\":true,\"phoneBill\":true,\"virtualAccount\":true},\"stayOutbound\":{\"easyCard\":true,\"card\":true,\"phoneBill\":true}}}"

        remoteConfig.setPayment(context, testJson01)

        assertTrue(preference.isRemoteConfigStaySimpleCardPaymentEnabled)
        assertTrue(preference.isRemoteConfigStayCardPaymentEnabled)
        assertTrue(preference.isRemoteConfigStayPhonePaymentEnabled)
        assertTrue(preference.isRemoteConfigStayVirtualPaymentEnabled)

        assertTrue(preference.isRemoteConfigStayOutboundSimpleCardPaymentEnabled)
        assertTrue(preference.isRemoteConfigStayOutboundCardPaymentEnabled)
        assertTrue(preference.isRemoteConfigStayOutboundPhonePaymentEnabled)

        assertTrue(preference.isRemoteConfigGourmetSimpleCardPaymentEnabled)
        assertTrue(preference.isRemoteConfigGourmetCardPaymentEnabled)
        assertTrue(preference.isRemoteConfigGourmetPhonePaymentEnabled)
        assertTrue(preference.isRemoteConfigGourmetVirtualPaymentEnabled)

        val testJson02 = "{\"paymentTypeEnabled\":{\"stay\":{\"easyCard\":false,\"card\":false,\"phoneBill\":false,\"virtualAccount\":false},\"gourmet\":{\"easyCard\":false,\"card\":false,\"phoneBill\":false,\"virtualAccount\":false},\"stayOutbound\":{\"easyCard\":false,\"card\":false,\"phoneBill\":false}}}"

        remoteConfig.setPayment(context, testJson02)

        assertFalse(preference.isRemoteConfigStaySimpleCardPaymentEnabled)
        assertFalse(preference.isRemoteConfigStayCardPaymentEnabled)
        assertFalse(preference.isRemoteConfigStayPhonePaymentEnabled)
        assertFalse(preference.isRemoteConfigStayVirtualPaymentEnabled)

        assertFalse(preference.isRemoteConfigStayOutboundSimpleCardPaymentEnabled)
        assertFalse(preference.isRemoteConfigStayOutboundCardPaymentEnabled)
        assertFalse(preference.isRemoteConfigStayOutboundPhonePaymentEnabled)

        assertFalse(preference.isRemoteConfigGourmetSimpleCardPaymentEnabled)
        assertFalse(preference.isRemoteConfigGourmetCardPaymentEnabled)
        assertFalse(preference.isRemoteConfigGourmetPhonePaymentEnabled)
        assertFalse(preference.isRemoteConfigGourmetVirtualPaymentEnabled)
    }

    fun testSetMessages() {
        val testJson01 = "{\"updateTime\":\"2017-12-15T11:00:00+10:00\",\"login\":{\"text01\":\"10초 회원가입하고\\n지금 10만원 할인쿠폰팩 받으세요!!\"},\"signup\":{\"text01\":\"지금 회원가입하면 *10만원 할인쿠폰팩*을 드려요!\",\"text02\":\"데일리호텔 회원이 되어주셔서\\n진심으로 감사합니다!\"},\"home\":{\"messageArea\":{\"login\":{\"enabled\":true},\"logout\":{\"enabled\":true,\"title\":\"회원가입하고 15,000 쿠폰 받으세요!\",\"callToAction\":\"지금 가입하기\"}},\"categoryArea\":{\"enabled\":true}}}"

        remoteConfig.setMessages(context, testJson01)

        assertEquals("10초 회원가입하고\\n지금 10만원 할인쿠폰팩 받으세요!!", preference.remoteConfigTextLoginText01)
        assertEquals("지금 회원가입하면 *10만원 할인쿠폰팩*을 드려요!", preference.remoteConfigTextSignUpText01)
        assertEquals("데일리호텔 회원이 되어주셔서\\n진심으로 감사합니다!", preference.remoteConfigTextSignUpText02)
        assertTrue(preference.isRemoteConfigHomeMessageAreaLoginEnabled)
        assertTrue(preference.isRemoteConfigHomeMessageAreaLogoutEnabled)
        assertEquals("회원가입하고 15,000 쿠폰 받으세요!", preference.remoteConfigHomeMessageAreaLogoutTitle)
        assertEquals("지금 가입하기", preference.remoteConfigHomeMessageAreaLogoutCallToAction)
        assertTrue(preference.remoteConfigHomeCategoryEnabled)
    }
}