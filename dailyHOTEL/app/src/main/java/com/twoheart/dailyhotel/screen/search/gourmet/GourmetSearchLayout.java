package com.twoheart.dailyhotel.screen.search.gourmet;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;

public class GourmetSearchLayout extends PlaceSearchLayout
{
    public GourmetSearchLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mContext.getString(R.string.label_view_myaround_gourmet);
    }

    @Override
    protected SpannableString getAroundPlaceTermText()
    {
        final String text = mContext.getString(R.string.label_search_need_to_agree_termsoflocation);
        return new SpannableString(text);
    }

    @Override
    protected String getSearchHintText()
    {
        return mContext.getString(R.string.label_search_gourmet_hint);
    }

    @Override
    protected int getRecentSearchesIcon(int type)
    {
        switch (type)
        {
            case GOURMET_ICON:
                return R.drawable.search_ic_02_gourmet;

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

        GourmetKeyword gourmetKeyword = (GourmetKeyword) keyword;

        if (gourmetKeyword.index > 0)
        {
            String keywordNameUpperCase = gourmetKeyword.name.toUpperCase();
            String textUpperCase = text.toUpperCase();

            int separatorIndex = keywordNameUpperCase.indexOf('>');
            int startIndex = keywordNameUpperCase.lastIndexOf(textUpperCase);
            int endIndex = startIndex + textUpperCase.length();

            if (startIndex > separatorIndex)
            {
                try
                {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(gourmetKeyword.name);
                    spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    titleTextView.setText(spannableStringBuilder);
                } catch (Exception e)
                {
                    titleTextView.setText(gourmetKeyword.name);
                }
            } else
            {
                titleTextView.setText(gourmetKeyword.name);
            }

            priceTextView.setVisibility(View.VISIBLE);

            if (gourmetKeyword.availableTickets == 0)
            {
                priceTextView.setText(mContext.getString(R.string.act_hotel_soldout));
            } else
            {
                priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, gourmetKeyword.price, false));
            }
        } else
        {
            titleTextView.setText(gourmetKeyword.name);
            priceTextView.setVisibility(View.INVISIBLE);
        }
    }
}