package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.shimmer.Shimmer;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewShimmerCardDataBinding;

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

    public void setDividerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.cardTopDividerView.setVisibility(visible ? VISIBLE : GONE);
    }

    //    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    //    private static void setGradientView(View view)
    //    {
    //        if (view == null)
    //        {
    //            return;
    //        }
    //
    //        // 그라디에이션 만들기.
    //        final int colors[] = {0x99000000, 0x66000000, 0x05000000, 0x00000000, 0x00000000};
    //        final float positions[] = {0.0f, 0.33f, 0.81f, 0.91f, 1.0f};
    //
    //        PaintDrawable paintDrawable = new PaintDrawable();
    //        paintDrawable.setShape(new RectShape());
    //
    //        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory()
    //        {
    //            @Override
    //            public Shader resize(int width, int height)
    //            {
    //                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
    //            }
    //        };
    //
    //        paintDrawable.setShaderFactory(shaderFactory);
    //
    //        if (VersionUtils.isOverAPI16() == true)
    //        {
    //            view.setBackground(paintDrawable);
    //        } else
    //        {
    //            view.setBackgroundDrawable(paintDrawable);
    //        }
    //    }

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

        // Shimmer 의 동작위치를 전체 동일하게 만들기 위한 설정
        mShimmer.setSimmerWidth(ScreenUtils.dpToPx(getContext(), 252.5d));
        mShimmer.setLinearGradientWidth(ScreenUtils.dpToPx(getContext(), 60d));

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
