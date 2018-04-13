package com.daily.dailyhotel.screen.mydaily.coupon

import android.content.Intent
import com.daily.dailyhotel.screen.common.web.DailyWebActivity
import com.daily.dailyhotel.screen.common.web.DailyWebInterface
import com.daily.dailyhotel.screen.common.web.DailyWebPresenter
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants

class CouponTermPresenter(activity: DailyWebActivity) : DailyWebPresenter(activity) {
    private var mCouponIndex = ""

    override fun createInstanceViewInterface(): DailyWebInterface.ViewInterface {
        return CouponTermView(activity, this)
    }

    override fun initAnalytics(): DailyWebInterface.AnalyticsInterface {
        return CouponTermAnalyticsImpl()
    }

    override fun onIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return true
        }


        if (intent.hasExtra(CouponTermActivity.INTENT_EXTRA_DATA_COUPON_IDX)) {
            mCouponIndex = intent.getStringExtra(CouponTermActivity.INTENT_EXTRA_DATA_COUPON_IDX)
        }

        if (isTextEmpty(mCouponIndex)) {
            mTitleText = getString(R.string.coupon_use_notice_text)
            mUrl = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigStaticUrlCoupon
        } else {
            mTitleText = getString(R.string.coupon_notice_text)

            mUrl = if (Constants.DEBUG ) {
                // 현재 접속하는 서버가 실서버인 경우와 테스트 서버인 경우 쿠폰 이용약관 서버가 다름
                if (DailyPreference.getInstance(activity).baseUrl.startsWith("https://prod-")) {
                    DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigStaticUrlProdCouponNote + mCouponIndex
                } else {
                    DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigStaticUrlDevCouponNote + mCouponIndex
                }
            } else {
                DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigStaticUrlProdCouponNote + mCouponIndex
            }
        }

        return !isTextEmpty(mTitleText, mUrl)

    }

    override fun onStart() {
        super.onStart()

        (mAnalytics as CouponTermInterface.AnalyticsInterface).onScreen(activity, mCouponIndex)
    }
}
