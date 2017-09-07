package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class GourmetDetailAnalyticsImpl implements GourmetDetailPresenter.GourmetDetailAnalyticsInterface
{
    private GourmetDetailAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(GourmetDetailAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public GourmetDetailAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

    }

    @Override
    public void onEventShareKakaoClick(Activity activity, boolean login, String userType, boolean benefitAlarm//
        , int gourmetIndex, String gourmetName)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.SERVICE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());

            if (login == true)
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.MEMBER);

                switch (userType)
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            } else
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            }

            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, benefitAlarm ? "on" : "off");
            params.put(AnalyticsManager.KeyType.SHARE_METHOD, AnalyticsManager.ValueType.KAKAO);
            params.put(AnalyticsManager.KeyType.VENDOR_ID, Integer.toString(gourmetIndex));
            params.put(AnalyticsManager.KeyType.VENDOR_NAME, gourmetName);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
                , AnalyticsManager.Action.GOURMET_ITEM_SHARE, AnalyticsManager.ValueType.KAKAO, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventShareSmsClick(Activity activity, boolean login, String userType, boolean benefitAlarm//
        , int gourmetIndex, String gourmetName)
    {
        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.SERVICE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());

            if (login == true)
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.MEMBER);

                switch (userType)
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            } else
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            }

            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, benefitAlarm ? "on" : "off");
            params.put(AnalyticsManager.KeyType.SHARE_METHOD, AnalyticsManager.ValueType.MESSAGE);
            params.put(AnalyticsManager.KeyType.VENDOR_ID, Integer.toString(gourmetIndex));
            params.put(AnalyticsManager.KeyType.VENDOR_NAME, gourmetName);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
                , AnalyticsManager.Action.GOURMET_ITEM_SHARE, AnalyticsManager.ValueType.MESSAGE, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventDownloadCoupon(Activity activity, String gourmetName)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_COUPON_DOWNLOAD, gourmetName, null);

    }

    @Override
    public void onEventDownloadCouponByLogin(Activity activity, boolean login)
    {
        if (activity == null)
        {
            return;
        }

        if (login == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.LOGIN_, null);

        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.CLOSED, null);
        }
    }
}
