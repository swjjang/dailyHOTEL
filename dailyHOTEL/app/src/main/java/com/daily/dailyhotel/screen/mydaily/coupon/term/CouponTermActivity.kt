package com.daily.dailyhotel.screen.mydaily.coupon.term

import android.content.Context
import android.content.Intent
import com.daily.dailyhotel.screen.common.web.DailyWebActivity
import com.daily.dailyhotel.screen.common.web.DailyWebPresenter
import com.daily.dailyhotel.util.takeNotEmpty

class CouponTermActivity : DailyWebActivity() {

    override fun createInstancePresenter(): DailyWebPresenter {
        return CouponTermPresenter(this)
    }

    companion object {
        /**
         * 공통 쿠폰 유의 사항
         *
         * @param context
         * @return
         */
        @JvmStatic
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, CouponTermActivity::class.java)
            intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, "")
            return intent
        }

        /**
         * 개별 쿠폰 유의 사항
         *
         * @param context
         * @param couponIdx 쿠폰 번호 ,  null 일때 공통 쿠폰 유의사항으로 이동
         * @return
         */
        @JvmStatic
        fun newInstance(context: Context, couponIdx: String): Intent {
            val intent = Intent(context, CouponTermActivity::class.java)

            couponIdx.takeNotEmpty {
                intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, it)
            }
            return intent
        }

        const val INTENT_EXTRA_DATA_COUPON_IDX = "coupon_idx"
    }
}
