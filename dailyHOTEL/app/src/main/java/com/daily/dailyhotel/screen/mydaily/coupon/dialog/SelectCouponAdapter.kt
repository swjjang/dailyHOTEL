package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.LayoutSelectCouponDataBinding
import com.twoheart.dailyhotel.util.DailyCalendar
import java.text.ParseException
import java.util.*

class SelectCouponAdapter(val context: Context, val list: MutableList<Coupon>, val listener: OnCouponItemLister) : RecyclerView.Adapter<SelectCouponAdapter.CouponViewHolder>() {
    var selectedPosition = -1
    var selectedMode = true

    interface OnCouponItemLister {
        fun onDownloadClick(position: Int)

        fun updatePositiveButton()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clear() {
        list.clear()
    }

    fun addAll(list: MutableList<Coupon>) {
        this.list += list
    }

    fun setAll(list: MutableList<Coupon>) {
        clear()
        addAll(list)
    }

    fun getItem(position: Int): Coupon? {
        return (position < 0 || list.size <= position).let { null } ?: list[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val viewDataBinding = DataBindingUtil.inflate<LayoutSelectCouponDataBinding>(LayoutInflater.from(context), R.layout.layout_select_coupon_data, parent, false)
        return CouponViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = getItem(position) ?: return

        holder.dataBinding.run {
            val amount = DailyTextUtils.getPriceFormat(context, coupon.amount, false)
            couponPriceTextView.text = amount
            couponNameTextView.text = coupon.title

            descriptionTextView.run {
                when (coupon.amountMinimum) {
                    0 -> {
                        visibility = View.GONE
                    }

                    else -> {
                        text = context.resources.getString(R.string.coupon_min_price_text, DailyTextUtils.getPriceFormat(context, coupon.amountMinimum, false))
                        visibility = View.VISIBLE
                    }
                }
            }

            expireTextView.run {
                try {
                    var expireText = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd")
                    expireText = String.format(Locale.KOREA, "- %s", expireText)
                    text = expireText
                    visibility = View.VISIBLE
                } catch (e: ParseException) {
                    Crashlytics.log("Select Coupon::coupon.validTo: " + coupon.validTo)
                    ExLog.d(e.message)
                    visibility = View.GONE
                }
            }

            // stay, gourmet, StayOutbound Type
            useableStayTextView.visibility = if (coupon.availableInStay) View.VISIBLE else View.GONE
            useableStayOutboundTextView.visibility = if (coupon.availableInOutboundHotel) View.VISIBLE else View.GONE
            useableGourmetTextView.visibility = if (coupon.availableInGourmet) View.VISIBLE else View.GONE

            setDownLoadLayout(this, Coupon.Type.REWARD == coupon.type, coupon.isDownloaded)

            if (selectedMode && coupon.isDownloaded) {
                setSelectedLayout(this, Coupon.Type.REWARD == coupon.type, selectedPosition == position)

                root.setOnClickListener {
                    val coupon = getItem(position)

                    if (coupon != null && coupon.isDownloaded) {
                        selectedPosition = position
                    } else {
                        selectedPosition = -1
                        listener.onDownloadClick(position)
                    }

                    notifyDataSetChanged()
                    listener.updatePositiveButton()
                }
            } else {
                root.setOnClickListener {
                    val coupon = getItem(position)

                    coupon?.let {
                        if (!it.isDownloaded) {
                            listener.onDownloadClick(position)
                        }
                    }
                }
            }
        }
    }

    fun getCoupon(couponCode: String?): Coupon? {
        if (couponCode.isTextEmpty()) {
            return null
        }

        if (list.isEmpty()) {
            return null
        }

        for (coupon in list) {
            if (couponCode.equals(coupon.couponCode, true)) {
                return coupon
            }
        }

        return null
    }

    private fun setSelectedLayout(dataBinding: LayoutSelectCouponDataBinding, rewardCoupon: Boolean, selected: Boolean) {
        dataBinding.run {
            when (selected) {
                true -> {
                    root.setBackgroundColor(context.resources.getColor(R.color.default_background_cfafafb))

                    couponPriceTextView.run {
                        setTextColor(context.resources.getColor(R.color.default_text_ceb2135))
                        setCompoundDrawablesWithIntrinsicBounds(if (rewardCoupon) R.drawable.vector_r_ic_s_17 else 0, 0, R.drawable.ic_check_s, 0)
                        isSelected = true
                    }

                    couponNameTextView.setTextColor(context.resources.getColor(R.color.default_text_ceb2135))
                }

                else -> {
                    root.setBackgroundResource(R.color.white)

                    couponPriceTextView.run {
                        setTextColor(context.resources.getColor(R.color.black))
                        setCompoundDrawablesWithIntrinsicBounds(if (rewardCoupon) R.drawable.vector_r_ic_s_17 else 0, 0, 0, 0)
                        isSelected = false
                    }

                    couponNameTextView.setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
                }
            }
        }
    }

    private fun setDownLoadLayout(dataBinding: LayoutSelectCouponDataBinding, rewardCoupon: Boolean, download: Boolean) {
        dataBinding.run {
            couponPriceTextView.setCompoundDrawablesWithIntrinsicBounds(if (rewardCoupon) R.drawable.vector_r_ic_s_17 else 0, 0, 0, 0)
            root.setBackgroundResource(R.color.white)

            when (download) {
                true -> {
                    couponPriceTextView.setTextColor(context.resources.getColor(R.color.black))
                    couponNameTextView.setTextColor(context.resources.getColor(R.color.black))
                    descriptionTextView.setTextColor(context.resources.getColor(R.color.default_text_c929292))
                    expireTextView.setTextColor(context.resources.getColor(R.color.default_text_c929292))

                    useableStayTextView.alpha = 1.0f
                    useableStayOutboundTextView.alpha = 1.0f
                    useableGourmetTextView.alpha = 1.0f
                    downloadCouponLayout.visibility = View.GONE
                }

                else -> {
                    val color = context.resources.getColor(R.color.default_text_cc5c5c5)

                    couponPriceTextView.setTextColor(color)
                    couponNameTextView.setTextColor(color)
                    descriptionTextView.setTextColor(color)
                    expireTextView.setTextColor(color)

                    useableStayTextView.alpha = 0.5f
                    useableStayOutboundTextView.alpha = 0.5f
                    useableGourmetTextView.alpha = 0.5f
                    downloadCouponLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class CouponViewHolder(val dataBinding: LayoutSelectCouponDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}