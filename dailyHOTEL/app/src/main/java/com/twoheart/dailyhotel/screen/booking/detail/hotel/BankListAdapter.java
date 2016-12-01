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

public class BankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener
{
    private List<Bank> mList;
    private Context mContext;
    private Bank mSelectedBank;
    private View.OnClickListener mOnClickListener;

    public BankListAdapter(Context context, List<Bank> list)
    {
        mContext = context;
        mList = list;

        mSelectedBank = null;
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

    public Bank getSelectedBank()
    {
        return mSelectedBank;
    }

    public void setSelectedBank(Bank bank)
    {
        mSelectedBank = bank;
    }

    public void setOnItemClickListener(View.OnClickListener listener)
    {
        mOnClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        ((BankViewHolder) holder).onBindViewHolder(position);
    }

    @Override
    public void onClick(View v)
    {
        Object tag = v.getTag();

        if (tag != null && tag instanceof Bank)
        {
            mSelectedBank = (Bank) tag;

            if (mOnClickListener != null)
            {
                mOnClickListener.onClick(v);
            }

            notifyDataSetChanged();
        }
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

            bankNameTextView.setOnClickListener(BankListAdapter.this);
        }

        public void onBindViewHolder(int position)
        {
            Bank bank = getItem(position);

            bankNameTextView.setText(bank.name);
            bankNameTextView.setTag(bank);

            if (mSelectedBank != null && mSelectedBank.code.equalsIgnoreCase(bank.code) == true)
            {
                bankNameTextView.setSelected(true);
                bankNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);
            } else
            {
                bankNameTextView.setSelected(false);
                bankNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

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
