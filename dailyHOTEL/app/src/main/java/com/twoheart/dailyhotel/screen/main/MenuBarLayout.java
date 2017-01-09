package com.twoheart.dailyhotel.screen.main;

import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public class MenuBarLayout implements View.OnClickListener
{
    private static final int MENU_HOTEL_INDEX = 0;
    private static final int MENU_BOOKING_INDEX = 1;
    private static final int MENU_MYDAILY_INDEX = 2;
    private static final int MENU_INFORMATION_INDEX = 3;

    private static final int MENU_COUNT = 4;

    private View[] mMenuView;
    private int mSelectedMenuIndex;
    private OnMenuBarSelectedListener mOnMenuBarSelectedListener;
    private BaseActivity mBaseActivity;
    private ViewGroup mViewGroup;
    private boolean mEnabled;

    public static class MenuBarLayoutOnPageChangeListener
    {
        private MenuBarLayout mMenuBarLayout;

        public MenuBarLayoutOnPageChangeListener(MenuBarLayout menuBarLayout)
        {
            mMenuBarLayout = menuBarLayout;
        }

        public void onPageChangeListener(int index)
        {
            mMenuBarLayout.setTranslationY(0);
            mMenuBarLayout.selectedMenu(index);
        }
    }

    public interface OnMenuBarSelectedListener
    {
        void onMenuSelected(int index);

        void onMenuUnselected(int index);

        void onMenuReselected(int index);
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
        mSelectedMenuIndex = MENU_HOTEL_INDEX;

        mMenuView = new View[MENU_COUNT];

        mMenuView[MENU_HOTEL_INDEX] = viewGroup.findViewById(R.id.hotelLayout);
        mMenuView[MENU_HOTEL_INDEX].setOnClickListener(this);

        mMenuView[MENU_BOOKING_INDEX] = viewGroup.findViewById(R.id.bookingLayout);
        mMenuView[MENU_BOOKING_INDEX].setOnClickListener(this);

        mMenuView[MENU_INFORMATION_INDEX] = viewGroup.findViewById(R.id.informationLayout);
        mMenuView[MENU_INFORMATION_INDEX].setOnClickListener(this);

        mMenuView[MENU_MYDAILY_INDEX] = viewGroup.findViewById(R.id.myDailyLayout);
        mMenuView[MENU_MYDAILY_INDEX].setOnClickListener(this);
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
            case R.id.hotelLayout:
                selectedMenu(MENU_HOTEL_INDEX);
                break;

            case R.id.bookingLayout:
                selectedMenu(MENU_BOOKING_INDEX);
                break;

            case R.id.informationLayout:
                selectedMenu(MENU_INFORMATION_INDEX);
                break;

            case R.id.myDailyLayout:
                selectedMenu(MENU_MYDAILY_INDEX);
                break;
        }
    }

    void selectedMenu(int index)
    {
        if (mSelectedMenuIndex == index)
        {
            mMenuView[index].setSelected(true);

            if (mOnMenuBarSelectedListener != null)
            {
                mOnMenuBarSelectedListener.onMenuReselected(index);
            }
        } else
        {
            mMenuView[mSelectedMenuIndex].setSelected(false);

            if (mOnMenuBarSelectedListener != null)
            {
                mOnMenuBarSelectedListener.onMenuUnselected(mSelectedMenuIndex);
            }

            mSelectedMenuIndex = index;
            mMenuView[index].setSelected(true);

            if (mOnMenuBarSelectedListener != null)
            {
                mOnMenuBarSelectedListener.onMenuSelected(index);
            }
        }
    }

    public void setEnabled(boolean enabled)
    {
        mEnabled = enabled;
    }

    public void setVisibility(int visibility)
    {
        mViewGroup.setVisibility(visibility);
    }

    public void setTranslationY(float translationY)
    {
        mViewGroup.setTranslationY(translationY);
    }

    public int getHeight()
    {
        return mViewGroup.getHeight();
    }

    public void setNewIconVisible(boolean isVisible)
    {
        mMenuView[MENU_INFORMATION_INDEX].findViewById(R.id.informationNewIconView).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
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
            case MENU_HOTEL_INDEX:
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
}
