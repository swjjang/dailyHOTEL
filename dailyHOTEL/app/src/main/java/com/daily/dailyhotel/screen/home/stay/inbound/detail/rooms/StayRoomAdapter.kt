package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Context
import android.databinding.DataBindingUtil
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
import com.daily.base.util.DailyImageSpan
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyImageView
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.util.*
import com.daily.dailyhotel.view.DailyRoomInfoGridView
import com.daily.dailyhotel.view.DailyStayRoomBedDescriptionLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ListRowStayRoomDataBinding
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
        val dataBinding = DataBindingUtil.inflate<ListRowStayRoomDataBinding>(
                LayoutInflater.from(parent.context), R.layout.list_row_stay_room_data, parent, false)

        dataBinding.roomLayout.layoutParams = RecyclerView.LayoutParams(getLayoutWidth().toInt(), RecyclerView.LayoutParams.MATCH_PARENT)

        return RoomViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val dataBinding: ListRowStayRoomDataBinding = holder.dataBinding

        val room = getItem(position) ?: return

        holder.dataBinding.root.setTag(R.id.blurView, holder.dataBinding.blurView)

        val margin = getLayoutMargin()
        (dataBinding.roomLayout.layoutParams as RecyclerView.LayoutParams).apply {
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

        setImageInformationView(dataBinding.root, position, room)

        dataBinding.roomNameTextView.text = room.name

        setAmountInformationView(dataBinding.root, room.amountInformation, false)

        setRefundInformationView(dataBinding.root, room.refundInformation)

        setBaseInformationGridView(dataBinding.root, room)

        setAttributeInformationView(dataBinding, room.attributeInformation)

        val benefitList = mutableListOf<String>()

        val breakfast = room.personsInformation?.breakfast ?: 0
        if (breakfast > 0) {
            benefitList.add(context.resources.getString(R.string.label_stay_room_breakfast_person, breakfast))
        }

        if (!room.benefit.isTextEmpty()) {
            benefitList.add(room.benefit)
        }
        setRoomBenefitInformationView(dataBinding, benefitList)

        setRewardAndCouponInformationView(dataBinding, room.provideRewardSticker, room.hasUsableCoupon)

        setCheckTimeInformationView(dataBinding, room.checkTimeInformation)

        setRoomDescriptionInformationView(dataBinding, room.descriptionList)

        setRoomAmenityInformationView(dataBinding, room.amenityList)

        setRoomChargeInformationView(dataBinding, room.roomChargeInformation)

        setNeedToKnowInformationView(dataBinding, room.needToKnowList)
    }

    fun setImageInformationView(root: View, position: Int, room: Room) {
        val defaultImageLayout: View? = root.findViewById(R.id.defaultImageLayout)
        val simpleDraweeView: SimpleDraweeView? = root.findViewById(R.id.simpleDraweeView)
        val moreIconView: View? = root.findViewById(R.id.moreIconView)
        val vrIconView: View? = root.findViewById(R.id.vrIconView)
        // StayRoomsView 의 invisibleLayout 에서 사용하는 뷰들 - 메소드 량 줄이기의 일환으로 여기서 처리
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
            val discountPriceUnitText = context.resources.getString(R.string.currency) + if (nights > 1) context.resources.getString(R.string.label_stay_detail_slash_one_nights) else ""
            text = discountPriceUnitText
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

    fun setBaseInformationGridView(root: View, room: Room) {
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

            bedVectorIconResId = if (bedVectorIconResId != 0) {
                bedType.vectorIconResId
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

    private fun setAttributeInformationView(dataBinding: ListRowStayRoomDataBinding, attribute: Room.AttributeInformation?) {
        if (attribute == null) {
            dataBinding.subInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.subInfoGroup.visibility = View.VISIBLE

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

        dataBinding.subInfoGridView.columnCount = 2

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
            dataBinding.subInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.subInfoGridView.setData(titleText, DailyRoomInfoGridView.ItemType.NONE, stringList, false)
    }

    private fun setRoomBenefitInformationView(dataBinding: ListRowStayRoomDataBinding, benefitList: MutableList<String>) {
        if (benefitList.isEmpty()) {
            dataBinding.roomBenefitGroup.visibility = View.GONE
            return
        }

        dataBinding.roomBenefitGroup.visibility = View.VISIBLE

        dataBinding.roomBenefitGridView.columnCount = 1
        dataBinding.roomBenefitGridView.setData(
                context.resources.getString(R.string.label_stay_room_benefit_title)
                , DailyRoomInfoGridView.ItemType.DOWN_CARET, benefitList, false)
    }

    private fun setRewardAndCouponInformationView(dataBinding: ListRowStayRoomDataBinding, rewardable: Boolean, useCoupon: Boolean) {
        if (rewardable || useCoupon) {
            dataBinding.discountInfoGroup.visibility = View.VISIBLE
        } else {
            dataBinding.discountInfoGroup.visibility = View.GONE
            return
        }

        var text = ""
        val rewardString = context.resources.getString(R.string.label_stay_room_rewardable)
        val couponString = context.resources.getString(R.string.label_stay_room_coupon_useable)

        if (rewardable) {
            text = "  $rewardString"
        }

        if (useCoupon) {
            if (!text.isTextEmpty()) {
                text += " ${context.resources.getString(R.string.label_stay_room_reward_coupon_or)} "
            }

            text += couponString
        }

        if (!text.isTextEmpty()) {
            text += context.resources.getString(R.string.label_stay_room_end_description)
        }

        val spannableString = SpannableString(text)

        val rewardStart = text.indexOf(rewardString)

        if (rewardStart != -1) {
            spannableString.setSpan(DailyImageSpan(context, R.drawable.r_ic_xs_14, DailyImageSpan.ALIGN_VERTICAL_CENTER), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_line_cfaae37)), rewardStart, rewardStart + rewardString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val couponStart = text.indexOf(couponString)
        if (couponStart != -1) {
            spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_text_cf27c7a)), couponStart, couponStart + couponString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        dataBinding.discountInfoTextView.text = spannableString
    }

    private fun setCheckTimeInformationView(dataBinding: ListRowStayRoomDataBinding, checkTimeInformation: StayDetail.CheckTimeInformation?) {
        if (checkTimeInformation == null) return

        if (isTextEmpty(checkTimeInformation.checkIn, checkTimeInformation.checkOut)) {
            dataBinding.checkTimeInfoLayout.visibility = View.GONE
            return
        }

        dataBinding.checkTimeInfoLayout.visibility = View.VISIBLE

        dataBinding.checkInTimeTextView.text = checkTimeInformation.checkIn
        dataBinding.checkOutTimeTextView.text = checkTimeInformation.checkOut
    }

    private fun setRoomDescriptionInformationView(dataBinding: ListRowStayRoomDataBinding, descriptionList: MutableList<String>?) {
        if (descriptionList == null || descriptionList.size == 0) {
            dataBinding.roomDescriptionGroup.visibility = View.GONE
            return
        }

        dataBinding.roomDescriptionGroup.visibility = View.VISIBLE

        dataBinding.roomDescriptionGridView.columnCount = 1
        dataBinding.roomDescriptionGridView.setData(
                context.resources.getString(R.string.label_stay_room_description_title)
                , DailyRoomInfoGridView.ItemType.DOT, descriptionList, false)
    }

    private fun setRoomAmenityInformationView(dataBinding: ListRowStayRoomDataBinding, amenityList: MutableList<String>) {
        if (amenityList.size == 0) {
            dataBinding.roomAmenityGroup.visibility = View.GONE
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
            dataBinding.roomAmenityGroup.visibility = View.GONE
            return
        }

        dataBinding.roomAmenityGroup.visibility = View.VISIBLE
        dataBinding.roomAmenityGridView.columnCount = 2
        dataBinding.roomAmenityGridView.setData(
                context.resources.getString(R.string.label_stay_room_amenity_title)
                , DailyRoomInfoGridView.ItemType.DOT, list, false)
    }

    private fun setRoomChargeInformationView(dataBinding: ListRowStayRoomDataBinding, info: Room.ChargeInformation?) {
        if (info == null || info.isAllHidden) {
            dataBinding.extraChargeLayout.visibility = View.GONE
            return
        }

        if (!info.extraPersonInformationList.isNotNullAndNotEmpty()) {
            dataBinding.extraChargePersonTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargePersonTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargePersonTableLayout.setTitleText(R.string.label_stay_room_extra_charge_person_title)
            dataBinding.extraChargePersonTableLayout.setTitleVisible(true)
            dataBinding.extraChargePersonTableLayout.clearTableLayout()

            info.extraPersonInformationList.forEach {
                var title = it.title

                getPersonRangeText(it.minAge, it.maxAge).letNotEmpty { title += " ($it)" }

                val subDescription = if (it.maxPersons > 0) context.resources.getString(R.string.label_room_max_person_range_format, it.maxPersons) else ""

                dataBinding.extraChargePersonTableLayout.addTableRow(title, getExtraChargePrice(it.amount), subDescription, false)
            }
        }

        if (info.extraInformation == null || info.extraInformation.isAllHidden) {
            dataBinding.extraChargeBedTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargeBedTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargeBedTableLayout.setTitleVisible(true)
            dataBinding.extraChargeBedTableLayout.setTitleText(R.string.label_stay_room_extra_charge_bed_title)
            dataBinding.extraChargeBedTableLayout.clearTableLayout()

            (info.extraInformation.extraBeddingEnable).runTrue {
                dataBinding.extraChargeBedTableLayout.addTableRow(
                        context.resources.getString(R.string.label_bedding)
                        , getExtraChargePrice(info.extraInformation.extraBedding), "", false)
            }

            (info.extraInformation.extraBedEnable).runTrue {
                dataBinding.extraChargeBedTableLayout.addTableRow(
                        context.resources.getString(R.string.label_extra_bed)
                        , getExtraChargePrice(info.extraInformation.extraBed), "", false)
            }

            dataBinding.extraChargeBedTableLayout.visibility = if (dataBinding.extraChargeBedTableLayout.getItemCount() == 0) View.GONE else View.VISIBLE
        }

        if (info.descriptionList == null || info.descriptionList.size == 0) {
            dataBinding.extraChargeDescriptionGridView.visibility = View.GONE
        } else {
            dataBinding.extraChargeDescriptionGridView.visibility = View.VISIBLE

            dataBinding.extraChargeDescriptionGridView.columnCount = 1
            dataBinding.extraChargeDescriptionGridView.setData(
                    ""
                    , DailyRoomInfoGridView.ItemType.DOT, info.descriptionList, false)
        }

        if (info.consecutiveInformation == null || !info.consecutiveInformation.enable) {
            dataBinding.extraChargeNightsTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargeNightsTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargeNightsTableLayout.setTitleVisible(true)
            dataBinding.extraChargeNightsTableLayout.setTitleText(R.string.label_stay_room_extra_charge_consecutive_title)
            dataBinding.extraChargeNightsTableLayout.clearTableLayout()

            dataBinding.extraChargeNightsTableLayout.addTableRow(
                    context.resources.getString(R.string.label_stay_room_extra_charge_consecutive_item_title)
                    , getExtraChargePrice(info.consecutiveInformation.charge), "", false)
        }
    }

    fun getPersonRangeText(minAge: Int, maxAge: Int): String {
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

    fun getExtraChargePrice(price: Int): String {
        if (price <= 0) {
            return context.resources.getString(R.string.label_free)
        }

        return DailyTextUtils.getPriceFormat(context, price, false)
    }

    private fun setNeedToKnowInformationView(dataBinding: ListRowStayRoomDataBinding, needToKnowList: MutableList<String>?) {
        if (needToKnowList == null || needToKnowList.size == 0) {
            dataBinding.roomCheckInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.roomCheckInfoGroup.visibility = View.VISIBLE

        dataBinding.roomCheckInfoGridView.columnCount = 1
        dataBinding.roomCheckInfoGridView.setData(
                context.resources.getString(R.string.label_stay_room_need_to_know_title)
                , DailyRoomInfoGridView.ItemType.DOT, needToKnowList, false)
    }

    fun getLayoutWidth(): Float {
        return ScreenUtils.getScreenWidth(context) * MENU_WIDTH_RATIO
    }

    fun getLayoutMargin(): Float {
        return ScreenUtils.getScreenWidth(context) * (1.0f - MENU_WIDTH_RATIO) / 2.0f
    }

    inner class RoomViewHolder(val dataBinding: ListRowStayRoomDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}