package com.daily.dailyhotel.screen.home.stay.inbound.detail.room

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.daily.base.OnBaseEventListener
import com.daily.base.util.*
import com.daily.base.widget.DailyNestedScrollView
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.entity.TrueVR
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.view.DailyRoomInfoView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.LayoutStayRoomDetailDataBinding
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan
import kotlin.math.roundToInt

class StayRoomItemView : RelativeLayout {
    companion object {
        const val MIN_SCALE_VALUE = 0.865f
        const val MAX_SCALE_VALUE = 1.0f
        const val RETURN_SCALE_GAP_PERCENT = 0.3f
        const val ANIMATION_DURATION = 200
    }

    enum class RoomType(private val stringResId: Int) {
        ONE_ROOM(R.string.label_room_type_one_room),
        LIVING_ROOM(R.string.label_room_type_living_room);

        fun getName(context: Context): String {
            return context.resources.getString(stringResId)
        }
    }

    enum class BedType(private val stringResId: Int = R.string.label_bed_type_unknown, val vectorIconResId: Int = R.drawable.vector_ic_detail_item_bed_double) {
        SINGLE(R.string.label_single, R.drawable.vector_ic_detail_item_bed_single),
        DOUBLE(R.string.label_double, R.drawable.vector_ic_detail_item_bed_double),
        SEMI_DOUBLE(R.string.label_semi_double, R.drawable.vector_ic_detail_item_bed_double),
        KING(R.string.label_king, R.drawable.vector_ic_detail_item_bed_double),
        QUEEN(R.string.label_queen, R.drawable.vector_ic_detail_item_bed_double),
        IN_FLOOR_HEATING(R.string.label_in_floor_heating, R.drawable.vector_ic_detail_item_bed_heatingroom),
        UNKNOWN(R.string.label_bed_type_unknown, R.drawable.vector_ic_detail_item_bed_double);

        fun getName(context: Context): String {
            return context.resources.getString(stringResId)
        }
    }

    enum class RoomAmenityType(private val stringResId: Int) {
        Kitchenette(R.string.label_cooking),
        Pc(R.string.label_computer),
        Bath(R.string.label_bathtub),
        Tv(R.string.label_television),
        SpaWallpool(R.string.label_whirlpool),
        PrivateBbq(R.string.label_private_bbq),
        Smokeable(R.string.label_smokeable),
        Karaoke(R.string.label_karaoke),
        PartyRoom(R.string.label_party_room),
        BathAmenity(R.string.label_bath_amenity),
        ShowerGown(R.string.label_shower_gown),
        ToothbrushSet(R.string.label_toothbrush_set),
        DisabledFacilities(R.string.label_disabled_facilities),
        Breakfast(R.string.label_breakfast),
        PrivatePool(R.string.label_private_pool);

        fun getName(context: Context): String {
            return context.resources.getString(stringResId)
        }
    }

    interface OnEventListener : OnBaseEventListener {
        fun onCloseClick()
        fun onMoreImageClick(roomName: String, roomIndex: Int)
        fun onVrImageClick(trueVrList: List<TrueVR>)
    }

    private lateinit var viewDataBinding: LayoutStayRoomDetailDataBinding
    private lateinit var bgDrawable: Drawable
    private var backgroundPaddingTop: Int = 0
    private var backgroundPaddingLeft: Int = 0
    private var backgroundPaddingRight: Int = 0
    private var minWidth: Int = ViewGroup.MarginLayoutParams.MATCH_PARENT
    private var minScale = MIN_SCALE_VALUE
    private var defaultTopMargin = 0
    private var returnScaleGap = 0.06f
    private var room: Room = Room()
    private var nights = 1
    private var currentScale: Float = MAX_SCALE_VALUE
    private var startScale: Float = MAX_SCALE_VALUE
    private val bedTypeStringList = mutableListOf<String>()
    private var bedTypeIconResId: Int = BedType.UNKNOWN.vectorIconResId
    private var attributeTitleText: String = ""
    private var attributeStructureList = mutableListOf<String>()
    private var roomBenefitList = mutableListOf<String>()
    var onEventListener: OnEventListener? = null

    constructor(context: Context?) : super(context) {
        initLayout(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initLayout(context, attrs)
    }

    private fun initLayout(context: Context?, attrs: AttributeSet?) {
        if (context == null) {
            return
        }

        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_stay_room_detail_data, this, true)

        bgDrawable = context.resources.getDrawable(R.drawable.product_detail_card)
        bgDrawable.run {
            val rect = Rect()
            getPadding(rect)

            backgroundPaddingLeft = rect.left
            backgroundPaddingRight = rect.right
            backgroundPaddingTop = rect.top
        }

        minWidth = (ScreenUtils.getScreenWidth(context) * MIN_SCALE_VALUE - backgroundPaddingLeft - backgroundPaddingRight).toInt()
        var minScale = getMinScale()
        returnScaleGap = (MAX_SCALE_VALUE - minScale) * RETURN_SCALE_GAP_PERCENT
    }

    fun setBackgroundVisible(visible: Boolean) {
        setBackgroundDrawable(if (visible) bgDrawable else null)
    }

    fun setScrollingEnabled(enabled: Boolean) {
        (viewDataBinding.nestedScrollView as DailyNestedScrollView).isScrollingEnabled = enabled
    }

    fun getBackgroundPaddingTop(): Int {
        return backgroundPaddingTop
    }

    fun setDefaultTopMargin(topMargin: Int) {
        defaultTopMargin = topMargin

        var params = layoutParams as? MarginLayoutParams
        params?.run {
            this.topMargin = topMargin
            requestLayout()
        }
    }

    fun addScale(gap: Float): Float {
        val addWidth = gap * ScreenUtils.getScreenWidth(context).toFloat() / ScreenUtils.getScreenHeight(context).toFloat()
        var toScale = currentScale
        layoutParams?.run {
            toScale = (layoutParams.width + addWidth) / ScreenUtils.getScreenWidth(context).toFloat()
            post {
                setScale(toScale)
            }
        }

        ExLog.d("sam - addScale = gap : $gap , addWidth : $addWidth , toScale : $toScale")
        return toScale
    }

    fun setScale(scale: Float) {
        val minScale = getMinScale()

        val toScale = when {
            scale < minScale -> {
                minScale
            }

            scale > MAX_SCALE_VALUE -> {
                MAX_SCALE_VALUE
            }

            else -> {
                scale
            }
        }

        currentScale = toScale

        val oneTopMarginGap = defaultTopMargin / (MAX_SCALE_VALUE - minScale) / 100
        val toTopMargin = (MAX_SCALE_VALUE - toScale) * oneTopMarginGap * 100

        val width = ScreenUtils.getScreenWidth(context) * toScale

        if  (layoutParams as? MarginLayoutParams != null) {

            val params = layoutParams as MarginLayoutParams
            params.let {
                it.width = width.toInt()
                it.topMargin = toTopMargin.roundToInt()
                requestLayout()
            }
        } else {
            layoutParams.let {
                it.width = width.toInt()
                requestLayout()
            }
        }
    }

    fun getCurrentScale(): Float {
        return currentScale
    }

    private fun getMinWidth(): Int {
        var screenWidth = ScreenUtils.getScreenWidth(context)

        return if (minWidth <= 0) {
            (screenWidth * MIN_SCALE_VALUE - backgroundPaddingLeft - backgroundPaddingRight).toInt()
        } else {
            minWidth
        }
    }

    fun getMinScale(): Float {
        return getMinWidth().toFloat() / ScreenUtils.getScreenWidth(context)
    }

    fun setStartScale() {
        startScale = currentScale
    }

    fun getNeedAnimation() : Boolean {

        ExLog.d("sam - getNeedAnimation : currentScale : $currentScale , startScale : $startScale , gap : ${currentScale - startScale} , returnScaleGap : $returnScaleGap")

        return when (currentScale) {
            startScale, MAX_SCALE_VALUE, getMinScale() -> {
                false
            }

            else -> {
                val scaleGap = currentScale - startScale
                Math.abs(scaleGap) >= returnScaleGap
            }
        }
    }

    fun setAfterScale() {
        when (currentScale) {
            startScale, MAX_SCALE_VALUE, getMinScale() -> {
                return
            }

            else -> {
                val scaleGap = currentScale - startScale
                val needReturn = Math.abs(scaleGap) < returnScaleGap

                when {
                    scaleGap > 0 -> {
                        // 증가
                        startAnimation(!needReturn)
                    }

                    else -> {
                        // 감소
                        startAnimation(needReturn)
                    }
                }
            }
        }
    }

    fun startAnimation(scaleUp: Boolean) {
        val end = if (scaleUp) MAX_SCALE_VALUE else getMinScale()
        val scaleGap = Math.abs(end - currentScale)
        val maxGap = MAX_SCALE_VALUE - minScale
//        val duration = ANIMATION_DURATION / (100 * maxGap) * (100 * scaleGap)
        val duration = ANIMATION_DURATION * scaleGap / maxGap

        ExLog.d("sam - duration = $duration")

        val animator = ValueAnimator.ofFloat(currentScale, end)
        animator.duration = duration.toLong()
        animator.addUpdateListener { animation ->
            setScale(animation.animatedValue as Float)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                visibility = when (scaleUp) {
                    true -> {
                        setScale(MAX_SCALE_VALUE)
                        View.VISIBLE
                    }

                    else -> {
                        setScale(getMinScale())
                        View.INVISIBLE
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                if (scaleUp && visibility != View.VISIBLE) {
                    visibility = View.VISIBLE
                }
            }
        })

        animator.start()
    }

    fun setData(room: Room, nights: Int = 1) {
        this.room = room
        this.nights = nights

        // room image
        imageUrl = if (room.imageInformation == null) {
            ""
        } else {
            room.imageInformation.imageMap.bigUrl
        }

        // trueVr List data
        trueVrList.clear()
        room.vrInformationList.forEach {
            val trueVr = TrueVR()
            trueVr.name = it.name
            trueVr.type = it.type
            trueVr.typeIndex = it.typeIndex
            trueVr.url = it.url

            trueVrList += trueVr
        }

        // bedType data
        bedTypeStringList.clear()

        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList
        bedTypeList?.forEachIndexed { index, bedTypeInformation ->
            val bedType: BedType = try {
                BedType.valueOf(bedTypeInformation.bedType.toUpperCase())
            } catch (e: Exception) {
                BedType.UNKNOWN
            }

            bedTypeIconResId = when (index) {
                0 -> {
                    if (bedType.vectorIconResId != 0) {
                        bedType.vectorIconResId
                    } else {
                        R.drawable.vector_ic_detail_item_bed_double
                    }
                }

                else -> {
                    R.drawable.vector_ic_detail_item_bed_double
                }
            }

            bedTypeStringList += when (bedType) {
                BedType.UNKNOWN -> {
                    bedType.getName(context)
                }
                else -> {
                    "${bedType.getName(context)} ${bedTypeInformation.count}" + when (index) {
                        bedTypeList.size - 1 -> context.resources.getString(R.string.label_bed_count_end_string)
                        else -> ""
                    }
                }
            }
        }

        bedTypeIconResId.takeIf { bedTypeIconResId == 0 }.let {
            BedType.UNKNOWN.vectorIconResId
        }

        // Attribute Information
        attributeTitleText = ""
        attributeStructureList.clear()
        room.attributeInformation?.run {
            val roomType: RoomType? = try {
                RoomType.valueOf(roomStructure)
            } catch (e: Exception) {
                null
            }

            attributeTitleText = roomType?.getName(context) ?: ""

            isEntireHouse.runTrue {
                if (!attributeTitleText.isTextEmpty()) {
                    attributeTitleText += "/"
                }

                attributeTitleText += context.resources.getString(R.string.label_room_type_entire_house)
            }

            isDuplex.runTrue {
                if (!attributeTitleText.isTextEmpty()) {
                    attributeTitleText += "/"
                }

                attributeTitleText += context.resources.getString(R.string.label_room_type_duplex_room)
            }

            var roomString = ""
            attributeStructureList.clear()

            structureInformationList?.forEach {
                when (it.type) {
                    "BED_ROOM" -> {
                        if (!roomString.isTextEmpty()) {
                            roomString += ", "
                        }

                        roomString += context.resources.getString(R.string.label_bed_room) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else ""
                    }

                    "IN_FLOOR_HEATING_ROOM" -> {
                        if (!roomString.isTextEmpty()) {
                            roomString += ", "
                        }

                        roomString += context.resources.getString(R.string.label_in_floor_heating_room) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else ""
                    }

                    "LIVING_ROOM" -> {
                        attributeStructureList.add(context.resources.getString(R.string.label_living_room) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else "")
                    }

                    "KITCHEN" -> {
                        attributeStructureList.add(context.resources.getString(R.string.label_kitchen) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else "")
                    }

                    "REST_ROOM" -> {
                        attributeStructureList.add(context.resources.getString(R.string.label_rest_room) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else "")
                    }

                    else -> {
                        // do nothing
                    }
                }
            }

            if (!roomString.isTextEmpty()) {
                attributeStructureList.add(0, roomString)
            }
        }

        // room benefit
        val roomBenefitList = mutableListOf<String>()

        val breakfast = room.personsInformation?.breakfast ?: 0
        if (breakfast > 0) {
            roomBenefitList.add(context.resources.getString(R.string.label_stay_room_breakfast_person, breakfast))
        }

        if (!room.benefit.isTextEmpty()) {
            roomBenefitList.add(room.benefit)
        }
    }

    fun notifyDataSetChanged() {
        (room.index == 0).runTrue { return }

        viewDataBinding.roomNameTextView.text = room.name

        setImageInformationView(imageUrl, room.name, room.index, room.imageCount > 0, trueVrList)
        setAmountInformationView(room.amountInformation)
        setRefundInformationView(room.refundInformation)
        setBaseInformationView(room.personsInformation, bedTypeIconResId, bedTypeStringList, room.squareMeter)
        setAttributeInformationView(attributeTitleText, attributeStructureList)
        setRoomBenefitInformationView(roomBenefitList)
        setRewardAndCouponInformationView(room.provideRewardSticker, room.hasUsableCoupon)
        setCheckTimeInformationView(room.checkTimeInformation)
        setRoomDescriptionInformationView(room.descriptionList)
        setRoomAmenityInformationView(room.amenityList)
        setRoomChargeInformationView(room.roomChargeInformation)
        setNeedToKnowInformationView(room.needToKnowList)
    }

    private var imageUrl: String? = null
    private val trueVrList = mutableListOf<TrueVR>()

    private fun setImageInformationView(imageUrl: String? = "", roomName: String, roomIndex: Int, showMore: Boolean = false, trueVrList: List<TrueVR> = listOf()) {
        viewDataBinding.run {
            moreIconView.visibility = if (showMore) View.VISIBLE else View.GONE
            vrIconView.run {
                visibility = if (DailyPreference.getInstance(context).trueVRSupport > 0
                        && trueVrList.isNotNullAndNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                setOnClickListener {
                    onEventListener?.onVrImageClick(trueVrList)
                }
            }

            closeImageView.setOnClickListener {
                onEventListener?.onCloseClick()
            }

            emptyLayout.run {
                when {
                    imageUrl.isTextEmpty() -> {
                        setOnClickListener(null)
                    }

                    else -> {
                        setOnClickListener {
                            onEventListener?.onMoreImageClick(roomName, roomIndex)
                        }
                    }
                }
            }

            simpleDraweeView.run {
                hierarchy.setPlaceholderImage(R.drawable.layerlist_room_no_image_holder)
                Util.requestImageResize(context, this, imageUrl)
            }
        }
    }

    private fun setAmountInformationView(amountInformation: Room.AmountInformation) {
        viewDataBinding.run {
            val showOriginPrice = amountInformation.priceAverage > 0
                    && amountInformation.priceAverage > amountInformation.discountAverage
            val showDiscountRate = amountInformation.discountRate in 5..100 && showOriginPrice

            discountPercentTextView.run {
                when (showDiscountRate) {
                    true -> {
                        val discountRateSpan = SpannableString("${amountInformation.discountRate}%")
                        discountRateSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface)
                                , discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        discountRateSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 14.0))
                                , discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        text = discountRateSpan
                        visibility = View.VISIBLE
                    }

                    false -> {
                        visibility = View.GONE
                    }
                }
            }

            priceTextView.run {
                when (showOriginPrice) {
                    true -> {
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        text = DailyTextUtils.getPriceFormat(context, amountInformation.priceAverage, false)
                        visibility = View.VISIBLE
                    }

                    false -> {
                        visibility = View.GONE
                    }
                }
            }

            discountPriceTextView.run {
                val discountPriceString = DailyTextUtils.getPriceFormat(context, amountInformation.discountAverage, false)
                text = discountPriceString.substring(0, discountPriceString.length - 1)
            }

            discountPriceUnitTextView?.run {
                val currencyString = context.resources.getString(R.string.currency)
                val discountPriceUnitSpan = SpannableString(currencyString + if (nights > 1) context.resources.getString(R.string.label_stay_detail_slash_one_nights) else "")

                @Suppress("DEPRECATION")
                discountPriceUnitSpan.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_text_c323232)), 0, currencyString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = discountPriceUnitSpan
            }
        }
    }

    private fun setRefundInformationView(refundInformation: StayDetail.RefundInformation?) {
        viewDataBinding.run {
            refundPolicyTextView.run {
                when (refundInformation) {
                    null -> {
                        refundPolicyTextView.visibility = View.GONE
                    }

                    else -> {
                        val isNrd = !refundInformation.policy.isTextEmpty()
                                && refundInformation.policy?.toLowerCase().equals("nrd", true)
                        var text = refundInformation.warningMessage
                        val backgroundResId: Int
                        val textColorResId: Int

                        if (isNrd) {
                            backgroundResId = R.drawable.shape_stay_room_refund_policy_nrd_background
                            textColorResId = R.color.default_text_cfb234a

                            if (text.isTextEmpty()) {
                                text = context.resources.getString(R.string.label_stay_room_default_nrd_text)
                            }
                        } else {
                            backgroundResId = R.drawable.shape_stay_room_refund_policy_refundable_background
                            textColorResId = R.color.default_text_c299aff
                        }

                        refundPolicyTextView.setTextColor(context.resources.getColor(textColorResId))
                        refundPolicyTextView.setBackgroundResource(backgroundResId)
                        refundPolicyTextView.visibility = if (text.isTextEmpty()) View.GONE else View.VISIBLE
                        refundPolicyTextView.text = text
                    }
                }
            }
        }
    }

    private fun setBaseInformationView(personsInformation: Room.PersonsInformation?, bedTypeIconResId: Int, bedTypeStringList: MutableList<String>, squareMeter: Float) {
        viewDataBinding.baseInfoGroup.run {
            (personsInformation == null && !bedTypeStringList.isNotNullAndNotEmpty() && squareMeter == 0f).runTrue {
                visibility = View.GONE
                return
            }

            visibility = View.VISIBLE

            setPersonInformationView(personsInformation)
            setBedInformationView(bedTypeIconResId, bedTypeStringList)
            setSquareInformationView(squareMeter)
        }
    }

    private fun setPersonInformationView(personsInformation: Room.PersonsInformation?) {
        var personVectorIconResId = R.drawable.vector_ic_detail_item_people_1
        var personTitle = context.resources.getString(R.string.label_standard_persons, 1)
        var personDescription = ""

        viewDataBinding.run {
            personsInformation?.let {
                personTitle = context.resources.getString(R.string.label_standard_persons, it.fixed)

                personVectorIconResId = when (it.fixed) {
                    0, 1 -> R.drawable.vector_ic_detail_item_people_1

                    2 -> R.drawable.vector_ic_detail_item_people_2

                    else -> R.drawable.vector_ic_detail_item_people_3
                }

                val subDescription = if (it.extra == 0) {
                    ""
                } else {
                    " " + context.resources.getString(if (it.extraCharge) {
                        R.string.label_bracket_pay
                    } else {
                        R.string.label_bracket_free
                    })
                }

                personDescription = context.resources.getString(R.string.label_stay_outbound_room_max_person_free, it.fixed + it.extra) + subDescription
            }

            personIconImageView.setVectorImageResource(personVectorIconResId)
            personTitleTextView.text = personTitle
            personDescriptionTextView.text = personDescription
        }
    }

    private fun setBedInformationView(bedTypeIconResId: Int, bedTypeStringList: MutableList<String>) {
        viewDataBinding.run {
            bedIconImageView.setVectorImageResource(bedTypeIconResId)
            bedDescriptionLayout.setData(bedTypeStringList)
        }
    }

    private fun setSquareInformationView(squareMeter: Float) {
        viewDataBinding.run {
            val pyoung = Math.round(squareMeter * 0.3025)
            when {
                pyoung < 1 -> squareInformationLayout.visibility = View.GONE
                else -> {
                    squareInformationLayout.visibility = View.VISIBLE
                    squareTitleTextView.text = "${squareMeter}m"

                    // ㎡×0.3025=평 - / 400 * 121  /   평×3.3058=㎡ - / 121 * 400
                    squareDescriptionTextView.text = context.resources.getString(R.string.label_pyoung_format, pyoung)
                }
            }
        }
    }

    private fun setAttributeInformationView(titleText: String, structureStringList: MutableList<String>) {
        viewDataBinding.run {
            if (titleText.isTextEmpty() && structureStringList.isEmpty()) {
                subInfoGroup.visibility = View.GONE
            } else {
                subInfoGroup.visibility = View.VISIBLE
                subInfoGridView.columnCount = 2
                subInfoGridView.setData(titleText, DailyRoomInfoView.ItemType.NONE, structureStringList, true)
            }
        }
    }

    private fun setRoomBenefitInformationView(roomBenefitList: MutableList<String>) {
        viewDataBinding.run {
            if (roomBenefitList.isEmpty()) {
                roomBenefitGroup.visibility = View.GONE
                return
            }

            roomBenefitGroup.visibility = View.VISIBLE
            roomBenefitGridView.run {
                columnCount = 1
                setData(context.resources.getString(R.string.label_stay_room_benefit_title)
                        , DailyRoomInfoView.ItemType.DOWN_CARET, roomBenefitList, true)
            }
        }
    }

    private fun setRewardAndCouponInformationView(rewardAble: Boolean, useCoupon: Boolean) {
        viewDataBinding.run {
            if (rewardAble || useCoupon) {
                discountInfoGroup.visibility = View.VISIBLE
            } else {
                discountInfoGroup.visibility = View.GONE
                return
            }

            var temp = ""
            val rewardString = context.resources.getString(R.string.label_stay_room_rewardable)
            val couponString = context.resources.getString(R.string.label_stay_room_coupon_useable)

            if (rewardAble) {
                temp = "  $rewardString"
            }

            if (useCoupon) {
                if (!temp.isTextEmpty()) {
                    temp += " ${context.resources.getString(R.string.label_stay_room_reward_coupon_or)} "
                }

                temp += couponString
            }

            if (!temp.isTextEmpty()) {
                temp += context.resources.getString(R.string.label_stay_room_end_description)
            }

            val spannableString = SpannableString(temp)

            val rewardStart = temp.indexOf(rewardString)

            if (rewardStart != -1) {
                spannableString.setSpan(DailyImageSpan(context, R.drawable.r_ic_xs_14, DailyImageSpan.ALIGN_VERTICAL_CENTER), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                @Suppress("DEPRECATION")
                spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_line_cfaae37)), rewardStart, rewardStart + rewardString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            val couponStart = temp.indexOf(couponString)
            if (couponStart != -1) {
                spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_text_cf27c7a)), couponStart, couponStart + couponString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            discountInfoTextView.text = spannableString
        }
    }

    private fun setCheckTimeInformationView(checkTimeInformation: StayDetail.CheckTimeInformation?) {
        if (checkTimeInformation == null) return

        viewDataBinding.run {
            if (isTextEmpty(checkTimeInformation.checkIn, checkTimeInformation.checkOut)) {
                checkTimeInfoLayout.visibility = View.GONE
                return
            }

            checkTimeInfoLayout.visibility = View.VISIBLE

            checkInTimeTextView.text = checkTimeInformation.checkIn
            checkOutTimeTextView.text = checkTimeInformation.checkOut
        }
    }

    private fun setRoomDescriptionInformationView(descriptionList: MutableList<String>?) {
        viewDataBinding.run {
            if (!descriptionList.isNotNullAndNotEmpty()) {
                roomDescriptionGroup.visibility = View.GONE
                return
            }

            roomDescriptionGroup.visibility = View.VISIBLE

            roomDescriptionGridView?.run {
                columnCount = 1
                setData(context.resources.getString(R.string.label_stay_room_description_title)
                        , DailyRoomInfoView.ItemType.DOT, descriptionList!!, true)
            }
        }
    }

    private fun setRoomAmenityInformationView(amenityList: MutableList<String>) {
        viewDataBinding.run {
            if (amenityList.size == 0) {
                roomAmenityGroup.visibility = View.GONE
                return
            }

            val list = mutableListOf<String>()
            amenityList.forEach {
                val amenityType: RoomAmenityType? = try {
                    RoomAmenityType.valueOf(it)
                } catch (e: Exception) {
                    null
                }

                amenityType?.run { list += amenityType.getName(context) }
            }

            if (list.isEmpty()) {
                roomAmenityGroup.visibility = View.GONE
                return
            }

            roomAmenityGroup.visibility = View.VISIBLE
            roomAmenityGridView?.run {
                columnCount = 2
                setData(context.resources.getString(R.string.label_stay_room_amenity_title)
                        , DailyRoomInfoView.ItemType.DOT, list, true)
            }
        }
    }

    fun setRoomChargeInformationView(info: Room.ChargeInformation?) {
        viewDataBinding.run {
            if (info == null || info.isAllHidden) {
                extraChargeLayout.visibility = View.GONE
                return
            }

            extraChargeLayout.visibility = View.VISIBLE

            extraChargePersonTableLayout.run {
                if (!info.extraPersonInformationList.isNotNullAndNotEmpty()) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE

                    setTitleTextSize(16f)
//                    setTitleTextSize(if (largeView) 16f else 15f)

                    setTitleText(R.string.label_stay_room_extra_charge_person_title)
                    setTitleVisible(true)
                    clearTableLayout()

                    info.extraPersonInformationList.forEach {
                        var title = it.title

                        getPersonRangeText(it.minAge, it.maxAge).letNotEmpty { title += " ($it)" }

                        val subDescription = if (it.maxPersons > 0) context.resources.getString(R.string.label_room_max_person_range_format, it.maxPersons) else ""

                        addTableRow(title, getExtraChargePrice(it.amount), subDescription, true)
                    }
                }
            }

            extraChargeBedTableLayout.run {
                if (info.extraInformation == null || info.extraInformation.isAllHidden) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE

                    setTitleTextSize(16f)
//                setTitleTextSize(if (largeView) 16f else 15f)

                    setTitleVisible(true)
                    setTitleText(R.string.label_stay_room_extra_charge_bed_title)
                    clearTableLayout()

                    (info.extraInformation.extraBeddingEnable).runTrue {
                        addTableRow(context.resources.getString(R.string.label_bedding)
                                , getExtraChargePrice(info.extraInformation.extraBedding), "", true)
                    }

                    (info.extraInformation.extraBedEnable).runTrue {
                        addTableRow(context.resources.getString(R.string.label_extra_bed)
                                , getExtraChargePrice(info.extraInformation.extraBed), "", true)
                    }

                    visibility = if (getItemCount() == 0) View.GONE else View.VISIBLE
                }
            }

            extraChargeNightsTableLayout.run {
                if (info.consecutiveInformation == null || !info.consecutiveInformation.enable) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE

                    setTitleTextSize(16f)
//                        setTitleTextSize(if (largeView) 16f else 15f)

                    setTitleVisible(true)
                    setTitleText(R.string.label_stay_room_extra_charge_consecutive_title)
                    clearTableLayout()

                    addTableRow(context.resources.getString(R.string.label_stay_room_extra_charge_consecutive_item_title)
                            , getExtraChargePrice(info.consecutiveInformation.charge), "", true)
                }
            }

            extraChargeDescriptionGridView?.run {
                if (info.descriptionList == null || info.descriptionList.size == 0) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE

                    columnCount = 1
                    setData(""
                            , DailyRoomInfoView.ItemType.DOT, info.descriptionList, true, true)
                }
            }
        }
    }

    private fun setNeedToKnowInformationView(needToKnowList: MutableList<String>?) {
        viewDataBinding.run {
            if (!needToKnowList.isNotNullAndNotEmpty()) {
                needToKnowInfoGroup.visibility = View.GONE
                return
            }

            needToKnowInfoGroup.visibility = View.VISIBLE

            needToKnowInfoGridView.run {
                columnCount = 1
                setData(context.resources.getString(R.string.label_stay_room_need_to_know_title)
                        , DailyRoomInfoView.ItemType.DOT, needToKnowList!!, true, true)
            }
        }
    }

    private fun getPersonRangeText(minAge: Int, maxAge: Int): String {
        return if (minAge == -1 && maxAge == -1) {
            ""
        } else if (minAge != -1 && maxAge != -1) {
            context.resources.getString(R.string.label_person_age_range_format, minAge, maxAge)
        } else if (minAge != -1) {
            context.resources.getString(R.string.label_person_age_and_over_format, minAge)
        } else {
            context.resources.getString(R.string.label_person_age_under_format, maxAge)
        }
    }

    private fun getExtraChargePrice(price: Int): String {
        if (price <= 0) {
            return context.resources.getString(R.string.label_free)
        }

        return DailyTextUtils.getPriceFormat(context, price, false)
    }
}