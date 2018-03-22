package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyImageView;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class StaySearchResultLayout extends PlaceSearchResultLayout
{
    private View mPopularSearchTagLayout;
    private FlexboxLayout mTagFlexboxLayout;

    public interface OnEventListener extends PlaceSearchResultLayout.OnEventListener
    {
        void onSearchStayOutboundClick();

        void onSearchGourmetClick();

        void onSearchPopularTag(CampaignTag campaignTag);
    }

    public StaySearchResultLayout(Context context, String callByScreen, OnBaseEventListener listener)
    {
        super(context, callByScreen, listener);
    }

    @Override
    protected void initEmptyLayout(ScrollView scrollView)
    {
        if (scrollView == null)
        {
            return;
        }

        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        // 상단 이미지
        DailyImageView emptyIconImageView = scrollView.findViewById(R.id.emptyIconImageView);
        emptyIconImageView.setVectorImageResource(R.drawable.no_hotel_ic);

        // 두번째 메시지
        TextView messageTextView02 = scrollView.findViewById(R.id.messageTextView02);
        messageTextView02.setText(R.string.message_searchresult_stay_empty_subtitle);

        // 스테이 인기 검색 태그
        TextView popularSearchTagTextView = scrollView.findViewById(R.id.popularSearchTagTextView);
        popularSearchTagTextView.setText(R.string.label_search_stay_popular_search_tag);

        //
        mPopularSearchTagLayout = scrollView.findViewById(R.id.popularSearchTagLayout);

        mTagFlexboxLayout = mPopularSearchTagLayout.findViewById(R.id.tagFlexboxLayout);
        mTagFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        mTagFlexboxLayout.setFlexWrap(FlexWrap.WRAP);

        // 하단 버튼
        View searchLeftLayout = scrollView.findViewById(R.id.searchLeftLayout);
        View searchRightLayout = scrollView.findViewById(R.id.searchRightLayout);

        searchLeftLayout.setOnClickListener(v -> ((StaySearchResultLayout.OnEventListener) mOnEventListener).onSearchStayOutboundClick());
        searchRightLayout.setOnClickListener(v -> ((StaySearchResultLayout.OnEventListener) mOnEventListener).onSearchGourmetClick());

        DailyTextView searchLeftTextView = searchLeftLayout.findViewById(R.id.searchLeftTextView);
        DailyTextView searchRightTextView = searchRightLayout.findViewById(R.id.searchRightTextView);

        searchLeftTextView.setText(R.string.label_searchresult_search_stayoutbound);
        searchLeftTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_search_shortcut_02_ob, 0, 0, 0);

        searchRightTextView.setText(R.string.label_searchresult_search_gourmet);
        searchRightTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_search_shortcut_03_gourmet, 0, 0, 0);
    }

    protected void setCalendarText(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        try
        {
            int nights = stayBookingDay.getNights();
            final String dateFormat = ScreenUtils.getScreenWidth(mContext) < 720 ? "MM.dd" : "MM.dd(EEE)";
            String date = String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookingDay.getCheckInDay(dateFormat)//
                , stayBookingDay.getCheckOutDay(dateFormat), nights);

            setCalendarText(date);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_hotel_ic;
    }

    @Override
    protected synchronized PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<StaySearchResultListFragment> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            StaySearchResultListFragment staySearchResultListFragment = new StaySearchResultListFragment();
            staySearchResultListFragment.setPlaceOnListFragmentListener(listener);
            staySearchResultListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(staySearchResultListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {
        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.SCREEN, AnalyticsManager.Screen.SEARCH_RESULT);
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, category, params);
    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {
        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.SCREEN, AnalyticsManager.Screen.SEARCH_RESULT);
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, category, params);
    }

    @Override
    public void setCampaignTagVisible(boolean visible)
    {
        if (mTagFlexboxLayout == null)
        {
            return;
        }

        mPopularSearchTagLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCampaignTagList(List<CampaignTag> campaignTagList)
    {
        if (mTagFlexboxLayout == null || campaignTagList == null || campaignTagList.size() == 0)
        {
            return;
        }

        mTagFlexboxLayout.removeAllViews();

        for (CampaignTag campaignTag : campaignTagList)
        {
            View view = getTagView(campaignTag);

            if (view != null)
            {
                mTagFlexboxLayout.addView(view);
            }
        }
    }

    @Override
    public boolean hasCampaignTag()
    {
        return mTagFlexboxLayout != null && mTagFlexboxLayout.getChildCount() > 0;
    }

    /**
     * 매번 add하는 것은 아니고 setCategoryAllTabLayout이후로 한번만 호출되어야 한다 여러번 안됨.
     *
     * @param categoryList
     * @param listener
     */
    public void addCategoryTabLayout(List<Category> categoryList,//
                                     PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (categoryList == null)
        {
            return;
        }

        int size = categoryList.size();

        if (size + mCategoryTabLayout.getTabCount() <= 2)
        {
            size = 1;
            setCategoryTabLayoutVisibility(View.GONE);

            mViewPager.setOffscreenPageLimit(size);
            mViewPager.clearOnPageChangeListeners();
        } else
        {
            setCategoryTabLayoutVisibility(View.VISIBLE);

            Category category;
            TabLayout.Tab tab;
            ArrayList<PlaceListFragment> list = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
            {
                category = categoryList.get(i);

                tab = mCategoryTabLayout.newTab();
                tab.setText(category.name);
                tab.setTag(category);
                mCategoryTabLayout.addTab(tab);

                StaySearchResultListFragment searchResultListFragment = new StaySearchResultListFragment();
                searchResultListFragment.setPlaceOnListFragmentListener(listener);
                searchResultListFragment.setBottomOptionLayout(mFloatingActionView);
                list.add(searchResultListFragment);
            }

            mFragmentPagerAdapter.addPlaceListFragment(list);
            mFragmentPagerAdapter.notifyDataSetChanged();

            mViewPager.setOffscreenPageLimit(mCategoryTabLayout.getTabCount());

            mCategoryTabLayout.setOnTabSelectedListener(mOnCategoryTabSelectedListener);

            FontManager.apply(mCategoryTabLayout, FontManager.getInstance(mContext).getRegularTypeface());
        }
    }

    public void removeCategoryTab(HashSet<String> existCategorySet)
    {
        int count = mCategoryTabLayout.getTabCount();
        TabLayout.Tab tab;
        Category category;

        for (int i = count - 1; i > 0; i--)
        {
            tab = mCategoryTabLayout.getTabAt(i);
            category = (Category) tab.getTag();

            if (existCategorySet.contains(category.code) == false)
            {
                mCategoryTabLayout.removeTabAt(i);
                mFragmentPagerAdapter.removeItem(i);
            }
        }

        int existTabCount = mCategoryTabLayout.getTabCount();

        // 2개 이하면 전체 탭 한개로 통합한다.
        if (existTabCount <= 2)
        {
            mCategoryTabLayout.removeTabAt(1);
            mFragmentPagerAdapter.removeItem(1);
            mFragmentPagerAdapter.notifyDataSetChanged();

            mViewPager.setOffscreenPageLimit(1);
            mViewPager.clearOnPageChangeListeners();
            setCategoryTabLayoutVisibility(View.GONE);
        } else
        {
            mFragmentPagerAdapter.notifyDataSetChanged();

            mViewPager.setOffscreenPageLimit(existTabCount);
        }
    }

    private View getTagView(CampaignTag campaignTag)
    {
        if (campaignTag == null)
        {
            return null;
        }

        final int DP_12 = ScreenUtils.dpToPx(mContext, 12);
        final int DP_5 = ScreenUtils.dpToPx(mContext, 5);

        DailyTextView dailyTextView = new DailyTextView(mContext);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dailyTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c666666));
        dailyTextView.setPadding(DP_12, 0, DP_12, 0);
        dailyTextView.setBackgroundResource(R.drawable.shape_fillrect_le7e7e7_bffffff_r50);
        dailyTextView.setGravity(Gravity.CENTER_VERTICAL);
        dailyTextView.setMaxLines(1);
        dailyTextView.setSingleLine();

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(mContext, 29));
        layoutParams.setMargins(DP_5, DP_5, DP_5, DP_5);

        dailyTextView.setLayoutParams(layoutParams);
        dailyTextView.setText("#" + campaignTag.campaignTag);
        dailyTextView.setTag(campaignTag);
        dailyTextView.setOnClickListener(v -> ((StaySearchResultLayout.OnEventListener) mOnEventListener).onSearchPopularTag((CampaignTag) v.getTag()));

        return dailyTextView;
    }
}
