package com.daily.dailyhotel.screen.home.stay.inbound.detailk;

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayDetailDataBinding
import io.reactivex.Observable

class StayDetailView(activity: StayDetailActivity, listener: StayDetailInterface.OnEventListener)//
    : BaseDialogView<StayDetailInterface.OnEventListener, ActivityStayDetailDataBinding>(activity, listener), StayDetailInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityStayDetailDataBinding) {
        initToolbar(viewDataBinding)
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityStayDetailDataBinding) {
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
    }

    override fun getSharedElementTransition(gradientType: Int): Observable<Boolean>? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setSharedElementTransitionEnabled(enabled: Boolean, gradientType: StayDetailActivity.TransGradientType) {
        if (enabled) {
            viewDataBinding.transImageView.visibility = View.VISIBLE
            viewDataBinding.transGradientBottomView.visibility = View.VISIBLE
            viewDataBinding.transNameTextView.visibility = View.VISIBLE

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
            viewDataBinding.transNameTextView.visibility = View.GONE
        }
    }

    override fun setInitializedLayout(name: String?, url: String?) {

    }

    override fun setTransitionVisible(visible: Boolean) {
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