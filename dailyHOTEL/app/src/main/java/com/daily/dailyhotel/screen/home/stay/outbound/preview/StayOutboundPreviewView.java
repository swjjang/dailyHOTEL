package com.daily.dailyhotel.screen.home.stay.outbound.preview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPreviewDataBinding;

import java.util.List;

public class StayOutboundPreviewView extends BaseDialogView<StayOutboundPreviewView.OnEventListener, ActivityStayOutboundPreviewDataBinding>//
    implements StayOutboundPreviewInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onWishClick();

        void onKakaoClick();

        void onMapClick();

        void onViewDetailClick();

        void onHideAnimation();
    }

    public StayOutboundPreviewView(BaseActivity baseActivity, StayOutboundPreviewView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPreviewDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        ViewGroup.LayoutParams layoutParams = viewDataBinding.popupLayout.getLayoutParams();

        if (isTabletDevice() == false)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 13 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 10 / 15;
        }

        viewDataBinding.popupLayout.setLayoutParams(layoutParams);

        viewDataBinding.popupLayout.setOnClickListener(this);
        viewDataBinding.closeImageView.setOnClickListener(this);
        viewDataBinding.wishTextView.setOnClickListener(this);
        viewDataBinding.shareKakaoTextView.setOnClickListener(this);
        viewDataBinding.mapTextView.setOnClickListener(this);

        // 이미지 연동
        viewDataBinding.imageLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                int imageHeight = ScreenUtils.getRatioHeightType4x3(getViewDataBinding().simpleDraweeView01.getWidth());
                getViewDataBinding().simpleDraweeView01.getLayoutParams().height = imageHeight;
                getViewDataBinding().simpleDraweeView02.getLayoutParams().height = imageHeight;
                getViewDataBinding().simpleDraweeView03.getLayoutParams().height = imageHeight;
                getViewDataBinding().simpleDraweeView04.getLayoutParams().height = imageHeight;
                getViewDataBinding().imageLayout.requestLayout();
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.popupLayout:
                getEventListener().onViewDetailClick();
                break;

            case R.id.closeImageView:
                getEventListener().onBackClick();
                break;

            case R.id.wishTextView:
                getEventListener().onWishClick();
                break;

            case R.id.shareKakaoTextView:
                getEventListener().onKakaoClick();
                break;

            case R.id.mapTextView:
                getEventListener().onMapClick();
                break;
        }
    }

    @Override
    public void setCategory(float rating, boolean provideRewardSticker)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        // 등급
        if ((int) rating == 0)
        {
            getViewDataBinding().categoryTextView.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().categoryTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().categoryTextView.setText(getString(R.string.label_stay_outbound_detail_grade, (int) rating));
        }

        int flag = provideRewardSticker ? View.VISIBLE : View.GONE;

        getViewDataBinding().dotImageView.setVisibility(flag);
        getViewDataBinding().rewardTextView.setVisibility(flag);
    }

    @Override
    public void setStayName(String stayName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().placeNameTextView.setText(stayName);
    }

    @Override
    public void setImages(List<DetailImageInformation> imageList)
    {
        if (getViewDataBinding() == null || imageList == null)
        {
            return;
        }

        final int MAX_COUNT = 4;

        // Image
        SimpleDraweeView[] simpleDraweeViews = new SimpleDraweeView[MAX_COUNT];
        simpleDraweeViews[0] = getViewDataBinding().simpleDraweeView01;
        simpleDraweeViews[1] = getViewDataBinding().simpleDraweeView02;
        simpleDraweeViews[2] = getViewDataBinding().simpleDraweeView03;
        simpleDraweeViews[3] = getViewDataBinding().simpleDraweeView04;

        int size = imageList.size();

        for (int i = 0; i < MAX_COUNT; i++)
        {
            simpleDraweeViews[i].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder_s);

            if (i < size)
            {
                simpleDraweeViews[i].setImageURI(imageList.get(i).getImageMap().smallUrl);
            }
        }
    }

    @Override
    public void setRoomInformation(boolean soldOut, int roomCount, int night, int minPrice, int maxPrice)
    {
        if (soldOut == true)
        {
            getViewDataBinding().productCountTextView.setText(R.string.message_preview_changed_price);

            getViewDataBinding().priceTextView.setVisibility(View.GONE);
            getViewDataBinding().stayAverageView.setVisibility(View.GONE);
        } else
        {
            // N개의 객실타입
            getViewDataBinding().productCountTextView.setText(getString(R.string.label_detail_stay_product_count, roomCount));

            try
            {
                if (night > 1)
                {
                    getViewDataBinding().stayAverageView.setVisibility(View.VISIBLE);
                } else
                {
                    getViewDataBinding().stayAverageView.setVisibility(View.GONE);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

            String priceFormat;

            if (minPrice == maxPrice)
            {
                priceFormat = DailyTextUtils.getPriceFormat(getContext(), maxPrice, false);
            } else
            {
                priceFormat = DailyTextUtils.getPriceFormat(getContext(), minPrice, false) + " ~ " + DailyTextUtils.getPriceFormat(getContext(), maxPrice, false);
            }

            getViewDataBinding().priceTextView.setText(priceFormat);
        }
    }

    @Override
    public void setWish(boolean wish, int wishCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (wish == true)
        {
            getViewDataBinding().wishTextView.setText(R.string.label_preview_remove_wish);
            getViewDataBinding().wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lp_01_wishlist_on, 0, 0);
        } else
        {
            getViewDataBinding().wishTextView.setText(R.string.label_preview_add_wish);
            getViewDataBinding().wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lp_01_wishlist_off, 0, 0);
        }

        if (wishCount <= 0)
        {
            getViewDataBinding().moreInformationLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().moreInformationLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().wishCountTextView.setText(getString(R.string.label_detail_wish_count, DailyTextUtils.formatIntegerToString(wishCount)));
        }
    }

    @Override
    public void showAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().getRoot() //
            , PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1.0f));

        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                objectAnimator.removeAllListeners();

                getViewDataBinding().getRoot().setScaleX(1.0f);
                getViewDataBinding().getRoot().setScaleY(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        objectAnimator.start();
    }

    @Override
    public void hideAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        ObjectAnimator scaleObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().getRoot() //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f));

        ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().getRoot(), "alpha", 1.0f, 0.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleObjectAnimator, alphaObjectAnimator);

        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animatorSet.removeAllListeners();

                getViewDataBinding().getRoot().setScaleX(0.7f);
                getViewDataBinding().getRoot().setScaleY(0.7f);

                getEventListener().onHideAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animatorSet.start();
    }
}
