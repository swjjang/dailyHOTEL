package com.daily.dailyhotel.screen.home.stay.inbound.detail;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetail;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayDetailAnalyticsImpl implements StayDetailPresenter.StayDetailAnalyticsInterface
{
    private StayDetailAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayDetailAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public StayDetailAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity, StayBookDateTime stayBookDateTime, StayDetail stayDetail, int priceFromList)
    {

    }

    @Override
    public void onScreenRoomList(Activity activity)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventShareKakaoClick(Activity activity, boolean login, String userType, boolean benefitAlarm, int gourmetIndex, String gourmetName)
    {

    }

    @Override
    public void onEventShareSmsClick(Activity activity, boolean login, String userType, boolean benefitAlarm, int gourmetIndex, String gourmetName)
    {

    }

    @Override
    public void onEventDownloadCoupon(Activity activity, String stayName)
    {

    }

    @Override
    public void onEventDownloadCouponByLogin(Activity activity, boolean login)
    {

    }

    @Override
    public void onEventShare(Activity activity)
    {

    }

    @Override
    public void onEventHasHiddenMenus(Activity activity)
    {

    }

    @Override
    public void onEventChangedPrice(Activity activity, boolean deepLink, String stayName, boolean soldOut)
    {

    }

    @Override
    public void onEventCalendarClick(Activity activity)
    {

    }

    @Override
    public void onEventOrderClick(Activity activity, StayBookDateTime stayBookDateTime, String stayName, String menuName, String category, int discountPrice)
    {

    }

    @Override
    public void onEventScrollTopMenuClick(Activity activity, String stayName)
    {

    }

    @Override
    public void onEventMenuClick(Activity activity, int menuIndex, int position)
    {

    }

    @Override
    public void onEventTrueReviewClick(Activity activity)
    {

    }

    @Override
    public void onEventMoreMenuClick(Activity activity, boolean opened, int stayIndex)
    {

    }

    @Override
    public void onEventImageClick(Activity activity, String stayName)
    {

    }

    @Override
    public void onEventConciergeClick(Activity activity)
    {

    }

    @Override
    public void onEventMapClick(Activity activity, String stayName)
    {

    }

    @Override
    public void onEventClipAddressClick(Activity activity, String stayName)
    {

    }

    @Override
    public void onEventWishClick(Activity activity, StayBookDateTime stayBookDateTime, StayDetail stayDetail, int priceFromList, boolean myWish)
    {

    }

    @Override
    public void onEventCallClick(Activity activity)
    {

    }

    @Override
    public void onEventFaqClick(Activity activity)
    {

    }

    @Override
    public void onEventHappyTalkClick(Activity activity)
    {

    }
}
