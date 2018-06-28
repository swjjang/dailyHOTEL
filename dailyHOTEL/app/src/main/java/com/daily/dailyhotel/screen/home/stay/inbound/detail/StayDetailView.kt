package com.daily.dailyhotel.screen.home.stay.inbound.detail

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.widget.NestedScrollView
import android.transition.Transition
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import com.daily.base.BaseDialogView
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ScreenUtils
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotNullTrueElseNullFalse
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.view.*
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.facebook.imagepipeline.image.ImageInfo
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.*
import com.twoheart.dailyhotel.util.EdgeEffectColor
import com.twoheart.dailyhotel.widget.AlphaTransition
import io.reactivex.Completable
import io.reactivex.Observable
import java.text.DecimalFormat
import java.util.*

class StayDetailView(activity: StayDetailActivity, listener: StayDetailInterface.OnEventListener)//
    : BaseDialogView<StayDetailInterface.OnEventListener, ActivityStayDetailDataBinding>(activity, listener), StayDetailInterface.ViewInterface {

    private var tabLayoutShowAnimator: ObjectAnimator? = null
    private var tabLayoutHideAnimator: ObjectAnimator? = null

    private var scrollLayoutDataBinding: DailyViewStayDetailScrollDataBinding? = null
    private var conciergeLayoutDataBinding: LayoutGourmetDetailConciergeDataBinding? = null

    override fun setContentView(viewDataBinding: ActivityStayDetailDataBinding) {
        initToolbar(viewDataBinding)
        initTabLayout(viewDataBinding)

        viewDataBinding.showRoomTextView.setOnClickListener { eventListener.onShowRoomClick() }
        hideWishTooltip()
        viewDataBinding.wishTooltipView.setOnClickListener { eventListener.onHideWishTooltipClick() }
        viewDataBinding.topButtonImageView.setOnClickListener { scrollTop() }
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityStayDetailDataBinding) {
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

    override fun initializedScrollLayout() {
        if (scrollLayoutDataBinding == null) {
            scrollLayoutDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_stay_detail_scroll_data, viewDataBinding.scrollLayout, true)
            conciergeLayoutDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_gourmet_detail_concierge_data, viewDataBinding.scrollLayout, true)

            initScrollView(viewDataBinding, scrollLayoutDataBinding!!)
            initEmptyView(viewDataBinding, scrollLayoutDataBinding!!)
            initRoomFilter(viewDataBinding, scrollLayoutDataBinding!!)

            setConciergeInformation()
        }
    }

    private fun initRoomFilter(viewDataBinding: ActivityStayDetailDataBinding, scrollLayoutDataBinding: DailyViewStayDetailScrollDataBinding) {
        viewDataBinding.roomFilterView.setOnDailyDetailRoomFilterListener(object : DailyDetailRoomFilterContentsView.OnDailyDetailRoomFilterListener {
            override fun onSelectedBedTypeFilter(selected: Boolean, bedType: String) {
                eventListener.onSelectedBedTypeFilter(selected, bedType)
            }

            override fun onSelectedFacilitiesFilter(selected: Boolean, facilities: String) {
                eventListener.onSelectedFacilitiesFilter(selected, facilities)
            }

            override fun onCloseClick() {
                eventListener.onCloseRoomFilterClick()
            }

            override fun onResetClick() {
                eventListener.onResetRoomFilterClick()
            }

            override fun onConfirmClick() {
                eventListener.onConfirmRoomFilterClick()
            }
        })

        scrollLayoutDataBinding.roomInformationView.setRoomInformationListener(object : DailyDetailRoomInformationView.OnDailyDetailRoomInformationListener {
            override fun onCalendarClick() {
                eventListener.onCalendarClick()
            }

            override fun onRoomFilterClick() {
                eventListener.onRoomFilterClick()
            }

            override fun onAveragePriceClick() {
                eventListener.onPriceTypeClick(StayDetailPresenter.PriceType.AVERAGE)
            }

            override fun onTotalPriceClick() {
                eventListener.onPriceTypeClick(StayDetailPresenter.PriceType.TOTAL)
            }

            override fun onRoomClick(room: Room) {
                eventListener.onRoomClick(room)
            }

            override fun onMoreRoomsClick(expanded: Boolean) {
                eventListener.onMoreRoomClick(expanded)
            }
        })

        viewDataBinding.stickyRoomFilterView.setRoomFilterListener(object : DailyDetailRoomFilterView.OnDailyDetailRoomFilterListener {
            override fun onCalendarClick() {
                eventListener.onCalendarClick()
            }

            override fun onRoomFilterClick() {
                eventListener.onRoomFilterClick()
            }
        })
    }

    override fun setInitializedLayout(name: String?, url: String?) {
        setStayName(name)

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
            viewDataBinding.transImageView.setImageURI(url)
        }
    }

    private fun initScrollView(viewDataBinding: ActivityStayDetailDataBinding, scrollLayoutDataBinding: DailyViewStayDetailScrollDataBinding) {
        val toolbarHeight = getDimensionPixelSize(R.dimen.toolbar_height)
        val tabLayoutHeight = ScreenUtils.dpToPx(context, 41.0)
        val stickyTopHeight = ScreenUtils.dpToPx(context, 69.0)
        val stickyHeight = ScreenUtils.dpToPx(context, 58.0)

        var previousInformationPosition = -1

        viewDataBinding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (viewDataBinding.scrollLayout.childCount < 2) {
                viewDataBinding.toolbarView.visibility = View.GONE
                return@OnScrollChangeListener
            }

            val titleLayout = viewDataBinding.scrollLayout.getChildAt(1)

            if (titleLayout.y - toolbarHeight > scrollY) {
                viewDataBinding.toolbarView.hideAnimation()
            } else {
                viewDataBinding.toolbarView.showAnimation()
            }

            viewDataBinding.fakeVRImageView.isEnabled = scrollY <= toolbarHeight

            val targetY = scrollY + toolbarHeight + tabLayoutHeight
            var scrollInformationPosition = 0

            if (scrollLayoutDataBinding.roomInformationTopLineView.y >= targetY) {
                hideTabLayout()
                hideRoomDetailButton()

                scrollInformationPosition = 0

            } else {
                showTabLayout()

                viewDataBinding.roomInformationTextView.isSelected = true
                viewDataBinding.stayInformationTextView.isSelected = false

                scrollInformationPosition = 1
            }

            if (scrollLayoutDataBinding.roomInformationView.y + stickyTopHeight >= targetY) {
                hideRoomFilterLayout()
                hideRoomDetailButton()
            } else {
                val transitionY = targetY + stickyHeight

                if (scrollLayoutDataBinding.roomInformationView.bottom >= transitionY) {
                    showRoomFilterLayout()
                    translationRoomFilterLayout(0.0f)

                    viewDataBinding.roomInformationTextView.isSelected = true
                    viewDataBinding.stayInformationTextView.isSelected = false

                    scrollInformationPosition = 1

                    hideRoomDetailButton()
                } else {
                    translationRoomFilterLayout((scrollLayoutDataBinding.roomInformationView.bottom - transitionY).toFloat())

                    if (transitionY - scrollLayoutDataBinding.roomInformationView.bottom < stickyHeight) {
                        viewDataBinding.roomInformationTextView.isSelected = true
                        viewDataBinding.stayInformationTextView.isSelected = false

                        scrollInformationPosition = 1

                        hideRoomDetailButton()
                    } else {
                        viewDataBinding.roomInformationTextView.isSelected = false
                        viewDataBinding.stayInformationTextView.isSelected = true

                        scrollInformationPosition = 2

                        showRoomDetailButton()
                    }
                }
            }

            (previousInformationPosition != scrollInformationPosition).runTrue {
                previousInformationPosition = scrollInformationPosition

                when (scrollInformationPosition) {
                    0 -> eventListener.onScrolledBaseInformation()
                    1 -> eventListener.onScrolledRoomInformation()
                    2 -> eventListener.onScrolledStayInformation()
                }
            }
        })

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge))
    }

    private fun initEmptyView(viewDataBinding: ActivityStayDetailDataBinding, scrollLayoutDataBinding: DailyViewStayDetailScrollDataBinding) {
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

                    viewDataBinding.scrollTopRoundView.alpha = 1.0f
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
            viewDataBinding.scrollTopRoundView.transitionName = getString(R.string.transition_round_top_view)

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

    private fun initTabLayout(viewDataBinding: ActivityStayDetailDataBinding) {
        viewDataBinding.roomInformationTextView.setOnClickListener { if (!it.isSelected) eventListener.onRoomInformationClick() }
        viewDataBinding.stayInformationTextView.setOnClickListener { if (!it.isSelected) eventListener.onStayInformationClick() }
    }

    @Synchronized
    override fun showTabLayout() {
        if (tabLayoutShowAnimator != null || viewDataBinding.tabLayout.visibility == View.VISIBLE) {
            return
        }

        tabLayoutHideAnimator?.cancel()
        tabLayoutHideAnimator = null

        tabLayoutShowAnimator = ObjectAnimator.ofFloat(viewDataBinding.tabLayout, View.ALPHA, viewDataBinding.tabLayout.alpha, 1.0f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

        tabLayoutShowAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                tabLayoutShowAnimator?.removeAllListeners()
                tabLayoutShowAnimator = null
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                viewDataBinding.tabLayout.visibility = View.VISIBLE
            }
        })

        tabLayoutShowAnimator?.start()
    }

    @Synchronized
    override fun hideTabLayout() {
        if (tabLayoutHideAnimator != null || viewDataBinding.tabLayout.visibility == View.INVISIBLE) {
            return
        }

        tabLayoutShowAnimator?.cancel()
        tabLayoutShowAnimator = null

        tabLayoutHideAnimator = ObjectAnimator.ofFloat(viewDataBinding.tabLayout, View.ALPHA, viewDataBinding.tabLayout.alpha, 0.0f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

        tabLayoutHideAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                tabLayoutHideAnimator?.removeAllListeners()
                tabLayoutHideAnimator = null

                viewDataBinding.tabLayout.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        tabLayoutHideAnimator?.start()
    }

    private fun showRoomFilterLayout() {
        if (viewDataBinding.stickyRoomFilterView.visibility == View.VISIBLE)
            return

        viewDataBinding.stickyRoomFilterView.visibility = View.VISIBLE
    }

    private fun hideRoomFilterLayout() {
        if (viewDataBinding.stickyRoomFilterView.visibility == View.INVISIBLE)
            return

        viewDataBinding.stickyRoomFilterView.visibility = View.INVISIBLE
        translationRoomFilterLayout(0.0f)
    }

    private fun translationRoomFilterLayout(transitionY: Float) {
        viewDataBinding.stickyRoomFilterView.translationY = transitionY
    }

    private fun showRoomDetailButton() {
        if (viewDataBinding.showRoomGroup.visibility == View.VISIBLE) {
            return
        }

        viewDataBinding.showRoomGroup.visibility = View.VISIBLE
    }

    private fun hideRoomDetailButton() {
        if (viewDataBinding.showRoomGroup.visibility == View.GONE) {
            return
        }

        viewDataBinding.showRoomGroup.visibility = View.GONE
    }

    override fun setWishCount(count: Int) {
        val wishCountText = when {
            count <= 0 -> null

            count > 9999 -> {
                val wishCount = count / 1000

                if (wishCount % 10 == 0)
                    getString(R.string.wishlist_count_over_10_thousand, (wishCount / 10).toString())
                else
                    getString(R.string.wishlist_count_over_10_thousand, (wishCount.toFloat() / 10.0f).toString())
            }

            else -> DecimalFormat("###,##0").format(count)
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

            if (viewDataBinding.fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF)) {
                viewDataBinding.fakeToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, DailyToolbarView.MenuItem.WISH_OFF) { eventListener.onWishClick() }
            }
        }
    }

    override fun setVRVisible(visible: Boolean) {
        val flag = if (visible) View.VISIBLE else View.GONE

        viewDataBinding.vrGroup.visibility = flag
        viewDataBinding.fakeVRImageView.setOnClickListener { eventListener.onTrueVRClick() }
    }

    override fun setMoreImageVisible(visible: Boolean) {
    }

    override fun setImageList(imageList: List<DetailImageInformation>) {
        viewDataBinding.imageLoopView.setImageList(imageList)
    }

    private fun setStayName(name: String?) {
        viewDataBinding.baseInformationView.setNameText(name)
    }

    override fun setBaseInformation(baseInformation: StayDetail.BaseInformation, nightsEnabled: Boolean, soldOut: Boolean) {
        viewDataBinding.baseInformationView.apply {
            setGradeName(baseInformation.grade.getName(context))
            setRewardsVisible(baseInformation.provideRewardSticker)
            setNameText(baseInformation.name)

            if (soldOut) {
                setPrice(getString(R.string.label_soldout))
                setPriceWonVisible(false)
                setNightsEnabled(false)
            } else {
                setPrice(DecimalFormat("###,##0").format(baseInformation.discount))
                setPriceWonVisible(true)
                setNightsEnabled(nightsEnabled)
            }

            setAwardsVisible(baseInformation.awards.letNotNullTrueElseNullFalse { setAwardsTitle(it.title) })
            setAwardsClickListener(View.OnClickListener { eventListener.onTrueAwardsClick() })
        }
    }

    override fun setLowestPriceRoom(discountAveragePrice: Int, nightsEnabled: Boolean) {
        viewDataBinding.baseInformationView.setPrice(DecimalFormat("###,##0").format(discountAveragePrice))
        viewDataBinding.baseInformationView.setNightsEnabled(nightsEnabled)
    }

    override fun setTrueReviewInformationVisible(visible: Boolean) {
        scrollLayoutDataBinding?.let {
            val flag = if (visible) View.VISIBLE else View.GONE

            it.trueReviewViewTopLineView.visibility = flag
            it.trueReviewView.visibility = flag
        }
    }

    override fun setTrueReviewInformation(trueReviewInformation: StayDetail.TrueReviewInformation) {
        scrollLayoutDataBinding?.trueReviewView?.let {

            if (trueReviewInformation.ratingPercent > 0 || trueReviewInformation.ratingCount > 0) {
                it.setSatisfactionVisible(true)
                it.setSatisfaction(trueReviewInformation.ratingPercent, trueReviewInformation.ratingCount)
            } else {
                it.setSatisfactionVisible(false)
            }

            it.setPreviewTrueReviewVisible(trueReviewInformation.review.letNotNullTrueElseNullFalse { primaryReview ->
                it.setPreviewTrueReview(primaryReview.comment, primaryReview.score.toString(), primaryReview.userId, primaryReview.createdAt)
            })

            if (trueReviewInformation.reviewTotalCount > 0) {
                it.setShowTrueReviewButtonVisible(true)
                it.setShowTrueReviewButtonText(trueReviewInformation.reviewTotalCount)
                it.setTrueReviewClickListener(View.OnClickListener { eventListener.onTrueReviewClick() })
            } else {
                it.setShowTrueReviewButtonVisible(false)
            }
        }
    }

    override fun setBenefitInformationVisible(visible: Boolean) {
        scrollLayoutDataBinding?.let {
            val flag = if (visible) View.VISIBLE else View.GONE

            it.businessBenefitTopLineView.visibility = flag
            it.businessBenefitView.visibility = flag
        }
    }

    override fun setBenefitInformation(benefitInformation: StayDetail.BenefitInformation) {
        scrollLayoutDataBinding?.businessBenefitView?.let {
            if (benefitInformation.title.isTextEmpty() && !benefitInformation.contentList.isNotNullAndNotEmpty()) {
                it.setBenefitVisible(false)
            } else {
                it.setBenefitVisible(true)

                if (benefitInformation.title.isTextEmpty()) {
                    it.setTitleVisible(false)
                } else {
                    it.setTitleVisible(true)
                    it.setTitleText(benefitInformation.title)
                }

                if (benefitInformation.contentList.isNotNullAndNotEmpty()) {
                    it.setContentsVisible(true)
                    it.setContents(benefitInformation.contentList)
                } else {
                    it.setContentsVisible(false)
                }
            }

            if (benefitInformation.coupon != null && benefitInformation.coupon!!.couponDiscount > 0) {
                it.setCouponButtonVisible(true)

                val couponPrice = DailyTextUtils.getPriceFormat(context, benefitInformation.coupon!!.couponDiscount, false)

                if (!benefitInformation.coupon!!.isDownloaded) {
                    setCouponButtonEnabled(true)
                    setCouponButtonText(context.getString(R.string.label_detail_download_coupon, couponPrice), true)
                } else {
                    setCouponButtonEnabled(false)
                    setCouponButtonText(context.getString(R.string.label_detail_complete_coupon_download, couponPrice), false)
                }

                it.setCouponButtonClickListener(View.OnClickListener { eventListener.onDownloadCouponClick() })
            } else {
                it.setCouponButtonVisible(false)
            }
        }
    }

    override fun setCouponButtonText(text: String, iconVisible: Boolean) {
        scrollLayoutDataBinding?.businessBenefitView?.setCouponButtonText(text, iconVisible)
    }

    override fun setCouponButtonEnabled(enabled: Boolean) {
        scrollLayoutDataBinding?.businessBenefitView?.setCouponButtonEnabled(enabled)
    }

    override fun setPriceAverageTypeVisible(visible: Boolean) {
        scrollLayoutDataBinding?.roomInformationView?.setPriceAverageTypeVisible(visible)
    }

    override fun setPriceAverageType(isAverageType: Boolean) {
        scrollLayoutDataBinding?.roomInformationView?.setPriceAverageType(isAverageType)
    }

    override fun setRoomFilterInformation(calendarText: CharSequence, roomFilterCount: Int) {
        scrollLayoutDataBinding?.roomInformationView?.let {
            it.setCalendar(calendarText)
            it.setRoomFilterCount(roomFilterCount)
        }

        viewDataBinding.stickyRoomFilterView.setCalendar(calendarText)
        viewDataBinding.stickyRoomFilterView.setRoomFilterCount(roomFilterCount)
    }

    override fun setEmptyRoomText(text: String?) {
        scrollLayoutDataBinding?.roomInformationView?.setEmptyRoomText(text)
    }

    override fun setEmptyRoomVisible(visible: Boolean, hasFilter: Boolean) {
        scrollLayoutDataBinding?.roomInformationView?.setEmptyRoomVisible(visible, hasFilter)
        viewDataBinding.stickyRoomFilterView.setRoomFilterVisible(if (hasFilter) true else !visible)
    }

    override fun setRoomList(roomList: List<Room>?) {
        scrollLayoutDataBinding?.roomInformationView?.setRoomList(roomList)
    }

    override fun setRoomActionButtonVisible(visible: Boolean) {
        scrollLayoutDataBinding?.roomInformationView?.setActionButtonVisible(visible)
    }

    override fun setRoomActionButtonText(text: String, leftResourceId: Int, rightResourceId: Int, drawablePadding: Int,
                                         textColorResourceId: Int, backgroundResourceId: Int) {
        scrollLayoutDataBinding?.roomInformationView?.setActionButton(text, leftResourceId, rightResourceId, drawablePadding, textColorResourceId, backgroundResourceId)
    }

    override fun setDailyCommentVisible(visible: Boolean) {
        scrollLayoutDataBinding?.dailyCommentView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setDailyComment(commentList: List<String>) {
        scrollLayoutDataBinding?.dailyCommentView?.setComments(commentList)
    }

    override fun setFacilities(roomCount: Int, facilities: List<FacilitiesPictogram>?) {
        scrollLayoutDataBinding?.facilitiesView?.let {
            if (roomCount <= 0 && !facilities.isNotNullAndNotEmpty()) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE

                if (roomCount > 0) {
                    it.setRoomCountVisible(true)
                    it.setRoomCount(roomCount)
                } else {
                    it.setRoomCountVisible(false)
                }

                it.setFacilities(facilities)
            }
        }
    }

    override fun setAddressInformationVisible(visible: Boolean) {
        scrollLayoutDataBinding?.addressView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setAddressInformation(addressInformation: StayDetail.AddressInformation) {
        scrollLayoutDataBinding?.addressView?.let {
            it.setAddressText(addressInformation.address)
            it.setOnAddressClickListener(object : DailyDetailAddressView.OnAddressClickListener {
                override fun onMapClick() {
                    eventListener.onMapClick()
                }

                override fun onCopyAddressClick() {
                    eventListener.onClipAddressClick()
                }

                override fun onSearchAddressClick() {
                    eventListener.onNavigatorClick()
                }
            })
        }
    }

    override fun setCheckTimeInformationVisible(visible: Boolean) {
        scrollLayoutDataBinding?.checkTimeInformationView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setCheckTimeInformation(checkTimeInformation: StayDetail.CheckTimeInformation) {
        scrollLayoutDataBinding?.checkTimeInformationView?.let {
            it.setCheckTimeText(checkTimeInformation.checkIn, checkTimeInformation.checkOut)

            if (checkTimeInformation.description.isNotNullAndNotEmpty()) {
                it.setInformationVisible(true)
                it.setInformation(checkTimeInformation.description)
            } else {
                it.setInformationVisible(false)
            }
        }
    }

    override fun setDetailInformationVisible(visible: Boolean) {
        scrollLayoutDataBinding?.detailInformationView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setDetailInformation(detailInformation: StayDetail.DetailInformation?, breakfastInformation: StayDetail.BreakfastInformation?) {
        scrollLayoutDataBinding?.let {
            it.detailInformationView.setInformation(detailInformation?.itemList)
            it.detailInformationView.setBreakfastInformation(breakfastInformation)
        }
    }

    override fun setCancellationAndRefundPolicyVisible(visible: Boolean) {
        scrollLayoutDataBinding?.let {
            val flag = if (visible) View.VISIBLE else View.GONE

            it.refundInformationTopLineView.visibility = flag
            it.refundInformationView.visibility = flag
        }
    }

    override fun setCancellationAndRefundPolicy(refundInformation: StayDetail.RefundInformation?, hasNRDRoom: Boolean) {
        scrollLayoutDataBinding?.refundInformationView?.setInformation(refundInformation)
    }

    override fun setCheckInformationVisible(visible: Boolean) {
        scrollLayoutDataBinding?.let {
            val flag = if (visible) View.VISIBLE else View.GONE

            it.checkInformationTopLineView.visibility = flag
            it.checkInformationView.visibility = flag
        }
    }

    override fun setCheckInformation(checkInformation: StayDetail.CheckInformation) {
        scrollLayoutDataBinding?.checkInformationView?.setInformation(checkInformation)
    }

    override fun setRewardVisible(visible: Boolean) {
        scrollLayoutDataBinding?.let {
            if (visible) {
                it.rewardCardLayout.visibility = View.VISIBLE
                it.conciergeTopLineView.layoutParams.height = ScreenUtils.dpToPx(context, 1.0)
            } else {
                it.rewardCardLayout.visibility = View.GONE
                it.conciergeTopLineView.layoutParams.height = ScreenUtils.dpToPx(context, 12.0)
            }

            it.conciergeTopLineView.requestLayout()
        }
    }

    override fun setRewardMemberInformation(titleText: String, optionText: String?, nights: Int, descriptionText: String?) {
        scrollLayoutDataBinding?.rewardCardView?.let {
            it.setGuideVisible(true)
            it.setOnGuideClickListener { eventListener.onRewardGuideClick() }

            if (optionText.isTextEmpty()) {
                it.setOptionVisible(false)
            } else {
                it.setOptionVisible(true)
                it.setOptionText(optionText)
                it.setOnOptionClickListener { eventListener.onRewardClick() }
            }

            it.setRewardTitleText(titleText)
            it.setDescriptionText(descriptionText)
            it.setStickerCount(nights)
        }
    }


    override fun setRewardNonMemberInformation(titleText: String, optionText: String?, campaignFreeNights: Int, descriptionText: String) {
        scrollLayoutDataBinding?.rewardCardView?.run {
            setGuideVisible(true)
            setOnGuideClickListener { eventListener.onRewardGuideClick() }

            if (optionText.isTextEmpty()) {
                setOptionVisible(false)
            } else {
                setOptionVisible(true)
                setOptionText(optionText)
                setOnOptionClickListener { eventListener.onLoginClick() }
            }

            setRewardTitleText(titleText)
            setDescriptionText(descriptionText)
            setCampaignFreeStickerCount(campaignFreeNights)
        }
    }

    override fun startRewardStickerAnimation() {
        scrollLayoutDataBinding?.let {
            (it.rewardCardLayout.visibility == View.VISIBLE).runTrue { it.rewardCardView.startCampaignStickerAnimation() }
        }
    }

    override fun stopRewardStickerAnimation() {
        scrollLayoutDataBinding?.let {
            (it.rewardCardLayout.visibility == View.VISIBLE).runTrue { it.rewardCardView.stopCampaignStickerAnimation() }
        }
    }

    private fun setConciergeInformation() {
        conciergeLayoutDataBinding?.let {
            val hour = DailyPreference.getInstance(context).operationTime.split("\\,".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val startHour = hour[0]
            val endHour = hour[1]
            val lunchTimes = DailyRemoteConfigPreference.getInstance(context).remoteConfigOperationLunchTime.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val startLunchTime = lunchTimes[0]
            val endLunchTime = lunchTimes[1]

            it.conciergeTime01TextView.text = getString(R.string.message_consult02, startHour, endHour)
            it.conciergeTime02TextView.text = getString(R.string.message_consult03, startLunchTime, endLunchTime)
            it.conciergeView.setOnClickListener { eventListener.onConciergeClick() }
        }
    }

    override fun scrollTop() {
        viewDataBinding.nestedScrollView.scrollTo(0, 0)
        viewDataBinding.nestedScrollView.smoothScrollTo(0, 0)
    }

    override fun showShareDialog() {
        DataBindingUtil.inflate<DialogShareDataBinding>(LayoutInflater.from(context),
                R.layout.dialog_share_data,
                null,
                false).apply {
            kakaoShareView.setOnClickListener {
                hideSimpleDialog()

                eventListener.onShareKakaoClick()
            }

            copyLinkView.setOnClickListener {
                hideSimpleDialog()

                eventListener.onCopyLinkClick()
            }

            moreShareView.setOnClickListener {
                hideSimpleDialog()

                eventListener.onMoreShareClick()
            }

            closeTextView.setOnClickListener { hideSimpleDialog() }

            showSimpleDialog(root, null, null, true)
        }
    }

    override fun showConciergeDialog(listener: DialogInterface.OnDismissListener) {
        DataBindingUtil.inflate<DialogConciergeDataBinding>(LayoutInflater.from(context), R.layout.dialog_concierge_data, null, false).apply {
            contactUs02Layout.visibility = View.GONE
            contactUs01TextView.setText(R.string.frag_faqs)
            contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0)

            contactUs01Layout.setOnClickListener {
                hideSimpleDialog()

                eventListener.onConciergeFaqClick()
            }

            kakaoDailyView.setOnClickListener {
                hideSimpleDialog()

                eventListener.onConciergeHappyTalkClick()
            }

            callDailyView.setOnClickListener {
                hideSimpleDialog()

                eventListener.onConciergeCallClick()
            }

            closeView.setOnClickListener { hideSimpleDialog() }

            showSimpleDialog(root, null, listener, true)
        }
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
        viewDataBinding.showRoomTextView.text = text
    }

    override fun setActionButtonEnabled(enabled: Boolean) {
        viewDataBinding.showRoomTextView.isEnabled = enabled
    }

    override fun scrollRoomInformation() {
        scrollLayoutDataBinding?.let {
            val toolbarHeight = getDimensionPixelSize(R.dimen.toolbar_height)
            val tabLayoutHeight = ScreenUtils.dpToPx(context, 41.0)

            viewDataBinding.nestedScrollView.abortScrolling()
            viewDataBinding.nestedScrollView.scrollTo(0, it.roomInformationTopLineView.y.toInt() - toolbarHeight - tabLayoutHeight + 1)
        }
    }

    override fun scrollStayInformation() {
        val toolbarHeight = getDimensionPixelSize(R.dimen.toolbar_height)
        val tabLayoutHeight = ScreenUtils.dpToPx(context, 41.0)

        viewDataBinding.nestedScrollView.abortScrolling()

        scrollLayoutDataBinding?.let {
            when {
                it.dailyCommentView.visibility == View.VISIBLE -> {
                    viewDataBinding.nestedScrollView.scrollTo(0, it.dailyCommentView.y.toInt() - toolbarHeight - tabLayoutHeight)
                }

                it.facilitiesView.visibility == View.VISIBLE -> {
                    viewDataBinding.nestedScrollView.scrollTo(0, it.facilitiesView.y.toInt() - toolbarHeight - tabLayoutHeight)
                }

                else -> {
                    viewDataBinding.nestedScrollView.scrollTo(0, it.addressView.y.toInt() - toolbarHeight - tabLayoutHeight)
                }
            }
        }
    }

    override fun showMoreRooms(animated: Boolean): Completable {
        return scrollLayoutDataBinding?.roomInformationView?.showMoreRoom(animated)
                ?: Completable.complete()
    }

    override fun hideMoreRooms() {
        scrollRoomInformation()
        scrollLayoutDataBinding?.roomInformationView?.hideMoreRoom()
    }

    override fun isShowMoreRooms(): Boolean {
        return scrollLayoutDataBinding?.roomInformationView?.isShowMoreRoom() ?: false
    }

    override fun setSelectedRoomFilter(selectedBedTypeSet: LinkedHashSet<String>, selectedFacilitiesSet: LinkedHashSet<String>) {
        viewDataBinding.roomFilterView.setSelectedBedType(selectedBedTypeSet)
        viewDataBinding.roomFilterView.setSelectedFacilities(selectedFacilitiesSet)
    }

    override fun setSelectedRoomFilterCount(selectedRoomFilterCount: Int) {
        viewDataBinding.roomFilterView.setFilterCount(selectedRoomFilterCount)
    }

    override fun showRoomFilter(): Completable {
        return Completable.create {
            ObjectAnimator.ofFloat(viewDataBinding.roomFilterView, View.TRANSLATION_Y, ScreenUtils.getScreenHeight(context).toFloat(), 0.0f).apply {
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        removeAllListeners()
                        it.onComplete()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        viewDataBinding.roomFilterView.visibility = View.VISIBLE
                    }
                })

                start()
            }
        }
    }

    override fun hideRoomFilter(): Completable {
        return Completable.create {
            ObjectAnimator.ofFloat(viewDataBinding.roomFilterView, View.TRANSLATION_Y, 0.0f, ScreenUtils.getScreenHeight(context).toFloat()).apply {
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        removeAllListeners()
                        viewDataBinding.roomFilterView.visibility = View.INVISIBLE
                        it.onComplete()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })

                start()
            }
        }
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