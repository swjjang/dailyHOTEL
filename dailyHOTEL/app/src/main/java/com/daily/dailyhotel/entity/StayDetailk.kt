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

    var facilitiesList: List<FacilitiesPictogram>? = null
    var totalRoomCount: Int = 0

    var addressInformation: AddressInformation? = null
    var checkTimeInformation: CheckTimeInformation? = null
    var detailInformation: DetailInformation? = null
    var breakfastInformation: BreakfastInformation? = null
    var refundInformation: RefundInformation? = null
    var checkInformation: CheckInformation? = null
    var rewardStickerCount: Int = 0

    var province: Province? = null
    var hasNRDRoom = false

    fun toStayDetail(): StayDetail {
        return StayDetail()
    }

    class VRInformation {
        var name: String? = null
        var type: String? = null
        var typeIndex: Int = 0
        var url: String? = null
    }

    class BaseInformation {
        var category: String? = null
        var grade: Stay.Grade = Stay.Grade.etc
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
        var bedTypeSet: HashSet<String>? = null
        var facilitiesSet: HashSet<String>? = null
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
        var items: List<Item>? = null
        var description: List<String>? = null

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

enum class FacilitiesPictogram constructor(private val nameResId: Int, private val imageResId: Int) {
    POOL(R.string.label_pool, R.drawable.vector_f_ic_facilities_swimming), // 수영장
    SAUNA(R.string.label_sauna, R.drawable.vector_f_ic_facilities_sauna), // 사우나
    SPAMASSAGE(R.string.label_spa_massage, R.drawable.vector_f_ic_facilities_spa_massage), // 스파마사지
    BREAKFAST(R.string.label_breakfast_restaurant, R.drawable.vector_f_ic_facilities_breakfast_restaurant), // 조식당
    CAFETERIA(R.string.label_allowed_pet, R.drawable.vector_f_ic_facilities_cafe), // 카페테리아
    SEMINARROOM(R.string.label_seminar_room, R.drawable.vector_f_ic_facilities_seminar_room), // 세미나실
    BUSINESSCENTER(R.string.label_business_center, R.drawable.vector_f_ic_facilities_business), // 비즈니스센터
    WIFI(R.string.label_wifi, R.drawable.vector_f_ic_facilities_wifi), // 무료WiFi
    FITNESS(R.string.label_fitness, R.drawable.vector_f_ic_facilities_fitness), // 피트니스
    CLUBLOUNGE(R.string.label_club_lounge, R.drawable.vector_f_ic_facilities_lounge), // 클럽라운지
    SHAREDBBQ(R.string.label_allowed_barbecue, R.drawable.vector_f_ic_facilities_bbq), // 공동바베큐
    PICKUPAVAILABLE(R.string.label_allowed_pet, R.drawable.vector_f_ic_facilities_pickup), // 픽업가능
    CONVENIENCESTORE(R.string.label_rent_convenience_store, R.drawable.vector_f_ic_facilities_mart), //편의점(매점)
    PARKING(R.string.label_parking, R.drawable.vector_f_ic_facilities_parking), // 주차가능
    NOPARKING(R.string.label_unabled_parking, R.drawable.vector_f_ic_facilities_no_parking), // 주차불가
    PET(R.string.label_allowed_pet, R.drawable.vector_f_ic_facilities_pets), // 반려동물
    KIDSPLAYROOM(R.string.label_kids_play_room, R.drawable.vector_f_ic_facilities_kidsplay), // 키즈플레이룸
    RENTBABYBED(R.string.label_rent_baby_bed, R.drawable.vector_f_ic_facilities_babycrib); // 아기침대대여

    fun getName(context: Context): String? {
        return if (nameResId == 0) null else context.getString(nameResId)
    }

    fun getImageResourceId(): Int {
        return imageResId
    }
}
