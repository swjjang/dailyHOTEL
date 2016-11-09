package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bank;

import java.util.List;

public class BankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Bank> mList;
    private Context mContext;

    public BankListAdapter(Context context, List<Bank> list)
    {
        mContext = context;
        mList = list;
    }

    public Bank getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    public void setData(List<Bank> list)
    {
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_coupon, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        ((BankViewHolder) holder).onBindViewHolder(position);
    }

    private class BankViewHolder extends RecyclerView.ViewHolder
    {
        private TextView bankNameTextView;
        private View dividerView;

        public BankViewHolder(View view)
        {
            super(view);

            bankNameTextView = (TextView) view.findViewById(R.id.textView);
            dividerView = view.findViewById(R.id.dividerView);
        }

        public void onBindViewHolder(int position)
        {
            Bank bank = getItem(position);

            bankNameTextView.setText(bank.name);

            if (position == getItemCount() - 1)
            {
                dividerView.setVisibility(View.GONE);
            } else
            {
                dividerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
