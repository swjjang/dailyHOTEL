package com.daily.dailyhotel.screen.copy.kotlin

import android.view.View
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.entity.DetailImageInformation
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayPreviewDataBinding

class StayPreviewView(activity: StayPreviewActivity, listener: StayPreviewInterface.OnEventListener)//
    : BaseDialogView<StayPreviewInterface.OnEventListener, ActivityStayPreviewDataBinding>(activity, listener), StayPreviewInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityStayPreviewDataBinding) {
        viewDataBinding.popupLayout.setOnClickListener { eventListener.onDetailClick() }
        viewDataBinding.closeView.setOnClickListener { eventListener.onBackClick() }
    }

    override fun setToolbarTitle(title: String?) {

    }

    override fun setName(name: String?) {
        viewDataBinding.nameTextView.text = name
    }

    override fun setCategory(category: String?, reward: Boolean) {
        viewDataBinding.categoryTextView.text = category

        val visibility = if (reward) View.VISIBLE else View.GONE

        viewDataBinding.dotImageView.visibility = visibility
        viewDataBinding.rewardTextView.visibility = visibility
    }

    override fun setImages(imageList: List<DetailImageInformation>?) {
        setImagesPlaceholder()

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

    private fun setImagesPlaceholder() {
        viewDataBinding.simpleDraweeView01.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder_s)
        viewDataBinding.simpleDraweeView02.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder_s)
        viewDataBinding.simpleDraweeView03.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder_s)
        viewDataBinding.simpleDraweeView04.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder_s)
    }

    private fun setImagesVisibility(image01Visibility: Int = View.GONE,//
                                    image02Visibility: Int = View.GONE,//
                                    image03Visibility: Int = View.GONE,//
                                    image04Visibility: Int = View.GONE) {
        viewDataBinding.simpleDraweeView01.visibility = image01Visibility
        viewDataBinding.simpleDraweeView02.visibility = image02Visibility
        viewDataBinding.simpleDraweeView03.visibility = image03Visibility
        viewDataBinding.simpleDraweeView04.visibility = image04Visibility
    }

    private fun setImagesUrl(image01Uri: String? = null,//
                             image02Uri: String? = null,//
                             image03Uri: String? = null,//
                             image04Uri: String? = null) {
        viewDataBinding.simpleDraweeView01.setImageURI(image01Uri)
        viewDataBinding.simpleDraweeView02.setImageURI(image02Uri)
        viewDataBinding.simpleDraweeView03.setImageURI(image03Uri)
        viewDataBinding.simpleDraweeView04.setImageURI(image04Uri)
    }

    override fun setRoomInformation(roomTypeCountText: String?, nightEnabled: Boolean, rangePriceText: String?) {
        viewDataBinding.productCountTextView.text = roomTypeCountText
        viewDataBinding.stayAverageView.visibility = if (nightEnabled) View.VISIBLE else View.GONE
        viewDataBinding.priceTextView.text = rangePriceText
    }

    override fun setReviewInformationVisible(visible: Boolean) {
        viewDataBinding.moreInformationLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setReviewInformation(reviewCountText: String?, wishCountText: String?) {
        viewDataBinding.trueReviewCountTextView.text = reviewCountText;
        viewDataBinding.wishCountTextView.text = wishCountText
    }
}