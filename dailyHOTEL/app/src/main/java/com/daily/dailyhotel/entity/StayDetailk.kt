package com.daily.dailyhotel.entity

import android.content.Context
import com.twoheart.dailyhotel.R

class StayDetailk : Configurations() {
    var index: Int = 0
    var wishCount: Int = 0
    var wish: Boolean = false
    var singleStay: Boolean = false
    var imageList: List<DetailImageInformation>? = null
    var vrInformation: List<VRInformation>? = null

    var baseInformation: BaseInformation? = null
    var trueReviewInformation: TrueReviewInformation? = null
    var benefitInformation: BenefitInformation? = null
    var roomInformation: RoomInformation? = null

    var dailyCommentList: List<String>? = null

    var facilityList: List<String>? = null
    var totalRoomCount: Int = 0

    var addressInformation: AddressInformation? = null
    var checkTimeInformation: CheckTimeInformation? = null
    var detailInformation: DetailInformation? = null
    var breakfastInformation: BreakfastInformation? = null
    var refundInformation: RefundInformation? = null
    var checkInformation: CheckInformation? = null
    var rewardStickerCount: Int = 0

    var province: Province? = null

    fun toStayDetail(): StayDetail {
        return StayDetail()
    }

    enum class Pictogram constructor(private val mNameResId: Int, val imageResId: Int) {
        PARKING(R.string.label_parking, R.drawable.f_ic_facilities_05),
        NO_PARKING(R.string.label_unabled_parking, R.drawable.f_ic_facilities_05_no_parking),
        POOL(R.string.label_pool, R.drawable.f_ic_facilities_06),
        FITNESS(R.string.label_fitness, R.drawable.f_ic_facilities_07),
        SAUNA(R.string.label_sauna, R.drawable.f_ic_facilities_16),
        BUSINESS_CENTER(R.string.label_business_center, R.drawable.f_ic_facilities_15),
        KIDS_PLAY_ROOM(R.string.label_kids_play_room, R.drawable.f_ic_facilities_17),
        SHARED_BBQ(R.string.label_allowed_barbecue, R.drawable.f_ic_facilities_09),
        PET(R.string.label_allowed_pet, R.drawable.f_ic_facilities_08),
        NONE(0, 0);

        fun getName(context: Context): String? {
            return if (mNameResId == 0) {
                null
            } else context.getString(mNameResId)

        }
    }

    class VRInformation {
        var name: String? = null
        var type: String? = null
        var typeIndex: Int = 0
        var url: String? = null
    }

    class BaseInformation {
        var category: String? = null
        var grade: Stay.Grade? = null
        var provideRewardSticker: Boolean = false
        var name: String? = null
        var discount: Int = 0
        var awards: TrueAwards? = null
    }

    class TrueReviewInformation {
        var ratingCount: Int = 0
        var ratingPercent: Int = 0
        var showRating: Boolean = false

        var review: PrimaryReview? = null
        var reviewTotalCount: Int = 0

        var reviewScores: List<ReviewScore>? = null

        class PrimaryReview {
            var score: Float = 0.0f
            var comment: String? = null
            var userId: String? = null
            var createdAt: String? = null
        }

        class ReviewScore {
            var type: String? = null
            var average: Float = 0.0f
        }
    }

    class BenefitInformation {
        var title: String? = null
        var contentList: List<String>? = null
        var coupon: Coupon? = null

        class Coupon {
            var couponDiscount: Int = 0
            var isDownloaded: Boolean = false
        }
    }

    class RoomInformation {
        var bedTypeList: HashSet<String>? = null
        var facilityList: HashSet<String>? = null
        var roomList: List<Room>? = null
    }

    class AddressInformation {
        var latitude: Double = 0.toDouble()
        var longitude: Double = 0.toDouble()
        var address: String? = null
    }

    class CheckTimeInformation {
        var checkIn: String? = null
        var checkOut: String? = null
        var description: List<String>? = null
    }

    class DetailInformation {
        var itemList: List<Item>? = null


        class Item {
            var type: String? = null
            var title: String? = null
            var contentList: List<String>? = null
        }
    }

    class BreakfastInformation {
        var description: String? = null

        var items: List<Item>? = null

        class Item {
            var amount: Int = 0
            var maxAge: Int = 0
            var maxPersons: Int = 0
            var minAge: Int = 0
            var title: String? = null
        }
    }

    class RefundInformation {
        var title: String? = null
        var type: String? = null
        var contentList: List<String>? = null
        var warningMessage: String? = null
    }

    class CheckInformation {
        var title: String? = null
        var contentList: List<String>? = null

        var waitingForBooking: Boolean = false
    }

    class Province {
        var index: Int = 0
        var name: String? = null
    }
}
