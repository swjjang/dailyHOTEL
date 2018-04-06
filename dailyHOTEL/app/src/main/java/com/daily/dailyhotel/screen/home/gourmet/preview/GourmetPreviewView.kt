package com.daily.dailyhotel.screen.home.gourmet.preview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.text.SpannableStringBuilder
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.daily.base.BaseDialogView
import com.daily.base.util.DailyTextUtils
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityGourmetPreviewDataBinding
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class GourmetPreviewView(activity: GourmetPreviewActivity, listener: GourmetPreviewInterface.OnEventListener)//
    : BaseDialogView<GourmetPreviewInterface.OnEventListener, ActivityGourmetPreviewDataBinding>(activity, listener), GourmetPreviewInterface.ViewInterface {

    private lateinit var simpleDraweeViewList: List<com.facebook.drawee.view.SimpleDraweeView>

    override fun setContentView(viewDataBinding: ActivityGourmetPreviewDataBinding) {

        simpleDraweeViewList = listOf(viewDataBinding.simpleDraweeView01
                , viewDataBinding.simpleDraweeView02
                , viewDataBinding.simpleDraweeView03
                , viewDataBinding.simpleDraweeView04)

        viewDataBinding.popupLayout.setOnClickListener { eventListener.onDetailClick() }
        viewDataBinding.wishTextView.setOnClickListener { eventListener.onWishClick() }
        viewDataBinding.shareKakaoTextView.setOnClickListener { eventListener.onKakaoClick() }
        viewDataBinding.mapTextView.setOnClickListener { eventListener.onMapClick() }
        viewDataBinding.closeView.setOnClickListener { eventListener.onCloseClick() }
    }

    override fun setToolbarTitle(title: String?) {

    }

    override fun setName(name: String?) {
        viewDataBinding.nameTextView.text = name
    }

    override fun setCategory(category: String?, subCategory: String?) {
        viewDataBinding.categoryTextView.text = category
        viewDataBinding.subCategoryTextView.text = subCategory

        val visibility = if (DailyTextUtils.isTextEmpty(subCategory)) View.GONE else View.VISIBLE

        viewDataBinding.subCategoryDotImageView.visibility = visibility
        viewDataBinding.subCategoryTextView.visibility = visibility
    }

    override fun setImages(imageList: Array<kotlin.String>?) {
        setImagesPlaceholder(R.drawable.layerlist_placeholder_s)

        when (imageList?.size) {
            0, null -> {
                setImagesVisibility(arrayOf(View.GONE, View.GONE, View.GONE, View.GONE))
            }

            1 -> {
                setImagesVisibility(arrayOf(View.VISIBLE, View.INVISIBLE, View.GONE, View.GONE))
                setImagesUrl(imageList)
            }

            2 -> {
                setImagesVisibility(arrayOf(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE))
                setImagesUrl(imageList)
            }

            3 -> {
                setImagesVisibility(arrayOf(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE))
                setImagesUrl(imageList)
            }

            else -> {
                setImagesVisibility(arrayOf(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE))
                setImagesUrl(imageList)
            }
        }
    }

    private fun setImagesPlaceholder(resourceId: Int) {
        simpleDraweeViewList.forEach { it.hierarchy.setPlaceholderImage(resourceId) }
    }

    private fun setImagesVisibility(visibilityList: Array<Int>) {
        visibilityList.take(simpleDraweeViewList.size).forEachIndexed { index, visibility -> simpleDraweeViewList[index].visibility = visibility }
    }

    private fun setImagesUrl(imageUris: Array<String>) {
        imageUris.take(simpleDraweeViewList.size).forEachIndexed { index, imageUri -> simpleDraweeViewList[index].setImageURI(imageUri) }
    }

    override fun setMenuInformation(menuTypeCountText: String?, rangePriceVisible: Boolean, rangePriceText: String?) {
        viewDataBinding.productCountTextView.text = menuTypeCountText
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
                , PropertyValuesHolder.ofFloat(View.SCALE_X, 0.7f, 1.0f)
                , PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.7f, 1.0f))
        val alphaObjectAnimator = ObjectAnimator.ofFloat(viewDataBinding.root, View.ALPHA, 0.0f, 1.0f)
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleObjectAnimator, alphaObjectAnimator)
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

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
                , PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.7f)
                , PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.7f))
        val alphaObjectAnimator = ObjectAnimator.ofFloat(viewDataBinding.root, View.ALPHA, 1.0f, 0.0f)
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleObjectAnimator, alphaObjectAnimator)
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

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