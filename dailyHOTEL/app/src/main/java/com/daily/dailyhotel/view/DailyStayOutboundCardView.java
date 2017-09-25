package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.ImageMap;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewStayOutboundCardDataBinding;

import java.io.IOException;
import java.text.DecimalFormat;

public class DailyStayOutboundCardView extends ConstraintLayout
{
    DailyViewStayOutboundCardDataBinding mViewDataBinding;

    public DailyStayOutboundCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyStayOutboundCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyStayOutboundCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_stay_outbound_card_data, this, true);
    }

    /**
     * @param benefit null이거나 비어있으면 항목 삭제.
     */
    public void setBenefitText(String benefit)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(benefit) == true)
        {
            mViewDataBinding.benefitTextView.setVisibility(GONE);
            mViewDataBinding.benefitTopLineView.setBackgroundResource(0);
        } else
        {
            mViewDataBinding.benefitTextView.setVisibility(VISIBLE);
            mViewDataBinding.benefitTextView.setText(benefit);
            mViewDataBinding.benefitTopLineView.setBackgroundResource(R.color.default_line_cf0f0f0);
        }
    }

    public void setImage(ImageMap imageMap)
    {
        if (mViewDataBinding == null || imageMap == null)
        {
            return;
        }

        // Image
        mViewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        String url;

        if (ScreenUtils.getScreenWidth(getContext()) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
            {
                url = imageMap.smallUrl;
            } else
            {
                url = imageMap.bigUrl;
            }
        } else
        {
            if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
            {
                url = imageMap.smallUrl;
            } else
            {
                url = imageMap.mediumUrl;
            }
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFailure(String id, Throwable throwable)
            {
                if (throwable instanceof IOException == true)
                {
                    if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
                    {
                        imageMap.bigUrl = null;
                    } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
                    {
                        imageMap.mediumUrl = null;
                    } else
                    {
                        // 작은 이미지를 로딩했지만 실패하는 경우.
                        return;
                    }

                    mViewDataBinding.simpleDraweeView.setImageURI(imageMap.smallUrl);
                }
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
            .setControllerListener(controllerListener).setUri(url).build();

        mViewDataBinding.simpleDraweeView.setController(draweeController);
    }

    public void setStickerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.stickerImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setDeleteVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.deleteImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnDeleteClickListener(OnClickListener onClickListener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.deleteImageView.setOnClickListener(onClickListener);
    }

    public void setWishVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnWishClickListener(OnClickListener onClickListener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setOnClickListener(onClickListener);
    }

    public void setGradeText(String grade)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.gradeTextView.setVisibility(DailyTextUtils.isTextEmpty(grade) ? GONE : VISIBLE);
        mViewDataBinding.gradeTextView.setText(grade);
    }

    public void setVRVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.vrTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setRatingText(float rating)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (rating == 0.0f)
        {
            mViewDataBinding.tripAdvisorLayout.setVisibility(View.GONE);
        } else
        {
            mViewDataBinding.tripAdvisorLayout.setVisibility(View.VISIBLE);
            mViewDataBinding.tripAdvisorRatingBar.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return true;
                }
            });
            mViewDataBinding.tripAdvisorRatingBar.setRating(rating);
            mViewDataBinding.tripAdvisorRatingTextView.setText(getContext().getString(R.string.label_stay_outbound_tripadvisor_rating, Float.toString(rating)));

            // 별등급이 기본이 5개 이기 때문에 빈공간에도 내용이 존재한다.
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mViewDataBinding.tripAdvisorRatingTextView.getLayoutParams();
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 2) - ScreenUtils.dpToPx(getContext(), (5 - (int) Math.ceil(rating)) * 10);
            mViewDataBinding.tripAdvisorRatingTextView.setLayoutParams(layoutParams);
        }
    }

    public void setNewVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.newStayTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setStayNameText(String stayName, String stayEnglishName)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.stayNameTextView.setText(stayName);
        mViewDataBinding.stayEnglishNameTextView.setText(stayEnglishName);
    }

    public void setDistanceVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.distanceTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setDistanceText(double distance)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.distanceTextView.setText(getContext().getString(R.string.label_distance_km, new DecimalFormat("#.#").format(distance)));
    }

    public void setAddressText(String address)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.addressTextView.setText(address);
    }

    public void setPriceVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        int flag = visible ? VISIBLE : GONE;

        mViewDataBinding.discountPercentTextView.setVisibility(flag);
        mViewDataBinding.percentImageView.setVisibility(flag);
        mViewDataBinding.discountPriceTextView.setVisibility(flag);
        mViewDataBinding.discountPriceWonTextView.setVisibility(flag);
        mViewDataBinding.priceTextView.setVisibility(flag);
        mViewDataBinding.couponTextView.setVisibility(flag);
        mViewDataBinding.averageNightsTextView.setVisibility(flag);
    }


    public void setPriceText(int discountPercent, int discountPrice, int price, String couponPrice, boolean nightsEnabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (discountPercent > 0)
        {
            mViewDataBinding.discountPercentTextView.setVisibility(VISIBLE);
            mViewDataBinding.percentImageView.setVisibility(VISIBLE);
            mViewDataBinding.discountPercentTextView.setText(Integer.toString(discountPercent));
        } else
        {
            mViewDataBinding.discountPercentTextView.setVisibility(GONE);
            mViewDataBinding.percentImageView.setVisibility(GONE);
        }

        if (discountPrice > 0)
        {
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            mViewDataBinding.discountPriceTextView.setText(decimalFormat.format(discountPrice));
            mViewDataBinding.discountPriceWonTextView.setVisibility(VISIBLE);
        } else
        {
            mViewDataBinding.discountPriceTextView.setText(R.string.label_soldout);
            mViewDataBinding.discountPriceWonTextView.setVisibility(GONE);
        }

        if (price <= 0 || price <= discountPrice)
        {
            mViewDataBinding.priceTextView.setVisibility(GONE);
        } else
        {
            mViewDataBinding.priceTextView.setPaintFlags(mViewDataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mViewDataBinding.priceTextView.setVisibility(VISIBLE);
            mViewDataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), price, false));
        }

        if (DailyTextUtils.isTextEmpty(couponPrice) == true)
        {
            mViewDataBinding.couponTextView.setVisibility(GONE);
        } else
        {
            mViewDataBinding.couponTextView.setVisibility(VISIBLE);
            mViewDataBinding.couponTextView.setText(couponPrice);
        }

        mViewDataBinding.averageNightsTextView.setVisibility(nightsEnabled ? VISIBLE : GONE);
    }

    public android.support.v4.util.Pair[] getOptionsCompat()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        android.support.v4.util.Pair[] pairs = new Pair[4];
        pairs[0] = android.support.v4.util.Pair.create(mViewDataBinding.simpleDraweeView, getContext().getString(R.string.transition_place_image));
        pairs[1] = android.support.v4.util.Pair.create(mViewDataBinding.stayNameTextView, getContext().getString(R.string.transition_place_name));
        pairs[2] = android.support.v4.util.Pair.create(mViewDataBinding.gradientTopView, getContext().getString(R.string.transition_gradient_top_view));
        pairs[3] = android.support.v4.util.Pair.create(mViewDataBinding.gradientBottomView, getContext().getString(R.string.transition_gradient_bottom_view));

        return pairs;
    }
}
