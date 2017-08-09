package com.daily.dailyhotel.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewToolbarDataBinding;

import java.util.ArrayList;
import java.util.List;

public class DailyToolbarView extends ConstraintLayout
{
    private static final int ANIMATION_DELAY = 200;

    private DailyViewToolbarDataBinding mViewDataBinding;

    private List<MenuItem> mMenuItemList;

    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;

    // 중복 불가
    public enum MenuItem
    {
        NONE(-1),
        HELP(R.drawable.navibar_ic_help),
        SHARE(R.drawable.navibar_ic_share_01_black),
        CALL(R.drawable.navibar_ic_call),
        CLOSE(R.drawable.navibar_ic_x),
        TRUE_VR(R.drawable.vector_navibar_ic_treuvr),
        WISH_OFF(R.drawable.vector_navibar_ic_heart_off_black),
        WISH_ON(R.drawable.vector_navibar_ic_heart_on);

        private int mResId;

        MenuItem(int resId)
        {
            mResId = resId;
        }

        public int getResourceId()
        {
            return mResId;
        }
    }

    public DailyToolbarView(Context context)
    {
        super(context);

        initLayout(context, null);
    }

    public DailyToolbarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context, attrs);
    }

    public DailyToolbarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs)
    {
        if (context == null)
        {
            return;
        }

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_toolbar_data, this, true);

        if (ScreenUtils.getScreenWidth(context) <= 480)
        {
            mViewDataBinding.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        mMenuItemList = new ArrayList<>();

        if (attrs != null)
        {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dailyToolbar);

            if (typedArray.hasValue(R.styleable.dailyToolbar_underLineHeight) == true)
            {
                float underLineHeight = typedArray.getDimension(R.styleable.dailyToolbar_underLineHeight, ScreenUtils.dpToPx(context, 1));
                setUnderLineHeight((int) underLineHeight);
            }
        }
    }

    public void setTitleText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(text);
    }

    public void setTitleText(@StringRes int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(resId);
    }

    public void setBackVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.backImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnBackClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.backImageView.setOnClickListener(listener);
    }

    public void setBackImageResource(@DrawableRes int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.backImageView.setVectorImageResource(resId);
    }

    /**
     * 중복 MenuItem 불가
     *
     * @param menuItem
     * @param listener
     */
    public void addMenuItem(MenuItem menuItem, String text, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(menuItem) == true)
        {
            return;
        }

        mMenuItemList.add(menuItem);

        addMenuItemView(menuItem, text, listener);
    }

    public void replaceMenuItem(MenuItem srcMenuItem, MenuItem dstMenuItem, String dstText, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(srcMenuItem) == false || hasMenuItem(dstMenuItem) == true)
        {
            return;
        }

        int index = getMenuItemIndex(srcMenuItem);

        mMenuItemList.remove(index);
        mMenuItemList.add(index, dstMenuItem);

        replaceMenuItemView(srcMenuItem, dstMenuItem, dstText, listener);
    }

    public void removeMenuItem(MenuItem menuItem)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        mMenuItemList.remove(index);

        removeMenuItemView(menuItem);
    }

    public void clearMenuItem()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mMenuItemList.clear();

        clearMenuItemView();
    }

    public void showAnimation()
    {
        if (mViewDataBinding == null || mShowAnimator != null || getVisibility() == VISIBLE)
        {
            return;
        }

        if (mHideAnimator != null)
        {
            mHideAnimator.cancel();
            mHideAnimator = null;
        }

        mShowAnimator = ObjectAnimator.ofFloat(this, ALPHA, 0.0f, 1.0f);
        mShowAnimator.setDuration(ANIMATION_DELAY);
        mShowAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mShowAnimator != null)
                {
                    mShowAnimator.removeAllListeners();
                }

                mShowAnimator = null;
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

        mShowAnimator.start();
    }

    public void hideAnimation()
    {
        if (mViewDataBinding == null || mHideAnimator != null || getVisibility() == GONE)
        {
            return;
        }

        if (mShowAnimator != null)
        {
            mShowAnimator.cancel();
            mShowAnimator = null;
        }

        mHideAnimator = ObjectAnimator.ofFloat(this, ALPHA, 1.0f, 0.0f);
        mHideAnimator.setDuration(ANIMATION_DELAY);
        mHideAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                setVisibility(GONE);
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

        mHideAnimator.start();
    }

    public void setUnderLineVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.toolbarUnderline.setVisibility(visible ? VISIBLE : GONE);
    }

    private void setUnderLineHeight(int height)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.toolbarUnderline.getLayoutParams().height = height;
        mViewDataBinding.toolbarUnderline.requestLayout();
    }

    private void addMenuItemView(MenuItem menuItem, String text, OnClickListener listener)
    {
        if (mViewDataBinding == null || menuItem == null)
        {
            return;
        }

        DailyTextView dailyTextView = new DailyTextView(getContext());
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dailyTextView.setTextColor(getContext().getResources().getColor(R.color.default_text_c323232));
        dailyTextView.setGravity(Gravity.CENTER);
        dailyTextView.setText(text);
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, menuItem.getResourceId(), 0);
        dailyTextView.setOnClickListener(listener);
        dailyTextView.setMinWidth(ScreenUtils.dpToPx(getContext(), 30));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(ScreenUtils.dpToPx(getContext(), 10), 0, 0, 0);
        mViewDataBinding.menuLayout.addView(dailyTextView, 0, layoutParams);
    }

    private void removeMenuItemView(MenuItem menuItem)
    {
        if (mViewDataBinding == null || menuItem == null)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        mViewDataBinding.menuLayout.removeViewAt(mMenuItemList.size() - index - 1);
    }

    private void replaceMenuItemView(MenuItem srcMenuItem, MenuItem dstMenuItem, String dstText, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(srcMenuItem) == false || hasMenuItem(dstMenuItem) == true)
        {
            return;
        }

        int index = getMenuItemIndex(srcMenuItem);

        DailyTextView dailyTextView = (DailyTextView) mViewDataBinding.menuLayout.getChildAt(mMenuItemList.size() - index - 1);
        dailyTextView.setText(dstText);
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, dstMenuItem.getResourceId(), 0);
    }

    private void clearMenuItemView()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.menuLayout.removeAllViews();
    }

    private boolean hasMenuItem(MenuItem searchMenuItem)
    {
        for (MenuItem menuItem : mMenuItemList)
        {
            if (searchMenuItem == menuItem)
            {
                return true;
            }
        }

        return false;
    }

    private int getMenuItemIndex(MenuItem menuItem)
    {
        int size = mMenuItemList.size();

        for (int i = 0; i < size; i++)
        {
            if (mMenuItemList.get(i) == menuItem)
            {
                return i;
            }
        }

        return -1;
    }
}
