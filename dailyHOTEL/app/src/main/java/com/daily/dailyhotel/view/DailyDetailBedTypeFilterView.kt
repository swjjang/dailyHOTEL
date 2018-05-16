package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBedtypeFilterDataBinding
import java.util.*

class DailyDetailBedTypeFilterView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailBedtypeFilterDataBinding

    private var listener: OnDailyDetailBedTypeFilterListener? = null

    interface OnDailyDetailBedTypeFilterListener {
        fun onSelectedFilter(bedType: String)

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_bedtype_filter_data, this, true)

        viewDataBinding.closeImageView.setOnClickListener { listener?.onCloseClick() }
        viewDataBinding.resetTextView.setOnClickListener { listener?.onResetClick() }
        viewDataBinding.confirmTextView.setOnClickListener { listener?.onConfirmClick() }
    }

    fun setOnDailyDetailBedTypeFilterListener(listener: OnDailyDetailBedTypeFilterListener) {
        this.listener = listener
    }

    fun setBedType(bedTypeSet: HashSet<String>) {
        val bedTypeViews = arrayOf(viewDataBinding.bedType01View, viewDataBinding.bedType02View, viewDataBinding.bedType03View, viewDataBinding.bedType04View)
        val bedTypeTextViews = arrayOf(viewDataBinding.bedType01TextView, viewDataBinding.bedType02TextView, viewDataBinding.bedType03TextView, viewDataBinding.bedType04TextView)
        val bedTypePictograms = arrayOf(BedTypePictogram.DOUBLE, BedTypePictogram.TWIN, BedTypePictogram.IN_FLOOR_HEATING, BedTypePictogram.SINGLE)
        var index = 0

        bedTypePictograms.forEach { bedTypePictogram ->
            if (bedTypeSet.contains(bedTypePictogram.name)) {
                bedTypeTextViews[index].apply {
                    text = bedTypePictogram.getName(context)
                    setCompoundDrawablesWithIntrinsicBounds(0, bedTypePictogram.getImageResourceId(), 0, 0)
                    visibility = View.VISIBLE
                    tag = bedTypePictogram;
                    setOnClickListener { listener?.onSelectedFilter(bedTypePictogram.name) }
                }

                bedTypeViews[index++].visibility = View.VISIBLE
            }
        }

        for (i in index until bedTypeTextViews.size) {
            bedTypeTextViews[i].visibility = View.INVISIBLE
            bedTypeViews[i].visibility = View.INVISIBLE
        }
    }

    fun setSelectedBedType(selectedBedType: LinkedHashSet<String>) {
        val bedTypeViews = arrayOf(viewDataBinding.bedType01View, viewDataBinding.bedType02View, viewDataBinding.bedType03View, viewDataBinding.bedType04View)
        val bedTypeTextViews = arrayOf(viewDataBinding.bedType01TextView, viewDataBinding.bedType02TextView, viewDataBinding.bedType03TextView, viewDataBinding.bedType04TextView)

        bedTypeTextViews.forEachIndexed { index, dailyTextView ->
            if (dailyTextView.visibility == View.VISIBLE && dailyTextView.tag != null) {
                val selected = (selectedBedType.contains(((dailyTextView.tag as BedTypePictogram).name)))
                bedTypeViews[index].isSelected = selected
                dailyTextView.isSelected = selected
            }
        }
    }

    fun setFilterCount(count: Int) {
        viewDataBinding.confirmTextView.text = context.getString(R.string.label_stay_detail_show_filtered_room, count)
    }

    private enum class BedTypePictogram constructor(private val nameResId: Int, private val imageResId: Int) {
        DOUBLE(R.string.label_double, R.drawable.vector_ic_bed_type_double),
        TWIN(R.string.label_twin, R.drawable.vector_ic_bed_type_twin),
        IN_FLOOR_HEATING(R.string.label_heatedfloors, R.drawable.vector_ic_bed_type_ondol),
        SINGLE(R.string.label_single, R.drawable.vector_ic_bed_type_single);

        fun getName(context: Context): String? {
            return if (nameResId == 0) null else context.getString(nameResId)
        }

        fun getImageResourceId(): Int {
            return imageResId
        }
    }
}