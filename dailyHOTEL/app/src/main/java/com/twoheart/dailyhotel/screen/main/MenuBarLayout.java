package com.twoheart.dailyhotel.screen.main;

import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public class MenuBarLayout implements View.OnClickListener
{
    private static final int MENU_HOTEL_INDEX = 0;
    private static final int MENU_GOURMET_INDEX = 1;
    private static final int MENU_BOOKINGL_INDEX = 2;
    private static final int MENU_INFORMATIONL_INDEX = 3;

    private static final int MENU_COUNT = 4;

    private View[] mMenuView;
    private int mSelectedMenuIndex;
    private OnMenuBarSelectedListener mOnMenuBarSelectedListener;
    private BaseActivity mBaseActivity;
    private ViewGroup mViewGroup;

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
        void onMenuSelected(int index);

        void onMenuUnselected(int index);

        void onMenuReselected(int intdex);
    }

    public MenuBarLayout(BaseActivity baseActivity, ViewGroup viewGroup, OnMenuBarSelectedListener listener)
    {
        mBaseActivity = baseActivity;
        mOnMenuBarSelectedListener = listener;

        mViewGroup = viewGroup;
        initLayout(viewGroup);
    }

    private void initLayout(ViewGroup viewGroup)
    {
        mSelectedMenuIndex = MENU_HOTEL_INDEX;

        mMenuView = new View[MENU_COUNT];

        mMenuView[MENU_HOTEL_INDEX] = viewGroup.findViewById(R.id.hotelLayout);
        mMenuView[MENU_HOTEL_INDEX].setOnClickListener(this);

        mMenuView[MENU_GOURMET_INDEX] = viewGroup.findViewById(R.id.gourmetLayout);
        mMenuView[MENU_GOURMET_INDEX].setOnClickListener(this);

        mMenuView[MENU_BOOKINGL_INDEX] = viewGroup.findViewById(R.id.bookingLayout);
        mMenuView[MENU_BOOKINGL_INDEX].setOnClickListener(this);

        mMenuView[MENU_INFORMATIONL_INDEX] = viewGroup.findViewById(R.id.informationLayout);
        mMenuView[MENU_INFORMATIONL_INDEX].setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (mBaseActivity.isLockUiComponent() == true)
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.hotelLayout:
                selectedMenu(MENU_HOTEL_INDEX);
                break;

            case R.id.gourmetLayout:
                selectedMenu(MENU_GOURMET_INDEX);
                break;

            case R.id.bookingLayout:
                selectedMenu(MENU_BOOKINGL_INDEX);
                break;

            case R.id.informationLayout:
                selectedMenu(MENU_INFORMATIONL_INDEX);
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
        mMenuView[MENU_INFORMATIONL_INDEX].findViewById(R.id.newEventIcon).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public String getName(int position)
    {
        switch (position)
        {
            case MENU_HOTEL_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_hotel);

            case MENU_GOURMET_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_fnb);

            case MENU_BOOKINGL_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_bookings);

            case MENU_INFORMATIONL_INDEX:
                return mBaseActivity.getString(R.string.menu_item_title_information);

            default:
                return null;
        }
    }
}
