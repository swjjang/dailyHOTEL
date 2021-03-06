package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import java.util.ArrayList;

public class CountryCodeListLayout implements OnItemClickListener
{
    private Context mContext;
    private PinnedSectionListView mListView;
    private CountryCodeListAdapter mCountryCodeListAdapter;
    private CountryCodeListActivity.OnUserActionListener mOnUserActionListener;

    public CountryCodeListLayout(Context context)
    {
        mContext = context;
    }

    public View createView()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_countrycode_list, null, false);

        mListView = view.findViewById(R.id.listView);
        EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        mListView.setTag("CountryCodeListLayout");

        return view;
    }

    public void setData(ArrayList<String[]> list, String selectedCountryCode)
    {
        if (mCountryCodeListAdapter == null)
        {
            mCountryCodeListAdapter = new CountryCodeListAdapter(mContext, 0, new ArrayList<String[]>());
        } else
        {
            mCountryCodeListAdapter.clear();
        }

        if (list == null)
        {
            mListView.setVisibility(View.GONE);
        } else
        {
            mListView.setVisibility(View.VISIBLE);

            int selectedIndex = -1;

            if (DailyTextUtils.isTextEmpty(selectedCountryCode) == false)
            {
                String code = selectedCountryCode.substring(selectedCountryCode.indexOf('\n') + 1);

                int size = list.size();

                for (int i = 0; i < size; i++)
                {
                    String[] countryCode = list.get(i);

                    if (code.equalsIgnoreCase(countryCode[1]) == true)
                    {
                        selectedIndex = i;
                        break;
                    }
                }
            }

            mCountryCodeListAdapter.addAll(list, selectedIndex);
            mListView.setAdapter(mCountryCodeListAdapter);
            mCountryCodeListAdapter.notifyDataSetChanged();
        }
    }

    public void setOnUserActionListener(CountryCodeListActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (mCountryCodeListAdapter.getItemViewType(position) == CountryCodeListAdapter.TYPE_SECTION)
        {
            return;
        }

        mCountryCodeListAdapter.setSelected(position);
        mCountryCodeListAdapter.notifyDataSetChanged();

        if (mOnUserActionListener != null)
        {
            mOnUserActionListener.selectCountry(mCountryCodeListAdapter.getItem(position));
        }
    }
}