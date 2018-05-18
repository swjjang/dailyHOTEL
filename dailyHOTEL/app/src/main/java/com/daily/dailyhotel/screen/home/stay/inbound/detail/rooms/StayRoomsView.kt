package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import com.daily.base.BaseDialogView
import com.daily.base.util.DailyTextUtils
import com.daily.dailyhotel.entity.Room
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayRoomsDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor
import io.reactivex.Observable
import io.reactivex.Observer

class StayRoomsView(activity: StayRoomsActivity, listener: StayRoomsInterface.OnEventListener)//
    : BaseDialogView<StayRoomsInterface.OnEventListener, ActivityStayRoomsDataBinding>(activity, listener)
        , StayRoomsInterface.ViewInterface, View.OnClickListener {
    private lateinit var listAdapter: StayRoomAdapter

    override fun setContentView(viewDataBinding: ActivityStayRoomsDataBinding) {
        viewDataBinding.run {
            closeImageView.setOnClickListener({
                eventListener.onBackClick()
            })

            recyclerView.layoutManager = ZoomCenterLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge))

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(recyclerView)

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val view = pagerSnapHelper.findSnapView(viewDataBinding.recyclerView.layoutManager)
                    val position = viewDataBinding.recyclerView.getChildAdapterPosition(view)

                    eventListener.onScrolled(position, true)
                }
            })

            if (!::listAdapter.isInitialized) {
                listAdapter = StayRoomAdapter(context, mutableListOf())
            }

            listAdapter.setEventListener(object : StayRoomAdapter.OnEventListener {
                override fun finish() {
                }

                override fun onMoreImageClick(position: Int) {
                    eventListener.onMoreImageClick(position)
                }

                override fun onVrImageClick(position: Int) {
                    eventListener.onVrImageClick(position)
                }
            })

            recyclerView.adapter = listAdapter

            guideLayout.setOnClickListener(this@StayRoomsView)
            guideLayout.visibility = View.GONE
        }
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.titleTextView.text = title
    }

    override fun setIndicatorText(position: Int) {
        val count = if (listAdapter.itemCount == 0) 1 else listAdapter.itemCount

        viewDataBinding.indicatorTextView.text = "$position / ${if (count == 0) 1 else count}"
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.closeImageView -> eventListener.onCloseClick()

            R.id.guideLayout -> eventListener.onGuideClick()

            R.id.bookingTextView -> eventListener.onBookingClick()

            else -> {
            }
        }
    }

    override fun setBookingButtonText(position: Int) {
        val price = listAdapter.getItem(position)?.let {
            it.amountInformation.discountTotal
        } ?: 0

        val text = context.resources.getString(R.string.label_stay_room_booking_button_text
                , DailyTextUtils.getPriceFormat(context, price, false))

        viewDataBinding.bookingTextView.text = text
    }

    override fun setNights(nights: Int) {
        listAdapter.setNights(nights)

        viewDataBinding.nightsTextView.text = context.resources.getString(R.string.label_nights, nights)
        viewDataBinding.nightsTextView.visibility = if (nights > 1) View.VISIBLE else View.GONE
    }

    override fun setRoomList(roomList: MutableList<Room>, position: Int) {
        if (roomList.size == 0) {
            return
        }

        listAdapter.setData(roomList)

        viewDataBinding.recyclerView.post {
            (viewDataBinding.recyclerView.layoutManager as LinearLayoutManager)//
                    .scrollToPositionWithOffset(position, listAdapter.getLayoutMargin().toInt())
        }
    }

    override fun notifyDataSetChanged() {
        listAdapter.notifyDataSetChanged()
    }

    override fun setGuideVisible(visible: Boolean) {
        viewDataBinding.guideLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun hideGuideAnimation(): Observable<Boolean> {
        val objectAnimator = ObjectAnimator.ofFloat(viewDataBinding.guideLayout, "alpha", 1.0f, 0.0f)

        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 300

        return object : Observable<Boolean>() {
            override fun subscribeActual(observer: Observer<in Boolean>) {
                objectAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}

                    override fun onAnimationEnd(animator: Animator) {
                        objectAnimator.removeAllListeners()

                        viewDataBinding.guideLayout.visibility = View.GONE

                        observer.onNext(true)
                        observer.onComplete()
                    }

                    override fun onAnimationCancel(animator: Animator) {}

                    override fun onAnimationRepeat(animator: Animator) {}
                })

                objectAnimator.start()
            }
        }
    }

    override fun showVrDialog(checkedChangeListener: CompoundButton.OnCheckedChangeListener
                              , positiveListener: View.OnClickListener
                              , onDismissListener: DialogInterface.OnDismissListener) {
        showSimpleDialog(null
                , getString(R.string.message_stay_used_data_guide)
                , getString(R.string.label_dont_again)
                , getString(R.string.dialog_btn_do_continue)
                , getString(R.string.dialog_btn_text_close)
                , checkedChangeListener, positiveListener
                , null, null, onDismissListener, true)
    }
}