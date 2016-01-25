package com.twoheart.dailyhotel.screen.hoteldetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HotelDetailRoomTypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SaleRoomInformation> mSaleRoomInformationList;
    private View.OnClickListener mOnClickListener;
    private int mSelectedPosition;


    public HotelDetailRoomTypeListAdapter(Context context, ArrayList<SaleRoomInformation> arrayList, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        mSaleRoomInformationList = new ArrayList<>();
        mSaleRoomInformationList.addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<? extends SaleRoomInformation> collection)
    {
        if (collection == null)
        {
            return;
        }

        mSaleRoomInformationList.clear();
        mSaleRoomInformationList.addAll(collection);
    }

    public void setSelected(int position)
    {
        mSelectedPosition = position;
    }

    public SaleRoomInformation getItem(int position)
    {
        if (mSaleRoomInformationList.size() <= position)
        {
            return null;
        }

        return mSaleRoomInformationList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.list_row_detail_roomtype, parent, false);

        return new SaleRoomInformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        SaleRoomInformation saleRoomInformation = getItem(position);

        if (saleRoomInformation == null)
        {
            return;
        }

        SaleRoomInformationViewHolder saleRoomInformationViewHolder = (SaleRoomInformationViewHolder) holder;

        saleRoomInformationViewHolder.viewRoot.setTag(position);

        if (mSelectedPosition == position)
        {
            saleRoomInformationViewHolder.viewRoot.setSelected(true);
        } else
        {
            saleRoomInformationViewHolder.viewRoot.setSelected(false);
        }

        saleRoomInformationViewHolder.roomTypeTextView.setText(saleRoomInformation.roomName);

        DecimalFormat comma = new DecimalFormat("###,##0");
        String currency = mContext.getString(R.string.currency);
        String price = comma.format(saleRoomInformation.averageDiscount);

        saleRoomInformationViewHolder.priceTextView.setText(price + currency);

        if (Util.isTextEmpty(saleRoomInformation.option) == true)
        {
            saleRoomInformationViewHolder.optionTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.optionTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.optionTextView.setText(saleRoomInformation.option);
        }

        if (Util.isTextEmpty(saleRoomInformation.roomBenefit) == true)
        {
            saleRoomInformationViewHolder.benefitTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.benefitTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.benefitTextView.setText(saleRoomInformation.roomBenefit);
        }
    }

    @Override
    public int getItemCount()
    {
        if (mSaleRoomInformationList == null)
        {
            return 0;
        }

        return mSaleRoomInformationList.size();
    }

    private class SaleRoomInformationViewHolder extends RecyclerView.ViewHolder
    {
        View viewRoot;
        TextView roomTypeTextView;
        TextView priceTextView;
        TextView optionTextView;
        TextView benefitTextView;

        public SaleRoomInformationViewHolder(View itemView)
        {
            super(itemView);

            viewRoot = itemView;

            roomTypeTextView = (TextView) itemView.findViewById(R.id.roomTypeTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            optionTextView = (TextView) itemView.findViewById(R.id.optionTextView);
            benefitTextView = (TextView) itemView.findViewById(R.id.benefitTextView);

            itemView.setOnClickListener(mOnClickListener);
        }
    }
}
