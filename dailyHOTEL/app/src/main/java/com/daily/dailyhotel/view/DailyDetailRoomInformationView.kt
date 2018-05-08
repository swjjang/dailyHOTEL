package com.daily.dailyhotel.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import com.daily.base.util.ExLog
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
    private var listener: OnDailyDetailRoomInformationListener? = null

    interface OnDailyDetailRoomInformationListener {
        fun onMoreRoomsClick(expanded: Boolean)
    }

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

        viewDataBinding.showMoreRoomsTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                listener?.let {
                    it.onMoreRoomsClick(viewDataBinding.moreRoomsLayout.visibility == View.VISIBLE)
                }
            }
        })
    }

    fun setRoomInformationListener(listener: OnDailyDetailRoomInformationListener) {
        this.listener = listener
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
                    it.discountPriceTextView.text = (if (isPriceAverageType) it.discountPriceTextView.getTag(it.discountPriceTextView.id + PRICE_AVERAGE_TAG)
                    else
                        it.discountPriceTextView.getTag(it.discountPriceTextView.id + PRICE_TOTAL_TAG)) as? String
                }
            }
        }

        if (viewDataBinding.moreRoomsLayout.childCount > 0) {
            for (i in 0..viewDataBinding.moreRoomsLayout.childCount) {
                DataBindingUtil.bind<DailyViewDetailRoomDataBinding>(viewDataBinding.moreRoomsLayout.getChildAt(i))?.let {
                    it.discountPriceTextView.text = (if (isPriceAverageType) it.discountPriceTextView.getTag(it.discountPriceTextView.id + PRICE_AVERAGE_TAG)
                    else
                        it.discountPriceTextView.getTag(it.discountPriceTextView.id + PRICE_TOTAL_TAG)) as? String
                }
            }
        }
    }

    fun setRoomList(roomList: List<Room>?) {
        viewDataBinding.roomsLayout.removeAllViews()
        viewDataBinding.moreRoomsLayout.removeAllViews()
        viewDataBinding.moreRoomsLayout.visibility = View.INVISIBLE

        if (roomList.isNotNullAndNotEmpty()) {
            roomList!!.forEachIndexed { index, room ->
                createRoomView(getViewGroupRoom(index), room)
            }

            if (viewDataBinding.moreRoomsLayout.childCount > 0) {
                viewDataBinding.moreRoomsLayout.apply {
                    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            try {
                                viewTreeObserver.removeOnPreDrawListener(this)
                                tag = viewDataBinding.moreRoomsLayout.height
                                layoutParams.height = 0
                                requestLayout()
                            } catch (e: Exception) {
                                ExLog.e(e.toString())
                            }

                            return false
                        }
                    })
                }
            }
        } else {
            // 객실이 없어요~
        }
    }

    private fun getViewGroupRoom(index: Int): ViewGroup {
        return if (index < 5) viewDataBinding.roomsLayout else viewDataBinding.moreRoomsLayout
    }

    private fun createRoomView(viewGroup: ViewGroup, room: Room) {
        val roomViewDataBinding = DataBindingUtil.inflate<DailyViewDetailRoomDataBinding>(LayoutInflater.from(context), R.layout.daily_view_detail_room_data, viewGroup, true)

        roomViewDataBinding.roomNameTextView.text = room.name;

        val bedTypeText = getBedType(room.bedTypeList)
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
        roomViewDataBinding.discountPriceTextView.setTag(roomViewDataBinding.discountPriceTextView.id + PRICE_AVERAGE_TAG, priceAverage)
        roomViewDataBinding.discountPriceTextView.setTag(roomViewDataBinding.discountPriceTextView.id + PRICE_TOTAL_TAG, priceTotal)

        roomViewDataBinding.couponTextView.visibility = if (room.hasUsableCoupon) View.VISIBLE else View.GONE
    }

    private fun getBedType(bedTypeList: List<Room.BedType>): String? {
        val bedStringBuilder = StringBuilder()

        bedTypeList.takeNotEmpty {
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

    private fun getPersons(personInformation: Room.Persons): String? {
        return if (personInformation.fixed > 0) {
            context.getString(R.string.label_stay_detail_person_information,
                    personInformation.fixed,
                    personInformation.fixed + personInformation.extra,
                    if (personInformation.extraCharge) context.getString(R.string.label_pay) else context.getString(R.string.label_free))
        } else null
    }

    fun setMoreRoomButtonVisible(visible: Boolean) {
        viewDataBinding.showMoreRoomsTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setMoreRoomCount(roomCount: Int) {
        viewDataBinding.showMoreRoomsTextView.text = context.getString(R.string.label_stay_detail_show_more_rooms, roomCount)
    }

    fun showMoreRoom() {
        val height = viewDataBinding.moreRoomsLayout.tag as Int
        if (height == 0 || viewDataBinding.moreRoomsLayout.visibility == View.VISIBLE) {
            return
        }

        ValueAnimator.ofInt(0, height).apply {
            addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
                valueAnimator?.let {
                    viewDataBinding.moreRoomsLayout.apply {
                        layoutParams.height = valueAnimator.animatedValue as Int
                        requestLayout()
                    }
                }
            })

            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    viewDataBinding.moreRoomsLayout.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    removeAllUpdateListeners()
                    removeAllListeners()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }.start()
    }

    fun hideMoreRoom() {
        viewDataBinding.moreRoomsLayout.apply {
            visibility = View.INVISIBLE
            layoutParams.height = 0
            requestLayout()
        }
    }
}
