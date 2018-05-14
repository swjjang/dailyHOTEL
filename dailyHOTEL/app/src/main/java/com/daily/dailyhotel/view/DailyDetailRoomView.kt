package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomDataBinding
import com.twoheart.dailyhotel.util.Util
import java.text.DecimalFormat
import java.util.*

private const val PRICE_AVERAGE_TAG = 1
private const val PRICE_TOTAL_TAG = 2

class DailyDetailRoomView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomDataBinding

    private var isPriceAverageType = false

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_room_data, this, true)

        setPriceAverageType(true)
    }

    fun setPriceAverageType(isAverageType: Boolean) {
        if (isPriceAverageType == isAverageType) return

        isPriceAverageType = isAverageType;
        viewDataBinding.discountPriceTextView.text = getPrice(isAverageType)
    }

    private fun getPrice(isAverageType: Boolean): String? {
        return (if (isAverageType) viewDataBinding.discountPriceTextView.getTag(viewDataBinding.discountPriceTextView.id + PRICE_AVERAGE_TAG)
        else viewDataBinding.discountPriceTextView.getTag(viewDataBinding.discountPriceTextView.id + PRICE_TOTAL_TAG)) as? String
    }

    fun setName(text: String?) {
        viewDataBinding.roomNameTextView.text = text
    }

    fun setImageUlr(url: String?) {
        Util.requestImageResize(context, viewDataBinding.simpleDraweeView, url)
    }

    fun setBedTypeText(bedTypeList: List<Room.BedInformation.BedTypeInformation>?) {
        val bedTypeText = getBedType(bedTypeList)
        if (bedTypeText.isTextEmpty()) {
            viewDataBinding.bedTypeTextView.visibility = View.GONE
        } else {
            viewDataBinding.bedTypeTextView.visibility = View.VISIBLE
            viewDataBinding.bedTypeTextView.text = bedTypeText
        }
    }

    fun setPersons(persons: Room.PersonsInformation?) {
        if (persons == null) {
            viewDataBinding.personsTextView.visibility = View.GONE
            viewDataBinding.breakfastTextView.visibility = View.GONE
            return
        }

        val personsText = getPersons(persons.fixed, persons.extra, persons.extraCharge)

        if (personsText.isTextEmpty()) {
            viewDataBinding.personsTextView.visibility = View.GONE
        } else {
            viewDataBinding.personsTextView.visibility = View.VISIBLE
            viewDataBinding.personsTextView.text = personsText
        }

        if (persons.breakfast > 0) {
            viewDataBinding.breakfastTextView.visibility = View.VISIBLE
            viewDataBinding.breakfastTextView.text = context.getString(R.string.label_stay_detail_include_person_breakfast, persons.breakfast)
        } else {
            viewDataBinding.breakfastTextView.visibility = View.GONE
        }
    }

    fun setBenefit(benefit: String?) {
        if (benefit.isTextEmpty()) {
            viewDataBinding.benefitTextView.visibility = View.GONE
        } else {
            viewDataBinding.benefitTextView.visibility = View.VISIBLE
            viewDataBinding.benefitTextView.text = benefit
        }
    }

    fun setPrice(discountAverage: Int, discountTotal: Int) {
        val priceAverage = DecimalFormat("###,##0").format(discountAverage)
        val priceTotal = DecimalFormat("###,##0").format(discountTotal)

        viewDataBinding.discountPriceTextView.text = if (isPriceAverageType) priceAverage else priceTotal
        viewDataBinding.discountPriceTextView.setTag(viewDataBinding.discountPriceTextView.id + PRICE_AVERAGE_TAG, priceAverage)
        viewDataBinding.discountPriceTextView.setTag(viewDataBinding.discountPriceTextView.id + PRICE_TOTAL_TAG, priceTotal)
    }

    fun setCouponVisible(visible: Boolean) {
        viewDataBinding.couponTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun getBedType(bedTypeList: List<Room.BedInformation.BedTypeInformation>?): String? {
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

    private fun getPersons(fixed: Int, extra: Int, extraCharge: Boolean): String? {
        return if (fixed > 0) {
            context.getString(R.string.label_stay_detail_person_information,
                    fixed, fixed + extra, if (extraCharge) context.getString(R.string.label_pay) else context.getString(R.string.label_free))
        } else null
    }
}
