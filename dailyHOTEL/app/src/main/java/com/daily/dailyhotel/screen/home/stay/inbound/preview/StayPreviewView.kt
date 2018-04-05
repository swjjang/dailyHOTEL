package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.text.SpannableStringBuilder
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.entity.DetailImageInformation
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayPreviewDataBinding
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class StayPreviewView(activity: StayPreviewActivity, listener: StayPreviewInterface.OnEventListener)//
    : BaseDialogView<StayPreviewInterface.OnEventListener, ActivityStayPreviewDataBinding>(activity, listener), StayPreviewInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityStayPreviewDataBinding) {
        viewDataBinding.root.visibility = View.INVISIBLE
        viewDataBinding.popupLayout.setOnClickListener { eventListener.onDetailClick() }

        viewDataBinding.wishTextView.setOnClickListener { eventListener.onWishClick() }
        viewDataBinding.shareKakaoTextView.setOnClickListener { eventListener.onKakaoClick() }
        viewDataBinding.mapTextView.setOnClickListener { eventListener.onMapClick() }
        viewDataBinding.closeView.setOnClickListener { eventListener.onBackClick() }
    }

    override fun setToolbarTitle(title: String?) {

    }

    override fun setName(name: String?) {
        viewDataBinding.nameTextView.text = name
    }

    override fun setCategory(category: String?, activeReward: Boolean) {
        viewDataBinding.categoryTextView.text = category

        val visibility = if (activeReward) View.VISIBLE else View.GONE

        viewDataBinding.rewardDotImageView.visibility = visibility
        viewDataBinding.rewardTextView.visibility = visibility
    }

    override fun setImages(imageList: List<DetailImageInformation>?) {
        setImagesPlaceholder(R.drawable.layerlist_placeholder_s)

        when (imageList?.size) {
            0, null -> {
                setImagesVisibility()
                return
            }

            1 -> {
                setImagesVisibility(View.VISIBLE, View.INVISIBLE)
                setImagesUrl(imageList.get(0).imageMap.smallUrl)
            }

            2 -> {
                setImagesVisibility(View.VISIBLE, View.VISIBLE)
                setImagesUrl(imageList.get(0).imageMap.smallUrl, imageList.get(1).imageMap.smallUrl)
            }

            3 -> {
                setImagesVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE)
                setImagesUrl(imageList.get(0).imageMap.smallUrl, imageList.get(1).imageMap.smallUrl, imageList.get(2).imageMap.smallUrl)
            }

            else -> {
                setImagesVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE)
                setImagesUrl(imageList.get(0).imageMap.smallUrl, imageList.get(1).imageMap.smallUrl, imageList.get(2).imageMap.smallUrl, imageList.get(3).imageMap.smallUrl)
            }
        }
    }

    private fun setImagesPlaceholder(resourceId: Int) {
        viewDataBinding.simpleDraweeView01.hierarchy.setPlaceholderImage(resourceId)
        viewDataBinding.simpleDraweeView02.hierarchy.setPlaceholderImage(resourceId)
        viewDataBinding.simpleDraweeView03.hierarchy.setPlaceholderImage(resourceId)
        viewDataBinding.simpleDraweeView04.hierarchy.setPlaceholderImage(resourceId)
    }

    private fun setImagesVisibility(image01Visibility: Int = View.GONE
                                    , image02Visibility: Int = View.GONE
                                    , image03Visibility: Int = View.GONE
                                    , image04Visibility: Int = View.GONE) {
        viewDataBinding.simpleDraweeView01.visibility = image01Visibility
        viewDataBinding.simpleDraweeView02.visibility = image02Visibility
        viewDataBinding.simpleDraweeView03.visibility = image03Visibility
        viewDataBinding.simpleDraweeView04.visibility = image04Visibility
    }

    private fun setImagesUrl(image01Uri: String? = null
                             , image02Uri: String? = null
                             , image03Uri: String? = null
                             , image04Uri: String? = null) {
        viewDataBinding.simpleDraweeView01.setImageURI(image01Uri)
        viewDataBinding.simpleDraweeView02.setImageURI(image02Uri)
        viewDataBinding.simpleDraweeView03.setImageURI(image03Uri)
        viewDataBinding.simpleDraweeView04.setImageURI(image04Uri)
    }

    override fun setRoomInformation(roomTypeCountText: String?, nightEnabled: Boolean, rangePriceVisible: Boolean, rangePriceText: String?) {
        viewDataBinding.productCountTextView.text = roomTypeCountText
        viewDataBinding.stayAverageView.visibility = if (nightEnabled) View.VISIBLE else View.GONE

        viewDataBinding.priceTextView.visibility = if (rangePriceVisible) View.VISIBLE else View.GONE
        viewDataBinding.priceTextView.text = rangePriceText
    }

    override fun setReviewInformationVisible(visible: Boolean) {
        viewDataBinding.moreInformationLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setReviewInformation(reviewCountVisible: Boolean, reviewCountText: SpannableStringBuilder?
                                      , wishCountVisible: Boolean, wishCountText: SpannableStringBuilder?) {

        if (reviewCountVisible) {
            viewDataBinding.trueReviewCountTextView.visibility = View.VISIBLE
            viewDataBinding.wishDotImageView.visibility = if (wishCountVisible) View.VISIBLE else View.GONE
        } else {
            viewDataBinding.trueReviewCountTextView.visibility = View.GONE
            viewDataBinding.wishDotImageView.visibility = View.GONE
        }

        viewDataBinding.trueReviewCountTextView.text = reviewCountText

        viewDataBinding.wishCountTextView.visibility = if (wishCountVisible) View.VISIBLE else View.GONE
        viewDataBinding.wishCountTextView.text = wishCountText
    }

    override fun setWish(wish: Boolean) {
        if (wish) {
            viewDataBinding.wishTextView.setText(R.string.label_preview_remove_wish)
            viewDataBinding.wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lp_01_wishlist_on, 0, 0)
        } else {
            viewDataBinding.wishTextView.setText(R.string.label_preview_add_wish)
            viewDataBinding.wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lp_01_wishlist_off, 0, 0)
        }
    }


    override fun setBookingButtonText(text: String) {
        viewDataBinding.bookingTextView.text = text
    }

    override fun showAnimation(): Completable {
        val scaleObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(viewDataBinding.root
                , PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1.0f)
                , PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1.0f))

        val alphaObjectAnimator = ObjectAnimator.ofFloat(viewDataBinding.root, "alpha", 0.0f, 1.0f)
        val animatorSet = AnimatorSet()

        animatorSet.playTogether(scaleObjectAnimator, alphaObjectAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        return Completable.create {
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animatorSet.removeAllListeners()

                    viewDataBinding.root.scaleX = 1.0f
                    viewDataBinding.root.scaleY = 1.0f

                    it.onComplete()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    viewDataBinding.root.visibility = View.VISIBLE
                }
            })

            animatorSet.start()
        }.subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun hideAnimation(): Completable {
        val scaleObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(viewDataBinding.root
                , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f)
                , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f))

        val alphaObjectAnimator = ObjectAnimator.ofFloat(viewDataBinding.root, "alpha", 1.0f, 0.0f)
        val animatorSet = AnimatorSet()

        animatorSet.playTogether(scaleObjectAnimator, alphaObjectAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        return Completable.create {
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animatorSet.removeAllListeners()

                    it.onComplete()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })

            animatorSet.start()
        }.subscribeOn(AndroidSchedulers.mainThread())
    }
}