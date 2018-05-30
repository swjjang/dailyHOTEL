package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomFilterContentsDataBinding
import java.util.*

class DailyDetailRoomFilterContentsView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomFilterContentsDataBinding

    private var listener: OnDailyDetailRoomFilterListener? = null

    private val bedTypeList: Array<String> = arrayOf("DOUBLE", "TWIN", "IN_FLOOR_HEATING", "SINGLE")
    private val facilitiesList: Array<String> = arrayOf("SPAWALLPOOL", "BATH", "AMENITY", "SHOWERGOWN",
            "TOOTHBRUSHSET", "PRIVATEBBQ", "PRIVATEPOOL", "PARTYROOM", "KARAOKE", "BREAKFAST",
            "PC", "TV", "KITCHENETTE", "SMOKEABLE", "DISABLEDFACILITIES")

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

        viewDataBinding.doubleTextView.setOnClickListener {
            it.isSelected = !it.isSelected
            listener?.onSelectedBedTypeFilter(it.isSelected, bedTypeList[0])
        }

        viewDataBinding.twinTextView.setOnClickListener {
            it.isSelected = !it.isSelected
            listener?.onSelectedBedTypeFilter(it.isSelected, bedTypeList[1])
        }

        viewDataBinding.inFloorHeatingTextView.setOnClickListener {
            it.isSelected = !it.isSelected
            listener?.onSelectedBedTypeFilter(it.isSelected, bedTypeList[2])
        }

        viewDataBinding.singleTextView.setOnClickListener {
            it.isSelected = !it.isSelected
            listener?.onSelectedBedTypeFilter(it.isSelected, bedTypeList[3])
        }

        facilitiesList.forEachIndexed { index, s ->
            viewDataBinding.roomFacilitiesFlexboxLayout.getChildAt(index).setOnClickListener {
                it.isSelected = !it.isSelected
                listener?.onSelectedFacilitiesFilter(it.isSelected, s)
            }
        }
    }

    fun setOnDailyDetailRoomFilterListener(listener: OnDailyDetailRoomFilterListener) {
        this.listener = listener
    }

    fun setSelectedBedType(selectedBedType: LinkedHashSet<String>) {
        val bedTypeTextViews = arrayOf(viewDataBinding.doubleTextView, viewDataBinding.twinTextView, viewDataBinding.inFloorHeatingTextView, viewDataBinding.singleTextView)

        bedTypeList.forEachIndexed { index, s ->
            bedTypeTextViews[index].isSelected = selectedBedType.contains(s)
        }
    }

    fun setSelectedFacilities(selectedFacilities: LinkedHashSet<String>) {
        val childCount = viewDataBinding.roomFacilitiesFlexboxLayout.childCount

        for (i in 0 until childCount) {
            viewDataBinding.roomFacilitiesFlexboxLayout.getChildAt(i).isSelected = selectedFacilities.contains(facilitiesList[i])
        }
    }

    fun setFilterCount(count: Int) {
        viewDataBinding.confirmTextView.isEnabled = count > 0
        viewDataBinding.confirmTextView.text = context.getString(R.string.label_stay_detail_show_filtered_room, count)
    }
}