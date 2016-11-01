package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyViewPager;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class WishListTabActivity extends BaseActivity
{
    private ArrayList<PlaceWishListFragment> mFragmentList;
    private WishListFragmentPageAdapter mPageAdapter;

    private WishListTabNetworkController mNetworkController;

    private DailyViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mLoginView;

    private PlaceType mPlaceType;

    private boolean mDontReloadAtOnResume;

    private static Intent


}
