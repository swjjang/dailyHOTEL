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
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewGourmetCardDataBinding;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;

public class DailyGourmetCardView extends ConstraintLayout
{
    private DailyViewGourmetCardDataBinding mViewDataBinding;

    public DailyGourmetCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyGourmetCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyGourmetCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_gourmet_card_data, this, true);

        setGradientView(mViewDataBinding.gradientBottomView);
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

    public void setImage(String url)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        Util.requestImageResize(getContext(), mViewDataBinding.simpleDraweeView, url);
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

    public void setOnDeleteClickListener(View.OnClickListener onClickListener)
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

    public void setOnWishClickListener(View.OnClickListener onClickListener)
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

    public void setReviewText(int satisfaction, int trueReviewCount)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        String reviewText;

        if (satisfaction > 0 && trueReviewCount > 0)
        {
            reviewText = Integer.toString(satisfaction) + "% (" + getContext().getString(R.string.label_truereview_count, trueReviewCount) + ")";
        } else if (satisfaction > 0)
        {
            reviewText = Integer.toString(satisfaction) + "%";
        } else if (trueReviewCount > 0)
        {
            reviewText = getContext().getString(R.string.label_truereview_count, trueReviewCount);
        } else
        {
            reviewText = null;
        }

        mViewDataBinding.trueReviewTextView.setVisibility(DailyTextUtils.isTextEmpty(reviewText) ? GONE : VISIBLE);
        mViewDataBinding.trueReviewTextView.setText(reviewText);
    }

    public void setNewVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.newGourmetTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setGourmetNameText(String stayName)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.gourmetNameTextView.setText(stayName);
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

    public void setPriceText(int discountPercent, int discountPrice, int price, String couponPrice, int person)
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

        if (person > 1)
        {
            mViewDataBinding.personTextView.setVisibility(VISIBLE);
            mViewDataBinding.personTextView.setText("/" + getContext().getString(R.string.label_persions, person));
        } else
        {
            mViewDataBinding.personTextView.setVisibility(GONE);
        }
    }

    public android.support.v4.util.Pair[] getOptionsCompat()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        android.support.v4.util.Pair[] pairs = new Pair[4];
        pairs[0] = android.support.v4.util.Pair.create(mViewDataBinding.simpleDraweeView, getContext().getString(R.string.transition_place_image));
        pairs[1] = android.support.v4.util.Pair.create(mViewDataBinding.gourmetNameTextView, getContext().getString(R.string.transition_place_name));
        pairs[2] = android.support.v4.util.Pair.create(mViewDataBinding.gradeTextView, getContext().getString(R.string.transition_place_grade));
        pairs[3] = android.support.v4.util.Pair.create(mViewDataBinding.gradientBottomView, getContext().getString(R.string.transition_gradient_bottom_view));

        return pairs;
    }

    private void setGradientView(View view)
    {
        if (view == null)
        {
            return;
        }

        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#E5000000"), Color.parseColor("#99000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.50f, 1.0f};

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        paintDrawable.setShaderFactory(shaderFactory);

        if (VersionUtils.isOverAPI16() == true)
        {
            view.setBackground(paintDrawable);
        } else
        {
            view.setBackgroundDrawable(paintDrawable);
        }
    }
}
