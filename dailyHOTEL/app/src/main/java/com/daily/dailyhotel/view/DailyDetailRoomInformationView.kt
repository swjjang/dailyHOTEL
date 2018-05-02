package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomInformationDataBinding
import java.text.DecimalFormat
import java.util.*

private const val PRICE_AVERAGE_TAG = 1
private const val PRICE_TOTAL_TAG = 2

class DailyDetailRoomInformationView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomInformationDataBinding

    private var isPriceAverageType = true

    constructor(context: Context) : super(context) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context)
    }

    private fun initLayout(context: Context) {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_room_information_data, this, true)
    }

    fun setCalendar(text: CharSequence) {
        viewDataBinding.calendarTextView.text = text
    }

    fun setBedTypeFilterCount(count: Int) {
        viewDataBinding.bedTypeFilterCountTextView.text = count.toString()
    }

    fun setFacilitiesTypeFilterCount(count: Int) {
        viewDataBinding.facilitiesFilterCountTextView.text = count.toString()
    }

    fun setPriceAverageType(isAverageType: Boolean) {
        if (isPriceAverageType == isAverageType) return

        isPriceAverageType = isAverageType;

        if (viewDataBinding.roomsLayout.childCount > 0) {
            for (i in 0..viewDataBinding.roomsLayout.childCount) {
                DataBindingUtil.bind<DailyViewDetailRoomDataBinding>(viewDataBinding.roomsLayout.getChildAt(i))?.let {
                    it.discountPriceTextView.text = (if (isPriceAverageType) it.discountPriceTextView.getTag(PRICE_AVERAGE_TAG) else it.discountPriceTextView.getTag(PRICE_TOTAL_TAG)) as? String
                }
            }
        }

        if (viewDataBinding.moreRoomsLayout.childCount > 0) {
            for (i in 0..viewDataBinding.moreRoomsLayout.childCount) {
                DataBindingUtil.bind<DailyViewDetailRoomDataBinding>(viewDataBinding.moreRoomsLayout.getChildAt(i))?.let {
                    it.discountPriceTextView.text = (if (isPriceAverageType) it.discountPriceTextView.getTag(PRICE_AVERAGE_TAG) else it.discountPriceTextView.getTag(PRICE_TOTAL_TAG)) as? String
                }
            }
        }
    }

    fun setRoomList(roomList: List<Room>?) {
        viewDataBinding.roomsLayout.removeAllViews()
        viewDataBinding.moreRoomsLayout.removeAllViews()

        if (roomList.isNotNullAndNotEmpty()) {
            roomList!!.forEachIndexed { index, room ->
                if (index < 5) {
                    createRoomView(viewDataBinding.roomsLayout, room)
                } else {
                    createRoomView(viewDataBinding.moreRoomsLayout, room)
                }
            }
        } else {
            // 객실이 없어요~
        }
    }

    private fun createRoomView(parentView: ViewGroup, room: Room): View {
        val roomViewDataBinding = DataBindingUtil.inflate<DailyViewDetailRoomDataBinding>(LayoutInflater.from(context), R.layout.daily_view_detail_room_data, parentView, true)

        roomViewDataBinding.roomNameTextView.text = room.name;

        val bedTypeText = getBedType(room.bedInformation)
        if (bedTypeText.isTextEmpty()) {
            roomViewDataBinding.bedTypeTextView.visibility = View.GONE
        } else {
            roomViewDataBinding.bedTypeTextView.visibility = View.VISIBLE
            roomViewDataBinding.bedTypeTextView.text = bedTypeText
        }

        val personsText = getPersons(room.persons)
        if (personsText.isTextEmpty()) {
            roomViewDataBinding.personsTextView.visibility = View.GONE
        } else {
            roomViewDataBinding.personsTextView.visibility = View.VISIBLE
            roomViewDataBinding.personsTextView.text = getPersons(room.persons)
        }

        if (room.persons != null && room.persons.breakfast > 0) {
            roomViewDataBinding.breakfastTextView.visibility = View.VISIBLE
            roomViewDataBinding.breakfastTextView.text = context.getString(R.string.label_stay_detail_include_person_breakfast, room.persons.breakfast)
        } else {
            roomViewDataBinding.breakfastTextView.visibility = View.GONE
        }

        if (room.benefit.isTextEmpty()) {
            roomViewDataBinding.benefitTextView.visibility = View.GONE
        } else {
            roomViewDataBinding.benefitTextView.visibility = View.VISIBLE
            roomViewDataBinding.benefitTextView.text = room.benefit
        }

        val priceAverage = DecimalFormat("###,##0").format(room.discountAverage)
        val priceTotal = DecimalFormat("###,##0").format(room.discountTotal)

        roomViewDataBinding.discountPriceTextView.text = if (isPriceAverageType) priceAverage else priceTotal
        roomViewDataBinding.discountPriceTextView.setTag(PRICE_AVERAGE_TAG, priceAverage)
        roomViewDataBinding.discountPriceTextView.setTag(PRICE_TOTAL_TAG, priceTotal)

        roomViewDataBinding.couponTextView.visibility = if (room.hasUsableCoupon) View.VISIBLE else View.GONE

        return roomViewDataBinding.root
    }

    private fun getBedType(bedInformation: Room.BedInformation): String? {
        val bedStringBuilder = StringBuilder()

        bedInformation.bedTypeList.takeNotEmpty {
            it.forEach {
                if (bedStringBuilder.isNotEmpty()) {
                    bedStringBuilder.append(',')
                }

                bedStringBuilder.append(it.bedType)
                bedStringBuilder.append(String.format(Locale.KOREA, " %d개", it.count))
            }
        }

        return bedStringBuilder.toString()
    }

    private fun getPersons(personInformation: Room.Person): String? {
        return if (personInformation.fixed > 0) {
            context.getString(R.string.label_stay_detail_person_information,
                    personInformation.fixed,
                    personInformation.fixed + personInformation.extra,
                    if (personInformation.extraCharge) context.getString(R.string.label_pay) else context.getString(R.string.label_free))
        } else null
    }

    fun setMoreRoomVisible(visible: Boolean) {
        viewDataBinding.showMoreRoomsTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setMoreRoomCount(roomCount: Int) {
        viewDataBinding.showMoreRoomsTextView.text = context.getString(R.string.label_stay_detail_show_more_rooms, roomCount)
    }
}
