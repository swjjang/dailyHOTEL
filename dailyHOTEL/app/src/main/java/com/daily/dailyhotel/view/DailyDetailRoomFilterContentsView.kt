package com.daily.dailyhotel.view

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.daily.base.util.ExLog
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomFilterContentsDataBinding
import java.util.*

class DailyDetailRoomFilterContentsView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomFilterContentsDataBinding

    private var listener: OnDailyDetailRoomFilterListener? = null

    interface OnDailyDetailRoomFilterListener {
        fun onSelectedBedTypeFilter(selected: Boolean, bedType: String)

        fun onSelectedFacilitiesFilter(selected: Boolean, facilities: String)

        fun onCloseClick()

        fun onResetClick()

        fun onConfirmClick()
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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_room_filter_contents_data, this, true)

        viewDataBinding.roomFacilitiesFlexboxLayout.flexDirection = FlexDirection.ROW
        viewDataBinding.roomFacilitiesFlexboxLayout.flexWrap = FlexWrap.WRAP

        viewDataBinding.closeImageView.setOnClickListener { listener?.onCloseClick() }
        viewDataBinding.resetTextView.setOnClickListener { listener?.onResetClick() }
        viewDataBinding.confirmTextView.setOnClickListener { listener?.onConfirmClick() }
    }

    fun setOnDailyDetailRoomFilterListener(listener: OnDailyDetailRoomFilterListener) {
        this.listener = listener
    }

    fun setBedType(bedTypeSet: HashSet<String>?) {
        bedTypeSet?.let {
            val bedTypeTextViews = arrayOf(viewDataBinding.bedType01TextView, viewDataBinding.bedType02TextView, viewDataBinding.bedType03TextView, viewDataBinding.bedType04TextView)
            val bedTypePictograms = arrayOf(BedTypePictogram.DOUBLE, BedTypePictogram.TWIN, BedTypePictogram.IN_FLOOR_HEATING, BedTypePictogram.SINGLE)
            var index = 0

            bedTypePictograms.forEach { bedTypePictogram ->
                if (it.contains(bedTypePictogram.name)) {
                    bedTypeTextViews[index++].apply {
                        text = bedTypePictogram.getName(context)
                        setCompoundDrawablesWithIntrinsicBounds(0, bedTypePictogram.getImageResourceId(), 0, 0)
                        tag = bedTypePictogram;
                        visibility = View.VISIBLE
                        setOnClickListener {
                            isSelected = !isSelected
                            listener?.onSelectedBedTypeFilter(isSelected, bedTypePictogram.name)
                        }
                    }
                }
            }

            for (i in index until bedTypeTextViews.size) {
                bedTypeTextViews[i].visibility = View.INVISIBLE
            }
        }
    }

    fun setSelectedBedType(selectedBedType: LinkedHashSet<String>) {
        val bedTypeTextViews = arrayOf(viewDataBinding.bedType01TextView, viewDataBinding.bedType02TextView, viewDataBinding.bedType03TextView, viewDataBinding.bedType04TextView)

        bedTypeTextViews.forEach {
            if (it.visibility == View.VISIBLE && it.tag != null) {
                val selected = (selectedBedType.contains(((it.tag as BedTypePictogram).name)))
                it.isSelected = selected
            }
        }
    }

    fun setFacilities(facilitiesSet: HashSet<String>?) {
        if (viewDataBinding.roomFacilitiesFlexboxLayout.childCount > 0) {
            viewDataBinding.roomFacilitiesFlexboxLayout.removeAllViews()
        }

        facilitiesSet?.takeWhile {
            try {
                Facilities.valueOf(it)
                true
            } catch (e: Exception) {
                ExLog.e(e.toString())
                false
            }
        }?.toSortedSet(Comparator<String> { o1, o2 -> Facilities.valueOf(o1).ordinal - Facilities.valueOf(o2).ordinal })?.forEach {
            createFacilitiesView(it)?.apply {
                viewDataBinding.roomFacilitiesFlexboxLayout.addView(this)
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun createFacilitiesView(facilities: String): View? {
        try {
            return DailyTextView(context).apply {
                tag = Facilities.valueOf(facilities)
                text = (tag as Facilities).getName(context)
                setTextColor(context.resources.getColorStateList(R.drawable.selector_text_color_c929292_ceb2135))
                setBackgroundResource(R.drawable.selector_fillrect_ce8e8e9_ceb2135_r3)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)

                layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(context, 40.0)).apply {
                    topMargin = ScreenUtils.dpToPx(context, 10.0)
                    leftMargin = ScreenUtils.dpToPx(context, 10.0)
                }

                val dp12 = ScreenUtils.dpToPx(context, 12.0)
                setPadding(dp12, 0, dp12, 0)
                gravity = Gravity.CENTER

                setOnClickListener {
                    isSelected = !isSelected
                    listener?.onSelectedFacilitiesFilter(isSelected, facilities)
                }
            }
        } catch (e: Exception) {
            ExLog.e(e.toString())
            return null
        }
    }

    fun setSelectedFacilities(selectedFacilities: LinkedHashSet<String>) {
        val childCount = viewDataBinding.roomFacilitiesFlexboxLayout.childCount

        for (i in 0 until childCount) {
            val childView = viewDataBinding.roomFacilitiesFlexboxLayout.getChildAt(i)
            val tag = childView.tag as Facilities

            childView.isSelected = selectedFacilities.contains(tag.name)
        }
    }

    fun setFilterCount(count: Int) {
        viewDataBinding.confirmTextView.text = context.getString(R.string.label_stay_detail_show_filtered_room, count)
    }

    private enum class BedTypePictogram constructor(private val nameResId: Int, private val imageResId: Int) {
        DOUBLE(R.string.label_double, R.drawable.vector_ic_bed_type_double),
        TWIN(R.string.label_twin, R.drawable.vector_ic_bed_type_twin),
        IN_FLOOR_HEATING(R.string.label_in_floor_heating, R.drawable.vector_ic_bed_type_ondol),
        SINGLE(R.string.label_single, R.drawable.vector_ic_bed_type_single);

        fun getName(context: Context): String? {
            return if (nameResId == 0) null else context.getString(nameResId)
        }

        fun getImageResourceId(): Int {
            return imageResId
        }
    }

    private enum class Facilities constructor(private val nameResId: Int) {
        SPAWALLPOOL(R.string.label_whirlpool), // 스파월풀
        BATH(R.string.label_bathtub), // 욕조구비
        AMENITY(R.string.label_bath_amenity), // Bath어메니티
        SHOWERGOWN(R.string.label_shower_gown), // 목욕가운
        TOOTHBRUSHSET(R.string.label_toothbrush_set), // 칫솔/치약
        PRIVATEBBQ(R.string.label_private_bbq), // 개별바베큐
        PRIVATEPOOL(R.string.label_private_pool), // Private pool
        PARTYROOM(R.string.label_party_room), // 파티룸
        KARAOKE(R.string.label_karaoke), // 노래방
        BREAKFAST(R.string.label_breakfast), // 조식포함
        PC(R.string.label_computer), // PC
        TV(R.string.label_television), // TV
        COOKING(R.string.label_cooking), // 취사가능
        SMOKEABLE(R.string.label_smokeable), // 흡연가능
        DISABLEDFACILITIES(R.string.label_disabled_facilities); // 장애인편의시설

        fun getName(context: Context): String? {
            return if (nameResId == 0) null else context.getString(nameResId)
        }
    }
}