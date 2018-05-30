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

//            viewDataBinding.recyclerView.setOnTouchListener(recyclerTouchListener)

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

                override fun onItemClick(position: Int) {
                    startInvisibleLayoutAnimation(true)
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
        val price = listAdapter.getItem(position)?.let {
            it.amountInformation.discountTotal
        } ?: 0

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
        dataBinding.emptyMoreIconView.visibility = if (room.imageCount > 0) View.VISIBLE else View.GONE
        dataBinding.vrIconView.visibility = if (room.vrInformationList.isNotNullAndNotEmpty()) View.VISIBLE else View.GONE
        dataBinding.emptyVrIconView.visibility = if (room.vrInformationList.isNotNullAndNotEmpty()) View.VISIBLE else View.GONE
        dataBinding.emptyVrIconView.setOnClickListener {
            eventListener.onVrImageClick(position)
        }

        dataBinding.emptyCloseImageView.setOnClickListener {
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

        val nightsString = if (listAdapter.getNights() > 1) context.resources.getString(R.string.label_stay_detail_slash_one_nights) else ""
        val discountPriceString = DailyTextUtils.getPriceFormat(context, amountInformation.discountAverage, false)

        val discountPriceSpan = SpannableString("$discountPriceString$nightsString")
        discountPriceSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface), discountPriceString.length - 1, discountPriceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        discountPriceSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 14.0)), discountPriceString.length - 1, discountPriceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        dataBinding.discountPriceTextView.text = discountPriceSpan

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

        val isNrd = !refundInformation.type.isTextEmpty() && refundInformation.type?.toLowerCase().equals("nrd", true)
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
            personDescription = context.resources.getString(R.string.label_stay_outbound_room_max_person_free, it.fixed + it.extra) + subDescription
        }

        dataBinding.personIconImageView.setVectorImageResource(personVectorIconResId)
        dataBinding.personTitleTextView.text = personTitle
        dataBinding.personDescriptionTextView.text = personDescription
    }

    private fun setBedInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, room: Room) {
        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        var bedVectorIconResId: Int = 0

        val typeStringList = mutableListOf<String>()

        bedTypeList?.forEach { bedTypeInformation ->
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

            typeStringList += "${bedType.getName(context)} ${bedTypeInformation.count}"
        }

        bedVectorIconResId.takeIf { bedVectorIconResId == 0 }.let {
            StayRoomAdapter.BedType.UNKNOWN.vectorIconResId
        }

        dataBinding.bedIconImageView.setVectorImageResource(bedVectorIconResId)
        dataBinding.bedDescriptionLayout.setData(typeStringList)
    }

    private fun setSquareInformationView(dataBinding: ListRowStayRoomInvisibleLayoutDataBinding, room: Room) {
        dataBinding.squareTitleTextView.text = "${room.squareMeter}m"

        val pyoung = Math.round(room.squareMeter / 400 * 121)
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

    private var minScaleX = 1f
    private var minTransY = 0f

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

        minScaleX = invisibleWidth.toFloat() / ScreenUtils.getScreenWidth(context).toFloat()

        ExLog.d("sam - top : $top , paddingTop : $paddingTop + paddingBottom : $paddingBottom , paddingLeft : $paddingLeft , minScaleX : $minScaleX")

        val scaleTransY = invisibleLayoutDataBinding.roomLayout.measuredHeight * (1f - minScaleX) / 2
        minTransY = (top + paddingTop) - scaleTransY

        invisibleLayoutDataBinding.roomLayout.apply {
            translationY = minTransY

            scaleX = minScaleX
            scaleY = minScaleX

            ExLog.d("sam - translationY : $translationY , top : ${this.top} , measuredHeight : $measuredHeight , minTransY : $minTransY")
        }

        EdgeEffectColor.setEdgeGlowColor(invisibleLayoutDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge))

        invisibleLayoutDataBinding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            invisibleLayoutDataBinding.emptyCloseImageView.translationY = scrollY.toFloat()
            invisibleLayoutDataBinding.emptyMoreIconView.translationY = scrollY.toFloat()
            invisibleLayoutDataBinding.emptyVrIconView.translationY = scrollY.toFloat()
        })

        invisibleLayoutDataBinding.nestedScrollView.setOnTouchListener(invisibleLayoutTouchListener)
    }

    override fun startInvisibleLayoutAnimation(scaleUp: Boolean) {
        val roomLayout = viewDataBinding.invisibleLayout!!.roomLayout

        val startScale = roomLayout.scaleX
        val end = if (scaleUp) 1.0f else minScaleX
        val startTransY = roomLayout.translationY
        val endTransY = if (scaleUp) 0.0f else minTransY

//        ExLog.d("sam - start : $startScale , end : $end , startTransY : $startTransY , endTransY : $endTransY")
        viewDataBinding.invisibleLayout!!.nestedScrollView.scrollY = 0

        val animatorSet = AnimatorSet()
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        val transAnimator = ValueAnimator.ofFloat(startTransY, endTransY)
        transAnimator.addUpdateListener { animation ->
            val transValue = animation.animatedValue as Float
//            ExLog.d("sam - transValue : $transValue")

            roomLayout.translationY = transValue
        }

        val scaleAnimator = ValueAnimator.ofFloat(startScale * 100, end * 100)
        scaleAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float / 100

//            ExLog.d("sam - value : $value")

            roomLayout.scaleX = value
            roomLayout.scaleY = value
        }

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (scaleUp && viewDataBinding.invisibleLayout!!.roomLayout.visibility != View.VISIBLE) {
                    viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (scaleUp) {
                    roomLayout.scaleX = 1.0f
                    roomLayout.scaleY = 1.0f
                    roomLayout.translationY = 0.0f
                    viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                } else {
                    roomLayout.scaleX = minScaleX
                    roomLayout.scaleY = minScaleX
                    roomLayout.translationY = minTransY
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

    private fun setInvisibleLayout(preY: Float, y: Float) {
        if (preY == y) return

        val roomViewHolder: StayRoomAdapter.RoomViewHolder = viewDataBinding.recyclerView.findViewHolderForAdapterPosition(0) as? StayRoomAdapter.RoomViewHolder
                ?: return

        val invisibleLayoutDataBinding = viewDataBinding.invisibleLayout ?: return

        val gap = y - preY // minus 면 상단으로 이동, plus 하단으로 이동

        val top = viewDataBinding.recyclerView.top
        val paddingTop = roomViewHolder.dataBinding.root.paddingTop
        val width = roomViewHolder.dataBinding.root.measuredWidth
        val paddingLeft = roomViewHolder.dataBinding.root.paddingLeft
        val paddingRight = roomViewHolder.dataBinding.root.paddingRight

        var minWidth = width - paddingLeft - paddingRight
        var invisibleWidth = when {
            minWidth + -gap > ScreenUtils.getScreenWidth(context) -> ScreenUtils.getScreenWidth(context)
            minWidth + -gap < minWidth -> minWidth
            else -> minWidth + -gap.toInt()
        }

        var newScaleX = invisibleWidth.toFloat() / ScreenUtils.getScreenWidth(context).toFloat()
        if (newScaleX > 1) {
            newScaleX = 1f
        }

        ExLog.d("sam - invisibleWidth : $invisibleWidth , screen : ${ScreenUtils.getScreenWidth(context).toFloat()} , newScaleX : $newScaleX")
        ExLog.d("sam - gap : $gap , ")

        val ratio: Float = ScreenUtils.getScreenWidth(context).toFloat() / ScreenUtils.getScreenHeight(context).toFloat()

        val minTransY = 0f - ((top + paddingTop) * ratio)
        var transY = when {
            minTransY + -gap > 0f -> 0f
            minTransY + -gap < minTransY -> minTransY
            else -> minTransY + -gap
        }

        invisibleLayoutDataBinding.scrollLayout.apply {
            translationY = transY
            scaleX = newScaleX
            scaleY = newScaleX
//            setOnTouchListener(invisibleLayoutTouchListener)

            ExLog.d("sam - translationY : $translationY , top : ${this.top}")
        }
    }

    private val recyclerTouchListener = object : View.OnTouchListener {
        private val MOVE_STATE_NONE = 0
        private val MOVE_STATE_SCROLL = 10
        private val MOVE_STATE_VIEWPAGER = 100
        private val MOVE_CALIBRATE_VALUE = 1.25f

        private var mMoveState: Int = 0
        private var mPrevX: Float = 0.toFloat()
        private var mPrevY: Float = 0.toFloat()

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (listAdapter.itemCount == 0) return false
            if (event == null) return false

            setRecyclerScrollEnabled()

            when (event.action and MotionEventCompat.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mPrevX = event.x
                    mPrevY = event.y

                    mMoveState = MOVE_STATE_NONE
                }

                MotionEvent.ACTION_UP -> run {

                    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

                    val x = (mPrevX - event.x).toInt()
                    val y = (mPrevY - event.y).toInt()

                    val distance = Math.sqrt((x * x + y * y).toDouble()).toInt()
                    if (distance < touchSlop) {
                        mMoveState = MOVE_STATE_NONE
                    }

                    // TODO : invisibleLayout 애니메이션 하기
                }

                MotionEvent.ACTION_CANCEL -> {
                    mMoveState = MOVE_STATE_NONE
                }

                MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    when (mMoveState) {
                        MOVE_STATE_NONE -> {
                            when {
                                Math.abs(x - mPrevX) == Math.abs(y - mPrevY) -> {
                                    // 안 움직이거나 x, y 정확히 대각선 일때
                                }

                                Math.abs(x - mPrevX) * MOVE_CALIBRATE_VALUE > Math.abs(y - mPrevY) -> {
                                    // x 축으로 이동한 경우.
                                    mMoveState = MOVE_STATE_VIEWPAGER

                                    if (viewDataBinding.invisibleLayout!!.roomLayout.visibility == View.VISIBLE) {
                                        viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.INVISIBLE
                                    }
                                }

                                else -> {
                                    // y축으로 이동한 경우.
                                    mMoveState = MOVE_STATE_SCROLL

                                    if (viewDataBinding.invisibleLayout!!.roomLayout.visibility != View.VISIBLE) {
                                        viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                                    }

                                    setInvisibleLayout(mPrevY, y)
                                }
                            }
                        }

                        MOVE_STATE_SCROLL -> {
                            if (viewDataBinding.invisibleLayout!!.roomLayout.visibility != View.VISIBLE) {
                                viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.VISIBLE
                            }

                            setInvisibleLayout(mPrevY, y)
                        }

                        MOVE_STATE_VIEWPAGER -> {
                            if (viewDataBinding.invisibleLayout!!.roomLayout.visibility == View.VISIBLE) {
                                viewDataBinding.invisibleLayout!!.roomLayout.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }

            return false
        }
    }

    private val invisibleLayoutTouchListener = object : View.OnTouchListener {
        private var mPrevY: Float = 0.toFloat()

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (viewDataBinding == null) return false
            if (listAdapter.itemCount == 0) return false

            setRecyclerScrollEnabled()

            when (event.action and MotionEventCompat.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mPrevY = event.y
                }

                MotionEvent.ACTION_UP -> {
                    // TODO : invisibleLayout 애니메이션 하기
                }

                MotionEvent.ACTION_CANCEL -> {
                }

                MotionEvent.ACTION_MOVE -> {
                    val y = event.y
                    val translationY = viewDataBinding.invisibleLayout!!.scrollLayout.translationY
                    val scrollY = viewDataBinding.invisibleLayout!!.nestedScrollView.scrollY

                    val preY = mPrevY - scrollY

                    ExLog.d("sam - mPrevY : $mPrevY , scrollY : $scrollY , preY : $preY , y : $y , translationY : $translationY")

//                    setInvisibleLayout(mPrevY, y)
                }
            }

            return false
        }
    }
}