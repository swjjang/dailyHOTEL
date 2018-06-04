package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Paint
import android.support.v4.view.MotionEventCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import com.daily.base.BaseDialogView
import com.daily.base.util.*
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.view.DailyRoomInfoGridView
import com.facebook.drawee.generic.RoundingParams
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayRoomsDataBinding
import com.twoheart.dailyhotel.databinding.ListRowStayRoomInvisibleLayoutDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan
import io.reactivex.Observable
import io.reactivex.Observer

class StayRoomsView(activity: StayRoomsActivity, listener: StayRoomsInterface.OnEventListener)//
    : BaseDialogView<StayRoomsInterface.OnEventListener, ActivityStayRoomsDataBinding>(activity, listener)
        , StayRoomsInterface.ViewInterface, View.OnClickListener {
    private lateinit var listAdapter: StayRoomAdapter

    private companion object {
        private const val MOVE_STATE_NONE = 0
        private const val MOVE_STATE_SCROLL = 10
        private const val MOVE_STATE_VIEWPAGER = 100
        private const val MOVE_STATE_START_ANIMATION = 1000
        private const val MOVE_STATE_END_ANIMATION = 10000
        private const val MOVE_CALIBRATE_VALUE = 0.95f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setContentView(viewDataBinding: ActivityStayRoomsDataBinding) {
        viewDataBinding.run {
            closeImageView.setOnClickListener({
                eventListener.onBackClick()
            })

            recyclerView.layoutManager = ZoomCenterLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge))

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(recyclerView)

            viewDataBinding.recyclerView.setOnTouchListener(recyclerTouchListener)

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val view = pagerSnapHelper.findSnapView(viewDataBinding.recyclerView.layoutManager)
                    val position = viewDataBinding.recyclerView.getChildAdapterPosition(view)

                    eventListener.onScrolled(position, true)
                }
            })

            if (!::listAdapter.isInitialized) {
                listAdapter = StayRoomAdapter(context, mutableListOf())
            }

            listAdapter.setEventListener(object : StayRoomAdapter.OnEventListener {
                override fun finish() {
                }

                override fun onMoreImageClick(position: Int) {
                    eventListener.onMoreImageClick(position)
                }

                override fun onVrImageClick(position: Int) {
                    eventListener.onVrImageClick(position)
                }
            })

            recyclerView.adapter = listAdapter

            bookingTextView.setOnClickListener(this@StayRoomsView)

            guideLayout.setOnClickListener(this@StayRoomsView)
            guideLayout.visibility = View.GONE
        }
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.titleTextView.text = title
    }

    override fun setIndicatorText(position: Int) {
        val count = if (listAdapter.itemCount == 0) 1 else listAdapter.itemCount

        viewDataBinding.indicatorTextView.text = "$position / ${if (count == 0) 1 else count}"
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.closeImageView -> eventListener.onCloseClick()

            R.id.guideLayout -> eventListener.onGuideClick()

            R.id.bookingTextView -> eventListener.onBookingClick()

            else -> {
            }
        }
    }

    override fun setBookingButtonText(position: Int) {
        val price = listAdapter.getItem(position)?.amountInformation?.discountTotal ?: 0

        val text = context.resources.getString(R.string.label_stay_room_booking_button_text
                , DailyTextUtils.getPriceFormat(context, price, false))

        viewDataBinding.bookingTextView.text = text
    }

    override fun setNights(nights: Int) {
        listAdapter.setNights(nights)

        viewDataBinding.nightsTextView.text = context.resources.getString(R.string.label_nights, nights)
        viewDataBinding.nightsTextView.visibility = if (nights > 1) View.VISIBLE else View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setRoomList(roomList: MutableList<Room>, position: Int) {
        if (roomList.size == 0) {
            return
        }

        listAdapter.setData(roomList)

        viewDataBinding.recyclerView.post {
            (viewDataBinding.recyclerView.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(position, listAdapter.getLayoutMargin().toInt())

            initInvisibleLayout()
        }
    }

    override fun notifyDataSetChanged() {
        listAdapter.notifyDataSetChanged()
    }

    private fun setRecyclerScrollEnabled() {
        val layoutManager = viewDataBinding.recyclerView.layoutManager as ZoomCenterLayoutManager
        if (viewDataBinding.invisibleLayout?.root?.visibility == View.VISIBLE) {
            layoutManager.setScrollEnabled(false)
        } else {
            layoutManager.setScrollEnabled(true)
        }
    }

    override fun setInvisibleData(position: Int) {
        val room = listAdapter.getItem(position) ?: return

        val dataBinding: ListRowStayRoomInvisibleLayoutDataBinding = viewDataBinding.invisibleLayout!!

        dataBinding.simpleDraweeView.hierarchy.setPlaceholderImage(R.drawable.layerlist_room_no_image_holder)
        dataBinding.moreIconView.visibility = if (room.imageCount > 0) View.VISIBLE else View.GONE
        dataBinding.vrIconView.visibility = if (room.vrInformationList.isNotNullAndNotEmpty()) View.VISIBLE else View.GONE
        dataBinding.vrIconView.setOnClickListener {
            eventListener.onVrImageClick(position)
        }

        dataBinding.closeImageView.setOnClickListener {
            eventListener.onCloseClick()
        }

        val stringUrl: String?
        stringUrl = if (room.imageInformation == null) {
            dataBinding.emptyLayout.setOnClickListener(null)
            null
        } else {
            dataBinding.emptyLayout.setOnClickListener {
                eventListener.onMoreImageClick(position)
            }

            room.imageInformation.imageMap.bigUrl
        }

        Util.requestImageResize(context, dataBinding.simpleDraweeView, stringUrl)

        dataBinding.roomNameTextView.text = room.name

        setAmountInformationView(dataBinding, room.amountInformation)

        setRefundInformationView(dataBinding, room.refundInformation)

        setBaseInformationGridView(dataBinding, room)

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

    override fun showInvisibleLayout(): Boolean {
        return viewDataBinding.invisibleLayout?.roomLayout?.visibility == View.VISIBLE
    }

    private fun setAmountInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, amountInformation: Room.AmountInformation) {
        dataBinding.discountPercentTextView.visibility = View.GONE
        dataBinding.priceTextView.visibility = View.GONE

        val showOriginPrice = amountInformation.priceAverage > 0 && amountInformation.priceAverage > amountInformation.discountAverage
        val showDiscountRate = amountInformation.discountRate in 5..100 && showOriginPrice

        showDiscountRate.runTrue {
            val discountRateSpan = SpannableString("${amountInformation.discountRate}%")
            discountRateSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface), discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            discountRateSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 14.0)), discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.discountPercentTextView.apply {
                text = discountRateSpan
                visibility = View.VISIBLE
            }
        }

        val discountPriceString = DailyTextUtils.getPriceFormat(context, amountInformation.discountAverage, false)
        dataBinding.discountPriceTextView.text = discountPriceString.substring(0, discountPriceString.length - 1)

        val priceUnitText = context.resources.getString(R.string.currency) + if (listAdapter.getNights() > 1) context.resources.getString(R.string.label_stay_detail_slash_one_nights) else ""
        dataBinding.discountPriceUnitTextView.text = priceUnitText

        showOriginPrice.runTrue {
            dataBinding.priceTextView.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                this.text = DailyTextUtils.getPriceFormat(context, amountInformation.priceAverage, false)
                this.visibility = View.VISIBLE
            }
        }
    }

    private fun setRefundInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, refundInformation: StayDetail.RefundInformation?) {
        if (refundInformation == null) {
            dataBinding.refundPolicyTextView.visibility = View.GONE
            return
        }

        val isNrd = !refundInformation.policy.isTextEmpty() && refundInformation.policy?.toLowerCase().equals("nrd", true)
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

        dataBinding.refundPolicyTextView.setTextColor(context.resources.getColor(textColorResId))
        dataBinding.refundPolicyTextView.setBackgroundResource(backgroundResId)
        dataBinding.refundPolicyTextView.visibility = if (text.isTextEmpty()) View.GONE else View.VISIBLE
        dataBinding.refundPolicyTextView.text = text
    }

    private fun setBaseInformationGridView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, room: Room) {
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

    private fun setPersonInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, room: Room) {
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

            val subDescription = if (it.extra == 0) "" else " " + context.resources.getString(if (it.extraCharge) R.string.label_bracket_pay else R.string.label_bracket_free)
            personDescription = context.resources.getString(R.string.label_stay_outbound_room_max_person_free, it.fixed + it.extra) + subDescription
        }

        dataBinding.personIconImageView.setVectorImageResource(personVectorIconResId)
        dataBinding.personTitleTextView.text = personTitle
        dataBinding.personDescriptionTextView.text = personDescription
    }

    private fun setBedInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, room: Room) {
        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        var bedVectorIconResId = 0

        val typeStringList = mutableListOf<String>()

        bedTypeList?.forEachIndexed { index, bedTypeInformation ->
            val bedType: StayRoomAdapter.BedType = try {
                StayRoomAdapter.BedType.valueOf(bedTypeInformation.bedType.toUpperCase())
            } catch (e: Exception) {
                StayRoomAdapter.BedType.UNKNOWN
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
            StayRoomAdapter.BedType.UNKNOWN.vectorIconResId
        }

        dataBinding.bedIconImageView.setVectorImageResource(bedVectorIconResId)
        dataBinding.bedDescriptionLayout.setData(typeStringList)
    }

    private fun setSquareInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, room: Room) {
        dataBinding.squareTitleTextView.text = "${room.squareMeter}m"

        // ㎡×0.3025=평 - / 400 * 121  /   평×3.3058=㎡ - / 121 * 400
        val pyoung = Math.round(room.squareMeter * 0.3025)
        dataBinding.squareDescriptionTextView.text = context.resources.getString(R.string.label_pyoung_format, pyoung)
    }

    private fun setAttributeInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, attribute: Room.AttributeInformation?) {
        if (attribute == null) {
            dataBinding.subInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.subInfoGroup.visibility = View.VISIBLE

        val roomType: StayRoomAdapter.RoomType = try {
            StayRoomAdapter.RoomType.valueOf(attribute.roomStructure)
        } catch (e: Exception) {
            StayRoomAdapter.RoomType.ONE_ROOM
        }

        var titleText = roomType.getName(context)

        attribute.isEntireHouse.runTrue { titleText += "/" + context.resources.getString(R.string.label_room_type_entire_house) }
        attribute.isDuplex.run { titleText += "/" + context.resources.getString(R.string.label_room_type_duplex_room) }

        dataBinding.subInfoGridView.columnCount = 2

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

        dataBinding.subInfoGridView.setData(titleText, DailyRoomInfoGridView.ItemType.NONE, stringList, true)
    }

    private fun setRoomBenefitInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, benefitList: MutableList<String>) {
        if (benefitList.isEmpty()) {
            dataBinding.roomBenefitGroup.visibility = View.GONE
            return
        }

        dataBinding.roomBenefitGroup.visibility = View.VISIBLE

        dataBinding.roomBenefitGridView.columnCount = 1
        dataBinding.roomBenefitGridView.setData(
                context.resources.getString(R.string.label_stay_room_benefit_title)
                , DailyRoomInfoGridView.ItemType.DOWN_CARET, benefitList, true)
    }

    private fun setRewardAndCouponInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, rewardable: Boolean, useCoupon: Boolean) {
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

    private fun setCheckTimeInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, checkTimeInformation: StayDetail.CheckTimeInformation?) {
        if (checkTimeInformation == null) return

        if (isTextEmpty(checkTimeInformation.checkIn, checkTimeInformation.checkOut)) {
            dataBinding.checkTimeInfoLayout.visibility = View.GONE
            return
        }

        dataBinding.checkTimeInfoLayout.visibility = View.VISIBLE

        dataBinding.checkInTimeTextView.text = checkTimeInformation.checkIn
        dataBinding.checkOutTimeTextView.text = checkTimeInformation.checkOut
    }

    private fun setRoomDescriptionInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, descriptionList: MutableList<String>?) {
        if (descriptionList == null || descriptionList.size == 0) {
            dataBinding.roomDescriptionGroup.visibility = View.GONE
            return
        }

        dataBinding.roomDescriptionGroup.visibility = View.VISIBLE

        dataBinding.roomDescriptionGridView.columnCount = 1
        dataBinding.roomDescriptionGridView.setData(
                context.resources.getString(R.string.label_stay_room_description_title)
                , DailyRoomInfoGridView.ItemType.DOT, descriptionList, true)
    }

    private fun setRoomAmenityInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, amenityList: MutableList<String>) {
        if (amenityList.size == 0) {
            dataBinding.roomAmenityGroup.visibility = View.GONE
            return
        }

        val list = mutableListOf<String>()
        amenityList.forEach {
            val amenityType: StayRoomAdapter.RoomAmenityType? = try {
                StayRoomAdapter.RoomAmenityType.valueOf(it)
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
                , DailyRoomInfoGridView.ItemType.DOT, list, true)
    }

    private fun setRoomChargeInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, info: Room.ChargeInformation?) {
        if (info == null || info.isAllHidden) {
            dataBinding.extraChargeLayout.visibility = View.GONE
            return
        }

        if (!info.extraPersonInformationList.isNotNullAndNotEmpty()) {
            dataBinding.extraChargePersonTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargePersonTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargePersonTableLayout.setTitleText(R.string.label_stay_room_extra_charge_person_title)
            dataBinding.extraChargePersonTableLayout.setTitleTextSize(16f)
            dataBinding.extraChargePersonTableLayout.setTitleVisible(true)
            dataBinding.extraChargePersonTableLayout.clearTableLayout()

            info.extraPersonInformationList.forEach {
                var title = it.title

                listAdapter.getPersonRangeText(it.minAge, it.maxAge).letNotEmpty { title += " ($it)" }

                val subDescription = if (it.maxPersons > 0) context.resources.getString(R.string.label_room_max_person_range_format, it.maxPersons) else ""

                dataBinding.extraChargePersonTableLayout.addTableRow(title, listAdapter.getExtraChargePrice(it.amount), subDescription, true)
            }
        }

        if (info.extraInformation == null || info.extraInformation.isAllHidden) {
            dataBinding.extraChargeBedTableLayout.visibility = View.GONE
            dataBinding.extraChargeDescriptionGridView.visibility = View.GONE
        } else {
            dataBinding.extraChargeBedTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargeBedTableLayout.setTitleVisible(true)
            dataBinding.extraChargeBedTableLayout.setTitleText(R.string.label_stay_room_extra_charge_bed_title)
            dataBinding.extraChargePersonTableLayout.setTitleTextSize(16f)
            dataBinding.extraChargeBedTableLayout.clearTableLayout()

            (info.extraInformation.extraBeddingEnable).runTrue {
                dataBinding.extraChargeBedTableLayout.addTableRow(
                        context.resources.getString(R.string.label_bedding)
                        , listAdapter.getExtraChargePrice(info.extraInformation.extraBedding), "", true)
            }

            (info.extraInformation.extraBedEnable).runTrue {
                dataBinding.extraChargeBedTableLayout.addTableRow(
                        context.resources.getString(R.string.label_extra_bed)
                        , listAdapter.getExtraChargePrice(info.extraInformation.extraBed), "", false)
            }

            dataBinding.extraChargeBedTableLayout.visibility = if (dataBinding.extraChargeBedTableLayout.getItemCount() == 0) View.GONE else View.VISIBLE

            dataBinding.extraChargeDescriptionGridView.columnCount = 1
            dataBinding.extraChargeDescriptionGridView.setData(
                    ""
                    , DailyRoomInfoGridView.ItemType.DOT, info.extraInformation.descriptionList, true)
        }

        if (info.consecutiveInformation == null || !info.consecutiveInformation.enable) {
            dataBinding.extraChargeNightsTableLayout.visibility = View.GONE
        } else {
            dataBinding.extraChargeNightsTableLayout.visibility = View.VISIBLE

            dataBinding.extraChargeNightsTableLayout.setTitleVisible(true)
            dataBinding.extraChargeNightsTableLayout.setTitleText(R.string.label_stay_room_extra_charge_consecutive_title)
            dataBinding.extraChargePersonTableLayout.setTitleTextSize(16f)
            dataBinding.extraChargeNightsTableLayout.clearTableLayout()

            dataBinding.extraChargeNightsTableLayout.addTableRow(
                    context.resources.getString(R.string.label_stay_room_extra_charge_consecutive_item_title)
                    , listAdapter.getExtraChargePrice(info.consecutiveInformation.charge), "", false)
        }
    }

    private fun setNeedToKnowInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, needToKnowList: MutableList<String>?) {
        if (needToKnowList == null || needToKnowList.size == 0) {
            dataBinding.roomCheckInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.roomCheckInfoGroup.visibility = View.VISIBLE

        dataBinding.roomCheckInfoGridView.columnCount = 1
        dataBinding.roomCheckInfoGridView.setData(
                context.resources.getString(R.string.label_stay_room_need_to_know_title)
                , DailyRoomInfoGridView.ItemType.DOT, needToKnowList, true)
    }

    override fun setGuideVisible(visible: Boolean) {
        viewDataBinding.guideLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun hideGuideAnimation(): Observable<Boolean> {
        val objectAnimator = ObjectAnimator.ofFloat(viewDataBinding.guideLayout, "alpha", 1.0f, 0.0f)

        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 300

        return object : Observable<Boolean>() {
            override fun subscribeActual(observer: Observer<in Boolean>) {
                objectAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}

                    override fun onAnimationEnd(animator: Animator) {
                        objectAnimator.removeAllListeners()

                        viewDataBinding.guideLayout.visibility = View.GONE

                        observer.onNext(true)
                        observer.onComplete()
                    }

                    override fun onAnimationCancel(animator: Animator) {}

                    override fun onAnimationRepeat(animator: Animator) {}
                })

                objectAnimator.start()
            }
        }
    }

    override fun showVrDialog(checkedChangeListener: CompoundButton.OnCheckedChangeListener
                              , positiveListener: View.OnClickListener
                              , onDismissListener: DialogInterface.OnDismissListener) {
        showSimpleDialog(null
                , getString(R.string.message_stay_used_data_guide)
                , getString(R.string.label_dont_again)
                , getString(R.string.dialog_btn_do_continue)
                , getString(R.string.dialog_btn_text_close)
                , checkedChangeListener, positiveListener
                , null, null, onDismissListener, true)
    }

    private val maxScaleX = 1f
    private var minScaleX = 1f
    private var maxTransY = 0f
    private val minTransY = 0f
    private var widthGap = 0f
    private val minImageRadius = 0f
    private val maxImageRadius = 6f

    private fun initInvisibleLayout() {
        if (listAdapter.itemCount == 0) return

        val roomViewHolder: StayRoomAdapter.RoomViewHolder = viewDataBinding.recyclerView.findViewHolderForAdapterPosition(0) as? StayRoomAdapter.RoomViewHolder
                ?: return

        val invisibleLayoutDataBinding = viewDataBinding.invisibleLayout ?: return

        val top = viewDataBinding.recyclerView.top
        val paddingTop = roomViewHolder.dataBinding.root.paddingTop
        val paddingBottom = roomViewHolder.dataBinding.root.paddingBottom
        val width = roomViewHolder.dataBinding.root.measuredWidth
        val paddingLeft = roomViewHolder.dataBinding.root.paddingLeft
        val paddingRight = roomViewHolder.dataBinding.root.paddingRight

        val invisibleWidth = width - paddingLeft - paddingRight

        widthGap = ScreenUtils.getScreenWidth(context).toFloat() - invisibleWidth.toFloat()

        minScaleX = invisibleWidth.toFloat() / ScreenUtils.getScreenWidth(context).toFloat()

        val scaleTransY = invisibleLayoutDataBinding.roomLayout.measuredHeight * (1f - minScaleX) / 2
        maxTransY = (top + paddingTop) - scaleTransY

        invisibleLayoutDataBinding.roomLayout.apply {
            translationY = maxTransY

            scaleX = minScaleX
            scaleY = minScaleX
        }

        invisibleLayoutDataBinding.closeImageView.translationY = 0f
        invisibleLayoutDataBinding.moreIconView.translationY = 0f
        invisibleLayoutDataBinding.vrIconView.translationY = 0f

        EdgeEffectColor.setEdgeGlowColor(invisibleLayoutDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge))

        val toolbarHeight = getDimensionPixelSize(R.dimen.toolbar_height)

        invisibleLayoutDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x)
        invisibleLayoutDataBinding.toolbarView.setOnBackClickListener {
            eventListener.onCloseClick()
        }

        invisibleLayoutDataBinding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            invisibleLayoutDataBinding.closeImageView.translationY = scrollY.toFloat()
            invisibleLayoutDataBinding.moreIconView.translationY = scrollY.toFloat()
            invisibleLayoutDataBinding.vrIconView.translationY = scrollY.toFloat()

            val titleLayout = invisibleLayoutDataBinding.scrollLayout.getChildAt(1)
            if (titleLayout.y - toolbarHeight > scrollY) {
                invisibleLayoutDataBinding.toolbarView.hideAnimation()
            } else {
                invisibleLayoutDataBinding.toolbarView.showAnimation()
            }
        })

        invisibleLayoutDataBinding.nestedScrollView.setOnTouchListener(invisibleLayoutTouchListener)
    }

    private fun setInvisibleLayout(preY: Float, y: Float, startScaleX: Float, startTransY: Float): Boolean {
        val value = y - preY
        if (value == 0f) return false

        val scaleXGap = maxScaleX - minScaleX
        val oneScaleX = scaleXGap / widthGap
        val oneTransY = maxTransY / widthGap

        var toScaleX = startScaleX - (oneScaleX * value)
        if (toScaleX > maxScaleX) {
            toScaleX = maxScaleX
        } else if (toScaleX < minScaleX) {
            toScaleX = minScaleX
        }

        var toTransY = startTransY - (-value * oneTransY)
        if (toTransY > maxTransY) {
            toTransY = maxTransY
        } else if (toTransY < minTransY) {
            toTransY = minTransY
        }

        val checkValue = 0.3f * maxTransY
        var imageRoundRadius = Math.round(toTransY / (maxTransY / 6)).toFloat()
        if (imageRoundRadius > maxImageRadius) {
            imageRoundRadius = maxImageRadius
        } else if (imageRoundRadius < minImageRadius) {
            imageRoundRadius = minImageRadius
        }

        var forceUpdate = Math.abs(startTransY - toTransY) >= checkValue

        viewDataBinding.invisibleLayout!!.roomLayout.translationY = toTransY
        viewDataBinding.invisibleLayout!!.roomLayout.scaleX = toScaleX
        viewDataBinding.invisibleLayout!!.roomLayout.scaleY = toScaleX

        setCloseImageAlphaVisible(toScaleX)

        val imageValue = ScreenUtils.dpToPx(context, imageRoundRadius.toDouble())
        val roundingParams: RoundingParams = RoundingParams.fromCornersRadii(imageValue.toFloat(), imageValue.toFloat(), 0f, 0f)
        viewDataBinding.invisibleLayout!!.simpleDraweeView.hierarchy.roundingParams = roundingParams

        return forceUpdate
    }

    override fun startInvisibleLayoutAnimation(scaleUp: Boolean) {
        val roomLayout = viewDataBinding.invisibleLayout!!.roomLayout

        val startScale = roomLayout.scaleX
        val end = if (scaleUp) 1.0f else minScaleX
        val startTransY = roomLayout.translationY
        val endTransY = if (scaleUp) 0.0f else maxTransY
        val duration = if (scaleUp) {
            (startTransY / maxTransY * 200).toLong()
        } else {
            ((maxTransY - startTransY) / maxTransY * 200).toLong()
        }

        viewDataBinding.invisibleLayout!!.nestedScrollView.scrollY = 0

        val checkValue = 0.3f * maxTransY

        val animatorSet = AnimatorSet()
        animatorSet.duration = duration
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        val transAnimator = ValueAnimator.ofFloat(startTransY, endTransY)
        transAnimator.addUpdateListener { animation ->
            val transValue = animation.animatedValue as Float
            roomLayout.translationY = transValue

            var imageRoundRadius = Math.round(transValue / (maxTransY / 6)).toFloat()
            if (imageRoundRadius > maxImageRadius) {
                imageRoundRadius = maxImageRadius
            } else if (imageRoundRadius < minImageRadius) {
                imageRoundRadius = minImageRadius
            }

            val imageValue = ScreenUtils.dpToPx(context, imageRoundRadius.toDouble())
            val roundingParams: RoundingParams = RoundingParams.fromCornersRadii(imageValue.toFloat(), imageValue.toFloat(), 0f, 0f)
            viewDataBinding.invisibleLayout!!.simpleDraweeView.hierarchy.roundingParams = roundingParams
        }

        val scaleAnimator = ValueAnimator.ofFloat(startScale * 100, end * 100)
        scaleAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float / 100
            roomLayout.scaleX = value
            roomLayout.scaleY = value

            setCloseImageAlphaVisible(value)
        }

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (scaleUp && viewDataBinding.invisibleLayout!!.roomLayout.visibility != View.VISIBLE) {
                    viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (scaleUp) {
                    roomLayout.scaleX = maxScaleX
                    roomLayout.scaleY = maxScaleX
                    roomLayout.translationY = minTransY
                    viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                } else {
                    roomLayout.scaleX = minScaleX
                    roomLayout.scaleY = minScaleX
                    roomLayout.translationY = maxTransY
                    viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.INVISIBLE
                }

                setRecyclerScrollEnabled()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })

        animatorSet.playTogether(scaleAnimator, transAnimator)
        animatorSet.start()
    }

    private fun setCloseImageAlphaVisible(alphaValue : Float) {
//        viewDataBinding.invisibleLayout!!.closeImageView.apply {
//            when {
//                alphaValue < 0.94f -> {
//                    visibility = View.GONE
//                }
//
//                alphaValue in 0.95f..0.99f -> {
//                    visibility = View.VISIBLE
//                    val toAlpha = (alphaValue - 0.94f) * 20
//                    ExLog.d("sam - toAlpha : $toAlpha")
//                    alpha = toAlpha
//                }
//
//                alphaValue > 0.99f -> {
//                    visibility = View.VISIBLE
//                    alpha = 1f
//                }
//            }
//        }

        viewDataBinding.invisibleLayout!!.closeImageView.apply {
            when {
                alphaValue < 0.90f -> {
                    visibility = View.GONE
                }

                alphaValue in 0.90f..0.94f -> {
                    visibility = View.VISIBLE
                    val toAlpha = (alphaValue - 0.89f) * 20
                    ExLog.d("sam - toAlpha : $toAlpha")
                    alpha = toAlpha
                }

                alphaValue > 0.94f -> {
                    visibility = View.VISIBLE
                    alpha = 1f
                }
            }
        }
    }

    private val recyclerTouchListener = object : View.OnTouchListener {
        private var moveState: Int = MOVE_STATE_NONE
        private var prevX: Float = 0.toFloat()
        private var prevY: Float = 0.toFloat()
        private var startScaleX = maxScaleX
        private var startTransY = minTransY

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (listAdapter.itemCount == 0) return false
            if (event == null) return false

            setRecyclerScrollEnabled()

            when (event.action and MotionEventCompat.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.x
                    prevY = event.y
                    startScaleX = viewDataBinding.invisibleLayout!!.roomLayout.scaleX
                    startTransY = viewDataBinding.invisibleLayout!!.roomLayout.translationY

                    moveState = MOVE_STATE_NONE
                }

                MotionEvent.ACTION_UP -> run {

                    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

                    val x = (prevX - event.x).toInt()
                    val y = (prevY - event.y).toInt()

                    val distance = Math.sqrt((x * x + y * y).toDouble()).toInt()
                    if (distance < touchSlop) {
                        val oldMoveState = moveState
                        moveState = MOVE_STATE_NONE

                        if (MOVE_STATE_NONE == oldMoveState) {
                            startInvisibleLayoutAnimation(true)
                            return false
                        }
                    }

                    when (moveState) {
                        MOVE_STATE_END_ANIMATION, MOVE_STATE_VIEWPAGER -> {
                            return false
                        }
                    }

                    // MoveState 가 MOVE_STATE_START_ANIMATION 이면 true , false
                    val scaleUp = moveState == MOVE_STATE_START_ANIMATION
                    startInvisibleLayoutAnimation(scaleUp)
                }

                MotionEvent.ACTION_CANCEL -> {
                    moveState = MOVE_STATE_NONE
                }

                MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    when (moveState) {
                        MOVE_STATE_NONE -> {
                            when {
                                Math.abs(x - prevX) == Math.abs(y - prevY) -> {
                                    // 안 움직이거나 x, y 정확히 대각선 일때
                                }

                                Math.abs(x - prevX) * MOVE_CALIBRATE_VALUE > Math.abs(y - prevY) -> {
                                    // x 축으로 이동한 경우.
                                    moveState = MOVE_STATE_VIEWPAGER

                                    if (viewDataBinding.invisibleLayout!!.roomLayout.visibility == View.VISIBLE) {
                                        viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.INVISIBLE
                                    }
                                }

                                else -> {
                                    // y축으로 이동한 경우.
                                    moveState = MOVE_STATE_SCROLL

                                    if (viewDataBinding.invisibleLayout!!.roomLayout.visibility != View.VISIBLE) {
                                        viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                                    }

                                    if (setInvisibleLayout(prevY, y, startScaleX, startTransY)) {
                                        moveState = MOVE_STATE_START_ANIMATION
                                    }
                                }
                            }
                        }

                        MOVE_STATE_SCROLL -> {
                            if (viewDataBinding.invisibleLayout!!.roomLayout.visibility != View.VISIBLE) {
                                viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                            }

                            if (setInvisibleLayout(prevY, y, startScaleX, startTransY)) {
                                moveState = MOVE_STATE_START_ANIMATION
                            }
                        }

                        MOVE_STATE_VIEWPAGER -> {
                            if (viewDataBinding.invisibleLayout!!.roomLayout.visibility == View.VISIBLE) {
                                viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.INVISIBLE
                            }
                        }

                        MOVE_STATE_END_ANIMATION -> {
                            return true
                        }

                        MOVE_STATE_START_ANIMATION -> {
                            moveState = MOVE_STATE_END_ANIMATION

                            val maxStart = startScaleX == maxScaleX
                            val scaleUp = when {
                                y - prevY > 0 -> {
                                    false
                                }

                                y - prevY < 0 -> {
                                    true
                                }

                                else -> {
                                    maxStart
                                }
                            }

                            startInvisibleLayoutAnimation(scaleUp)
                            return true
                        }
                    }
                }
            }

            return false
        }
    }

    private val invisibleLayoutTouchListener = object : View.OnTouchListener {
        private var preY: Float = 0.toFloat()
        private var startScaleX = maxScaleX
        private var startTransY = minTransY
        private var moveState: Int = MOVE_STATE_NONE

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (viewDataBinding == null) return false
            if (listAdapter.itemCount == 0) return false

            setRecyclerScrollEnabled()

            when (event.action and MotionEventCompat.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    preY = event.y
                    startScaleX = viewDataBinding.invisibleLayout!!.roomLayout.scaleX
                    startTransY = viewDataBinding.invisibleLayout!!.roomLayout.translationY

                    moveState = MOVE_STATE_NONE
                }

                MotionEvent.ACTION_UP -> {
                    if (viewDataBinding.invisibleLayout!!.nestedScrollView.scrollY != 0) {
                        moveState = MOVE_STATE_NONE
                        return false
                    }

                    val oldState = moveState
                    moveState = MOVE_STATE_NONE

                    if (oldState == MOVE_STATE_END_ANIMATION) {
                        return true
                    }

                    // MoveState 가 MOVE_STATE_START_ANIMATION 이면 false , true
                    val scaleUp = moveState != MOVE_STATE_START_ANIMATION
                    startInvisibleLayoutAnimation(scaleUp)
                }

                MotionEvent.ACTION_CANCEL -> {
                }

                MotionEvent.ACTION_MOVE -> {
                    if (moveState == MOVE_STATE_NONE) {
                        moveState = MOVE_STATE_SCROLL
                    }

                    val y = event.y
                    val scaleX = viewDataBinding.invisibleLayout!!.roomLayout.scaleX
                    val scrollY = viewDataBinding.invisibleLayout!!.nestedScrollView.scrollY

                    if (scrollY > 0) {
                        preY = y
                        startScaleX = maxScaleX
                        startTransY = minTransY
                        return false
                    }

                    if (MOVE_STATE_END_ANIMATION == moveState) {
                        // Touch 및 scroll 이벤트가 들어옴으로 인해 scroll 이동 되는 이슈 방지
                        return true
                    }

                    if (MOVE_STATE_START_ANIMATION == moveState) {
                        moveState = MOVE_STATE_END_ANIMATION

                        val maxStart = startScaleX == maxScaleX
                        val scaleUp = when {
                            y - preY > 0 -> {
                                false
                            }

                            y - preY < 0 -> {
                                true
                            }

                            else -> {
                                maxStart
                            }
                        }

                        startInvisibleLayoutAnimation(scaleUp)
                        return true
                    }

                    if (setInvisibleLayout(preY, y, startScaleX, startTransY)) {
                        moveState = MOVE_STATE_START_ANIMATION
                    }

                    if (scaleX < maxScaleX) {
                        return true
                    }
                }
            }

            return false
        }
    }
}