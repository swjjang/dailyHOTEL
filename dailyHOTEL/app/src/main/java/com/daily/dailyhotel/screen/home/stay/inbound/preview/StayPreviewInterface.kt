package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.app.Activity
import android.text.SpannableStringBuilder
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import io.reactivex.Completable

interface StayPreviewInterface {

    interface ViewInterface : BaseDialogViewInterface {

        fun setName(name: String?)

        fun setCategory(category: String?, activeReward: Boolean)

        fun setImages(imageList: Array<String>?)

        fun setRoomInformation(roomTypeCountText: String?, nightsEnabled: Boolean, rangePriceVisible: Boolean, rangePriceText: String?)

        fun setReviewInformationVisible(visible: Boolean)

        fun setReviewInformation(reviewCountVisible: Boolean, reviewCountText: SpannableStringBuilder?, wishCountVisible: Boolean, wishCountText: SpannableStringBuilder?)

        fun setWish(wish: Boolean)

        fun setBookingButtonText(text: String)

        fun showAnimation(): Completable

        fun hideAnimation(): Completable
    }

    interface OnEventListener : OnBaseEventListener {

        fun onDetailClick()

        fun onWishClick()

        fun onKakaoClick()

        fun onMapClick()

        fun onCloseClick()
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {

        fun onScreen(activity: Activity, category: String?)

        fun onEventBackClick(activity: Activity)

        fun onEventCloseClick(activity: Activity)

        fun onEventWishClick(activity: Activity, wish: Boolean)

        fun onEventKakaoClick(activity: Activity)

        fun onEventMapClick(activity: Activity)

        fun onEventDetailClick(activity: Activity)
    }
}
