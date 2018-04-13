package com.daily.dailyhotel.screen.mydaily.coupon;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.screen.common.web.DailyWebActivity;
import com.daily.dailyhotel.screen.common.web.DailyWebInterface;
import com.daily.dailyhotel.screen.common.web.DailyWebPresenter;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

public class CouponTermPresenter extends DailyWebPresenter
{
    private String mCouponIndex = "";

    public CouponTermPresenter(@NonNull DailyWebActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected DailyWebInterface.ViewInterface createInstanceViewInterface()
    {
        return new CouponTermView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(DailyWebActivity activity)
    {
        super.constructorInitialize(activity);
    }

    @Override
    protected DailyWebInterface.AnalyticsInterface initAnalytics()
    {
        return new CouponTermAnalyticsImpl();
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(CouponTermActivity.INTENT_EXTRA_DATA_COUPON_IDX))
        {
            mCouponIndex = intent.getStringExtra(CouponTermActivity.INTENT_EXTRA_DATA_COUPON_IDX);
        }

        if (DailyTextUtils.isTextEmpty(mCouponIndex))
        {
            mTitleText = getString(R.string.coupon_use_notice_text);
            mUrl = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlCoupon();
        } else
        {
            mTitleText = getString(R.string.coupon_notice_text);

            if (Constants.DEBUG == true)
            {
                // 현재 접속하는 서버가 실서버인 경우와 테스트 서버인 경우 쿠폰 이용약관 서버가 다름
                if (DailyPreference.getInstance(getActivity()).getBaseUrl().startsWith("https://prod-") == true)
                {
                    mUrl = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlProdCouponNote() + mCouponIndex;
                } else
                {
                    mUrl = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlDevCouponNote() + mCouponIndex;
                }
            } else
            {
                mUrl = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlProdCouponNote() + mCouponIndex;
            }
        }

        if (DailyTextUtils.isTextEmpty(mTitleText, mUrl) == true)
        {
            return false;
        }

        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        ((CouponTermInterface.AnalyticsInterface) mAnalytics).onScreen(getActivity(), mCouponIndex);
    }
}
