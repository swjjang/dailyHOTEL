package com.twoheart.dailyhotel.view.widget;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyToolbarLayout
{
    private AppCompatActivity mAppCompatActivity;
    private Toolbar mToolbar;

    public DailyToolbarLayout(AppCompatActivity appCompatActivity, Toolbar toolbar)
    {
        mAppCompatActivity = appCompatActivity;
        mToolbar = toolbar;
    }

    public void initToolbarRegion(View.OnClickListener listener)
    {
        if (mToolbar.getTag() != null)
        {
            return;
        }

        final TextView textView = (TextView) mToolbar.findViewById(R.id.titleTextView);
        textView.setText(null);
        textView.setCompoundDrawables(null, null, null, null);
        textView.setOnClickListener(listener);

        final View view = mToolbar.findViewById(R.id.biImageView);

        view.post(new Runnable()
        {
            @Override
            public void run()
            {
                textView.setMaxWidth(view.getLeft() - textView.getLeft() - Util.dpToPx(mAppCompatActivity, 5));
            }
        });

        mToolbar.setTag(mToolbar.getId(), textView);
        mAppCompatActivity.setSupportActionBar(mToolbar);
    }

    public void initToolbarRegionMenu(View.OnClickListener listener)
    {
        ImageView menu1 = (ImageView) mToolbar.findViewById(R.id.menu1View);
        ImageView menu2 = (ImageView) mToolbar.findViewById(R.id.menu2View);

        setToolbarRegionMenu(R.drawable.navibar_ic_map, R.drawable.navibar_ic_sorting_01);

        menu1.setOnClickListener(listener);
        menu2.setOnClickListener(listener);

        setToolbarRegionMenuVisibility(false);
    }

    public void initToolbar(String title)
    {
        initToolbar(title, false);
    }

    public void initToolbar(String title, boolean isTransparent)
    {
        initToolbar(title, true, isTransparent);
    }

    public void initToolbar(String title, boolean isBackPressed, boolean isTransparent)
    {
        mAppCompatActivity.setSupportActionBar(mToolbar);
        setToolbarTransparent(isTransparent);

        FontManager.apply(mToolbar, FontManager.getInstance(mAppCompatActivity).getRegularTypeface());

        TextView textView = (TextView) mToolbar.findViewById(R.id.titleTextView);
        textView.setText(title);

        View backView = mToolbar.findViewById(R.id.backImageView);

        if (isBackPressed == true)
        {
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAppCompatActivity.finish();
                }
            });
        } else
        {
            backView.setVisibility(View.GONE);
        }
    }

    public void setToolbarRegionMenuVisibility(boolean isVisibility)
    {
        View menu1 = mToolbar.findViewById(R.id.menu1View);
        View menu2 = mToolbar.findViewById(R.id.menu2View);

        if (isVisibility == true)
        {
            menu1.setVisibility(View.VISIBLE);
            menu2.setVisibility(View.VISIBLE);
        } else
        {
            menu1.setVisibility(View.INVISIBLE);
            menu2.setVisibility(View.INVISIBLE);
        }
    }

    public void setToolbarRegionMenu(int menu1ResId, int menu2ResId)
    {
        ImageView menu1 = (ImageView) mToolbar.findViewById(R.id.menu1View);
        ImageView menu2 = (ImageView) mToolbar.findViewById(R.id.menu2View);

        if (menu1ResId > 0)
        {
            menu1.setVisibility(View.VISIBLE);
            menu1.setImageResource(menu1ResId);
            menu1.setTag(menu1ResId);
        } else if (menu1ResId < 0)
        {
            menu1.setVisibility(View.GONE);
        } else
        {
            menu1.setVisibility(View.VISIBLE);
        }

        if (menu2ResId > 0)
        {
            menu2.setVisibility(View.VISIBLE);
            menu2.setImageResource(menu2ResId);
            menu2.setTag(menu2ResId);
        } else if (menu2ResId < 0)
        {
            menu2.setVisibility(View.GONE);
        } else
        {
            menu2.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarRegionText(String title)
    {
        // 인덱스 번호는 나중에 바뀜
        TextView textView = (TextView) mToolbar.findViewById(R.id.titleTextView);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_ic_region_v, 0);
        textView.setText(title);
    }

    public void setToolbarText(String title)
    {
        TextView textView = (TextView) mToolbar.findViewById(R.id.titleTextView);
        textView.setText(title);
    }

    public void setToolbarTransparent(boolean isTransparent)
    {
        if (isTransparent == true)
        {
            mToolbar.setTitleTextColor(mAppCompatActivity.getResources().getColor(android.R.color.transparent));
            mToolbar.setBackgroundColor(mAppCompatActivity.getResources().getColor(android.R.color.transparent));
        } else
        {
            mToolbar.setTitleTextColor(mAppCompatActivity.getResources().getColor(R.color.actionbar_title));
            mToolbar.setBackgroundColor(mAppCompatActivity.getResources().getColor(R.color.white));
        }
    }
}
