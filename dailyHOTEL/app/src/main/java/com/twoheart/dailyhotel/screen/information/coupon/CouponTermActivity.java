package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyWebView;

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

        setContentView(R.layout.activity_bonus_n_coupon_term);

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

        if (Util.isTextEmpty(mCouponIdx) == true)
        {
            setWebView(Crypto.getUrlDecoderEx(URL_WEB_COMMON_COUPON_TERMS));
        } else
        {
            // 현재 접속하는 서버가 실서버인 경우와 테스트 서버인 경우 쿠폰 이용약관 서버가 다름
            if (Crypto.getUrlDecoderEx(URL_DAILYHOTEL_SERVER_DEFAULT).startsWith("http://dev-") == false)
            {
                setWebView(Crypto.getUrlDecoderEx(URL_WEB_EACH_COUPON_TERMS) + mCouponIdx);
            } else
            {
                setWebView(Crypto.getUrlDecoderEx(URL_WEB_EACH_COUPON_TERMS_DEV) + mCouponIdx);
            }
        }

        initToolbar();
        initLayout((DailyWebView) mWebView);
    }

    private void initToolbar()
    {
        int titleString;

        if (Util.isTextEmpty(mCouponIdx) == false)
        {
            titleString = R.string.coupon_notice_text;
        } else
        {
            titleString = R.string.coupon_use_notice_text;
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

    private void initLayout(final DailyWebView dailyWebView)
    {
        final View topButtonView = findViewById(R.id.topButtonView);
        topButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dailyWebView.setScrollY(0);
            }
        });

        topButtonView.setVisibility(View.INVISIBLE);

        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    topButtonView.setVisibility(View.INVISIBLE);
                } else
                {
                    topButtonView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (Util.isTextEmpty(mCouponIdx) == true)
        {
            AnalyticsManager.getInstance(CouponTermActivity.this).recordScreen(AnalyticsManager.Screen.MENU_COUPON_GENERAL_TERMS_OF_USE);
        } else
        {
            AnalyticsManager.getInstance(CouponTermActivity.this).recordScreen(AnalyticsManager.Screen.MENU_COUPON_INDIVIDUAL_TERMS_OF_USE);
        }

    }
}
