package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Credit;

import java.text.DecimalFormat;
import java.util.List;

public class BonusListAdapter extends ArrayAdapter<Credit>
{
    private List<Credit> mItems;
    private Context mContext;
    private int mResourceId;

    public BonusListAdapter(Context context, int resourceId, List<Credit> mCreditList)
    {
        super(context, resourceId, mCreditList);
        mItems = mCreditList;
        mContext = context;
        mResourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;

        if (convertView == null)
        {
            view = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
        } else
        {
            view = convertView;
        }

        Credit element = mItems.get(position);

        if (element != null)
        {
            TextView bonus = (TextView) view.findViewById(R.id.bonusTextView);
            TextView content = (TextView) view.findViewById(R.id.contentTextView);
            TextView expires = (TextView) view.findViewById(R.id.expireTextView);

            DecimalFormat comma = new DecimalFormat("###,##0");
            String strBonus = comma.format(element.getBonus());

            bonus.setText(strBonus + mContext.getString(R.string.currency));
            content.setText(element.getContent());
            expires.setText(mContext.getString(R.string.prefix_expire_time) + " : " + element.getExpires());
        }

        return view;
    }
}
