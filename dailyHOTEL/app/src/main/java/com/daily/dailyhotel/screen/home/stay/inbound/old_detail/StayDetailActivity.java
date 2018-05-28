package com.daily.dailyhotel.screen.home.stay.inbound.old_detail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayDetailActivity extends BaseActivity<StayDetailPresenter>
{
    public static final int NONE_PRICE = -1;

    static final int REQUEST_CODE_CALENDAR = 10000;
    static final int REQUEST_CODE_HAPPYTALK = 10002;
    static final int REQUEST_CODE_MAP = 10004;
    static final int REQUEST_CODE_IMAGE_LIST = 10005;
    static final int REQUEST_CODE_CALL = 10006;
    static final int REQUEST_CODE_PAYMENT = 10007;
    static final int REQUEST_CODE_LOGIN = 10008;
    static final int REQUEST_CODE_PROFILE_UPDATE = 10009;
    static final int REQUEST_CODE_DOWNLOAD_COUPON = 10010;
    static final int REQUEST_CODE_LOGIN_IN_BY_WISH = 10011;
    static final int REQUEST_CODE_LOGIN_IN_BY_COUPON = 10012;
    static final int REQUEST_CODE_LOGIN_IN_BY_BOOKING = 10013;
    static final int REQUEST_CODE_TRUE_VIEW = 10014;
    static final int REQUEST_CODE_TRUE_VR = 10015;
    static final int REQUEST_CODE_NAVIGATOR = 10016;
    static final int REQUEST_CODE_REWARD = 10017;
    static final int REQUEST_CODE_WEB = 10018;

    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_MULTITRANSITION = "multiTransition";
    static final String INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE = "gradientType";
    static final String INTENT_EXTRA_DATA_LIST_PRICE = "listPrice";

    public static final String INTENT_EXTRA_DATA_WISH = "wish";
    public static final String INTENT_EXTRA_DATA_CHANGED_PRICE = "changedPrice";
    public static final String INTENT_EXTRA_DATA_SOLD_OUT = "soldOut";

    public static final int TRANS_GRADIENT_BOTTOM_TYPE_NONE = -1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_MAP = 1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_LIST = 2;

    /**
     * @param context
     * @param stayIndex
     * @param stayName
     * @param imageUrl
     * @param listPrice
     * @param checkInDateTime
     * @param checkOutDateTime
     * @param isUsedMultiTransition
     * @param gradientType
     * @param analyticsParam
     * @return
     */
    public static Intent newInstance(Context context, int stayIndex, String stayName, String imageUrl//
        , int listPrice, String checkInDateTime, String checkOutDateTime//
        , boolean isUsedMultiTransition, int gradientType//
        , StayDetailAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_LIST_PRICE, listPrice);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_MULTITRANSITION, isUsedMultiTransition);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, gradientType);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayDetailPresenter createInstancePresenter()
    {
        return new StayDetailPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
