package com.daily.dailyhotel.screen.home.stay.inbound.detailk

import android.annotation.TargetApi
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.widget.NestedScrollView
import android.transition.Transition
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letReturnTrueElseReturnFalse
import com.daily.dailyhotel.view.DailyDetailEmptyView
import com.daily.dailyhotel.view.DailyToolbarView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.facebook.imagepipeline.image.ImageInfo
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayDetailkDataBinding
import com.twoheart.dailyhotel.databinding.DialogDailyAwardsDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor
import com.twoheart.dailyhotel.widget.AlphaTransition
import io.reactivex.Observable
import java.text.DecimalFormat
import java.util.*

class StayDetailView(activity: StayDetailActivity, listener: StayDetailInterface.OnEventListener)//
    : BaseDialogView<StayDetailInterface.OnEventListener, ActivityStayDetailkDataBinding>(activity, listener), StayDetailInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityStayDetailkDataBinding) {
        initToolbar(viewDataBinding)

        viewDataBinding.nestedScrollView.visibility = View.INVISIBLE
        viewDataBinding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (getViewDataBinding().scrollLayout.childCount < 2) {
                getViewDataBinding().toolbarView.visibility = View.GONE
                return@OnScrollChangeListener
            }

            val titleLayout = getViewDataBinding().scrollLayout.getChildAt(1)
            val TOOLBAR_HEIGHT = getDimensionPixelSize(R.dimen.toolbar_height)

            if (titleLayout.y - TOOLBAR_HEIGHT > scrollY) {
                getViewDataBinding().toolbarView.hideAnimation()
            } else {
                getViewDataBinding().toolbarView.showAnimation()
            }

            getViewDataBinding().fakeVRImageView.isEnabled = scrollY <= TOOLBAR_HEIGHT
        })

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge))

        viewDataBinding.showRoomTextView.setOnClickListener(View.OnClickListener { eventListener.onShowRoomClick() })
        hideWishTooltip()
        viewDataBinding.wishTooltipView.setOnClickListener(View.OnClickListener { eventListener.onHideWishTooltipClick() })

        setEmptyView()
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityStayDetailkDataBinding) {
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
        viewDataBinding.toolbarView.clearMenuItem()
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.WISH_OFF, null) { eventListener.onWishClick() }
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null) { eventListener.onShareClick() }
        viewDataBinding.toolbarView.visibility = View.INVISIBLE

        viewDataBinding.fakeToolbarView.setOnBackClickListener { eventListener.onBackClick() }
        viewDataBinding.fakeToolbarView.clearMenuItem()
        viewDataBinding.fakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.WISH_OFF, null) { eventListener.onWishClick() }
        viewDataBinding.fakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null) { eventListener.onShareClick() }
    }

    override fun setInitializedLayout(name: String?, url: String?) {
        if (url.isTextEmpty()) {
            viewDataBinding.imageLoopView.setLineIndicatorVisible(false)
        } else {
            viewDataBinding.imageLoopView.setLineIndicatorVisible(true)

            val detailImage = DetailImageInformation().apply {
                imageMap = ImageMap().apply {
                    smallUrl = null
                    mediumUrl = url
                    bigUrl = url
                }
            }

            val imageList = ArrayList<DetailImageInformation>()
            imageList.add(detailImage)

            viewDataBinding.imageLoopView.setImageList(imageList)
            viewDataBinding.transImageView.setImageURI(Uri.parse(url))
        }
    }

    private fun setEmptyView() {
        viewDataBinding.detailEmptyView.setOnEventListener(object : DailyDetailEmptyView.OnEventListener {
            override fun onStopMove(event: MotionEvent) {
                viewDataBinding.nestedScrollView.isScrollingEnabled = false

                try {
                    viewDataBinding.imageLoopView.onTouchEvent(event)
                } catch (e: Exception) {
                }
            }

            override fun onHorizontalMove(event: MotionEvent) {
                try {
                    viewDataBinding.imageLoopView.onTouchEvent(event)
                } catch (e: Exception) {
                    event.action = MotionEvent.ACTION_CANCEL
                    event.setLocation(viewDataBinding.imageLoopView.pageScrollX.toFloat(), viewDataBinding.imageLoopView.pageScrollY.toFloat())
                    viewDataBinding.imageLoopView.onTouchEvent(event)
                }
            }

            override fun onVerticalMove(event: MotionEvent) {
                viewDataBinding.nestedScrollView.isScrollingEnabled = true
            }

            override fun onCancelMove(event: MotionEvent) {
                try {
                    viewDataBinding.imageLoopView.onTouchEvent(event)
                } catch (e: Exception) {
                    event.action = MotionEvent.ACTION_CANCEL
                    event.setLocation(viewDataBinding.imageLoopView.pageScrollX.toFloat(), viewDataBinding.imageLoopView.pageScrollY.toFloat())
                    viewDataBinding.imageLoopView.onTouchEvent(event)
                }

                viewDataBinding.nestedScrollView.isScrollingEnabled = true
            }

            override fun onImageClick() {
                eventListener.onImageClick(viewDataBinding.imageLoopView.position)
            }
        })
    }

    override fun setTransitionVisible(visible: Boolean) {
        viewDataBinding.transImageView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        viewDataBinding.transGradientBottomView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    @TargetApi(value = 21)
    override fun getSharedElementTransition(gradientType: StayDetailActivity.TransGradientType): Observable<Boolean> {
        window.sharedElementEnterTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP).apply {
            val inBottomAlphaTransition = AlphaTransition(1.0f, 0.0f, LinearInterpolator()).apply {
                addTarget(getString(R.string.transition_gradient_bottom_view))
            }

            val inTopAlphaTransition = AlphaTransition(0.0f, 1.0f, LinearInterpolator()).apply {
                addTarget(getString(R.string.transition_gradient_top_view))
            }

            addTransition(inBottomAlphaTransition)
            addTransition(inTopAlphaTransition)
        }

        window.sharedElementReturnTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP).apply {
            val outBottomAlphaTransition = AlphaTransition(0.0f, 1.0f, LinearInterpolator()).apply {
                addTarget(getString(R.string.transition_gradient_bottom_view))
            }

            val outTopAlphaTransition = AlphaTransition(1.0f, 0.0f, LinearInterpolator()).apply {
                addTarget(getString(R.string.transition_gradient_top_view))
            }

            addTransition(outBottomAlphaTransition)
            addTransition(outTopAlphaTransition)
            duration = 200
        }

        return Observable.create {
            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {}

                override fun onTransitionEnd(transition: Transition) {
                    setTransitionVisible(false)

                    it.onNext(true)
                    it.onComplete()
                }

                override fun onTransitionCancel(transition: Transition) {}

                override fun onTransitionPause(transition: Transition) {}

                override fun onTransitionResume(transition: Transition) {}
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setSharedElementTransitionEnabled(enabled: Boolean, gradientType: StayDetailActivity.TransGradientType) {
        if (enabled) {
            viewDataBinding.transImageView.visibility = View.VISIBLE
            viewDataBinding.transGradientBottomView.visibility = View.VISIBLE

            viewDataBinding.transImageView.transitionName = getString(R.string.transition_place_image)
            viewDataBinding.transGradientBottomView.transitionName = getString(R.string.transition_gradient_bottom_view)
            viewDataBinding.imageLoopView.transitionName = getString(R.string.transition_gradient_top_view)

            when (gradientType) {
                StayDetailActivity.TransGradientType.LIST -> viewDataBinding.transGradientBottomView.background = getGradientBottomDrawable()

                StayDetailActivity.TransGradientType.MAP -> viewDataBinding.transGradientBottomView.setBackgroundResource(R.color.black_a28)

                else -> viewDataBinding.transGradientBottomView.background = null
            }
        } else {
            viewDataBinding.transImageView.visibility = View.GONE
            viewDataBinding.transGradientBottomView.visibility = View.GONE
        }
    }

    override fun showWishTooltip() {
        viewDataBinding.wishTooltipGroup.visibility = View.VISIBLE
    }

    override fun hideWishTooltip() {
        viewDataBinding.wishTooltipGroup.visibility = View.GONE
    }

    override fun setWishCount(count: Int) {
        val wishCountText: String?

        when {
            count <= 0 -> wishCountText = null

            count > 9999 -> {
                val wishCount = count / 1000

                wishCountText = if (wishCount % 10 == 0)
                    getString(R.string.wishlist_count_over_10_thousand, (wishCount / 10).toString())
                else
                    getString(R.string.wishlist_count_over_10_thousand, (wishCount.toFloat() / 10.0f).toString())
            }

            else -> wishCountText = DecimalFormat("###,##0").format(count)
        }

        when {
            viewDataBinding.toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) ->
                viewDataBinding.toolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_OFF, wishCountText) { eventListener.onWishClick() }

            viewDataBinding.toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON) ->
                viewDataBinding.toolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON, wishCountText) { eventListener.onWishClick() }
        }

        when {
            viewDataBinding.fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) ->
                viewDataBinding.fakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_OFF, wishCountText) { eventListener.onWishClick() }

            viewDataBinding.fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON) ->
                viewDataBinding.fakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, wishCountText) { eventListener.onWishClick() }
        }
    }

    override fun setWishSelected(selected: Boolean) {
        if (selected) {
            if (viewDataBinding.toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF)) {
                viewDataBinding.toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_FILL_ON) { eventListener.onWishClick() }
            }

            if (viewDataBinding.fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF)) {
                viewDataBinding.fakeToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_LINE_ON) { eventListener.onWishClick() }
            }
        } else {
            if (viewDataBinding.toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON)) {
                viewDataBinding.toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON, DailyToolbarView.MenuItem.WISH_OFF) { eventListener.onWishClick() }
            }

            if (viewDataBinding.toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF)) {
                viewDataBinding.toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, DailyToolbarView.MenuItem.WISH_OFF) { eventListener.onWishClick() }
            }
        }
    }

    override fun setVRVisible(visible: Boolean) {
        val flag = if (visible) View.VISIBLE else View.GONE

        viewDataBinding.vrImageView.visibility = flag
        viewDataBinding.fakeVRImageView.visibility = flag
        viewDataBinding.fakeVRImageView.setOnClickListener { v -> eventListener.onTrueVRClick() }
    }

    override fun setMoreImageVisible(visible: Boolean) {
    }

    override fun setImageList(imageList: List<DetailImageInformation>) {
        viewDataBinding.imageLoopView.setImageList(imageList)
    }

    override fun setBaseInformation(baseInformation: StayDetailk.BaseInformation, nightsEnabled: Boolean) {
        viewDataBinding.baseInformationView.setCategoryName(baseInformation.category)
        viewDataBinding.baseInformationView.setRewardsVisible(baseInformation.provideRewardSticker)
        viewDataBinding.baseInformationView.setNameText(baseInformation.name)
        viewDataBinding.baseInformationView.setNightsEnabled(nightsEnabled)

        viewDataBinding.baseInformationView.setAwardsVisible(baseInformation.awards.letReturnTrueElseReturnFalse {
            viewDataBinding.baseInformationView.setAwardsTitle(it.title)
        })
    }

    override fun setTrueReviewInformationVisible(visible: Boolean) {
    }

    override fun setTrueReviewInformation(trueReviewInformation: StayDetailk.TrueReviewInformation) {
    }

    override fun setBenefitInformationVisible(visible: Boolean) {
    }

    override fun setBenefitInformation(benefitInformation: StayDetailk.BenefitInformation) {
    }

    override fun setRoomFilterInformation(checkDateTime: CharSequence, bedTypeFilterList: List<String>?, RoomTypeFilterList: List<String>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRoomList(roomList: List<Room>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRoomPriceType(priceType: StayDetailPresenter.PriceType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDailyCommentVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDailyComment(dailyCommentList: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAmenities(roomCount: Int, Amenities: List<String>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAddressInformationVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAddressInformation(addressInformation: StayDetailk.AddressInformation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCheckTimeInformationVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCheckTimeInformation(checkTimeInformation: StayDetailk.CheckTimeInformation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDetailInformationVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDetailInformation(detailInformation: StayDetailk.DetailInformation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBreakfastInformationVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBreakfastInformation(breakfastInformation: StayDetailk.BreakfastInformation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCancellationAndRefundPolicyVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCancellationAndRefundPolicy(refundInformation: StayDetailk.RefundInformation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCheckInformationVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCheckInformation(checkTimeInformation: StayDetailk.CheckInformation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRewardVisible(visible: Boolean) {
    }

    override fun setRewardMemberInformation(titleText: String, optionText: String, nights: Int, descriptionText: String) {
    }

    override fun setRewardNonMemberInformation(titleText: String, optionText: String, campaignFreeNights: Int, descriptionText: String) {
    }

    override fun startRewardStickerAnimation() {
    }

    override fun stopRewardStickerAnimation() {
    }

    override fun setConciergeInformation() {
    }

    override fun scrollTop() {
    }

    override fun showShareDialog(listener: DialogInterface.OnDismissListener) {
    }

    override fun showWishPopup(myWish: Boolean): Observable<Boolean> {
        return Observable.just(true)
    }

    override fun showConciergeDialog(listener: DialogInterface.OnDismissListener) {
    }

    override fun showVRDialog(checkedChangeListener: CompoundButton.OnCheckedChangeListener,
                              positiveListener: View.OnClickListener,
                              onDismissListener: DialogInterface.OnDismissListener) {
        showSimpleDialog(null, getString(R.string.message_stay_used_data_guide), getString(R.string.label_dont_again)//
                , getString(R.string.dialog_btn_do_continue), getString(R.string.dialog_btn_text_close)//
                , checkedChangeListener, positiveListener//
                , null, null, onDismissListener, true)
    }

    override fun showTrueAwardsDialog(trueAwards: TrueAwards?, onDismissListener: DialogInterface.OnDismissListener) {
        trueAwards?.let {
            val dataBinding = DataBindingUtil.inflate<DialogDailyAwardsDataBinding>(LayoutInflater.from(context),
                    R.layout.dialog_daily_awards_data, null, false)

            dataBinding.awardImageView.setImageResource(R.drawable.img_popup_detail_trueawards)
            dataBinding.awardImageView.controller = Fresco.newDraweeControllerBuilder().setControllerListener(object : BaseControllerListener<ImageInfo>() {
                override fun onFailure(id: String?, throwable: Throwable?) {
                    super.onFailure(id, throwable)

                    dataBinding.awardImageView.setImageResource(R.drawable.img_popup_detail_trueawards)
                }
            }).setUri(it.imageUrl).build()

            dataBinding.awardTitleTextView.text = context.getString(R.string.label_daily_true_awards_popup_title_formet, it.title)
            dataBinding.awardDescriptionTextView.text = it.description
            dataBinding.confirmTextView.setOnClickListener { hideSimpleDialog() }

            showSimpleDialog(dataBinding.root, null, onDismissListener, true)
        }
    }

    override fun setActionButtonText(text: String) {
    }

    override fun setActionButtonEnabled(enabled: Boolean) {
    }


    /**
     * 리스트에서 사용하는것과 동일한다.
     *
     * @return
     */
    private fun getGradientBottomDrawable(): PaintDrawable {
        return PaintDrawable().apply {
            shape = RectShape()
            shaderFactory = object : ShapeDrawable.ShaderFactory() {
                override fun resize(width: Int, height: Int): Shader {
                    val colors = intArrayOf(-0x67000000, 0x66000000, 0x05000000, 0x00000000, 0x00000000)
                    val positions = floatArrayOf(0.0f, 0.33f, 0.81f, 0.91f, 1.0f)

                    return LinearGradient(0f, height.toFloat(), 0f, 0f, colors, positions, Shader.TileMode.CLAMP)
                }
            }
        }
    }
}