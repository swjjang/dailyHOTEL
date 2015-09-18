package com.twoheart.dailyhotel.adapter;

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

public class CreditListAdapter extends ArrayAdapter<Credit>
{

    private List<Credit> mItems;
    private Context mContext;
    private int mResourceId;

    public CreditListAdapter(Context context, int resourceId, List<Credit> mCreditList)
    {
        super(context, resourceId, mCreditList);
        this.mItems = mCreditList;
        this.mContext = context;
        this.mResourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(mResourceId, null);
            v.setTag(position);
        }

        Credit element = mItems.get(position);

        if (element != null)
        {
            TextView bonus = (TextView) v.findViewById(R.id.list_row_credit_bonus);
            TextView content = (TextView) v.findViewById(R.id.list_row_credit_content);
            TextView expires = (TextView) v.findViewById(R.id.list_row_credit_expires);

            DecimalFormat comma = new DecimalFormat("###,##0");
            String strBonus = comma.format(element.getBonus());

            bonus.setText("₩" + strBonus);
            content.setText(element.getContent());
            expires.setText(mContext.getString(R.string.prefix_expire_time) + ", " + element.getExpires() + "");
        }

        // pinkred_font
        //		GlobalFont.apply((ViewGroup) v);
        return v;
    }

}
