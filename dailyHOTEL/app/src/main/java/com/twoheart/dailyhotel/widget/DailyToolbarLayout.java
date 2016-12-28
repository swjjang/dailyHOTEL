package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyToolbarLayout
{
    private static final int ANIMATION_DELAY = 200;

    private Context mContext;
    private View mToolbar;
    private AlphaAnimation mAlphaAnimation;

    public DailyToolbarLayout(Context context, View toolbar)
    {
        mContext = context;
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

    public void initToolbar(String title, View.OnClickListener backPressedListener)
    {
        initToolbar(title, backPressedListener, false);
    }

    public void initToolbar(String title, View.OnClickListener backPressedListener, boolean isTransparent)
    {
        setToolbarTransparent(isTransparent);

        FontManager.apply(mToolbar, FontManager.getInstance(mContext).getRegularTypeface());

        TextView textView = getTitleTextView(mContext);
        textView.setText(title);

        View backView = mToolbar.findViewById(R.id.backImageView);

        if (backPressedListener == null)
        {
            backView.setVisibility(View.GONE);
            textView.setPadding(Util.dpToPx(mContext, 15), 0, 0, 0);
        } else
        {
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(backPressedListener);
            textView.setPadding(0, 0, 0, 0);
        }
    }

    public void setToolbarMenuClickListener(View.OnClickListener listener)
    {
        View menu1 = mToolbar.findViewById(R.id.menu1View);
        View menu2 = mToolbar.findViewById(R.id.menu2View);

        if (menu1 == null || menu2 == null)
        {
            Util.restartApp(mContext);
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
            Util.restartApp(mContext);
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
            Util.restartApp(mContext);
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
            Util.restartApp(mContext);
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

    public void setBackImageView(int backResId)
    {
        ImageView backView = (ImageView) mToolbar.findViewById(R.id.backImageView);
        backView.setImageResource(backResId);
    }

    public void setToolbarText(String title)
    {
        TextView textView = getTitleTextView(mContext);
        textView.setText(title);
    }

    public View getToolbar()
    {
        return mToolbar;
    }

    public void setToolbarTransparent(boolean isTransparent)
    {
        TextView textView = getTitleTextView(mContext);

        if (isTransparent == true)
        {
            textView.setTextColor(mContext.getResources().getColor(android.R.color.transparent));
            mToolbar.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        } else
        {
            textView.setTextColor(mContext.getResources().getColor(R.color.actionbar_title));
            mToolbar.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }

    boolean mIsShowAnimationStart;
    boolean mIsHideAnimationStart;

    public void setToolbarVisibility(boolean visible, boolean isAnimation)
    {
        if (mToolbar == null)
        {
            return;
        }

        if (visible == true)
        {
            if (mToolbar.getVisibility() != View.VISIBLE)
            {
                if (isAnimation == true && mIsShowAnimationStart == false)
                {
                    mIsHideAnimationStart = false;
                    showAnimation();
                } else
                {
                    mToolbar.setVisibility(View.VISIBLE);

                    mIsShowAnimationStart = false;
                    mIsHideAnimationStart = false;
                }
            }
        } else
        {
            if (mToolbar.getVisibility() == View.VISIBLE)
            {
                if (isAnimation == true && mIsHideAnimationStart == false)
                {
                    mIsShowAnimationStart = false;
                    hideAnimation();
                } else
                {
                    mToolbar.setVisibility(View.GONE);

                    mIsShowAnimationStart = false;
                    mIsHideAnimationStart = false;
                }
            }
        }
    }

    private void showAnimation()
    {
        if (mToolbar == null)
        {
            return;
        }

        if (mIsShowAnimationStart == true)
        {
            return;
        } else
        {
            mIsShowAnimationStart = true;
        }

        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(ANIMATION_DELAY);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                mToolbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mIsShowAnimationStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mToolbar.startAnimation(mAlphaAnimation);
    }

    private void hideAnimation()
    {
        if (mToolbar == null)
        {
            return;
        }

        if (mIsHideAnimationStart == true)
        {
            return;
        } else
        {
            mIsHideAnimationStart = true;
        }

        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(ANIMATION_DELAY);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mToolbar.setVisibility(View.GONE);
                mIsHideAnimationStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mToolbar.startAnimation(mAlphaAnimation);
    }
}
