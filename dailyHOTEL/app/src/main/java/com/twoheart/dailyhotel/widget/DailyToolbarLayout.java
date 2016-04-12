package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyToolbarLayout
{
    private AppCompatActivity mAppCompatActivity;
    private View mToolbar;

    public DailyToolbarLayout(AppCompatActivity appCompatActivity, View toolbar)
    {
        mAppCompatActivity = appCompatActivity;
        mToolbar = toolbar;
    }

    private TextView getTitleTextView(Context context)
    {
        TextView textView = (TextView) mToolbar.findViewById(R.id.titleTextView);

        if (Util.getLCDWidth(context) <= 480)
        {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        return textView;
    }

    public void initToolbarRegion(View.OnClickListener listener)
    {
        if (mToolbar.getTag() != null)
        {
            return;
        }

        final TextView textView = getTitleTextView(mAppCompatActivity);
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
    }

    public void initToolbarRegionMenu(View.OnClickListener listener)
    {
        setToolbarMenu(R.drawable.navibar_ic_map, R.drawable.navibar_ic_search_black);

        setToolbarMenuClickListener(listener);

        setToolbarMenuVisibility(false);
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
        setToolbarTransparent(isTransparent);

        FontManager.apply(mToolbar, FontManager.getInstance(mAppCompatActivity).getRegularTypeface());

        TextView textView = getTitleTextView(mAppCompatActivity);
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

        menu1.setOnClickListener(listener);
        menu2.setOnClickListener(listener);
    }

    public void setToolbarMenuVisibility(boolean isVisibility)
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

    public void setToolbarMenuEnable(boolean enableMenu1, boolean enableMenu2)
    {
        View menu1 = mToolbar.findViewById(R.id.menu1View);
        View menu2 = mToolbar.findViewById(R.id.menu2View);

        menu1.setEnabled(enableMenu1);
        menu2.setEnabled(enableMenu2);
    }

    public void setToolbarMenu(String menu1Text, String menu2Text)
    {
        TextView menu1 = (TextView) mToolbar.findViewById(R.id.menu1View);
        TextView menu2 = (TextView) mToolbar.findViewById(R.id.menu2View);

        if (menu1 == null || menu2 == null)
        {
            Util.restartApp(mAppCompatActivity);
            return;
        }

        if (Util.isTextEmpty(menu1Text) == false)
        {
            menu1.setVisibility(View.VISIBLE);
            menu1.setText(menu1Text);
        } else
        {
            menu1.setVisibility(View.GONE);
            menu1.setText(null);
        }

        if (Util.isTextEmpty(menu2Text) == false)
        {
            menu2.setVisibility(View.VISIBLE);
            menu2.setText(menu2Text);
        } else
        {
            menu2.setVisibility(View.GONE);
            menu2.setText(null);
        }
    }

    public void setToolbarMenu(int menu1ResId, int menu2ResId)
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
        TextView textView = getTitleTextView(mAppCompatActivity);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_ic_region_v, 0);
        textView.setText(title);
    }

    public void setToolbarText(String title)
    {
        TextView textView = getTitleTextView(mAppCompatActivity);
        textView.setText(title);
    }

    public View getToolbar()
    {
        return mToolbar;
    }

    public void setToolbarTransparent(boolean isTransparent)
    {
        TextView textView = getTitleTextView(mAppCompatActivity);

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
        if (mToolbar == null)
        {
            return;
        }

        mToolbar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
