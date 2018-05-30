package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.daily.base.util.DailyImageSpan
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomDataBinding
import com.twoheart.dailyhotel.util.Util
import java.text.DecimalFormat

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
        viewDataBinding.wonTextView.text = context.getString(R.string.currency) + if (isAverageType) context.getString(R.string.label_stay_detail_slash_one_nights) else ""
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

    fun setRewardVisible(visible: Boolean) {
        viewDataBinding.rewardImageView.visibility = if (visible) View.VISIBLE else View.GONE
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
        val bedTypeMap = LinkedHashMap<String, Int>().apply {
            put("DOUBLE", 0)
            put("SINGLE", 0)
            put("IN_FLOOR_HEATING", 0)
            put("UNKNOWN", 0)
        }

        bedTypeList?.forEach {
            when (it.bedType) {
                "DOUBLE", "KING", "QUEEN", "SEMI_DOUBLE" -> bedTypeMap["DOUBLE"] = bedTypeMap["DOUBLE"]!! + it.count

                "SINGLE" -> bedTypeMap["SINGLE"] = bedTypeMap["SINGLE"]!! + it.count

                "IN_FLOOR_HEATING" -> bedTypeMap["IN_FLOOR_HEATING"] = bedTypeMap["IN_FLOOR_HEATING"]!! + it.count

                "UNKNOWN" -> bedTypeMap["UNKNOWN"] = 1
            }
        }

        val doubleCount = bedTypeMap["DOUBLE"]!!
        if (doubleCount > 0) {
            bedStringBuilder.append(context.getString(R.string.label_double))
            bedStringBuilder.append(context.getString(R.string.label_booking_count, doubleCount))
        }

        val singleCount = bedTypeMap["SINGLE"]!!
        if (singleCount > 0) {
            if (bedStringBuilder.isNotEmpty()) {
                bedStringBuilder.append(", ")
            }
            bedStringBuilder.append(context.getString(R.string.label_single))
            bedStringBuilder.append(context.getString(R.string.label_booking_count, singleCount))
        }

        val inFloorHeatingCount = bedTypeMap["IN_FLOOR_HEATING"]!!
        if (inFloorHeatingCount > 0) {
            if (bedStringBuilder.isNotEmpty()) {
                bedStringBuilder.append(", ")
            }
            bedStringBuilder.append(context.getString(R.string.label_in_floor_heating))
            bedStringBuilder.append(context.getString(R.string.label_booking_count, inFloorHeatingCount))
        }

        val unKnownCount = bedTypeMap["UNKNOWN"]!!
        if (unKnownCount > 0) {
            if (bedStringBuilder.isNotEmpty()) {
                bedStringBuilder.append(", ")
            }
            bedStringBuilder.append(context.getString(R.string.label_bed_type_unknown))
        }

        return bedStringBuilder.toString()
    }

    private fun getPersons(fixed: Int, extra: Int, extraCharge: Boolean): CharSequence? {
        return if (fixed > 0) {
            val text = context.getString(R.string.label_stay_detail_person_information,
                    fixed, fixed + extra, if (extraCharge) context.getString(R.string.label_pay) else context.getString(R.string.label_free))
            val startIndex = text.indexOf('돋')

            SpannableStringBuilder(text).apply {
                setSpan(DailyImageSpan(context, R.drawable.layerlist_over_b4d666666_s2_p4, DailyImageSpan.ALIGN_VERTICAL_CENTER), startIndex, startIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else null
    }
}
