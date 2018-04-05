package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.text.SpannableStringBuilder
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.DetailImageInformation
import io.reactivex.Completable

interface StayPreviewInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setName(name: String?)

        fun setCategory(category: String?, activeReward: Boolean)

        fun setImages(imageList: List<DetailImageInformation>?)

        fun setRoomInformation(roomTypeCountText: String?, nightEnabled: Boolean, rangePriceVisible: Boolean, rangePriceText: String?)

        fun setReviewInformationVisible(visible: Boolean)

        fun setReviewInformation(reviewCountVisible: Boolean, reviewCountText: SpannableStringBuilder?, wishCountVisible: Boolean, wishCountText: SpannableStringBuilder?)

        fun setBookingButtonText(text: String)

        fun hidePreviewAnimation(): Completable
    }

    interface OnEventListener : OnBaseEventListener {
        fun onDetailClick()

        fun onWishClick()

        fun onKakaoClick()

        fun onMapClick()
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
