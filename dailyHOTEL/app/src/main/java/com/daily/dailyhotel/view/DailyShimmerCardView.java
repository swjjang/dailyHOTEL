package com.daily.dailyhotel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.view.shimmer.Shimmer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewShimmerCardDataBinding;

import java.io.IOException;

public class DailyShimmerCardView extends ConstraintLayout
{
    DailyViewShimmerCardDataBinding mViewDataBinding;
    private Shimmer mShimmer;

    public DailyShimmerCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyShimmerCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyShimmerCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mShimmer = new Shimmer();

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_shimmer_card_data, this, true);
        mViewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

//        setGradientView(mViewDataBinding.gradientBottomView);
    }

    public void setImage(ImageMap imageMap)
    {
        if (mViewDataBinding == null || imageMap == null)
        {
            return;
        }

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

    public void setWishVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setWish(boolean wish)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setVectorImageResource(wish ? R.drawable.vector_list_ic_heart_on : R.drawable.vector_list_ic_heart_off);
    }

    public void setOnWishClickListener(OnClickListener onClickListener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setOnClickListener(onClickListener);
    }


    public void setDividerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.cardTopDividerView.setVisibility(visible ? VISIBLE : GONE);
    }

    public Pair[] getOptionsCompat()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        Pair[] pairs = new Pair[3];
        pairs[0] = Pair.create(mViewDataBinding.simpleDraweeView, getContext().getString(R.string.transition_place_image));
        pairs[1] = Pair.create(mViewDataBinding.gradientTopView, getContext().getString(R.string.transition_gradient_top_view));
        pairs[2] = Pair.create(mViewDataBinding.gradientBottomView, getContext().getString(R.string.transition_gradient_bottom_view));

        return pairs;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void setGradientView(View view)
    {
        if (view == null)
        {
            return;
        }

        // 그라디에이션 만들기.
        final int colors[] = {0x99000000, 0x66000000, 0x05000000, 0x00000000, 0x00000000};
        final float positions[] = {0.0f, 0.33f, 0.81f, 0.91f, 1.0f};

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

    public void startShimmer()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (mShimmer == null)
        {
            mShimmer = new Shimmer();
        }

        if (mShimmer.isAnimating())
        {
            return;
        }

        mShimmer.setSimmerWidth(ScreenUtils.dpToPx(getContext(), 252.5));

        mShimmer.start(mViewDataBinding.shimmerView1);
        mShimmer.start(mViewDataBinding.shimmerView2);
        mShimmer.start(mViewDataBinding.shimmerView3);
    }

    public void cancelShimmer()
    {
        if (mShimmer == null)
        {
            return;
        }

        mShimmer.cancel();
    }
}
