package com.twoheart.dailyhotel.screen.main;

import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;

public class MenuBarLayout implements View.OnClickListener
{
    private static final int MENU_HOTEL_INDEX = 0;
    private static final int MENU_GOURMET_INDEX = 1;
    private static final int MENU_BOOKINGL_INDEX = 2;
    private static final int MENU_INFORMATIONL_INDEX = 3;

    private static final int MENU_COUNT = 4;

    private ViewGroup mMenuBarLayout;
    private View[] mMenuView;
    private int mSelectedMenuIndex;
    private OnMenuBarSelectedListener mOnMenuBarSelectedListener;

    public static class MenuBarLayoutOnPageChangeListener
    {
        private MenuBarLayout mMenuBarLayout;

        public MenuBarLayoutOnPageChangeListener(MenuBarLayout menuBarLayout)
        {
            mMenuBarLayout = menuBarLayout;
        }

        public void onPageChangeListener(int index)
        {
            mMenuBarLayout.selectedMenu(index);
        }
    }

    public interface OnMenuBarSelectedListener
    {
        public void onMenuSelected(int index);

        public void onMenuUnselected(int index);

        public void onMenuReselected(int intdex);
    }

    public MenuBarLayout(ViewGroup viewGroup, OnMenuBarSelectedListener listener)
    {
        mMenuBarLayout = viewGroup;
        mOnMenuBarSelectedListener = listener;

        initLayout(mMenuBarLayout);
    }

    private void initLayout(ViewGroup viewGroup)
    {
        mSelectedMenuIndex = MENU_HOTEL_INDEX;

        mMenuView = new View[MENU_COUNT];

        mMenuView[MENU_HOTEL_INDEX] = viewGroup.findViewById(R.id.hotelView);
        mMenuView[MENU_HOTEL_INDEX].setOnClickListener(this);

        mMenuView[MENU_GOURMET_INDEX] = viewGroup.findViewById(R.id.gourmetView);
        mMenuView[MENU_GOURMET_INDEX].setOnClickListener(this);

        mMenuView[MENU_BOOKINGL_INDEX] = viewGroup.findViewById(R.id.bookingView);
        mMenuView[MENU_BOOKINGL_INDEX].setOnClickListener(this);

        mMenuView[MENU_INFORMATIONL_INDEX] = viewGroup.findViewById(R.id.informationView);
        mMenuView[MENU_INFORMATIONL_INDEX].setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.hotelView:
                selectedMenu(MENU_HOTEL_INDEX);
                break;

            case R.id.gourmetView:
                selectedMenu(MENU_GOURMET_INDEX);
                break;

            case R.id.bookingView:
                selectedMenu(MENU_BOOKINGL_INDEX);
                break;

            case R.id.informationView:
                selectedMenu(MENU_INFORMATIONL_INDEX);
                break;
        }
    }

    public void setVisibility(boolean isVisible)
    {
        mMenuBarLayout.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
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

    public void setNewIconVisible(boolean isVisible)
    {
        mMenuView[MENU_INFORMATIONL_INDEX].findViewById(R.id.newEventIcon).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }
}
