package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyWebView;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

@Deprecated
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
        intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, DailyTextUtils.isTextEmpty(couponIdx) ? "" : couponIdx);
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

        if (DailyTextUtils.isTextEmpty(mCouponIdx) == true)
        {
            setWebView(DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStaticUrlCoupon());
        } else
        {
            if (Constants.DEBUG == true)
            {
                String url;

                url = DailyPreference.getInstance(this).getBaseUrl();

                // 현재 접속하는 서버가 실서버인 경우와 테스트 서버인 경우 쿠폰 이용약관 서버가 다름
                if (url.startsWith("https://prod-") == true)
                {
                    setWebView(DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStaticUrlProdCouponNote() + mCouponIdx);
                } else
                {
                    setWebView(DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStaticUrlDevCouponNote() + mCouponIdx);
                }
            } else
            {
                setWebView(DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStaticUrlProdCouponNote() + mCouponIdx);
            }
        }

        initToolbar();
        initLayout((DailyWebView) mWebView);
    }

    private void initToolbar()
    {
        int titleString;

        if (DailyTextUtils.isTextEmpty(mCouponIdx) == false)
        {
            titleString = R.string.coupon_notice_text;
        } else
        {
            titleString = R.string.coupon_use_notice_text;
        }

        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(titleString);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
                smoothScrollTop(dailyWebView);
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

        View homeButtonView = findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (DailyTextUtils.isTextEmpty(mCouponIdx) == true)
        {
            AnalyticsManager.getInstance(CouponTermActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_COUPON_GENERAL_TERMS_OF_USE, null);
        } else
        {
            AnalyticsManager.getInstance(CouponTermActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_COUPON_INDIVIDUAL_TERMS_OF_USE, null);
        }

    }
}
