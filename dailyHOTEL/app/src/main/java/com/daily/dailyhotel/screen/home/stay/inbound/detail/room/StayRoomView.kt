package com.daily.dailyhotel.screen.home.stay.inbound.detail.room

import android.animation.*
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import com.daily.base.BaseDialogView
import com.daily.base.util.DailyTextUtils
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.TrueVR
import com.daily.dailyhotel.screen.home.stay.inbound.detail.room.StayRoomItemView.OnEventListener
import com.daily.dailyhotel.util.runTrue
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayRoomsDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor
import io.reactivex.Observable
import io.reactivex.Observer

class StayRoomView(activity: StayRoomActivity, listener: StayRoomInterface.OnEventListener)//
    : BaseDialogView<StayRoomInterface.OnEventListener, ActivityStayRoomsDataBinding>(activity, listener)
        , StayRoomInterface.ViewInterface {
    private lateinit var listAdapter: StayRoomAdapter
    private var nights = 1

    private companion object {
        private const val MOVE_STATE_NONE = 0
        private const val MOVE_STATE_SCROLL = 10
        private const val MOVE_STATE_VIEWPAGER = 100
        private const val MOVE_STATE_START_ANIMATION = 1000
        private const val MOVE_STATE_END_ANIMATION = 10000
        private const val MOVE_CALIBRATE_VALUE = 0.95f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setContentView(viewDataBinding: ActivityStayRoomsDataBinding) {
        viewDataBinding.run {
            closeImageView.setOnClickListener { eventListener.onBackClick() }

            recyclerView.layoutManager = RoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge))

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(recyclerView)

            viewDataBinding.recyclerView.setOnTouchListener(recyclerTouchListener)

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val view = pagerSnapHelper.findSnapView(viewDataBinding.recyclerView.layoutManager)
                    val position = viewDataBinding.recyclerView.getChildAdapterPosition(view)

                    eventListener.onScrolled(position, true)

                    if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                        setRoomDetailData(position)
                    }
                }
            })

            if (!::listAdapter.isInitialized) {
                listAdapter = StayRoomAdapter(context, mutableListOf())
            }

            listAdapter.setEventListener(object : OnEventListener {
                override fun onBackClick() {
                }

                override fun onCloseClick() {
                    eventListener.onCloseClick()
                }

                override fun onMoreImageClick(roomName: String, roomIndex: Int) {
                    eventListener.onMoreImageClick(roomName, roomIndex)
                }

                override fun onVrImageClick(trueVrList: List<TrueVR>) {
                    eventListener.onVrImageClick(trueVrList)
                }
            })

            recyclerView.adapter = listAdapter

            bookingTextView.setOnClickListener { eventListener.onBookingClick() }

            guideLayout.setOnClickListener { eventListener.onGuideClick() }
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

    override fun setBookingButtonText(position: Int) {
        val price = listAdapter.getItem(position)?.amountInformation?.discountTotal ?: 0

        val text = context.resources.getString(R.string.label_stay_room_booking_button_text
                , DailyTextUtils.getPriceFormat(context, price, false))

        viewDataBinding.bookingTextView.text = text
    }

    override fun setNights(nights: Int) {
        this.nights = nights
        listAdapter.setNights(nights)

        viewDataBinding.nightsTextView.text = context.resources.getString(R.string.label_nights, nights)
        viewDataBinding.nightsTextView.visibility = if (nights > 1) View.VISIBLE else View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setRoomList(roomList: MutableList<Room>, position: Int) {
        if (roomList.size == 0) {
            return
        }

        listAdapter.setData(roomList)
    }

    override fun notifyDataSetChanged() {
        listAdapter.notifyDataSetChanged()
    }

    override fun setRecyclerPosition(position: Int) {
        viewDataBinding.recyclerView.post {
            (viewDataBinding.recyclerView.layoutManager as? LinearLayoutManager)?.run {
                scrollToPositionWithOffset(position, listAdapter.getLayoutMargin().toInt())
            }
        }
    }

    private fun setRecyclerScrollEnabled() {
        val layoutManager = viewDataBinding.recyclerView.layoutManager as RoomLayoutManager
        if (viewDataBinding.roomDetailLayout?.visibility == View.VISIBLE) {
            layoutManager.setScrollEnabled(false)
        } else {
            layoutManager.setScrollEnabled(true)
        }
    }

    override fun setRoomDetailData(position: Int) {
        viewDataBinding.recyclerView.post {
            val room = listAdapter.getItem(position) ?: return@post
            viewDataBinding.roomDetailLayout.setData(room, listAdapter.getNights())
            viewDataBinding.roomDetailLayout.notifyDataSetChanged()
        }
    }

    override fun showRoomDetailLayout(): Boolean {
        return viewDataBinding.roomDetailLayout.visibility == View.VISIBLE
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

    override fun initRoomDetailLayout(position: Int) {
        val root = viewDataBinding.roomDetailLayout

        root.setDefaultTopMargin(viewDataBinding.recyclerView.top + root.getBackgroundPaddingTop())
        root.setBackgroundVisible(false)
        root.setScale(root.getMinScale())
        root.onEventListener = object : OnEventListener {
            override fun onBackClick() {
            }

            override fun onCloseClick() {
                eventListener.onCloseClick()
            }

            override fun onMoreImageClick(roomName: String, roomIndex: Int) {
                eventListener.onMoreImageClick(roomName, roomIndex)
            }

            override fun onVrImageClick(trueVrList: List<TrueVR>) {
                eventListener.onVrImageClick(trueVrList)
            }
        }
    }

    override fun startRoomDetailLayoutAnimation(scaleUp: Boolean) {
        viewDataBinding.roomDetailLayout.startAnimation(scaleUp)
    }

    private val recyclerTouchListener = object : View.OnTouchListener {
        private var moveState: Int = MOVE_STATE_NONE
        private var prevX: Float = 0.toFloat()
        private var prevY: Float = 0.toFloat()
        private var canClickAble = true

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (listAdapter.itemCount == 0) return false
            if (event == null) return false
            if (viewDataBinding.recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) return false

            setRecyclerScrollEnabled()

            @Suppress("DEPRECATION")
            when (event.action and MotionEventCompat.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.x
                    prevY = event.y
                    viewDataBinding.roomDetailLayout.setStartScale()
                    canClickAble = true

                    moveState = MOVE_STATE_NONE
                }

                MotionEvent.ACTION_UP -> run {
                    canClickAble.runTrue {
                        canClickAble = getClickAble(prevX, prevY, event.x, event.y)
                    }

                    if (canClickAble) {
                        moveState = MOVE_STATE_NONE
                        startRoomDetailLayoutAnimation(true)
                        return true
                    }

                    when (moveState) {
                        MOVE_STATE_END_ANIMATION, MOVE_STATE_VIEWPAGER -> {
                            return false
                        }

                        else -> {
                            viewDataBinding.roomDetailLayout.setAfterScale()
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    moveState = MOVE_STATE_NONE
                    canClickAble = true
                }

                MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    canClickAble.runTrue {
                        canClickAble = getClickAble(prevX, prevY, x, y)
                    }

                    when (moveState) {
                        MOVE_STATE_NONE -> {
                            when {
                                Math.abs(x - prevX) == Math.abs(y - prevY) -> {
                                    // 안 움직이거나 x, y 정확히 대각선 일때
                                }

                                Math.abs(x - prevX) * MOVE_CALIBRATE_VALUE > Math.abs(y - prevY) -> {
                                    // x 축으로 이동한 경우.
                                    moveState = MOVE_STATE_VIEWPAGER

                                    if (viewDataBinding.roomDetailLayout.visibility == View.VISIBLE) {
                                        viewDataBinding.roomDetailLayout.visibility = View.INVISIBLE
                                    }
                                }

                                else -> {
                                    // y축으로 이동한 경우.
                                    moveState = MOVE_STATE_SCROLL

                                    val toScale = viewDataBinding.roomDetailLayout.addScale(prevY - y)
                                    if (toScale > viewDataBinding.roomDetailLayout.getMinScale()) {
                                        viewDataBinding.roomDetailLayout.visibility = View.VISIBLE
                                    } else {
                                        viewDataBinding.roomDetailLayout.visibility = View.INVISIBLE
                                    }

                                    if (viewDataBinding.roomDetailLayout.getNeedAnimation()) {
                                        moveState = MOVE_STATE_START_ANIMATION
                                    }
                                }
                            }
                        }

                        MOVE_STATE_SCROLL -> {
                            val toScale = viewDataBinding.roomDetailLayout.addScale(prevY - y)
                            if (toScale > viewDataBinding.roomDetailLayout.getMinScale()) {
                                viewDataBinding.roomDetailLayout.visibility = View.VISIBLE
                            } else {
                                viewDataBinding.roomDetailLayout.visibility = View.INVISIBLE
                            }

                            if (viewDataBinding.roomDetailLayout.getNeedAnimation()) {
                                moveState = MOVE_STATE_START_ANIMATION
                            }
                        }

                        MOVE_STATE_VIEWPAGER -> {
                            if (viewDataBinding.roomDetailLayout.visibility == View.VISIBLE) {
                                viewDataBinding.roomDetailLayout.visibility = View.INVISIBLE
                            }
                        }

                        MOVE_STATE_END_ANIMATION -> {
                            return true
                        }

                        MOVE_STATE_START_ANIMATION -> {
                            moveState = MOVE_STATE_END_ANIMATION

                            viewDataBinding.roomDetailLayout.setAfterScale()
                            return true
                        }
                    }
                }
            }

            return false
        }
    }

    private fun getClickAble(preX: Float, preY: Float, eventX: Float, eventY: Float): Boolean {
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        val x = (preX - eventX).toInt()
        val y = (preY - eventY).toInt()
        val distance = Math.sqrt((x * x + y * y).toDouble()).toInt()

        return distance < touchSlop
    }
}