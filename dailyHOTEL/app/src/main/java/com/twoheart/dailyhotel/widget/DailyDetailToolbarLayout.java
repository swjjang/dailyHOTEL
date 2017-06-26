package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyImageView;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyDetailToolbarLayout extends DailyToolbarLayout
{
    public DailyDetailToolbarLayout(Context context, View toolbar)
    {
        super(context, toolbar);
    }

    @Override
    public void setToolbarMenuClickListener(View.OnClickListener listener)
    {
        View menu1 = mToolbar.findViewById(R.id.menu1View);
        View menu2 = mToolbar.findViewById(R.id.menu2View);
        View menu3TextView = mToolbar.findViewById(R.id.menu3TextView);

        if (menu1 == null || menu2 == null || menu3TextView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        menu1.setOnClickListener(listener);
        menu2.setOnClickListener(listener);
        menu3TextView.setOnClickListener(listener);
    }

    public void setToolbarMenu(int menu1ResId, int menu2ResId, int menu3ResId, String menu3Text)
    {
        ImageView menu1 = (ImageView) mToolbar.findViewById(R.id.menu1View);
        ImageView menu2 = (ImageView) mToolbar.findViewById(R.id.menu2View);
        TextView menu3TextView = (TextView) mToolbar.findViewById(R.id.menu3TextView);

        if (menu1 == null || menu2 == null || menu3TextView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (menu1ResId > 0)
        {
            menu1.setVisibility(View.VISIBLE);
            menu1.setImageResource(menu1ResId);
        } else if (menu1ResId < 0)
        {
            menu1.setVisibility(View.GONE);
            menu1.setImageResource(0);
        } else
        {
            menu1.setVisibility(View.VISIBLE);
        }

        if (menu2ResId > 0)
        {
            menu2.setVisibility(View.VISIBLE);
            menu2.setImageResource(menu2ResId);
        } else if (menu2ResId < 0)
        {
            menu2.setVisibility(View.GONE);
            menu2.setImageResource(0);
        } else
        {
            menu2.setVisibility(View.VISIBLE);
        }

        if (menu3ResId > 0)
        {
            menu3TextView.setVisibility(View.VISIBLE);
            menu3TextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, menu3ResId, 0);
            menu3TextView.setText(menu3Text);
        } else if (menu3ResId < 0)
        {
            menu3TextView.setVisibility(View.GONE);
            menu3TextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            menu3TextView.setText(null);
        } else
        {
            menu3TextView.setVisibility(View.VISIBLE);
        }
    }
}
