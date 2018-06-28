package com.daily.dailyhotel.screen.home.stay.inbound.detail.room

import android.content.Context
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.util.*
import com.daily.base.widget.DailyImageView
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.view.DailyRoomInfoTableView
import com.daily.dailyhotel.view.DailyRoomInfoView
import com.daily.dailyhotel.view.DailyStayRoomBedDescriptionLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.place.base.OnBaseEventListener
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan

class StayRoomAdapter(private val context: Context, private val list: MutableList<Room>, private var nights: Int = 1) : RecyclerView.Adapter<StayRoomAdapter.RoomViewHolder>() {

    companion object {
        const val MENU_WIDTH_RATIO = 0.865f
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

    private var onEventListener: OnEventListener? = null

    interface OnEventListener : OnBaseEventListener {
        fun onMoreImageClick(position: Int)
        fun onVrImageClick(position: Int)
    }

    fun setEventListener(listener: OnEventListener?) {
        onEventListener = listener
    }

    fun getItem(position: Int): Room? {
        return if (position in 0 until list.size) {
            list[position]
        } else null
    }

    fun setData(list: MutableList<Room>) {
        this.list.clear()
        this.list += list
    }

    fun setNights(nights: Int) {
        this.nights = nights
    }

    fun getNights(): Int {
        return nights
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val roomLayout: View = LayoutInflater.from(context).inflate(R.layout.list_row_stay_room_data, parent, false)

        roomLayout.layoutParams = RecyclerView.LayoutParams(getLayoutWidth().toInt(), RecyclerView.LayoutParams.MATCH_PARENT)

        return RoomViewHolder(roomLayout)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = getItem(position) ?: return
        val roomNameTextView: DailyTextView? = holder.itemView.findViewById(R.id.roomNameTextView)

        val margin = getLayoutMargin()
        (holder.rootView.layoutParams as RecyclerView.LayoutParams).run {
            when (position) {
                0 -> {
                    leftMargin = margin.toInt()
                    rightMargin = 0
                }

                itemCount - 1 -> {
                    leftMargin = 0
                    rightMargin = margin.toInt()
                }

                else -> {
                    leftMargin = 0
                    rightMargin = 0
                }
            }
        }

        setImageInformationView(holder.itemView, position, room)

        roomNameTextView?.text = room.name

        setAmountInformationView(holder.rootView, room.amountInformation, false)

        setRefundInformationView(holder.rootView, room.refundInformation)

        setBaseInformationView(holder.rootView, room)

        setAttributeInformationView(holder.rootView, room.attributeInformation)

        val benefitList = mutableListOf<String>()

        val breakfast = room.personsInformation?.breakfast ?: 0
        if (breakfast > 0) {
            benefitList.add(context.resources.getString(R.string.label_stay_room_breakfast_person, breakfast))
        }

        if (!room.benefit.isTextEmpty()) {
            benefitList.add(room.benefit)
        }
        setRoomBenefitInformationView(holder.rootView, benefitList, false)

        setRewardAndCouponInformationView(holder.rootView, room.provideRewardSticker, room.hasUsableCoupon)

        setCheckTimeInformationView(holder.rootView, room.checkTimeInformation)

        setRoomDescriptionInformationView(holder.rootView, room.descriptionList, false)

        setRoomAmenityInformationView(holder.rootView, room.amenityList, false)

        setRoomChargeInformationView(holder.rootView, room.roomChargeInformation, false)

        setNeedToKnowInformationView(holder.rootView, room.needToKnowList, false)
    }

    fun setImageInformationView(root: View, position: Int, room: Room) {
        val defaultImageLayout: View? = root.findViewById(R.id.defaultImageLayout)
        val simpleDraweeView: SimpleDraweeView? = root.findViewById(R.id.simpleDraweeView)
        val moreIconView: View? = root.findViewById(R.id.moreIconView)
        val vrIconView: View? = root.findViewById(R.id.vrIconView)
        // StayRoomView 의 invisibleLayout 에서 사용하는 뷰들 - 메소드 량 줄이기의 일환으로 여기서 처리
        val emptyLayout: View? = root.findViewById(R.id.emptyLayout)
        val closeImageView: View? = root.findViewById(R.id.closeImageView)

        moreIconView?.run {
            visibility = if (room.imageCount > 0) View.VISIBLE else View.GONE
        }

        vrIconView?.run {
            visibility = if (DailyPreference.getInstance(context).trueVRSupport > 0
                    && room.vrInformationList.isNotNullAndNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            setOnClickListener {
                onEventListener?.onVrImageClick(position)
            }
        }

        closeImageView?.run {
            setOnClickListener {
                onEventListener?.finish()
            }
        }

        val stringUrl: String? = if (room.imageInformation == null) {
            null
        } else {
            room.imageInformation.imageMap.bigUrl
        }

        defaultImageLayout?.run {
            when {
                stringUrl.isTextEmpty() -> {
                    setOnClickListener(null)
                }

                else -> {
                    setOnClickListener {
                        onEventListener?.onMoreImageClick(position)
                    }
                }
            }
        }

        emptyLayout?.run {
            when {
                stringUrl.isTextEmpty() -> {
                    setOnClickListener(null)
                }

                else -> {
                    setOnClickListener {
                        onEventListener?.onMoreImageClick(position)
                    }
                }
            }
        }

        simpleDraweeView?.run {
            hierarchy.setPlaceholderImage(R.drawable.layerlist_room_no_image_holder)
            Util.requestImageResize(context, this, stringUrl)
        }
    }

    fun setAmountInformationView(root: View, amountInformation: Room.AmountInformation, largeView: Boolean) {
        val discountPercentTextView: DailyTextView? = root.findViewById(R.id.discountPercentTextView)
        val priceTextView: DailyTextView? = root.findViewById(R.id.priceTextView)
        val discountPriceTextView: DailyTextView? = root.findViewById(R.id.discountPriceTextView)
        val discountPriceUnitTextView: DailyTextView? = root.findViewById(R.id.discountPriceUnitTextView)

        val showOriginPrice = amountInformation.priceAverage > 0
                && amountInformation.priceAverage > amountInformation.discountAverage
        val showDiscountRate = amountInformation.discountRate in 5..100 && showOriginPrice

        discountPercentTextView?.run {
            when (showDiscountRate) {
                true -> {
                    val discountRateSpan = SpannableString("${amountInformation.discountRate}%")
                    discountRateSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface)
                            , discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    val discountRateTextSize = if (largeView) 14.0 else 12.0
                    discountRateSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, discountRateTextSize))
                            , discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    text = discountRateSpan
                    visibility = View.VISIBLE
                }

                false -> {
                    visibility = View.GONE
                }
            }
        }

        priceTextView?.run {
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

        discountPriceTextView?.run {
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

    fun setRefundInformationView(root: View, refundInformation: StayDetail.RefundInformation?) {
        val refundPolicyTextView: DailyTextView? = root.findViewById(R.id.refundPolicyTextView)

        refundPolicyTextView?.run {
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

    fun setBaseInformationView(root: View, room: Room) {
        val baseInfoGroup: View? = root.findViewById(R.id.baseInfoGroup)
        baseInfoGroup?.run {
            val personsInformation: Room.PersonsInformation? = room.personsInformation
            val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

            if (personsInformation == null && !bedTypeList.isNotNullAndNotEmpty() && room.squareMeter == 0f) {
                baseInfoGroup.visibility = View.GONE
                return
            }

            baseInfoGroup.visibility = View.VISIBLE

            setPersonInformationView(root, room)
            setBedInformationView(root, room)
            setSquareInformationView(root, room)
        }
    }

    private fun setPersonInformationView(root: View, room: Room) {
        val personIconImageView: DailyImageView? = root.findViewById(R.id.personIconImageView)
        val personTitleTextView: DailyTextView? = root.findViewById(R.id.personTitleTextView)
        val personDescriptionTextView: DailyTextView? = root.findViewById(R.id.personDescriptionTextView)

        val personsInformation: Room.PersonsInformation? = room.personsInformation

        var personVectorIconResId = 0
        var personTitle = ""
        var personDescription = ""

        personsInformation?.let {
            personTitle = context.resources.getString(R.string.label_standard_persons, it.fixed)

            personVectorIconResId = when (it.fixed) {
                0, 1 -> R.drawable.vector_ic_detail_item_people_1

                2 -> R.drawable.vector_ic_detail_item_people_2

                else -> R.drawable.vector_ic_detail_item_people_3
            }

            val subDescription = if (it.extra == 0) "" else {
                " " + context.resources.getString(if (it.extraCharge) {
                    R.string.label_bracket_pay
                } else {
                    R.string.label_bracket_free
                })
            }
            personDescription = context.resources.getString(R.string.label_stay_outbound_room_max_person_free, it.fixed + it.extra) + subDescription
        }

        personIconImageView?.setVectorImageResource(personVectorIconResId)
        personTitleTextView?.text = personTitle
        personDescriptionTextView?.text = personDescription
    }

    private fun setBedInformationView(root: View, room: Room) {
        val bedIconImageView: DailyImageView? = root.findViewById(R.id.bedIconImageView)
        val bedDescriptionLayout: DailyStayRoomBedDescriptionLayout? = root.findViewById(R.id.bedDescriptionLayout)

        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        var bedVectorIconResId = 0

        val typeStringList = mutableListOf<String>()

        bedTypeList?.forEachIndexed { index, bedTypeInformation ->
            val bedType: BedType = try {
                BedType.valueOf(bedTypeInformation.bedType.toUpperCase())
            } catch (e: Exception) {
                BedType.UNKNOWN
            }

            bedVectorIconResId = if (index == 0) {
                if (bedType.vectorIconResId != 0) {
                    bedType.vectorIconResId
                } else {
                    R.drawable.vector_ic_detail_item_bed_double
                }
            } else {
                R.drawable.vector_ic_detail_item_bed_double
            }

            typeStringList += if (StayRoomAdapter.BedType.UNKNOWN == bedType) {
                bedType.getName(context)
            } else {
                "${bedType.getName(context)} ${bedTypeInformation.count}" + if (index == bedTypeList.size - 1) {
                    context.resources.getString(R.string.label_bed_count_end_string)
                } else {
                    ""
                }
            }
        }

        bedVectorIconResId.takeIf { bedVectorIconResId == 0 }.let {
            BedType.UNKNOWN.vectorIconResId
        }

        bedIconImageView?.setVectorImageResource(bedVectorIconResId)
        bedDescriptionLayout?.setData(typeStringList)
    }

    private fun setSquareInformationView(root: View, room: Room) {
        val squareInformationLayout: View? = root.findViewById(R.id.squareInformationLayout)
        val squareTitleTextView: DailyTextView? = root.findViewById(R.id.squareTitleTextView)
        val squareDescriptionTextView: DailyTextView? = root.findViewById(R.id.squareDescriptionTextView)

        val pyoung = Math.round(room.squareMeter * 0.3025)
        when {
            pyoung < 1 -> squareInformationLayout?.visibility = View.GONE
            else -> {
                squareInformationLayout?.visibility = View.VISIBLE
                squareTitleTextView?.text = "${room.squareMeter}m"

                // ㎡×0.3025=평 - / 400 * 121  /   평×3.3058=㎡ - / 121 * 400
                squareDescriptionTextView?.text = context.resources.getString(R.string.label_pyoung_format, pyoung)
            }
        }
    }

    fun setAttributeInformationView(root: View, attribute: Room.AttributeInformation?, largeView: Boolean = false) {
        val subInfoGroup: View = root.findViewById(R.id.subInfoGroup) ?: return
        val subInfoGridView: DailyRoomInfoView = root.findViewById(R.id.subInfoGridView)

        if (attribute == null) {
            subInfoGroup.visibility = View.GONE
            return
        }

        subInfoGridView.run {
            val roomType: StayRoomAdapter.RoomType? = try {
                StayRoomAdapter.RoomType.valueOf(attribute.roomStructure)
            } catch (e: Exception) {
                null
            }

            var titleText = roomType?.getName(context) ?: ""

            attribute.isEntireHouse.runTrue {
                if (!titleText.isTextEmpty()) {
                    titleText += "/"
                }

                titleText += context.resources.getString(R.string.label_room_type_entire_house)
            }

            attribute.isDuplex.runTrue {
                if (!titleText.isTextEmpty()) {
                    titleText += "/"
                }

                titleText += context.resources.getString(R.string.label_room_type_duplex_room)
            }

            subInfoGridView.columnCount = 2

            val stringList = mutableListOf<String>()
            var roomString = ""

            attribute.structureInformationList?.forEach {
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
                        stringList += context.resources.getString(R.string.label_living_room) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else ""
                    }

                    "KITCHEN" -> {
                        stringList += context.resources.getString(R.string.label_kitchen) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else ""
                    }

                    "REST_ROOM" -> {
                        stringList += context.resources.getString(R.string.label_rest_room) + if (it.count > 1) {
                            " ${it.count}${context.resources.getString(R.string.label_bed_count_end_string)}"
                        } else ""
                    }

                    else -> {
                        // do nothing
                    }
                }
            }

            if (!roomString.isTextEmpty()) {
                stringList.add(0, roomString)
            }

            if (titleText.isTextEmpty() && !stringList.isNotNullAndNotEmpty()) {
                subInfoGroup.visibility = View.GONE
            } else {
                subInfoGroup.visibility = View.VISIBLE
                subInfoGridView.setData(titleText, DailyRoomInfoView.ItemType.NONE, stringList, largeView)
            }
        }
    }

    fun setRoomBenefitInformationView(root: View, benefitList: MutableList<String>, largeView: Boolean) {
        val roomBenefitGroup: View = root.findViewById(R.id.roomBenefitGroup) ?: return
        val roomBenefitGridView: DailyRoomInfoView? = root.findViewById(R.id.roomBenefitGridView)

        if (benefitList.isEmpty()) {
            roomBenefitGroup.visibility = View.GONE
            return
        }

        roomBenefitGroup.visibility = View.VISIBLE

        roomBenefitGridView?.run {
            columnCount = 1
            setData(context.resources.getString(R.string.label_stay_room_benefit_title)
                    , DailyRoomInfoView.ItemType.DOWN_CARET, benefitList, largeView)
        }
    }

    fun setRewardAndCouponInformationView(root: View, rewardAble: Boolean, useCoupon: Boolean) {
        val discountInfoGroup: View = root.findViewById(R.id.discountInfoGroup) ?: return
        val discountInfoTextView: DailyTextView? = root.findViewById(R.id.discountInfoTextView)

        if (rewardAble || useCoupon) {
            discountInfoGroup.visibility = View.VISIBLE
        } else {
            discountInfoGroup.visibility = View.GONE
            return
        }

        discountInfoTextView?.run {
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

            this.text = spannableString
        }
    }

    fun setCheckTimeInformationView(root: View, checkTimeInformation: StayDetail.CheckTimeInformation?) {
        if (checkTimeInformation == null) return

        val checkTimeInfoLayout: View = root.findViewById(R.id.checkTimeInfoLayout) ?: return
        val checkInTimeTextView: DailyTextView? = root.findViewById(R.id.checkInTimeTextView)
        val checkOutTimeTextView: DailyTextView? = root.findViewById(R.id.checkOutTimeTextView)

        if (isTextEmpty(checkTimeInformation.checkIn, checkTimeInformation.checkOut)) {
            checkTimeInfoLayout.visibility = View.GONE
            return
        }

        checkTimeInfoLayout.visibility = View.VISIBLE

        checkInTimeTextView?.text = checkTimeInformation.checkIn
        checkOutTimeTextView?.text = checkTimeInformation.checkOut
    }

    fun setRoomDescriptionInformationView(root: View, descriptionList: MutableList<String>?, largeView: Boolean) {
        val roomDescriptionGroup: View = root.findViewById(R.id.roomDescriptionGroup) ?: return
        val roomDescriptionGridView: DailyRoomInfoView? = root.findViewById(R.id.roomDescriptionGridView)

        if (descriptionList == null || descriptionList.size == 0) {
            roomDescriptionGroup.visibility = View.GONE
            return
        }

        roomDescriptionGroup.visibility = View.VISIBLE

        roomDescriptionGridView?.run {
            columnCount = 1
            setData(context.resources.getString(R.string.label_stay_room_description_title)
                    , DailyRoomInfoView.ItemType.DOT, descriptionList, largeView)
        }
    }

    fun setRoomAmenityInformationView(root: View, amenityList: MutableList<String>, largeView: Boolean) {
        val roomAmenityGroup: View = root.findViewById(R.id.roomAmenityGroup) ?: return
        val roomAmenityGridView: DailyRoomInfoView? = root.findViewById(R.id.roomAmenityGridView)

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
                    , DailyRoomInfoView.ItemType.DOT, list, largeView)
        }
    }

    fun setRoomChargeInformationView(root: View, info: Room.ChargeInformation?, largeView: Boolean) {
        val extraChargeLayout: View = root.findViewById(R.id.extraChargeLayout) ?: return
        val extraChargePersonTableLayout: DailyRoomInfoTableView? = root.findViewById(R.id.extraChargePersonTableLayout)
        val extraChargeBedTableLayout: DailyRoomInfoTableView? = root.findViewById(R.id.extraChargeBedTableLayout)
        val extraChargeNightsTableLayout: DailyRoomInfoTableView? = root.findViewById(R.id.extraChargeNightsTableLayout)
        val extraChargeDescriptionGridView: DailyRoomInfoView? = root.findViewById(R.id.extraChargeDescriptionGridView)


        if (info == null || info.isAllHidden) {
            extraChargeLayout.visibility = View.GONE
            return
        }

        extraChargeLayout.visibility = View.VISIBLE

        extraChargePersonTableLayout?.run {
            if (!info.extraPersonInformationList.isNotNullAndNotEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE

                largeView.runTrue {
                    setTitleTextSize(if (largeView) 16f else 15f)
                }

                setTitleText(R.string.label_stay_room_extra_charge_person_title)
                setTitleVisible(true)
                clearTableLayout()

                info.extraPersonInformationList.forEach {
                    var title = it.title

                    getPersonRangeText(it.minAge, it.maxAge).letNotEmpty { title += " ($it)" }

                    val subDescription = if (it.maxPersons > 0) context.resources.getString(R.string.label_room_max_person_range_format, it.maxPersons) else ""

                    addTableRow(title, getExtraChargePrice(it.amount), subDescription, largeView)
                }
            }
        }

        extraChargeBedTableLayout?.run {
            if (info.extraInformation == null || info.extraInformation.isAllHidden) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE

                largeView.runTrue {
                    setTitleTextSize(if (largeView) 16f else 15f)
                }

                setTitleVisible(true)
                setTitleText(R.string.label_stay_room_extra_charge_bed_title)
                clearTableLayout()

                (info.extraInformation.extraBeddingEnable).runTrue {
                    addTableRow(context.resources.getString(R.string.label_bedding)
                            , getExtraChargePrice(info.extraInformation.extraBedding), "", largeView)
                }

                (info.extraInformation.extraBedEnable).runTrue {
                    addTableRow(context.resources.getString(R.string.label_extra_bed)
                            , getExtraChargePrice(info.extraInformation.extraBed), "", largeView)
                }

                visibility = if (getItemCount() == 0) View.GONE else View.VISIBLE
            }
        }

        extraChargeNightsTableLayout?.run {
            if (info.consecutiveInformation == null || !info.consecutiveInformation.enable) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE

                largeView.runTrue {
                    setTitleTextSize(if (largeView) 16f else 15f)
                }

                setTitleVisible(true)
                setTitleText(R.string.label_stay_room_extra_charge_consecutive_title)
                clearTableLayout()

                addTableRow(context.resources.getString(R.string.label_stay_room_extra_charge_consecutive_item_title)
                        , getExtraChargePrice(info.consecutiveInformation.charge), "", largeView)
            }
        }

        extraChargeDescriptionGridView?.run {
            if (info.descriptionList == null || info.descriptionList.size == 0) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE

                columnCount = 1
                setData(""
                        , DailyRoomInfoView.ItemType.DOT, info.descriptionList, largeView, true)
            }
        }
    }

    fun setNeedToKnowInformationView(root: View, needToKnowList: MutableList<String>?, largeView: Boolean) {
        val roomCheckInfoGroup: View = root.findViewById(R.id.roomCheckInfoGroup) ?: return
        val roomCheckInfoGridView: DailyRoomInfoView? = root.findViewById(R.id.roomCheckInfoGridView)

        if (needToKnowList == null || needToKnowList.size == 0) {
            roomCheckInfoGroup.visibility = View.GONE
            return
        }

        roomCheckInfoGroup.visibility = View.VISIBLE

        roomCheckInfoGridView?.run {
            columnCount = 1
            setData(context.resources.getString(R.string.label_stay_room_need_to_know_title)
                    , DailyRoomInfoView.ItemType.DOT, needToKnowList, largeView, true)
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

    private fun getLayoutWidth(): Float {
        return ScreenUtils.getScreenWidth(context) * MENU_WIDTH_RATIO
    }

    fun getLayoutMargin(): Float {
        return ScreenUtils.getScreenWidth(context) * (1.0f - MENU_WIDTH_RATIO) / 2.0f
    }

    inner class RoomViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView)
}