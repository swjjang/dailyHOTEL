package com.daily.dailyhotel.screen.home.stay.outbound.detail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailActivity extends BaseActivity<StayOutboundDetailPresenter>
{
    public static final int NONE_PRICE = -1;

    static final int REQUEST_CODE_CALENDAR = 10000;
    static final int REQUEST_CODE_PEOPLE = 10001;
    static final int REQUEST_CODE_HAPPYTALK = 10002;
    static final int REQUEST_CODE_AMENITY = 10003;
    static final int REQUEST_CODE_MAP = 10004;
    static final int REQUEST_CODE_IMAGE_LIST = 10005;
    static final int REQUEST_CODE_CALL = 10006;
    static final int REQUEST_CODE_PAYMENT = 10007;
    static final int REQUEST_CODE_LOGIN = 10008;
    static final int REQUEST_CODE_PROFILE_UPDATE = 10009;
    static final int REQUEST_CODE_NAVIGATOR = 10010;
    static final int REQUEST_CODE_DETAIL = 10011;
    static final int REQUEST_CODE_PREVIEW = 10012;
    static final int REQUEST_CODE_REWARD = 10013;
    static final int REQUEST_CODE_LOGIN_IN_BY_BOOKING = 10014;
    static final int REQUEST_CODE_WEB = 10015;
    static final int REQUEST_CODE_LOGIN_IN_BY_WISH = 10016;
    static final int REQUEST_CODE_WISH_DIALOG = 10017;
    static final int REQUEST_CODE_LOGIN_IN_BY_COUPON = 10018;
    static final int REQUEST_CODE_DOWNLOAD_COUPON = 10019;

    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_STAY_ENGLISH_NAME = "stayEnglishName";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";
    static final String INTENT_EXTRA_DATA_MULTITRANSITION = "multiTransition";
    static final String INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE = "gradientType";
    static final String INTENT_EXTRA_DATA_LIST_PRICE = "listPrice";

    public static final int TRANS_GRADIENT_BOTTOM_TYPE_NONE = -1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_MAP = 1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_LIST = 2;

    /**
     * @param context
     * @param stayIndex
     * @param stayName
     * @param imageUrl
     * @param listTotalPrice        totalPrice가 넘어온다. 평균가로 했더니 리스트와 상세에서 서버 계산이 다른 경우가 발생했다.
     * @param checkInDateTime       ISO-8601
     * @param checkOutDateTime      ISO-8601
     * @param numberOfAdults
     * @param childList
     * @param isUsedMultiTransition
     * @param gradientType
     * @param analyticsParam
     * @return
     */
    public static Intent newInstance(Context context, int stayIndex, String stayName, String stayEnglishName, String imageUrl//
        , int listTotalPrice, String checkInDateTime, String checkOutDateTime//
        , int numberOfAdults, ArrayList<Integer> childList, boolean isUsedMultiTransition, int gradientType//
        , StayOutboundDetailAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayOutboundDetailActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_ENGLISH_NAME, stayEnglishName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_LIST_PRICE, listTotalPrice);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);
        intent.putExtra(INTENT_EXTRA_DATA_MULTITRANSITION, isUsedMultiTransition);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, gradientType);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, StayOutboundDetailActivity.class);

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
    protected StayOutboundDetailPresenter createInstancePresenter()
    {
        return new StayOutboundDetailPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
