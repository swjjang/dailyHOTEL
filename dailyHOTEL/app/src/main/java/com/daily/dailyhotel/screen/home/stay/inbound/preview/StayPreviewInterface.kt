package com.daily.dailyhotel.screen.copy.kotlin

import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.DetailImageInformation

interface StayPreviewInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setName(name: String?)
        fun setCategory(category: String?, reward: Boolean)
        fun setImages(imageList: List<DetailImageInformation>?)
        fun setRoomInformation(roomTypeCountText: String?, nightEnabled: Boolean, rangePriceText: String?)
        fun setReviewInformationVisible(visible: Boolean)
        fun setReviewInformation(reviewCountText: String?, wishCountText: String?)
    }

    interface OnEventListener : OnBaseEventListener {
        fun onDetailClick()
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
