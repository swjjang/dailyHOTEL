package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.entity.ObjectItem
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.LayoutCouponboxCouponDataBinding
import com.twoheart.dailyhotel.databinding.ListRowCouponListFooterDataBinding
import com.twoheart.dailyhotel.util.CouponUtil

class CouponListAdapter(private val context: Context, private val list: MutableList<ObjectItem>, private val listener: OnCouponItemListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    companion object {
//        private const val VIEW_TYPE_HEADER = 1
//        private val VIEW_TYPE_ITEM = 2
//        private val VIEW_TYPE_FOOTER = 3
//
//        private val FOOTER_COUNT = 1
//    }

    interface OnCouponItemListener {
        fun startNotice()

        fun startCouponHistory()

        fun showNotice(view: View, position: Int)

        fun onDownloadClick(view: View, position: Int)
    }

    fun getItem(position: Int): ObjectItem {
        return (list.size > 0).let { list[position] }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].mType
    }

    fun setData(list: MutableList<ObjectItem>) {
        this.list.clear()
        this.list += list
    }

    fun getCoupon(couponCode: String): Coupon? {
        return (list.size > 0).let {
            for (item in list) {
                when (item.mType) {
                    ObjectItem.TYPE_ENTRY -> {
                        val coupon = item as Coupon
                        if (coupon.couponCode.equals(couponCode, ignoreCase = true)) {
                            return coupon
                        }
                    }
                }
            }

            return null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            ObjectItem.TYPE_HEADER_VIEW -> {
//
//            }

            ObjectItem.TYPE_FOOTER_VIEW -> {
                val viewDataBinding = DataBindingUtil.inflate<ListRowCouponListFooterDataBinding>(
                        LayoutInflater.from(parent.context), R.layout.list_row_coupon_list_footer_data, parent, false)
                return FooterViewHolder(viewDataBinding)
            }

            else -> {
                val viewDataBinding = DataBindingUtil.inflate<LayoutCouponboxCouponDataBinding>(
                        LayoutInflater.from(parent.context), R.layout.layout_couponbox_coupon_data, parent, false)
                return ItemViewHolder(viewDataBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ObjectItem.TYPE_HEADER_VIEW -> {

            }

            ObjectItem.TYPE_FOOTER_VIEW -> {
                (holder as FooterViewHolder).onBindViewHolder()
            }

            else -> {
                (holder as ItemViewHolder).onBindViewHolder(position)
            }
        }
    }

    private inner class ItemViewHolder(val dataBinding: LayoutCouponboxCouponDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun onBindViewHolder(position: Int) {
            val item = getItem(position)
            if (ObjectItem.TYPE_ENTRY != item.mType) {
                return
            }

            val coupon = item.getItem<Coupon>()

            with(dataBinding.couponPriceTextView) {
                setCompoundDrawablesWithIntrinsicBounds(
                        if (Coupon.Type.REWARD == coupon.type) R.drawable.vector_r_ic_s_17 else 0, 0, 0, 0)

                text = DailyTextUtils.getPriceFormat(context, coupon.amount, false)
            }

            dataBinding.couponNameTextView.text = coupon.title
            dataBinding.expireTextView.text = CouponUtil.getAvailableDatesString(coupon.validFrom, coupon.validTo)

            with(dataBinding.dueDateTextView) {
                val dueDate = CouponUtil.getDueDateCount(coupon.serverDate, coupon.validTo)
                val dueDateString = if (dueDate > 1) {
                    // 2일 남음 이상
                    context.resources.getString(R.string.coupon_duedate_text, dueDate)
                } else {
                    // 오늘까지
                    context.resources.getString(R.string.coupon_today_text)
                }

                text = dueDateString

                // 8일 남음 이상 true , 7일 남음 부터 오늘까지 false
                setTextColor(context.resources.getColor(if (dueDate > 7) R.color.coupon_description_text else R.color.coupon_red_wine_text))
            }

            var lastLineText = ""
            val isEmptyStayFromTo = DailyTextUtils.isTextEmpty(coupon.stayFrom, coupon.stayTo)
            val isEmptyAmountMinimum = coupon.amountMinimum == 0

            if (!isEmptyAmountMinimum) {
                lastLineText += context.resources.getString( //
                        if (isEmptyStayFromTo == false) R.string.coupon_min_price_short_text else R.string.coupon_min_price_text, //
                        DailyTextUtils.getPriceFormat(context, coupon.amountMinimum, false))
            }

            if (!isEmptyStayFromTo) {
                if (!isEmptyAmountMinimum) {
                    lastLineText += ",\n"
                }

                lastLineText += CouponUtil.getDateOfStayAvailableString(context, coupon.stayFrom, coupon.stayTo)
            }

            val viewWidth = dataBinding.descriptionTextView.width - dataBinding.descriptionTextView.paddingLeft - dataBinding.descriptionTextView.paddingRight
            if (viewWidth == 0) {
                val lineText = lastLineText
                dataBinding.descriptionTextView.post {
                    val viewWidth = dataBinding.descriptionTextView.width - dataBinding.descriptionTextView.paddingLeft - dataBinding.descriptionTextView.paddingRight
                    setDescriptionText(viewWidth, lineText)
                }
            } else {
                setDescriptionText(viewWidth, lastLineText)
            }

            if (coupon.isDownloaded) {
                //usable
                dataBinding.downloadIconView.visibility = View.GONE
                dataBinding.useIconView.visibility = View.VISIBLE
                dataBinding.baseView.setBackgroundResource(R.drawable.more_coupon_bg)
                dataBinding.noticeTextView.setTextColor(context.resources.getColor(R.color.coupon_description_text))
            } else {
                //download
                dataBinding.downloadIconView.visibility = View.VISIBLE
                dataBinding.useIconView.visibility = View.GONE
                dataBinding.baseView.setBackgroundResource(R.drawable.more_coupon_download_bg)
                dataBinding.noticeTextView.setTextColor(context.resources.getColor(R.color.white_a80))
            }

            dataBinding.useableStayTextView.visibility = if (coupon.availableInStay) View.VISIBLE else View.GONE
            dataBinding.useableStayOutboundTextView.visibility = if (coupon.availableInOutboundHotel) View.VISIBLE else View.GONE
            dataBinding.useableGourmetTextView.visibility = if (coupon.availableInGourmet == true) View.VISIBLE else View.GONE

            val couponNotice = context.getString(R.string.coupon_notice_text)
            val spannableString = SpannableString(couponNotice)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.noticeTextView.text = spannableString
            dataBinding.noticeTextView.setOnClickListener { v -> listener.showNotice(v, position) }

            dataBinding.downloadIconView.setOnClickListener { v -> listener.onDownloadClick(v, position) }
        }

        private fun setDescriptionText(viewWidth: Int, lastLineText:String?) {
            var lastLineText = lastLineText

            if (DailyTextUtils.isTextEmpty(lastLineText)) {
                lastLineText = ""
            }

            val typeface: Typeface = FontManager.getInstance(context).regularTypeface
            val textWidth = DailyTextUtils.getTextWidth(context, lastLineText, 11.0, typeface)

            (viewWidth <= textWidth).let {
                lastLineText = lastLineText?.replace(", ", ",\n")
            }

            dataBinding.descriptionTextView.text = lastLineText
        }

    }

    private inner class FooterViewHolder(val dataBinding: ListRowCouponListFooterDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun onBindViewHolder() {
            dataBinding.couponUseNoticeTextView.setOnClickListener { listener.startNotice() }
            dataBinding.couponHistoryTextView.setOnClickListener { listener.startCouponHistory() }
        }
    }
}