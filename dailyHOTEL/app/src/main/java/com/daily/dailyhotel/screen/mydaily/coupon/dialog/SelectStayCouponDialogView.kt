package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import com.daily.base.BaseDialogView
import com.daily.base.util.ExLog
import com.daily.base.util.ScreenUtils
import com.daily.base.util.VersionUtils
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.screen.mydaily.coupon.dialog.SelectCouponAdapter.OnCouponItemLister
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivitySelectCouponDialogDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor

class SelectStayCouponDialogView(activity: SelectStayCouponDialogActivity, listener: SelectStayCouponDialogInterface.OnEventListener)//
    : BaseDialogView<SelectStayCouponDialogInterface.OnEventListener, ActivitySelectCouponDialogDataBinding>(activity, listener)
        , SelectStayCouponDialogInterface.ViewInterface, View.OnClickListener {

    private lateinit var listAdapter: SelectCouponAdapter

    override fun setContentView(viewDataBinding: ActivitySelectCouponDialogDataBinding) {
        viewDataBinding.run {
            EdgeEffectColor.setEdgeGlowColor(recyclerView, context.resources.getColor(R.color.default_over_scroll_edge))

            (context as Activity).let {
                if (ScreenUtils.isTabletDevice(it)) {
                    dialogLayout.layoutParams.apply {
                        width = ScreenUtils.getScreenWidth(context) * 10 / 15
                    }
                }
            }

            positiveTextView.isEnabled = false

            negativeTextView.setOnClickListener(this@SelectStayCouponDialogView)
            positiveTextView.setOnClickListener(this@SelectStayCouponDialogView)
            confirmTextView.setOnClickListener(this@SelectStayCouponDialogView)

            punchMaskLayout.run {
                    val drawable = BackgroundDrawable(context, this)
                if (VersionUtils.isOverAPI16()) {
                    background = drawable
                } else {
                    setBackgroundDrawable(background)
                }
            }

            setVisibility(false)

            if (!::listAdapter.isInitialized) {
                listAdapter = SelectCouponAdapter(context, mutableListOf(), object : OnCouponItemLister {
                    override fun onDownloadClick(position: Int) {
                        listAdapter.getItem(position)?.let {
                            eventListener.onCouponDownloadClick(it)
                        } ?: ExLog.d("sam : coupon is null")
                    }

                    override fun updatePositiveButton() {
                        viewDataBinding.positiveTextView.isEnabled = listAdapter.selectedPosition != -1
                    }
                })

                recyclerView.adapter = listAdapter
            }
        }

    }

    override fun setToolbarTitle(title: String?) {
    }

    override fun setVisibility(visible: Boolean) {
        viewDataBinding.dialogLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setOneButtonLayout(visible: Boolean, resId: Int) {
        viewDataBinding.run {
            confirmTextView.setText(resId)

            when (visible) {
                true -> {
                    twoButtonLayout.visibility = View.GONE
                    oneButtonLayout.visibility = View.VISIBLE
                }

                else -> {
                    oneButtonLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun setTwoButtonLayout(visible: Boolean, positiveResId: Int, negativeResId: Int) {
        viewDataBinding.run {
            positiveTextView.setText(positiveResId)
            negativeTextView.setText(negativeResId)

            when (visible) {
                true -> {
                    twoButtonLayout.visibility = View.VISIBLE
                    oneButtonLayout.visibility = View.GONE
                }

                else -> {
                    twoButtonLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun setTitle(resId: Int) {
        viewDataBinding.titleTextView.setText(resId)
    }

    override fun setData(list: MutableList<Coupon>?, selected: Boolean) {
        if (list == null || list.isEmpty()) {
            return
        }

        listAdapter.setAll(list)
        listAdapter.selectedMode = selected
        listAdapter.notifyDataSetChanged()
    }

    fun getCoupon(couponCode: String?): Coupon? {
        return listAdapter.getCoupon(couponCode)
    }

    override fun getCouponCount(): Int {
        return listAdapter.itemCount
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.negativeTextView -> eventListener.onBackClick()

            R.id.positiveTextView -> {
                val coupon = listAdapter.getItem(listAdapter.selectedPosition)
                coupon?.let { eventListener.setResult(it) }
            }

            R.id.confirmTextView -> eventListener.onBackClick()
        }
    }

    private inner class BackgroundDrawable(context: Context, val view: View) : Drawable() {
        private val circleBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.circle)
        private val paint = Paint()
        private val overPaint = Paint()

        init {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            overPaint.alpha = 0x99
        }

        override fun draw(canvas: Canvas?) {
            val cY = 0f
            val cX = view.x - circleBitmap.width / 2

            canvas?.run {
                drawBitmap(circleBitmap, cX, cY, paint)
                drawBitmap(circleBitmap, cX + view.width, cY, paint)

                drawBitmap(circleBitmap, cX, cY, overPaint)
                drawBitmap(circleBitmap, cX + view.width, cY, overPaint)
            }
        }

        override fun setAlpha(alpha: Int) {
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
        }

    }
}