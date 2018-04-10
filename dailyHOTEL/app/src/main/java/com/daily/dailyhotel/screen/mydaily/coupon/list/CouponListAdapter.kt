package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.entity.ObjectItem
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.LayoutCouponboxCouponDataBinding

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
                val viewDataBinding = DataBindingUtil.inflate<LayoutCouponboxCouponDataBinding>(
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

            }

            else -> {

            }
        }
    }

    private inner class ItemViewHolder(val dataBinding: LayoutCouponboxCouponDataBinding): RecyclerView.ViewHolder(dataBinding.root) {

    }

    private inner class FooterViewHolder(val dataBinding: LayoutListRowCouponListFooterDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {

    }
}