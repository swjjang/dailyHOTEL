package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon on 2016. 12. 21..
 */

public class EmailCompleteAdapter extends ArrayAdapter<String>
{
    private Context mContext;

    List<String> mFilterEmailPostfixList;
    private List<String> mEmailPostfixList;

    public EmailCompleteAdapter(Context context, List<String> emailList)
    {
        super(context, 0, emailList);

        mContext = context;

        mFilterEmailPostfixList = new ArrayList<>();
        mEmailPostfixList = emailList;
    }

    @Override
    public int getCount()
    {
        return mFilterEmailPostfixList.size();
    }

    @Override
    public Filter getFilter()
    {
        return new EmailFilter(this, mEmailPostfixList);
    }

    @Nullable
    @Override
    public String getItem(int position)
    {
        if (mFilterEmailPostfixList.size() <= position)
        {
            return "";
        }

        return mFilterEmailPostfixList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;

        if (convertView == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_autocomplete_email_item, parent, false);
        } else
        {
            view = convertView;
        }

        String emailPostfix = mFilterEmailPostfixList.get(position);

        TextView textView = (TextView) view;
        textView.setText(emailPostfix);

        return view;
    }

    class EmailFilter extends Filter
    {
        private EmailCompleteAdapter mAdapter;
        private List<String> mOriginalList;
        private List<String> mFilteredList;

        public EmailFilter(EmailCompleteAdapter adapter, List<String> originalList)
        {
            mAdapter = adapter;
            mOriginalList = originalList;
            mFilteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            mFilteredList.clear();

            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0)
            {
            } else
            {
                String email = constraint.toString().trim();

                int atSignPosition = email.indexOf('@');

                if (atSignPosition >= 0)
                {
                    String emailPrefix = email.substring(0, atSignPosition);
                    String emailPostfix = email.substring(atSignPosition);

                    for (String companyEmailPostfix : mOriginalList)
                    {
                        if (companyEmailPostfix.startsWith(emailPostfix) == true)
                        {
                            mFilteredList.add(emailPrefix + companyEmailPostfix);
                        }
                    }
                }
            }

            results.values = mFilteredList;
            results.count = mFilteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            if (mAdapter == null)
            {
                return;
            }

            mAdapter.mFilterEmailPostfixList.clear();

            if (results.values != null)
            {
                mAdapter.mFilterEmailPostfixList.addAll((List) results.values);
            }

            mAdapter.notifyDataSetChanged();
        }
    }
}
