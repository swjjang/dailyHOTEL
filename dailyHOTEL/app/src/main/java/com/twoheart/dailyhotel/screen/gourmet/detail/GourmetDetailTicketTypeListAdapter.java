package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GourmetDetailTicketTypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<TicketInformation> mTicketInformationList;
    private View.OnClickListener mOnClickListener;
    private int mSelectedPosition;


    public GourmetDetailTicketTypeListAdapter(Context context, ArrayList<TicketInformation> arrayList, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        mTicketInformationList = new ArrayList<>();
        mTicketInformationList.addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<? extends TicketInformation> collection)
    {
        if (collection == null)
        {
            return;
        }

        mTicketInformationList.clear();
        mTicketInformationList.addAll(collection);
    }

    public void setSelected(int position)
    {
        mSelectedPosition = position;
    }

    public int setSelectIndex(int index)
    {
        if (mTicketInformationList == null)
        {
            return 0;
        }

        int size = mTicketInformationList.size();

        for (int i = 0; i < size; i++)
        {
            TicketInformation roomInformation = mTicketInformationList.get(i);

            if (roomInformation.index == index)
            {
                setSelected(i);
                notifyDataSetChanged();
                return i;
            }
        }

        return 0;
    }

    public TicketInformation getItem(int position)
    {
        if (mTicketInformationList.size() <= position)
        {
            return null;
        }

        return mTicketInformationList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.list_row_detail_tickettype, parent, false);

        return new TicketInformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        TicketInformation ticketInformation = getItem(position);

        if (ticketInformation == null)
        {
            return;
        }

        TicketInformationViewHolder ticketInformationViewHolder = (TicketInformationViewHolder) holder;

        ticketInformationViewHolder.viewRoot.setTag(position);

        if (mSelectedPosition == position)
        {
            ticketInformationViewHolder.viewRoot.setSelected(true);
        } else
        {
            ticketInformationViewHolder.viewRoot.setSelected(false);
        }

        ticketInformationViewHolder.nameTextView.setText(ticketInformation.name);

        String price = Util.getPriceFormat(mContext, ticketInformation.price, false);
        String discountPrice = Util.getPriceFormat(mContext, ticketInformation.discountPrice, false);

        if (ticketInformation.price <= 0 || ticketInformation.price <= ticketInformation.discountPrice)
        {
            ticketInformationViewHolder.priceTextView.setVisibility(View.GONE);
            ticketInformationViewHolder.priceTextView.setText(null);
        } else
        {
            ticketInformationViewHolder.priceTextView.setVisibility(View.VISIBLE);
            ticketInformationViewHolder.priceTextView.setText(price);
        }

        ticketInformationViewHolder.discountPriceTextView.setText(discountPrice);

        if (Util.isTextEmpty(ticketInformation.option) == true)
        {
            ticketInformationViewHolder.optionTextView.setVisibility(View.GONE);
        } else
        {
            ticketInformationViewHolder.optionTextView.setVisibility(View.VISIBLE);
            ticketInformationViewHolder.optionTextView.setText(ticketInformation.option);
        }

        ticketInformationViewHolder.amenitiesTextView.setVisibility(View.GONE);

        if (Util.isTextEmpty(ticketInformation.benefit) == true)
        {
            ticketInformationViewHolder.benefitTextView.setVisibility(View.GONE);
        } else
        {
            ticketInformationViewHolder.benefitTextView.setVisibility(View.VISIBLE);
            ticketInformationViewHolder.benefitTextView.setText(ticketInformation.benefit);
        }
    }

    @Override
    public int getItemCount()
    {
        if (mTicketInformationList == null)
        {
            return 0;
        }

        return mTicketInformationList.size();
    }

    private class TicketInformationViewHolder extends RecyclerView.ViewHolder
    {
        View viewRoot;
        TextView nameTextView;
        TextView priceTextView;
        TextView discountPriceTextView;
        TextView optionTextView;
        TextView amenitiesTextView;
        TextView benefitTextView;

        public TicketInformationViewHolder(View itemView)
        {
            super(itemView);

            viewRoot = itemView;

            nameTextView = (TextView) itemView.findViewById(R.id.roomTypeTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            discountPriceTextView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            optionTextView = (TextView) itemView.findViewById(R.id.optionTextView);
            amenitiesTextView = (TextView) itemView.findViewById(R.id.amenitiesTextView);
            benefitTextView = (TextView) itemView.findViewById(R.id.benefitTextView);

            itemView.setOnClickListener(mOnClickListener);
        }
    }
}
