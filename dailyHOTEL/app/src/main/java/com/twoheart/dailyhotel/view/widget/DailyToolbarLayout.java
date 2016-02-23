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
        setToolbarRegionMenu(R.drawable.navibar_ic_map, -1);

        setToolbarMenuClickListener(listener);

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

            textView.setPadding(0, 0, 0, 0);
        } else
        {
            backView.setVisibility(View.GONE);
            textView.setPadding(Util.dpToPx(mAppCompatActivity, 15), 0, 0, 0);
        }
    }

    public void setToolbarMenuClickListener(View.OnClickListener listener)
    {
        View menu1 = mToolbar.findViewById(R.id.menu1View);
        View menu2 = mToolbar.findViewById(R.id.menu2View);

        if (menu1 == null || menu2 == null)
        {
            Util.restartApp(mAppCompatActivity);
            return;
        }

        if (menu1 != null)
        {
            menu1.setOnClickListener(listener);
        }

        if (menu2 != null)
        {
            menu2.setOnClickListener(listener);
        }
    }

    public void setToolbarRegionMenuVisibility(boolean isVisibility)
    {
        View menu1 = mToolbar.findViewById(R.id.menu1View);
        View menu2 = mToolbar.findViewById(R.id.menu2View);

        if (menu1 == null || menu2 == null)
        {
            Util.restartApp(mAppCompatActivity);
            return;
        }

        if (isVisibility == true)
        {
            menu1.setVisibility(View.VISIBLE);

            if (menu2.getTag() != null)
            {
                menu2.setVisibility(View.VISIBLE);
            }
        } else
        {
            menu1.setVisibility(View.INVISIBLE);

            if (menu2.getTag() != null)
            {
                menu2.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setToolbarRegionMenu(int menu1ResId, int menu2ResId)
    {
        ImageView menu1 = (ImageView) mToolbar.findViewById(R.id.menu1View);
        ImageView menu2 = (ImageView) mToolbar.findViewById(R.id.menu2View);

        if (menu1 == null || menu2 == null)
        {
            Util.restartApp(mAppCompatActivity);
            return;
        }

        if (menu1ResId > 0)
        {
            menu1.setVisibility(View.VISIBLE);
            menu1.setImageResource(menu1ResId);
            menu1.setTag(menu1ResId);
        } else if (menu1ResId < 0)
        {
            menu1.setVisibility(View.GONE);
            menu1.setTag(null);
            menu1.setImageResource(0);
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
            menu2.setTag(null);
            menu2.setImageResource(0);
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

    public Toolbar getToolbar()
    {
        return mToolbar;
    }

    public void setToolbarTransparent(boolean isTransparent)
    {
        TextView textView = (TextView) mToolbar.findViewById(R.id.titleTextView);

        if (isTransparent == true)
        {
            textView.setTextColor(mAppCompatActivity.getResources().getColor(android.R.color.transparent));
            mToolbar.setBackgroundColor(mAppCompatActivity.getResources().getColor(android.R.color.transparent));
        } else
        {
            textView.setTextColor(mAppCompatActivity.getResources().getColor(R.color.actionbar_title));
            mToolbar.setBackgroundColor(mAppCompatActivity.getResources().getColor(R.color.white));
        }
    }

    public void setToolbarVisibility(boolean visible)
    {
        mToolbar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
