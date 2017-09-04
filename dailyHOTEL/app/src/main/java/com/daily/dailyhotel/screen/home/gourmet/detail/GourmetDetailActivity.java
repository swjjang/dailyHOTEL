package com.daily.dailyhotel.screen.home.gourmet.detail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetDetailActivity extends BaseActivity<GourmetDetailPresenter>
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

    static final String INTENT_EXTRA_DATA_GOURMET_INDEX = "gourmetIndex";
    static final String INTENT_EXTRA_DATA_GOURMET_NAME = "gourmetName";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";
    static final String INTENT_EXTRA_DATA_CATEGORY = "category";
    static final String INTENT_EXTRA_DATA_SOLDOUT = "soldOut";
    static final String INTENT_EXTRA_DATA_MULTITRANSITION = "multiTransition";
    static final String INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE = "gradientType";
    static final String INTENT_EXTRA_DATA_REFRESH = "refresh";
    static final String INTENT_EXTRA_DATA_LIST_PRICE = "listPrice";

    public static final int TRANS_GRADIENT_BOTTOM_TYPE_NONE = -1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_MAP = 1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_LIST = 2;

    /**
     *
     * @param context
     * @param gourmetIndex
     * @param gourmetName
     * @param imageUrl
     * @param listPrice
     * @param visitDate - ISO-8601
     * @param category
     * @param soldOut
     * @param isUsedMultiTransition
     * @param gradientType
     * @param analyticsParam
     * @return
     */
    public static Intent newInstance(Context context, int gourmetIndex, String gourmetName, String imageUrl//
        , int listPrice, String visitDate, String category, boolean soldOut //
        , boolean isUsedMultiTransition, int gradientType, GourmetDetailAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_INDEX, gourmetIndex);
        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_LIST_PRICE, listPrice);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDate);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(INTENT_EXTRA_DATA_SOLDOUT, soldOut);
        intent.putExtra(INTENT_EXTRA_DATA_MULTITRANSITION, isUsedMultiTransition);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, gradientType);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

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
    protected GourmetDetailPresenter createInstancePresenter()
    {
        return new GourmetDetailPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
