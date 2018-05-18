package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.util.*
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayDetailk
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.view.DailyRoomInfoGridView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ListRowStayRoomDataBinding
import com.twoheart.dailyhotel.place.base.OnBaseEventListener
import com.twoheart.dailyhotel.util.DailyCalendar
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan

class StayRoomAdapter(private val context: Context, private val list: MutableList<Room>, private var nights: Int = 1) : RecyclerView.Adapter<StayRoomAdapter.RoomViewHolder>() {

    companion object {
        const val MENU_WIDTH_RATIO = 0.865f
    }


    enum class RoomType(val stringResId: Int) {
        ONE_ROOM(R.string.label_room_type_one_room),
        LIVING_ROOM(R.string.label_room_type_living_room);

        fun getName(context: Context): String {
            return context.resources.getString(stringResId)
        }
    }

    enum class BedType(val stringResId: Int = R.string.label_bed_type_unknown, val vectorIconResId: Int = R.drawable.vector_ic_detail_item_bed_double) {
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

    enum class RoomAmenityType(val stringResId: Int) {
        Cooking(R.string.label_cooking),
        Pc(R.string.label_computer),
        Bath(R.string.label_bathtub),
        Tv(R.string.label_television),
        SpaWallpool(R.string.label_whirlpool),
        PrivateBbq(R.string.label_private_bbq),
        Smokeable(R.string.label_smoke_able),
        Karaoke(R.string.label_karaoke),
        PartyRoom(R.string.label_party_room),
        Amenity(R.string.label_bath_amenity),
        ShowerGown(R.string.label_shower_gown),
        ToothbrushSet(R.string.label_tooth_brush_set),
        DisabledFacilities(R.string.label_disabled_facilities);

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
        return (list.size > 0).let { list[position] }
    }

    fun setData(list: MutableList<Room>) {
        this.list.clear()
        this.list += list
    }

    fun setNights(nights: Int) {
        this.nights = nights
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

        if (room.imageInformation == null) {
            dataBinding.defaultImageLayout.visibility = View.GONE
            dataBinding.defaultImageLayout.setOnClickListener(null)
        } else {
            dataBinding.defaultImageLayout.visibility = View.VISIBLE
            dataBinding.defaultImageLayout.setOnClickListener {
                onEventListener?.let {
                    it.onMoreImageClick(position)
                }
            }

            dataBinding.simpleDraweeView.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder)
            Util.requestImageResize(context, dataBinding.simpleDraweeView, room.imageInformation.imageMap.bigUrl)

            dataBinding.moreIconView.visibility = if (room.imageCount > 0) View.VISIBLE else View.GONE
            dataBinding.vrIconView.visibility = if (room.vrInformationList.isNotNullAndNotEmpty()) View.VISIBLE else View.GONE
            dataBinding.vrIconView.setOnClickListener {
                onEventListener?.let {
                    it.onVrImageClick(position)
                }
            }
        }

        dataBinding.roomNameTextView.text = room.name

        setAmountInformationView(dataBinding, room.amountInformation)

        setRefundInformationView(dataBinding, room.refundInformation)

        setBaseInformationGridView(dataBinding, room)

        setAttributeInformationView(dataBinding, room.attributeInformation)

        var benefitList = mutableListOf<String>()

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

        setRoomChargeInformatinoView(dataBinding, room.roomChargeInformation)

        setNeedToKnowInformationView(dataBinding, room.needToKnowList)
    }

    private fun setAmountInformationView(dataBinding: ListRowStayRoomDataBinding, amountInformation: Room.AmountInformation) {
        if (amountInformation == null) {
            dataBinding.discountPercentTextView.visibility = View.GONE
            dataBinding.priceTextView.visibility = View.GONE
            dataBinding.discountPriceTextView.setText(R.string.label_soldout)

            ExLog.e("amountInformation is null")
        } else {
            dataBinding.discountPercentTextView.visibility = View.VISIBLE
            dataBinding.priceTextView.visibility = View.VISIBLE

            val discountRateSpan = SpannableString("${amountInformation.discountRate}%")
            discountRateSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface), discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            discountRateSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 12.0)), discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.discountPercentTextView.text = discountRateSpan

            val nightsString = if (nights > 1) context.resources.getString(R.string.label_stay_detail_slash_one_nights) else ""
            val discountPriceString = DailyTextUtils.getPriceFormat(context, amountInformation.discountAverage, false)

            val discountPriceSpan = SpannableString("$discountPriceString$nightsString")
            discountPriceSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface), discountPriceString.length - 1, discountPriceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            discountPriceSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 12.0)), discountPriceString.length - 1, discountPriceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.discountPriceTextView.text = discountPriceSpan

            val priceSpan = SpannableString(DailyTextUtils.getPriceFormat(context, amountInformation.priceAverage, false))
            priceSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 12.0)), 0, priceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            priceSpan.setSpan(StrikethroughSpan(), 0, priceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.priceTextView.text = priceSpan
        }
    }

    private fun setRefundInformationView(dataBinding: ListRowStayRoomDataBinding, refundInformation: StayDetailk.RefundInformation?) {
        if (refundInformation == null) {
            dataBinding.refundPolicyTextView.visibility = View.GONE
            return
        }

        val isNrd = !refundInformation.type.isTextEmpty() && refundInformation.type?.toLowerCase().equals("nrd", true)
        if (!isNrd) {
            dataBinding.refundPolicyTextView.visibility = View.GONE
            return
        }

        var text = refundInformation.warningMessage
        if (text.isTextEmpty()) {
            text = context.resources.getString(R.string.label_stay_room_default_nrd_text)
        }

        dataBinding.refundPolicyTextView.visibility = View.VISIBLE
        dataBinding.refundPolicyTextView.text = text
    }

    private fun setBaseInformationGridView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val personsInformation: Room.PersonsInformation? = room.personsInformation
        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        if (personsInformation == null && !bedTypeList.isNotNullAndNotEmpty() && room.squareMeter == 0f) {
            dataBinding.baseInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.baseInfoGroup.visibility = View.VISIBLE

        setPersonInformationView(dataBinding, room)
        setBedInformationView(dataBinding, room)
        setSquareInformationView(dataBinding, room)
    }

    private fun setPersonInformationView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val personsInformation: Room.PersonsInformation? = room.personsInformation

        var personVectorIconResId: Int = 0
        var personTitle: String = ""
        var personDescription: String = ""

        personsInformation?.let {
            personTitle = context.resources.getString(R.string.label_standard_persons, it.fixed)

            personVectorIconResId = when (it.fixed) {
                0, 1 -> R.drawable.vector_ic_detail_item_people_1

                2 -> R.drawable.vector_ic_detail_item_people_2

                else -> R.drawable.vector_ic_detail_item_people_3
            }

            val subDescription = if (it.extra == 0) "" else " " + context.resources.getString(if (it.extraCharge) R.string.label_bracket_pay else R.string.label_bracket_free)
            personDescription = context.resources.getString(R.string.label_stay_outbound_room_max_person_free) + subDescription
        }

        dataBinding.personIconImageView.setVectorImageResource(personVectorIconResId)
        dataBinding.personTitleTextView.text = personTitle
        dataBinding.personDescriptionTextView.text = personDescription
    }

    private fun setBedInformationView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        var bedVectorIconResId: Int = 0

        val typeStringList = mutableListOf<String>()

        bedTypeList?.forEach { bedTypeInformation ->
            val bedType: BedType = try {
                BedType.valueOf(bedTypeInformation.bedType.toUpperCase())
            } catch (e: Exception) {
                BedType.UNKNOWN
            }

            bedVectorIconResId = if (bedVectorIconResId == 0) {
                bedType.vectorIconResId
            } else {
                R.drawable.vector_ic_detail_item_bed_double
            }

            typeStringList += "${bedType.getName(context)} ${bedTypeInformation.count}"
            typeStringList += "${bedType.getName(context)} ${bedTypeInformation.count}"
        }

        bedVectorIconResId.takeIf { bedVectorIconResId == 0 }.let {
            BedType.UNKNOWN.vectorIconResId
        }

        dataBinding.bedIconImageView.setVectorImageResource(bedVectorIconResId)
        dataBinding.bedDescriptionLayout.setData(typeStringList)
    }

    private fun setSquareInformationView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        dataBinding.squareTitleTextView.text = "${room.squareMeter}m"

        val pyoung = Math.round(room.squareMeter / 400 * 121)
        dataBinding.squareDescriptionTextView.text = context.resources.getString(R.string.label_pyoung_format, pyoung)
    }

    private fun setAttributeInformationView(dataBinding: ListRowStayRoomDataBinding, attribute: Room.AttributeInformation?) {
        if (attribute == null) {
            dataBinding.subInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.subInfoGroup.visibility = View.VISIBLE

        val roomType: RoomType = try {
            RoomType.valueOf(attribute.roomStructure)
        } catch (e: Exception) {
            RoomType.ONE_ROOM
        }

        var titleText = roomType.getName(context)

        attribute.isEntireHouse.runTrue { titleText += "/" + context.resources.getString(R.string.label_room_type_entire_house) }
        attribute.isDuplex.run { titleText += "/" + context.resources.getString(R.string.label_room_type_duplex_room) }

        dataBinding.subInfoGridView.setTitleText(titleText)
        dataBinding.subInfoGridView.setTitleVisible(true)
        dataBinding.subInfoGridView.setColumnCount(2)

        val stringList = mutableListOf<String>()
        var roomString = ""

        attribute.structureInformationList?.forEach {
            when (it.type) {
                "BED_ROOM" -> {
                    if (!roomString.isTextEmpty()) {
                        roomString += ", "
                    }

                    roomString += context.resources.getString(R.string.label_bed_room_format, it.count)
                }

                "IN_FLOOR_HEATING_ROOM" -> {
                    if (!roomString.isTextEmpty()) {
                        roomString += ", "
                    }

                    roomString += context.resources.getString(R.string.label_in_floor_heating_room_format, it.count)
                }

                "LIVING_ROOM" -> {
                    stringList += context.resources.getString(R.string.label_living_room_format, it.count)
                }

                "KITCHEN" -> {
                    stringList += context.resources.getString(R.string.label_kitchen_format, it.count)
                }

                "REST_ROOM" -> {
                    stringList += context.resources.getString(R.string.label_rest_room_format, it.count)
                }

                else -> {
                    // do nothing
                }
            }
        }

        if (!roomString.isTextEmpty()) {
            stringList.add(0, roomString)
        }

        dataBinding.subInfoGridView.setData(DailyRoomInfoGridView.ItemType.NONE, stringList)
    }

    private fun setRoomBenefitInformationView(dataBinding: ListRowStayRoomDataBinding, benefitList: MutableList<String>) {
        if (benefitList.isEmpty()) {
            dataBinding.roomBenefitGroup.visibility = View.GONE
            return
        }

        dataBinding.roomBenefitGroup.visibility = View.VISIBLE

        dataBinding.roomAmenityGridView.setTitleText(R.string.label_stay_room_benefit_title)
        dataBinding.roomBenefitGridView.setColumnCount(1)
        dataBinding.roomBenefitGridView.setData(DailyRoomInfoGridView.ItemType.DOWN_CARET, benefitList)
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
                text += context.resources.getString(R.string.label_stay_room_reward_coupon_or)
            }

            text += couponString
        }

        if (!text.isTextEmpty()) {
            text += context.resources.getString(R.string.label_stay_room_end_description)
        }

        val spannableString = SpannableString(text)

        val rewardStart = text.indexOf(rewardString)

        if (rewardStart != -1) {
            spannableString.setSpan(DailyImageSpan(context, R.drawable.vector_ic_r_ic_xs_14, DailyImageSpan.ALIGN_VERTICAL_CENTER), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_line_cfaae37)), rewardStart, rewardStart + rewardString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val couponStart = text.indexOf(couponString)
        if (couponStart != -1) {
            spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_text_cf27c7a)), couponStart, rewardStart + couponString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        dataBinding.discountInfoTextView.text = spannableString
    }

    private fun setCheckTimeInformationView(dataBinding: ListRowStayRoomDataBinding, checkTimeInformation: StayDetailk.CheckTimeInformation?) {
        if (checkTimeInformation == null) return

        val checkInTime = DailyCalendar.convertDateFormatString(checkTimeInformation.checkIn, "HH:mm:ss", "HH:mm")
        val checkOutTime = DailyCalendar.convertDateFormatString(checkTimeInformation.checkOut, "HH:mm:ss", "HH:mm")

        if (isTextEmpty(checkInTime, checkOutTime)) {
            dataBinding.checkTimeInfoLayout.visibility = View.GONE
            return
        }

        dataBinding.checkTimeInfoLayout.visibility = View.VISIBLE

        dataBinding.checkInTimeTextView.text = checkInTime
        dataBinding.checkOutTimeTextView.text = checkOutTime
    }

    private fun setRoomDescriptionInformationView(dataBinding: ListRowStayRoomDataBinding, descriptionList: MutableList<String>?) {
        if (descriptionList == null || descriptionList.size == 0) {
            dataBinding.roomDescriptionGroup.visibility = View.GONE
            return
        }

        dataBinding.roomDescriptionGroup.visibility = View.VISIBLE

        dataBinding.roomDescriptionGridView.setTitleText(R.string.label_stay_room_description_title)
        dataBinding.roomDescriptionGridView.setColumnCount(1)
        dataBinding.roomDescriptionGridView.setData(DailyRoomInfoGridView.ItemType.DOT, descriptionList)
    }

    private fun setRoomAmenityInformationView(dataBinding: ListRowStayRoomDataBinding, amenityList: MutableList<String>) {
        if (amenityList == null || amenityList.size == 0) {
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
        dataBinding.roomAmenityGridView.setTitleText(R.string.label_stay_room_amenity_title)
        dataBinding.roomAmenityGridView.setColumnCount(1)
        dataBinding.roomAmenityGridView.setData(DailyRoomInfoGridView.ItemType.DOT, list)
    }

    private fun setRoomChargeInformatinoView(dataBinding: ListRowStayRoomDataBinding, info: Room.ChargeInformation?) {
        if (info == null) {
            dataBinding.extraChargeLayout.visibility = View.GONE
            return
        }

        if (!info.extraPersonInformationList.isNotNullAndNotEmpty() && info.extraInformation == null && info.consecutiveInformation == null) {
            dataBinding.extraChargeLayout.visibility = View.GONE
            return
        }

        if (info.extraPersonInformationList.isNotNullAndNotEmpty()) {
            dataBinding.extraChargePersonTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargePersonTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargePersonTableLayout.setTitleText(R.string.label_stay_room_extra_charge_person_title)
            dataBinding.extraChargePersonTableLayout.setTitleVisible(true)
            dataBinding.extraChargePersonTableLayout.clearTableLayout()

            val personList = info.extraPersonInformationList
            info.extraPersonInformationList.forEach {
                var title = it.title

                getPersonRangeText(it.minAge, it.maxAge).letNotEmpty { title += " ($it)" }

                val subDescription = if (it.maxPersons > 0) context.resources.getString(R.string.label_room_max_person_range_format, it.maxPersons) else ""

                dataBinding.extraChargePersonTableLayout.addTableRow(title, getExtraChargePrice(it.amount), subDescription)
            }
        }

        if (info.extraInformation == null) {
            dataBinding.extraChargeBedTableLayout.visibility = View.GONE
            dataBinding.extraChargeDescriptionGridView.visibility = View.GONE
        } else {
            dataBinding.extraChargeBedTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargeBedTableLayout.setTitleVisible(true)
            dataBinding.extraChargeBedTableLayout.setTitleText(R.string.label_stay_room_extra_charge_bed_title)
            dataBinding.extraChargeBedTableLayout.clearTableLayout()

            (info.extraInformation.extraBeddingEnable).runTrue {
                dataBinding.extraChargeBedTableLayout.addTableRow(context.resources.getString(R.string.label_bedding), getExtraChargePrice(info.extraInformation.extraBedding))
            }

            (info.extraInformation.extraBedEnable).runTrue {
                dataBinding.extraChargeBedTableLayout.addTableRow(context.resources.getString(R.string.label_extra_bed), getExtraChargePrice(info.extraInformation.extraBed))
            }

            dataBinding.extraChargeBedTableLayout.visibility = if (itemCount == 0) View.GONE else View.VISIBLE

            dataBinding.extraChargeDescriptionGridView.setColumnCount(1)
            dataBinding.extraChargeDescriptionGridView.setTitleVisible(false)
            dataBinding.extraChargeDescriptionGridView.setData(DailyRoomInfoGridView.ItemType.DOT, info.extraInformation.descriptionList)
        }

        if (info.consecutiveInformation == null || !info.consecutiveInformation.enable) {
            dataBinding.extraChargeNightsTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargeNightsTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargeNightsTableLayout.setTitleVisible(true)
            dataBinding.extraChargeNightsTableLayout.setTitleText(R.string.label_stay_room_extra_charge_bed_title)
            dataBinding.extraChargeNightsTableLayout.clearTableLayout()

            dataBinding.extraChargeNightsTableLayout.addTableRow(context.resources.getString(R.string.label_stay_room_extra_charge_consecutive_item_title), getExtraChargePrice(info.consecutiveInformation.charge))
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

    private fun setNeedToKnowInformationView(dataBinding: ListRowStayRoomDataBinding, needToKnowList: MutableList<String>?) {
        if (needToKnowList == null || needToKnowList.size == 0) {
            dataBinding.roomCheckInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.roomCheckInfoGroup.visibility = View.VISIBLE

        dataBinding.roomCheckInfoGridView.setTitleText(R.string.label_stay_room_need_to_know_title)
        dataBinding.roomCheckInfoGridView.setColumnCount(1)
        dataBinding.roomCheckInfoGridView.setData(DailyRoomInfoGridView.ItemType.DOT, needToKnowList)
    }

    fun getLayoutWidth(): Float {
        return ScreenUtils.getScreenWidth(context) * MENU_WIDTH_RATIO
    }

    fun getLayoutMargin(): Float {
        return ScreenUtils.getScreenWidth(context) * (1.0f - MENU_WIDTH_RATIO) / 2.0f
    }

    inner class RoomViewHolder(val dataBinding: ListRowStayRoomDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}