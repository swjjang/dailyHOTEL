package com.twoheart.dailyhotel.screen.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public class MenuBarLayout implements View.OnClickListener
{
    private static final int MENU_HOME_INDEX = 0;
    private static final int MENU_BOOKING_INDEX = 1;
    private static final int MENU_MYDAILY_INDEX = 2;
    private static final int MENU_INFORMATION_INDEX = 3;

    private static final int MENU_COUNT = 4;

    private View[] mMenuView;
    private int mSelectedMenuIndex = -1;
    private OnMenuBarSelectedListener mOnMenuBarSelectedListener;
    private BaseActivity mBaseActivity;
    private ViewGroup mViewGroup;
    private boolean mEnabled;
    private ValueAnimator mValueAnimator;

    public static class MenuBarLayoutOnPageChangeListener
    {
        private MenuBarLayout mMenuBarLayout;

        public MenuBarLayoutOnPageChangeListener(MenuBarLayout menuBarLayout)
        {
            mMenuBarLayout = menuBarLayout;
        }

        public void onPageChangeListener(boolean isCallMenuBar, int index)
        {
            if (mMenuBarLayout == null)
            {
                return;
            }

            if (isCallMenuBar == false)
            {
                mMenuBarLayout.selectedMenu(index);
            }
        }
    }

    public interface OnMenuBarSelectedListener
    {
        void onMenuSelected(boolean isCallMenuBar, int index, int previousIndex);

        void onMenuUnselected(boolean isCallMenuBar, int index);

        void onMenuReselected(boolean isCallMenuBar, int index);
    }

    public MenuBarLayout(BaseActivity baseActivity, ViewGroup viewGroup, OnMenuBarSelectedListener listener)
    {
        mBaseActivity = baseActivity;
        mOnMenuBarSelectedListener = listener;
        mEnabled = true;
        mViewGroup = viewGroup;
        initLayout(viewGroup);
    }

    private void initLayout(ViewGroup viewGroup)
    {
        mMenuView = new View[MENU_COUNT];

        mMenuView[MENU_HOME_INDEX] = viewGroup.findViewById(R.id.homeLayout);
        mMenuView[MENU_HOME_INDEX].setOnClickListener(this);

        mMenuView[MENU_BOOKING_INDEX] = viewGroup.findViewById(R.id.bookingLayout);
        mMenuView[MENU_BOOKING_INDEX].setOnClickListener(this);

        mMenuView[MENU_MYDAILY_INDEX] = viewGroup.findViewById(R.id.myDailyLayout);
        mMenuView[MENU_MYDAILY_INDEX].setOnClickListener(this);

        mMenuView[MENU_INFORMATION_INDEX] = viewGroup.findViewById(R.id.informationLayout);
        mMenuView[MENU_INFORMATION_INDEX].setOnClickListener(this);

        selectedMenu(MENU_HOME_INDEX);
    }

    @Override
    public void onClick(View v)
    {
        if (mBaseActivity.isLockUiComponent() == true || mEnabled == false)
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.homeLayout:
                selectedMenu(MENU_HOME_INDEX);
                break;

            case R.id.bookingLayout:
                selectedMenu(MENU_BOOKING_INDEX);
                break;

            case R.id.myDailyLayout:
                selectedMenu(MENU_MYDAILY_INDEX);
                break;

            case R.id.informationLayout:
                selectedMenu(MENU_INFORMATION_INDEX);
                break;
        }
    }

    void selectedMenu(int index)
    {
        if (mSelectedMenuIndex == index)
        {
            if (mOnMenuBarSelectedListener != null)
            {
                mOnMenuBarSelectedListener.onMenuReselected(true, index);
            }
        } else
        {
            if (mSelectedMenuIndex >= 0)
            {
                hideMenuText(mMenuView[mSelectedMenuIndex]);

                if (mOnMenuBarSelectedListener != null)
                {
                    mOnMenuBarSelectedListener.onMenuUnselected(true, mSelectedMenuIndex);
                }
            }

            showMenuText(mMenuView[index]);

            if (mOnMenuBarSelectedListener != null && mSelectedMenuIndex >= 0)
            {
                mOnMenuBarSelectedListener.onMenuSelected(true, index, mSelectedMenuIndex);
            }

            mSelectedMenuIndex = index;
        }
    }

    void showMenuText(final View view)
    {
        if (view == null)
        {
            return;
        }

        Object animator = view.getTag();

        if (animator != null && animator instanceof AnimatorSet)
        {
            ((AnimatorSet) animator).cancel();
        }

        final View textView = getMenuTextView(view);

        if (textView == null)
        {
            return;
        }

        final int DP_4 = ScreenUtils.dpToPx(view.getContext(), 4);
        final ValueAnimator showValueAnimator1 = ValueAnimator.ofInt(view.getPaddingTop(), DP_4);
        showValueAnimator1.setDuration(200);
        showValueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                int value = (int) animation.getAnimatedValue();

                view.setPadding(0, value, 0, 0);
            }
        });

        final ValueAnimator showValueAnimator2 = ValueAnimator.ofInt(textView.getPaddingTop(), ScreenUtils.dpToPx(view.getContext(), 29));
        showValueAnimator2.setDuration(400);
        showValueAnimator2.setInterpolator(PathInterpolatorCompat.create(0.0f, 0.74f, 0.22f, 1.28f));
        showValueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                int value = (int) animation.getAnimatedValue();

                textView.setPadding(0, value, 0, 0);
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(showValueAnimator1, showValueAnimator2);
        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                showValueAnimator1.removeAllUpdateListeners();
                showValueAnimator2.removeAllUpdateListeners();
                animatorSet.removeAllListeners();

                int paddingTop = view.getPaddingTop();

                if (paddingTop == DP_4)
                {
                    view.setSelected(true);
                }

                view.setTag(null);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        view.setTag(animatorSet);
        animatorSet.start();
    }

    void hideMenuText(final View view)
    {
        if (view == null)
        {
            return;
        }

        Object animator = view.getTag();

        if (animator != null && animator instanceof AnimatorSet)
        {
            ((AnimatorSet) animator).cancel();
        }

        final View textView = getMenuTextView(view);

        if (textView == null)
        {
            return;
        }

        final int DP_10 = ScreenUtils.dpToPx(view.getContext(), 10);
        final ValueAnimator hideValueAnimator1 = ValueAnimator.ofInt(view.getPaddingTop(), DP_10);
        hideValueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                int value = (int) animation.getAnimatedValue();

                view.setPadding(0, value, 0, 0);
            }
        });

        final ValueAnimator hideValueAnimator2 = ValueAnimator.ofInt(textView.getPaddingTop(), ScreenUtils.dpToPx(view.getContext(), 50));
        hideValueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                int value = (int) animation.getAnimatedValue();

                textView.setPadding(0, value, 0, 0);
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(hideValueAnimator1, hideValueAnimator2);
        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                hideValueAnimator1.removeAllUpdateListeners();
                hideValueAnimator2.removeAllUpdateListeners();
                animatorSet.removeAllListeners();

                int paddingTop = view.getPaddingTop();

                if (paddingTop == DP_10)
                {
                    view.setSelected(false);
                }

                view.setTag(null);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        view.setTag(animatorSet);
        animatorSet.start();
    }

    public void setEnabled(boolean enabled)
    {
        mEnabled = enabled;
    }

    public void setVisibility(int visibility)
    {
        mViewGroup.setVisibility(visibility);
    }

    public boolean isVisibility()
    {
        return mViewGroup.getVisibility() == View.VISIBLE;
    }

    private void setTranslationY(float translationY)
    {
        if (mViewGroup == null)
        {
            return;
        }

        mViewGroup.setTranslationY(translationY);
    }

    private float getTranslationY()
    {
        return mViewGroup.getTranslationY();
    }

    private int getHeight()
    {
        return mViewGroup.getHeight();
    }

    public void setMyDailyNewIconVisible(boolean isVisible)
    {
        mMenuView[MENU_MYDAILY_INDEX].findViewById(R.id.myDailyNewIconView).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setInformationNewIconVisible(boolean isVisible)
    {
        mMenuView[MENU_INFORMATION_INDEX].findViewById(R.id.informationNewIconView).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public String getName(int position)
    {
        switch (position)
        {
            case MENU_HOME_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_home);

            case MENU_BOOKING_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_bookings);

            case MENU_MYDAILY_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_mydaily);

            case MENU_INFORMATION_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_information);

            default:
                return null;
        }
    }

    private View getMenuTextView(View view)
    {
        if (view == null)
        {
            return null;

        }

        switch (view.getId())
        {
            case R.id.homeLayout:
                return view.findViewById(R.id.homeView);

            case R.id.bookingLayout:
                return view.findViewById(R.id.bookingView);

            case R.id.myDailyLayout:
                return view.findViewById(R.id.myDailyView);

            case R.id.informationLayout:
                return view.findViewById(R.id.informationView);

            default:
                return null;
        }
    }

    public void showMenuBar()
    {
        setTranslationY(0);
    }

    public void showMenuBarAnimation(boolean force)
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            if (force == true)
            {
                mValueAnimator.cancel();
                mValueAnimator = null;
            } else
            {
                return;
            }
        }

        if (force == false && isVisibility() == true)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofFloat(getTranslationY(), 0.0f);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.setDuration(300);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();

                setTranslationY(value);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }

    public void hideMenuBarAnimation()
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            return;
        }

        if (isVisibility() == false)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(0, mViewGroup.getHeight());
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.setDuration(300);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                int value = (int) animation.getAnimatedValue();

                setTranslationY(value);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            boolean isCanceled;

            @Override
            public void onAnimationStart(Animator animation)
            {
                isCanceled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator.removeAllListeners();

                if (isCanceled == false)
                {
                    setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                isCanceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }
}
