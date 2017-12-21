package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
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
import com.daily.base.widget.DailyNestedScrollView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewStayCardDataBinding;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;

public class DailyStayListEmptyView extends android.support.v4.widget.NestedScrollView
{
    private DailyViewStayCardDataBinding mViewDataBinding;

    public DailyStayListEmptyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyStayListEmptyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyStayListEmptyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_stay_list_empty_data, this, true);
    }
}
