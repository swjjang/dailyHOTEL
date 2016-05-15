package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import java.util.ArrayList;
import java.util.Collection;

public class CountryCodeListAdapter extends ArrayAdapter<String[]> implements PinnedSectionListView.PinnedSectionListAdapter
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_SECTION = 1;

    private ArrayList<String[]> mArrayList;
    private Context mContext;
    private int mSelectedIndex;

    public CountryCodeListAdapter(Context context, int resourceId, ArrayList<String[]> eventList)
    {
        super(context, resourceId, eventList);

        mContext = context;
        addAll(eventList, -1);
    }

    public void addAll(Collection<? extends String[]> collection, int selectedIndex)
    {
        if (collection == null)
        {
            return;
        }

        mSelectedIndex = selectedIndex;

        if (mArrayList == null)
        {
            mArrayList = new ArrayList<>();
        }

        mArrayList.addAll(collection);
    }

    public void setSelected(int index)
    {
        mSelectedIndex = index;
    }

    @Override
    public int getCount()
    {
        if (mArrayList == null)
        {
            return 0;
        }

        return mArrayList.size();
    }

    @Override
    public String[] getItem(int position)
    {
        if (mArrayList == null)
        {
            return null;
        }

        return mArrayList.get(position);
    }

    @Override
    public void clear()
    {
        if (mArrayList == null)
        {
            return;
        }

        mArrayList.clear();

        super.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        String[] countryCode = getItem(position);

        if (Util.isTextEmpty(countryCode[1]) == true)
        {
            if (convertView == null || convertView.getTag().equals(R.layout.list_row_countrycode_section) == false)
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_row_countrycode_section, parent, false);

                view.setTag(R.layout.list_row_countrycode_section);
            } else
            {
                view = convertView;
            }

            TextView sectionTextView = (TextView) view.findViewById(R.id.sectionTextView);

            sectionTextView.setText(countryCode[0]);
        } else
        {
            if (convertView == null || convertView.getTag().equals(R.layout.list_row_countrycode) == false)
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_row_countrycode, parent, false);

                view.setTag(R.layout.list_row_countrycode);
            } else
            {
                view = convertView;
            }

            CheckBox radioCheckBox = (CheckBox) view.findViewById(R.id.radioCheckBox);
            TextView codeTextView = (TextView) view.findViewById(R.id.codeTextView);

            radioCheckBox.setText(countryCode[0]);
            codeTextView.setText(countryCode[1]);

            if (mSelectedIndex == position)
            {
                codeTextView.setSelected(true);
                radioCheckBox.setChecked(true);
            } else
            {
                codeTextView.setSelected(false);
                radioCheckBox.setChecked(false);
            }
        }

        return view;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == TYPE_SECTION;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (Util.isTextEmpty(getItem(position)[1]) == true)
        {
            return TYPE_SECTION;
        } else
        {
            return TYPE_ENTRY;
        }
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }
}
