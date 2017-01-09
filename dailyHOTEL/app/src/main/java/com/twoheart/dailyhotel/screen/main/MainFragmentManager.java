package com.twoheart.dailyhotel.screen.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.screen.booking.list.BookingListFragment;
import com.twoheart.dailyhotel.screen.common.ErrorFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainFragment;
import com.twoheart.dailyhotel.screen.information.InformationFragment;
import com.twoheart.dailyhotel.screen.mydaily.MyDailyFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

public class MainFragmentManager
{
    public static final int INDEX_ERROR_FRAGMENT = 100;
    public static final int INDEX_HOME_FRAGMENT = 0;
    public static final int INDEX_BOOKING_FRAGMENT = 1;
    public static final int INDEX_MYDAILY_FRAGMENT = 2;
    public static final int INDEX_INFORMATION_FRAGMENT = 3;

    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private ViewGroup mContentLayout;
    private int mIndexLastFragment;
    private int mIndexMainLastFragment; // 호텔, 고메

    private BaseActivity mBaseActivity;
    private PlaceMainFragment.OnMenuBarListener mOnMenuBarListener;
    private MenuBarLayout.MenuBarLayoutOnPageChangeListener mMenuBarLayoutOnPageChangeListener;

    public MainFragmentManager(BaseActivity activity, ViewGroup viewGroup, //
                               PlaceMainFragment.OnMenuBarListener onMenuBarListener, MenuBarLayout.MenuBarLayoutOnPageChangeListener listener)
    {
        if (activity == null || viewGroup == null)
        {
            throw new NullPointerException("activity == null || viewGroup == null");
        }

        mIndexLastFragment = -1;
        mBaseActivity = activity;
        mFragmentManager = activity.getSupportFragmentManager();
        mContentLayout = viewGroup;
        mOnMenuBarListener = onMenuBarListener;
        mMenuBarLayoutOnPageChangeListener = listener;
    }

    public int getLastIndexFragment()
    {
        return mIndexLastFragment;
    }

    public int getLastMainIndexFragment()
    {
        return mIndexMainLastFragment;
    }

    public Fragment getCurrentFragment()
    {
        return mFragment;
    }

    /**
     * 네비게이션 드로워 메뉴에서 선택할 수 있는 Fragment를 반환하는 메서드이다.
     *
     * @param index Fragment 리스트에 해당하는 index를 받는다.
     * @return 요청한 index에 해당하는 Fragment를 반환한다. => 기능 변경, 누를때마다 리프레시
     */
    public Fragment getFragment(int index)
    {
        switch (index)
        {
            case INDEX_HOME_FRAGMENT:
            {
                PlaceMainFragment placeMainFragment = new StayMainFragment();
                placeMainFragment.setMenuBarListener(mOnMenuBarListener);
                return placeMainFragment;
            }

            case INDEX_BOOKING_FRAGMENT:
            {
                mOnMenuBarListener.onMenuBarEnabled(true);
                return new BookingListFragment();
            }

            case INDEX_MYDAILY_FRAGMENT:
            {
                mOnMenuBarListener.onMenuBarEnabled(true);
                return new MyDailyFragment();
            }

            case INDEX_INFORMATION_FRAGMENT:
            {
                mOnMenuBarListener.onMenuBarEnabled(true);
                return new InformationFragment();
            }

            case INDEX_ERROR_FRAGMENT:
            {
                mOnMenuBarListener.onMenuBarEnabled(true);
                ErrorFragment fragment = new ErrorFragment();
                fragment.setMenuManager(this);
                return fragment;
            }
        }

        return null;
    }

    /**
     * Fragment 컨테이너에서 해당 Fragment로 변경하여 표시한다.
     *
     * @param fragment Fragment 리스트에 보관된 Fragement들을 받는 것이 좋다.
     */
    public void replaceFragment(Fragment fragment, String tag)
    {
        if (mBaseActivity.isFinishing() == true)
        {
            return;
        }

        if (Util.isOverAPI17() == true)
        {
            if (mBaseActivity.isDestroyed() == true)
            {
                return;
            }
        }

        try
        {
            clearFragmentBackStack();

            mFragment = fragment;

            mFragmentManager.beginTransaction().replace(mContentLayout.getId(), fragment, tag).commitAllowingStateLoss();
        } catch (IllegalStateException e)
        {
            if (Constants.DEBUG == false)
            {
                Crashlytics.log("StayListLayout");
                Crashlytics.logException(e);
            }
        } catch (Exception e)
        {
            // 에러가 나는 경우 앱을 재부팅 시킨다.
            Util.restartApp(mBaseActivity);
        }
    }

    /**
     * Fragment 컨테이너의 표시되는 Fragment를 변경할 때 Fragment 컨테이너에 적재된 Fragment들을 정리한다.
     */
    private void clearFragmentBackStack()
    {
        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i)
        {
            mFragmentManager.popBackStackImmediate();
        }
    }

    public void select(int index, boolean isRefresh)
    {
        if (index != mIndexLastFragment || isRefresh == true)
        {
            switch (index)
            {
                case INDEX_ERROR_FRAGMENT:
                    replaceFragment(getFragment(INDEX_ERROR_FRAGMENT), String.valueOf(INDEX_ERROR_FRAGMENT));
                    return;

                case INDEX_INFORMATION_FRAGMENT:
                    mIndexLastFragment = INDEX_INFORMATION_FRAGMENT;
                    break;

                case INDEX_MYDAILY_FRAGMENT:
                    mIndexLastFragment = INDEX_MYDAILY_FRAGMENT;
                    break;

                case INDEX_BOOKING_FRAGMENT:
                    mIndexLastFragment = INDEX_BOOKING_FRAGMENT;
                    break;

                case INDEX_HOME_FRAGMENT:
                default:
                    mIndexLastFragment = INDEX_HOME_FRAGMENT;
                    mIndexMainLastFragment = INDEX_HOME_FRAGMENT;

                    DailyPreference.getInstance(mBaseActivity).setLastMenu(mBaseActivity.getString(R.string.label_dailyhotel));
                    break;
            }

            replaceFragment(getFragment(mIndexLastFragment), String.valueOf(mIndexLastFragment));
        }

        if (mMenuBarLayoutOnPageChangeListener != null)
        {
            mMenuBarLayoutOnPageChangeListener.onPageChangeListener(mIndexLastFragment);
        }
    }
}
