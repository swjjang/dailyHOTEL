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
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomInformationDataBinding

private const val PRICE_AVERAGE_TAG = 1
private const val PRICE_TOTAL_TAG = 2

class DailyDetailRoomInformationView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomInformationDataBinding

    private var isPriceAverageType = false
    private var listener: OnDailyDetailRoomInformationListener? = null

    interface OnDailyDetailRoomInformationListener {
        fun onCalendarClick()

        fun onRoomFilterClick()

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

        viewDataBinding.actionButtonView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                listener?.let {
                    it.onMoreRoomsClick(viewDataBinding.moreRoomsLayout.visibility == View.VISIBLE)
                }
            }
        })

        setPriceAverageType(true)

        viewDataBinding.roomFilterView.setRoomFilterListener(object : DailyDetailRoomFilterView.OnDailyDetailRoomFilterListener {
            override fun onCalendarClick() {
                listener?.onCalendarClick()
            }

            override fun onRoomFilterClick() {
                listener?.onRoomFilterClick()
            }
        })
    }

    fun setRoomInformationListener(listener: OnDailyDetailRoomInformationListener) {
        this.listener = listener
    }

    fun setCalendar(text: CharSequence) {
        viewDataBinding.roomFilterView.setCalendar(text)
    }

    fun setRoomFilterCount(count: Int) {
        viewDataBinding.roomFilterView.setRoomFilterCount(count)
    }

    fun setPriceAverageTypeVisible(visible: Boolean) {
        viewDataBinding.priceTypeGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setPriceAverageType(isAverageType: Boolean) {
        if (isPriceAverageType == isAverageType) return

        isPriceAverageType = isAverageType;

        viewDataBinding.averagePriceTextView.isSelected = isAverageType
        viewDataBinding.totalPriceTextView.isSelected = !isAverageType

        if (isAverageType) {
            viewDataBinding.averagePriceTextView.typeface = FontManager.getInstance(context).mediumTypeface
            viewDataBinding.totalPriceTextView.typeface = FontManager.getInstance(context).regularTypeface
        } else {
            viewDataBinding.averagePriceTextView.typeface = FontManager.getInstance(context).regularTypeface
            viewDataBinding.totalPriceTextView.typeface = FontManager.getInstance(context).mediumTypeface
        }

        if (viewDataBinding.roomsLayout.childCount > 0) {
            for (i in 0..viewDataBinding.roomsLayout.childCount) {
                (viewDataBinding.roomsLayout.getChildAt(i) as? DailyDetailRoomView)?.apply { setPriceAverageType(isAverageType) }
            }

            if (viewDataBinding.moreRoomsLayout.childCount > 0) {
                for (i in 0..viewDataBinding.moreRoomsLayout.childCount) {
                    (viewDataBinding.roomsLayout.getChildAt(i) as? DailyDetailRoomView)?.apply { setPriceAverageType(isAverageType) }
                }
            }
        }
    }

    fun setRoomList(roomList: List<Room>?) {
        if (viewDataBinding.roomsLayout.childCount > 0) {
            viewDataBinding.roomsLayout.removeAllViews()
        }

        if (viewDataBinding.moreRoomsLayout.childCount > 0) {
            viewDataBinding.moreRoomsLayout.removeAllViews()
        }

        viewDataBinding.moreRoomsLayout.visibility = View.INVISIBLE

        if (roomList.isNotNullAndNotEmpty()) {
            roomList!!.forEachIndexed { index, room ->
                getViewGroupRoom(index).apply {
                    if (index > 0) addView(createDividerView(), LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, 11.0))

                    addView(DailyDetailRoomView(context).apply {
                        setPriceAverageType(isPriceAverageType)
                        setName(room.name)
                        setImageUlr(room.imageInformation?.imageMap?.smallUrl)
                        setBedTypeText(room.bedInformation.bedTypeList)
                        setPersons(room.personsInformation)
                        setBenefit(room.benefit)
                        setPrice(room.amountInformation.discountAverage, room.amountInformation.discountTotal)
                        setCouponVisible(room.hasUsableCoupon)
                    }, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
            }

            if (viewDataBinding.moreRoomsLayout.childCount > 0) {
                viewDataBinding.moreRoomsLayout.doOnPreDraw {
                    it.tag = it.height
                    it.layoutParams.height = 0
                    it.requestLayout()
                }
            }
        } else {
            // 객실이 없어요~
        }
    }

    private fun getViewGroupRoom(index: Int): ViewGroup {
        return if (index < 5) viewDataBinding.roomsLayout else viewDataBinding.moreRoomsLayout
    }

    private fun createDividerView(): View {
        return View(context).apply {
            setBackgroundResource(R.drawable.layerlist_top_line_divider_le7e7e7)
        }
    }

    fun setActionButtonVisible(visible: Boolean) {
        viewDataBinding.actionButtonGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setActionButton(text: String, leftResourceId: Int, rightResourceId: Int) {
        viewDataBinding.actionButtonTextView.text = text
        viewDataBinding.actionButtonTextView.setCompoundDrawablesWithIntrinsicBounds(leftResourceId, 0, rightResourceId, 0)
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

    fun isShowMoreRoom(): Boolean {
        return viewDataBinding.moreRoomsLayout.visibility == View.VISIBLE
    }

    fun setSoldOutVisible(visible: Boolean) {
        viewDataBinding.soldOutRoomGroup.visibility = if (visible) View.VISIBLE else View.GONE
        viewDataBinding.roomFilterView.setRoomFilterVisible(visible)

        val flag = if (visible) View.GONE else View.VISIBLE
        viewDataBinding.priceTypeGroup.visibility = flag
        viewDataBinding.roomsLayout.visibility = flag
        viewDataBinding.moreRoomsLayout.visibility = flag
        viewDataBinding.actionButtonGroup.visibility = flag
    }
}
