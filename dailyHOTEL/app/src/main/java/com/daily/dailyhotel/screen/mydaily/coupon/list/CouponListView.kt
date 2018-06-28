package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.entity.ObjectItem
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityCouponListDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor

class CouponListView(activity: CouponListActivity, listener: CouponListInterface.OnEventListener)//
    : BaseDialogView<CouponListInterface.OnEventListener, ActivityCouponListDataBinding>(activity, listener), CouponListInterface.ViewInterface {

    private lateinit var sortArrayAdapter: SortArrayAdapter
    private lateinit var listAdapter: CouponListAdapter

    override fun setContentView(viewDataBinding: ActivityCouponListDataBinding) {
        initToolbar(viewDataBinding)
        initRecyclerView(viewDataBinding)

        viewDataBinding.couponLayout.visibility = View.INVISIBLE

        val strings = context.resources.getTextArray(R.array.coupon_sort_array)
        sortArrayAdapter = SortArrayAdapter(context, R.layout.list_row_coupon_spinner, strings)
        sortArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item)

        viewDataBinding.sortSpinner.adapter = sortArrayAdapter

        viewDataBinding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortArrayAdapter.selectedPosition = position

                // 최초 진입 시 sortSpinner 가 선택 되면서 setData를 2번 하게되는 이슈로 처리 추가
                if (::listAdapter.isInitialized) {
                    eventListener.onItemSelectedSpinner(position)
                }
            }
        }
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityCouponListDataBinding) {
        viewDataBinding.toolbarView.apply {
            setTitleText(R.string.actionbar_title_coupon_list)
            setOnBackClickListener { eventListener.onBackClick() }
        }

        viewDataBinding.registerCouponView.setOnClickListener { eventListener.startRegisterCoupon() }
    }

    private fun initRecyclerView(viewDataBinding: ActivityCouponListDataBinding) {
        viewDataBinding.apply {
            couponUseNoticeTextView.setOnClickListener { eventListener.startNotice() }
            couponHistoryTextView.setOnClickListener { eventListener.startCouponHistory() }

            recyclerView.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
                scrollToPosition(0)
            }

            EdgeEffectColor.setEdgeGlowColor(recyclerView, context.resources.getColor(R.color.default_over_scroll_edge))
        }
    }

    private fun updateHeaderTextView(count: Int) {
        viewDataBinding.couponTextView.text = context.getString(R.string.coupon_header_text, count)
    }

    override fun setSelectionSpinner(sortType: CouponListActivity.SortType) {
        val position = when (sortType) {
            CouponListActivity.SortType.STAY -> 1
            CouponListActivity.SortType.GOURMET -> 2
            else -> 0
        }

        sortArrayAdapter.selectedPosition = position
        viewDataBinding.sortSpinner.setSelection(position)
    }

    override fun setData(list: List<Coupon>, sortType: CouponListActivity.SortType, isScrollTop: Boolean) {
        viewDataBinding.apply {
            emptyView.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE

            if (list.isEmpty() && sortType == CouponListActivity.SortType.ALL) {
                couponLayout.visibility = View.GONE
                return
            }

            couponLayout.visibility = View.VISIBLE

            updateHeaderTextView(list.size)

            val itemList = mutableListOf<ObjectItem>()
            for (coupon in list) {
                itemList.add(ObjectItem(ObjectItem.TYPE_ENTRY, coupon))
            }

            itemList.add(ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, Any()))

            if (!::listAdapter.isInitialized) {
                listAdapter = CouponListAdapter(context, itemList, object : CouponListAdapter.OnCouponItemListener {
                    override fun startNotice() {
                        eventListener.startNotice()
                    }

                    override fun startCouponHistory() {
                        eventListener.startCouponHistory()
                    }

                    override fun showNotice(view: View, position: Int) {
                        val objectItem = listAdapter.getItem(position)

                        (ObjectItem.TYPE_ENTRY == objectItem.mType).let {
                            eventListener.showListItemNotice(objectItem.getItem())
                        }
                    }

                    override fun onDownloadClick(view: View, position: Int) {
                        val objectItem = listAdapter.getItem(position)

                        (ObjectItem.TYPE_ENTRY == objectItem.mType).let {
                            eventListener.onListItemDownLoadClick(objectItem.getItem())
                        }
                    }
                })

                recyclerView.adapter = listAdapter
            } else {
                listAdapter.setData(itemList)
                listAdapter.notifyDataSetChanged()
            }

            isScrollTop.let { recyclerView.scrollToPosition(0) }
        }
    }

    fun getCoupon(couponCode: String): Coupon? {
        return listAdapter.getCoupon(couponCode)
    }

    @Suppress("DEPRECATION")
    private inner class SortArrayAdapter(context: Context, resourceId: Int, list: Array<CharSequence>)
        : ArrayAdapter<CharSequence>(context, resourceId, list) {
        var selectedPosition: Int = 0

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view = super.getDropDownView(position, convertView, parent)

            if (view != null) {
                val textView = view as TextView
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
                textView.isSelected = selectedPosition == position

                if (selectedPosition == position) {
                    textView.setTextColor(context.resources.getColor(R.color.default_text_ceb2135))
                } else {
                    textView.setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
                }
            }

            return view
        }
    }
}