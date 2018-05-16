package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBedtypeFilterDataBinding
import kotlinx.android.synthetic.main.activity_contact_us.view.*
import java.util.*

class DailyDetailBedTypeFilterView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailBedtypeFilterDataBinding

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
    }

    fun setBedType(bedTypeSet: HashSet<String>) {
        val bedTypeViews = arrayOf(viewDataBinding.bedType01TextView, viewDataBinding.bedType02TextView, viewDataBinding.bedType03TextView, viewDataBinding.bedType04TextView)
        val bedTypePictograms = arrayOf(BedTypePictogram.DOUBLE, BedTypePictogram.TWIN, BedTypePictogram.IN_FLOOR_HEATING, BedTypePictogram.SINGLE)
        var index = 0

        bedTypePictograms.forEach {
            if (bedTypeSet.contains(it.name)) {
                bedTypeViews[index++].apply {
                    text = it.getName(context)
                    setCompoundDrawablesWithIntrinsicBounds(0, it.getImageResourceId(), 0, 0)
                    visibility = View.VISIBLE
                    tag = it;
                }
            }
        }

        for (i in index..bedTypeViews.size) {
            bedTypeViews[i].visibility = View.INVISIBLE
        }
    }

    fun setSelectedBedType(selectedBedType: LinkedHashSet<String>) {
        val bedTypeViews = arrayOf(viewDataBinding.bedType01TextView, viewDataBinding.bedType02TextView, viewDataBinding.bedType03TextView, viewDataBinding.bedType04TextView)

        bedTypeViews.forEach {
            if (dailyTextView.visibility == View.VISIBLE && dailyTextView.tag != null) {
                it.isSelected = (selectedBedType.contains(((it.tag as BedTypePictogram).name)))
            }
        }
    }

    private enum class BedTypePictogram constructor(private val nameResId: Int, private val imageResId: Int) {
        DOUBLE(R.string.label_pool, R.drawable.vector_ic_bed_type_double),
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