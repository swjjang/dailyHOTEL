package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyImageView;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class GourmetSearchResultLayout extends PlaceSearchResultLayout
{
    private View mPopularSearchTagLayout;
    private FlexboxLayout mTagFlexboxLayout;

    public interface OnEventListener extends PlaceSearchResultLayout.OnEventListener
    {
        void onSearchStayClick();

        void onSearchStayOutboundClick();

        void onSearchPopularTag(CampaignTag campaignTag);
    }

    public GourmetSearchResultLayout(Context context, String callByScreen, OnBaseEventListener listener)
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
        emptyIconImageView.setVectorImageResource(R.drawable.no_gourmet_ic);

        // 두번째 메시지
        TextView messageTextView02 = scrollView.findViewById(R.id.messageTextView02);
        messageTextView02.setText(R.string.message_searchresult_gourmet_empty_subtitle);

        // 고메 인기 검색 태그
        TextView popularSearchTagTextView = scrollView.findViewById(R.id.popularSearchTagTextView);
        popularSearchTagTextView.setText(R.string.label_search_gourmet_popular_search_tag);

        //
        mPopularSearchTagLayout = scrollView.findViewById(R.id.popularSearchTagLayout);

        mTagFlexboxLayout = mPopularSearchTagLayout.findViewById(R.id.tagFlexboxLayout);
        mTagFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        mTagFlexboxLayout.setFlexWrap(FlexWrap.WRAP);

        // 하단 버튼
        View searchLeftLayout = scrollView.findViewById(R.id.searchLeftLayout);
        View searchRightLayout = scrollView.findViewById(R.id.searchRightLayout);

        searchLeftLayout.setOnClickListener(v -> ((GourmetSearchResultLayout.OnEventListener) mOnEventListener).onSearchStayClick());
        searchRightLayout.setOnClickListener(v -> ((GourmetSearchResultLayout.OnEventListener) mOnEventListener).onSearchStayOutboundClick());

        DailyTextView searchLeftTextView = searchLeftLayout.findViewById(R.id.searchLeftTextView);
        DailyTextView searchRightTextView = searchRightLayout.findViewById(R.id.searchRightTextView);

        searchLeftTextView.setText(R.string.label_searchresult_search_stay);
        searchLeftTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_search_shortcut_01_stay, 0, 0, 0);

        searchRightTextView.setText(R.string.label_searchresult_search_stayoutbound);
        searchRightTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_search_shortcut_02_ob, 0, 0, 0);
    }

    protected void setCalendarText(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
            return;
        }

        try
        {
            final String dateFormat = "MM.dd(EEE)";

            setCalendarText(gourmetBookingDay.getVisitDay(dateFormat));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_gourmet_ic;
    }

    @Override
    protected synchronized PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<GourmetSearchResultListFragment> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            GourmetSearchResultListFragment gourmetSearchResultListFragment = new GourmetSearchResultListFragment();
            gourmetSearchResultListFragment.setPlaceOnListFragmentListener(listener);
            gourmetSearchResultListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(gourmetSearchResultListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {

    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {

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
        dailyTextView.setOnClickListener(v -> ((GourmetSearchResultLayout.OnEventListener) mOnEventListener).onSearchPopularTag((CampaignTag) v.getTag()));

        return dailyTextView;
    }
}
