package com.twoheart.dailyhotel.screen.search.stay;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.SearchOptionItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.screen.search.PlaceSearchRecyclerAdapter;
import com.twoheart.dailyhotel.screen.search.SearchOptionItemListAdapter;

import java.util.ArrayList;
import java.util.List;

public class StaySearchLayout extends PlaceSearchLayout
{
    public interface OnEventListener extends PlaceSearchLayout.OnEventListener
    {
        void onStayOutboundClick();
    }

    public StaySearchLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        View stayOutboundLayout = view.findViewById(R.id.stayOutboundLayout);
        stayOutboundLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    ((StaySearchLayout.OnEventListener) mOnEventListener).onStayOutboundClick();
                }
            }
        });
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mContext.getString(R.string.label_view_myaround_hotel);
    }

    @Override
    protected SpannableString getAroundPlaceTermText()
    {
        final String text = mContext.getString(R.string.label_search_need_to_agree_termsoflocation);
        SpannableString spannableString = new SpannableString(text);

        if (mContext.getResources().getDisplayMetrics().densityDpi <= 240)
        {
            spannableString.setSpan(new ScaleXSpan(0.8f), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    @Override
    protected String getSearchHintText()
    {
        return mContext.getString(R.string.label_search_hotel_hint);
    }

    @Override
    protected int getRecentSearchesIcon(int type)
    {
        switch (type)
        {
            case HOTEL_ICON:
                return R.drawable.search_ic_02_hotel;

            default:
                return R.drawable.search_ic_03_recent;
        }
    }

    @Override
    protected void updateSuggestLayout(TextView titleTextView, TextView priceTextView, Keyword keyword, String text)
    {
        if (keyword == null || titleTextView == null || priceTextView == null)
        {
            return;
        }

        StayKeyword stayKeyword = (StayKeyword) keyword;

        if (stayKeyword.index > 0)
        {
            String keywordNameUpperCase = stayKeyword.name.toUpperCase();
            String textUpperCase = text.toUpperCase();

            int separatorIndex = keywordNameUpperCase.indexOf('>');
            int startIndex = keywordNameUpperCase.lastIndexOf(textUpperCase);
            int endIndex = startIndex + textUpperCase.length();

            if (startIndex > separatorIndex)
            {
                try
                {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stayKeyword.name);
                    spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    titleTextView.setText(spannableStringBuilder);
                } catch (Exception e)
                {
                    titleTextView.setText(stayKeyword.name);
                }
            } else
            {
                titleTextView.setText(stayKeyword.name);
            }

            priceTextView.setVisibility(View.VISIBLE);

            if (stayKeyword.availableRooms == 0)
            {
                priceTextView.setText(mContext.getString(R.string.act_hotel_soldout));
            } else
            {
                priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayKeyword.price, false));
            }
        } else
        {
            titleTextView.setText(stayKeyword.name);
            priceTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setRecyclerViewData(List<? extends Place> recentlyList, ArrayList<CampaignTag> campaignTagList, List<Keyword> recentSearchList)
    {
        ArrayList<SearchOptionItem> recentlyPlaceDataList = new ArrayList<>();
        ArrayList<SearchOptionItem> campaignTagDataList = new ArrayList<>();
        ArrayList<SearchOptionItem> recentSearchDataList = new ArrayList<>();

        for (Place place : recentlyList)
        {
            Stay stay = (Stay) place;

            SearchOptionItem item = new SearchOptionItem();
            item.iconResId = R.drawable.vector_ob_search_ic_02_hotel;
            item.itemText = stay.name;
            item.object = stay;
            recentlyPlaceDataList.add(item);
        }

        for (CampaignTag campaignTag : campaignTagList)
        {
            SearchOptionItem item = new SearchOptionItem();
            item.iconResId = R.drawable.vector_search_ic_04_tag;
            item.itemText = campaignTag.campaignTag;
            item.object = campaignTag;
            campaignTagDataList.add(item);
        }

        for (Keyword keyword : recentSearchList)
        {
            SearchOptionItem item = new SearchOptionItem();
            item.iconResId = keyword.icon;
            item.itemText = keyword.name;
            item.object = keyword;
            recentSearchDataList.add(item);
        }

        mRecyclerAdapter = new PlaceSearchRecyclerAdapter(mContext //
            , PlaceSearchRecyclerAdapter.TYPE_STAY, recentlyPlaceDataList //
            , campaignTagDataList, recentSearchDataList);

        mRecyclerView.setAdapter(mRecyclerAdapter);
    }
}