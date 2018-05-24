package com.daily.dailyhotel.screen.home.stay.inbound.detailk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam

class StayDetailActivity : BaseActivity<StayDetailPresenter>() {

    enum class TransGradientType {
        NONE, MAP, LIST
    }

    companion object {
        const val NONE_PRICE = -1

        internal const val REQUEST_CODE_CALENDAR = 10000
        internal const val REQUEST_CODE_HAPPYTALK = 10002
        internal const val REQUEST_CODE_MAP = 10004
        internal const val REQUEST_CODE_IMAGE_LIST = 10005
        internal const val REQUEST_CODE_CALL = 10006
        internal const val REQUEST_CODE_PAYMENT = 10007
        internal const val REQUEST_CODE_LOGIN = 10008
        internal const val REQUEST_CODE_PROFILE_UPDATE = 10009
        internal const val REQUEST_CODE_DOWNLOAD_COUPON = 10010
        internal const val REQUEST_CODE_LOGIN_IN_BY_COUPON = 10012
        internal const val REQUEST_CODE_LOGIN_IN_BY_BOOKING = 10013
        internal const val REQUEST_CODE_TRUE_VIEW = 10014
        internal const val REQUEST_CODE_TRUE_VR = 10015
        internal const val REQUEST_CODE_NAVIGATOR = 10016
        internal const val REQUEST_CODE_REWARD = 10017
        internal const val REQUEST_CODE_WEB = 10018
        internal const val REQUEST_CODE_WISH_DIALOG = 10019
        internal const val REQUEST_CODE_ROOM = 10020

        internal const val INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex"
        internal const val INTENT_EXTRA_DATA_STAY_NAME = "stayName"
        internal const val INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl"
        internal const val INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime"
        internal const val INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime"
        internal const val INTENT_EXTRA_DATA_MULTITRANSITION = "multiTransition"
        internal const val INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE = "gradientType"
        internal const val INTENT_EXTRA_DATA_LIST_PRICE = "listPrice"
        internal const val REQUEST_CODE_BEDTYPE_FILTER = "bedTypeFilter"
        internal const val REQUEST_CODE_FACILITIES_FILTER = "facilitiesFilter"

        const val INTENT_EXTRA_DATA_WISH = "wish"
        const val INTENT_EXTRA_DATA_CHANGED_PRICE = "changedPrice"
        const val INTENT_EXTRA_DATA_SOLD_OUT = "soldOut"

        @JvmStatic
        fun newInstance(context: Context, stayIndex: Int, stayName: String, imageUrl: String,
                        viewPrice: Int, checkInDateTime: String, checkOutDateTime: String,
                        bedTypeFilter: List<String>?, facilitiesFilter: List<String>?,
                        isUsedMultiTransition: Boolean, gradientType: TransGradientType = TransGradientType.NONE,
                        analyticsParam: StayDetailAnalyticsParam): Intent {
            return Intent(context, StayDetailActivity::class.java)
                    .putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex)
                    .putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName)
                    .putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl)
                    .putExtra(INTENT_EXTRA_DATA_LIST_PRICE, viewPrice)
                    .putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime)
                    .putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime)
                    .putStringArrayListExtra(REQUEST_CODE_BEDTYPE_FILTER, bedTypeFilter?.let { ArrayList(it) })
                    .putStringArrayListExtra(REQUEST_CODE_FACILITIES_FILTER, facilitiesFilter?.let { ArrayList(it) })
                    .putExtra(INTENT_EXTRA_DATA_MULTITRANSITION, isUsedMultiTransition)
                    .putExtra(INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, gradientType.name)
                    .putExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS, analyticsParam)
        }

        @JvmStatic
        fun newInstance(context: Context, deepLink: String): Intent {
            return Intent(context, StayDetailActivity::class.java)
                    .putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): StayDetailPresenter {
        return StayDetailPresenter(this)
    }

    override fun finish() {
        super.finish()
    }
}