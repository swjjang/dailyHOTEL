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
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewToolbarDataBinding;
import com.twoheart.dailyhotel.databinding.DailyViewToolbarMenuItemDataBinding;

import java.util.ArrayList;
import java.util.List;

public class DailyToolbarView extends ConstraintLayout
{
    private static final int ANIMATION_DELAY = 200;

    private DailyViewToolbarDataBinding mViewDataBinding;

    private List<Pair<MenuItem, DailyViewToolbarMenuItemDataBinding>> mMenuItemList;

    ObjectAnimator mShowAnimator;
    ObjectAnimator mHideAnimator;

    private ThemeColor mThemeColor;

    // 중복 불가
    public enum MenuItem
    {
        NONE(-1, true),
        HELP(R.drawable.navibar_ic_help, true),
        SHARE(R.drawable.navibar_ic_share_01_black, true),
        CALL(R.drawable.navibar_ic_call, true),
        CLOSE(R.drawable.navibar_ic_x, true),
        //        TRUE_VR(R.drawable.vector_navibar_ic_treuvr, true),
        WISH_OFF(R.drawable.vector_navibar_ic_heart_off_black, true),
        WISH_LINE_ON(R.drawable.vector_navibar_ic_heart_on_strokefill, false),
        WISH_FILL_ON(R.drawable.vector_navibar_ic_heart_on_fill, false),
        SEARCH(R.drawable.navibar_ic_search, true),
        ORDER_MENUS(R.drawable.vector_navibar_ic_menu, true);

        private int mResId;
        private boolean mSupportChangedColor;

        MenuItem(int resId, boolean supportChangedColor)
        {
            mResId = resId;
            mSupportChangedColor = supportChangedColor;
        }

        public int getResourceId()
        {
            return mResId;
        }

        public boolean supportChangedColor()
        {
            return mSupportChangedColor;
        }
    }

    // xml에서만 가능
    public enum ThemeColor
    {
        DEFAULT(R.color.default_text_c323232, R.color.default_background_c454545),
        WHITE(R.color.white, R.color.white);

        private int mTextColorResId;
        private int mIconColorResId;

        ThemeColor(int textColorResId, int iconColorResId)
        {
            mTextColorResId = textColorResId;
            mIconColorResId = iconColorResId;
        }

        public int getTextColorResourceId()
        {
            return mTextColorResId;
        }

        public int getIconColorResourceId()
        {
            return mIconColorResId;
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

        mThemeColor = ThemeColor.DEFAULT;

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_toolbar_data, this, true);

        if (ScreenUtils.getScreenWidth(context) <= 480)
        {
            mViewDataBinding.dailyTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
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

            if (typedArray.hasValue(R.styleable.dailyToolbar_underLineVisible) == true)
            {
                boolean underLineVisible = typedArray.getBoolean(R.styleable.dailyToolbar_underLineVisible, true);
                setUnderLineVisible(underLineVisible);
            }

            if (typedArray.hasValue(R.styleable.dailyToolbar_themeColor) == true)
            {
                try
                {
                    int themeColor = typedArray.getInt(R.styleable.dailyToolbar_themeColor, 0);

                    switch (themeColor)
                    {
                        case 1:
                            mThemeColor = ThemeColor.WHITE;
                            break;

                        default:
                            mThemeColor = ThemeColor.DEFAULT;
                            break;
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    mThemeColor = ThemeColor.DEFAULT;
                }
            }

            typedArray.recycle();
        }

        setBackImageResource(R.drawable.navibar_ic_back_01_black);
    }

    public void setTitleText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        switch (mThemeColor)
        {
            case DEFAULT:
                break;

            case WHITE:
                setTitleTextColor(getResources().getColor(mThemeColor.getTextColorResourceId()));
                break;
        }

        mViewDataBinding.dailyTitleTextView.setText(text);
    }

    public void setTitleText(@StringRes int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        switch (mThemeColor)
        {
            case DEFAULT:
                break;

            case WHITE:
                setTitleTextColor(getResources().getColor(mThemeColor.getTextColorResourceId()));
                break;
        }

        mViewDataBinding.dailyTitleTextView.setText(resId);
    }

    public void setBackVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dailyTitleImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnBackClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dailyTitleImageView.setOnClickListener(listener);
    }

    public void setBackImageResource(@DrawableRes int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dailyTitleImageView.setVectorImageResource(resId);

        switch (mThemeColor)
        {
            case DEFAULT:
                break;

            case WHITE:
                mViewDataBinding.dailyTitleImageView.setColorFilter(getResources().getColor(mThemeColor.getIconColorResourceId()));
                break;
        }
    }

    /**
     * 중복 MenuItem 불가
     * 넣을때 왼쪽에서 부터 넣도록 한다.
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

        DailyViewToolbarMenuItemDataBinding viewDataBinding = addMenuItemView(menuItem, text, listener);

        if (viewDataBinding != null)
        {
            mMenuItemList.add(new Pair(menuItem, viewDataBinding));
        }
    }

    /**
     * @param menuItem
     * @param text
     * @param count    0 보다 작으면 0, 100보다 크면 99+, 나머지는 표시
     * @param listener
     */
    public void addMenuItem(MenuItem menuItem, String text, int count, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(menuItem) == true)
        {
            return;
        }

        DailyViewToolbarMenuItemDataBinding viewDataBinding = addMenuItemView(menuItem, text, count, listener);

        if (viewDataBinding != null)
        {
            mMenuItemList.add(new Pair(menuItem, viewDataBinding));
        }
    }

    public void updateMenuItem(MenuItem menuItem, String text, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(menuItem) == false)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        if (index < 0)
        {
            return;
        }

        updateMenuItemView(mMenuItemList.get(index).second, menuItem, text, listener);
    }

    public void updateMenuItem(MenuItem menuItem, String text, int count, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(menuItem) == false)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        if (index < 0)
        {
            return;
        }

        updateMenuItemView(mMenuItemList.get(index).second, menuItem, text, count, listener);
    }

    public void replaceMenuItem(MenuItem srcMenuItem, MenuItem menuItem, String text, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(srcMenuItem) == false)
        {
            return;
        }

        int index = getMenuItemIndex(srcMenuItem);

        if (index < 0)
        {
            return;
        }

        updateMenuItemView(mMenuItemList.get(index).second, menuItem, text, listener);
    }

    public void replaceMenuItem(MenuItem srcMenuItem, MenuItem menuItem, OnClickListener listener)
    {
        if (mViewDataBinding == null || hasMenuItem(srcMenuItem) == false)
        {
            return;
        }

        int index = getMenuItemIndex(srcMenuItem);

        if (index < 0)
        {
            return;
        }

        updateMenuItemView(mMenuItemList.get(index).second, menuItem, listener);
    }

    public void removeMenuItem(MenuItem menuItem)
    {
        if (mViewDataBinding == null || hasMenuItem(menuItem) == false)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        if (index < 0)
        {
            return;
        }

        mMenuItemList.remove(index);

        removeMenuItemView(menuItem);
    }

    public void setMenuItemVisible(MenuItem menuItem, boolean visible)
    {
        if (mViewDataBinding == null || hasMenuItem(menuItem) == false)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        if (index < 0)
        {
            return;
        }

        mMenuItemList.get(index).second.getRoot().setVisibility(visible ? VISIBLE : GONE);
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
                if (mHideAnimator != null)
                {
                    mHideAnimator.removeAllUpdateListeners();
                }

                mHideAnimator = null;

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

    private void setTitleTextColor(int color)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dailyTitleTextView.setTextColor(color);
    }

    private void setUnderLineVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dailyToolbarUnderline.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setUnderLineHeight(int height)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dailyToolbarUnderline.getLayoutParams().height = height;
        mViewDataBinding.dailyToolbarUnderline.requestLayout();
    }

    private DailyViewToolbarMenuItemDataBinding addMenuItemView(MenuItem menuItem, String text, OnClickListener listener)
    {
        if (mViewDataBinding == null || menuItem == null)
        {
            return null;
        }

        DailyViewToolbarMenuItemDataBinding viewDataBinding = getMenuItemView(mViewDataBinding.dailyMenuItemLayout, menuItem, text, listener);

        return viewDataBinding;
    }

    private DailyViewToolbarMenuItemDataBinding addMenuItemView(MenuItem menuItem, String text, int count, OnClickListener listener)
    {
        if (mViewDataBinding == null || menuItem == null)
        {
            return null;
        }

        DailyViewToolbarMenuItemDataBinding viewDataBinding = getMenuItemView(mViewDataBinding.dailyMenuItemLayout, menuItem, text, count, listener);

        return viewDataBinding;
    }

    private void removeMenuItemView(MenuItem menuItem)
    {
        if (mViewDataBinding == null || menuItem == null)
        {
            return;
        }

        int index = getMenuItemIndex(menuItem);

        if (index < 0)
        {
            return;
        }

        mViewDataBinding.dailyMenuItemLayout.removeViewAt(mMenuItemList.size() - index - 1);
    }

    private void clearMenuItemView()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (mViewDataBinding.dailyMenuItemLayout.getChildCount() > 0)
        {
            mViewDataBinding.dailyMenuItemLayout.removeAllViews();
        }
    }

    public boolean hasMenuItem(MenuItem menuItem)
    {
        for (Pair pair : mMenuItemList)
        {
            if (menuItem == pair.first)
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
            if (mMenuItemList.get(i).first == menuItem)
            {
                return i;
            }
        }

        return -1;
    }

    private DailyViewToolbarMenuItemDataBinding getMenuItemView(ViewGroup viewGroup, MenuItem menuItem, String text, OnClickListener listener)
    {
        if (viewGroup == null || menuItem == null)
        {
            return null;
        }

        DailyViewToolbarMenuItemDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.daily_view_toolbar_menu_item_data, viewGroup, true);

        updateMenuItemView(viewDataBinding, menuItem, text, listener);

        return viewDataBinding;
    }

    private DailyViewToolbarMenuItemDataBinding getMenuItemView(ViewGroup viewGroup, MenuItem menuItem, String text, int count, OnClickListener listener)
    {
        if (viewGroup == null || menuItem == null)
        {
            return null;
        }

        DailyViewToolbarMenuItemDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.daily_view_toolbar_menu_item_data, viewGroup, true);

        // Count를 넣는 아이콘은 오른쪽 패딩을 없앤다.
        viewDataBinding.getRoot().setPadding(0, 0, 0, 0);

        updateMenuItemView(viewDataBinding, menuItem, text, count, listener);

        return viewDataBinding;
    }


    private void updateMenuItemView(DailyViewToolbarMenuItemDataBinding viewDataBinding, MenuItem menuItem, OnClickListener listener)
    {
        if (viewDataBinding == null || menuItem == null)
        {
            return;
        }

        viewDataBinding.dailyImageView.setVectorImageResource(menuItem.getResourceId());

        switch (mThemeColor)
        {
            case DEFAULT:
                break;

            case WHITE:
                if (menuItem.supportChangedColor() == true)
                {
                    viewDataBinding.dailyImageView.setColorFilter(getResources().getColor(mThemeColor.getIconColorResourceId()));
                } else
                {
                    viewDataBinding.dailyImageView.clearColorFilter();
                }

                if (viewDataBinding.dailyTextView.getVisibility() != GONE)
                {
                    viewDataBinding.dailyTextView.setTextColor(getResources().getColor(mThemeColor.getTextColorResourceId()));
                }
                break;
        }

        viewDataBinding.getRoot().setOnClickListener(listener);
    }

    private void updateMenuItemView(DailyViewToolbarMenuItemDataBinding viewDataBinding, MenuItem menuItem, String text, OnClickListener listener)
    {
        if (viewDataBinding == null || menuItem == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            viewDataBinding.dailyTextView.setVisibility(GONE);
        } else
        {
            viewDataBinding.dailyTextView.setVisibility(VISIBLE);
            viewDataBinding.dailyTextView.setText(text);
        }

        updateMenuItemView(viewDataBinding, menuItem, listener);
    }

    private void updateMenuItemView(DailyViewToolbarMenuItemDataBinding viewDataBinding, MenuItem menuItem, String text, int count, OnClickListener listener)
    {
        if (viewDataBinding == null || menuItem == null)
        {
            return;
        }

        viewDataBinding.dailyCountTextView.setVisibility(VISIBLE);

        if (count < 0)
        {
            viewDataBinding.dailyCountTextView.setText(Integer.toString(0));
        } else if (count < 100)
        {
            viewDataBinding.dailyCountTextView.setText(Integer.toString(count));
        } else
        {
            viewDataBinding.dailyCountTextView.setText("99+");
        }

        updateMenuItemView(viewDataBinding, menuItem, text, listener);
    }
}
