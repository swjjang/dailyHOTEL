package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

/**
 * Created by android_sam on 2016. 5. 30..
 */
public class CouponTermActivity extends WebViewActivity
{
    private static final String INTENT_EXTRA_DATA_COUPON_IDX = "coupon_idx";

    private String mCouponIdx = "";

    /**
     * 공통 쿠폰 유의 사항
     *
     * @param context
     * @return
     */
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, CouponTermActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, "");
        return intent;
    }

    /**
     * 개별 쿠폰 유의 사항
     *
     * @param context
     * @param couponIdx 쿠폰 번호 ,  null 일때 공통 쿠폰 유의사항으로 이동
     * @return
     */
    public static Intent newInstance(Context context, String couponIdx)
    {
        Intent intent = new Intent(context, CouponTermActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, Util.isTextEmpty(couponIdx) ? "" : couponIdx);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coupon_term);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        if (intent.hasExtra(INTENT_EXTRA_DATA_COUPON_IDX))
        {
            mCouponIdx = intent.getStringExtra(INTENT_EXTRA_DATA_COUPON_IDX);
        }

        initToolbar();
    }

    private void initToolbar()
    {
        int titleString = R.string.actionbar_title_common_coupon_term_activity;
        if (Util.isTextEmpty(mCouponIdx) == true)
        {
            titleString = R.string.actionbar_title_each_coupon_term_activity;
        }

        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(titleString), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.TERMSOFUSE);

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (Util.isTextEmpty(mCouponIdx))
        {
            setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_COMMON_COUPON_TERMS));
        } else
        {
            setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_EACH_COUPON_TERMS) + mCouponIdx);
        }
    }
}
