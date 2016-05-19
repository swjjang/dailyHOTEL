package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;

import java.text.DecimalFormat;
import java.util.List;

public class BonusListAdapter extends ArrayAdapter<Bonus>
{
    private List<Bonus> mBonusList;
    private Context mContext;

    public BonusListAdapter(Context context, int resourceId, List<Bonus> list)
    {
        super(context, resourceId, list);

        mContext = context;
        mBonusList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;

        if (convertView == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_row_bonus, parent, false);
        } else
        {
            view = convertView;
        }

        Bonus bonus = mBonusList.get(position);

        TextView bonusTextView = (TextView) view.findViewById(R.id.bonusTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        TextView expireTextView = (TextView) view.findViewById(R.id.expireTextView);
        View underLineView = view.findViewById(R.id.underLineView);

        String format = null;

        if (bonus.bonus > 0)
        {
            format = "+###,##0";
        } else
        {
            format = "-###,##0";
        }

        DecimalFormat comma = new DecimalFormat(format);
        String strBonus = comma.format(bonus.bonus);

        bonusTextView.setText(strBonus + mContext.getString(R.string.currency));
        contentTextView.setText(bonus.content);
        expireTextView.setText(mContext.getString(R.string.prefix_expire_time) + " : " + bonus.expires);

        if (position == mBonusList.size() - 1)
        {
            underLineView.setVisibility(View.VISIBLE);
        } else
        {
            underLineView.setVisibility(View.GONE);
        }

        return view;
    }
}
