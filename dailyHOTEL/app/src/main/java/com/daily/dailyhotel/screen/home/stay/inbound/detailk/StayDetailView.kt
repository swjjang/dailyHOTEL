package com.daily.dailyhotel.screen.home.stay.inbound.detailk;

import android.content.DialogInterface
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.CompoundButton
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.entity.DetailImageInformation
import com.daily.dailyhotel.entity.TrueAwards
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayDetailDataBinding
import com.twoheart.dailyhotel.databinding.ActivityStayDetailkDataBinding
import io.reactivex.Observable

class StayDetailView(activity: StayDetailActivity, listener: StayDetailInterface.OnEventListener)//
    : BaseDialogView<StayDetailInterface.OnEventListener, ActivityStayDetailkDataBinding>(activity, listener), StayDetailInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityStayDetailkDataBinding) {
        initToolbar(viewDataBinding)
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityStayDetailkDataBinding) {
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
    }

    override fun setInitializedLayout(name: String?, url: String?) {

    }

    override fun setTransitionVisible(visible: Boolean) {
    }

    override fun getSharedElementTransition(gradientType: StayDetailActivity.TransGradientType): Observable<Boolean> {
        return Observable.just(true)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setSharedElementTransitionEnabled(enabled: Boolean, gradientType: StayDetailActivity.TransGradientType) {
        if (enabled) {
            viewDataBinding.transImageView.visibility = View.VISIBLE
            viewDataBinding.transGradientBottomView.visibility = View.VISIBLE

            viewDataBinding.transImageView.transitionName = getString(R.string.transition_place_image);
            viewDataBinding.transGradientBottomView.transitionName = getString(R.string.transition_gradient_bottom_view);
            viewDataBinding.imageLoopView.transitionName = getString(R.string.transition_gradient_top_view);

            when (gradientType) {
                StayDetailActivity.TransGradientType.LIST -> viewDataBinding.transGradientBottomView.background = getGradientBottomDrawable();

                StayDetailActivity.TransGradientType.MAP -> viewDataBinding.transGradientBottomView.setBackgroundResource(R.color.black_a28)

                else -> viewDataBinding.transGradientBottomView.background = null
            }
        } else {
            viewDataBinding.transImageView.visibility = View.GONE
            viewDataBinding.transGradientBottomView.visibility = View.GONE
        }
    }

    override fun showWishTooltip() {
    }

    override fun hideWishTooltip() {
    }

    override fun setWishCount(count: Int) {
    }

    override fun setWishSelected(selected: Boolean) {
    }

    override fun setVRVisible(visible: Boolean) {
    }

    override fun setMoreImageVisible(visible: Boolean) {
    }

    override fun setImageList(imageList: List<DetailImageInformation>) {
    }

    override fun setBaseInformation() {
    }

    override fun setTrueAwardsVisible(visible: Boolean) {
    }

    override fun setTrueReview() {
    }

    override fun setBenefit() {
    }

    override fun setRoomFilter() {
    }

    override fun setRoomList() {
    }

    override fun setDailyComment() {
    }

    override fun setAmenities() {
    }

    override fun setAddress() {
    }

    override fun setCheckDateInformation() {
    }

    override fun setDetailInformation() {
    }

    override fun setBreakfastInformation() {
    }

    override fun setCancellationAndRefundPolicy() {
    }

    override fun setWaitingBookingVisible() {
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

    override fun showTrueVRDialog(checkedChangeListener: CompoundButton.OnCheckedChangeListener, positiveListener: View.OnClickListener, onDismissListener: DialogInterface.OnDismissListener) {
    }

    override fun showTrueAwardsDialog(trueAwards: TrueAwards?, onDismissListener: DialogInterface.OnDismissListener) {
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