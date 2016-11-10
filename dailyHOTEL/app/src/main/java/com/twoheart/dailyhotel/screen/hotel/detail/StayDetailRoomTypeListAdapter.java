package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayDetailRoomTypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<RoomInformation> mRoomInformationList;
    private View.OnClickListener mOnClickListener;
    private int mSelectedPosition;
    private int mViewPriceType;

    public StayDetailRoomTypeListAdapter(Context context, ArrayList<RoomInformation> arrayList, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        mRoomInformationList = new ArrayList<>();
        mRoomInformationList.addAll(arrayList);
        mViewPriceType = StayDetailLayout.VIEW_AVERAGE_PRICE;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<? extends RoomInformation> collection)
    {
        if (collection == null)
        {
            return;
        }

        mRoomInformationList.clear();
        mRoomInformationList.addAll(collection);
    }

    public void setSelected(int position)
    {
        mSelectedPosition = position;
    }

    public int setSelectIndex(int index)
    {
        if(mRoomInformationList == null)
        {
            return 0;
        }

        int size = mRoomInformationList.size();

        for(int i = 0; i < size; i++)
        {
            RoomInformation roomInformation = mRoomInformationList.get(i);

            if(roomInformation.roomIndex == index)
            {
                setSelected(i);
                notifyDataSetChanged();
                return i;
            }
        }

        return 0;
    }

    public RoomInformation getItem(int position)
    {
        if (mRoomInformationList.size() <= position)
        {
            return null;
        }

        return mRoomInformationList.get(position);
    }

    public void setChangedViewPrice(int type)
    {
        mViewPriceType = type;
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
        RoomInformation roomInformation = getItem(position);

        if (roomInformation == null)
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

        saleRoomInformationViewHolder.roomTypeTextView.setText(roomInformation.roomName);

        String price, discountPrice;

        if (mViewPriceType == StayDetailLayout.VIEW_TOTAL_PRICE)
        {
            price = Util.getPriceFormat(mContext, roomInformation.price * roomInformation.nights, false);
            discountPrice = Util.getPriceFormat(mContext, roomInformation.totalDiscount, false);
        } else
        {
            price = Util.getPriceFormat(mContext, roomInformation.price, false);
            discountPrice = Util.getPriceFormat(mContext, roomInformation.averageDiscount, false);
        }

        if (roomInformation.price <= 0 || roomInformation.price <= roomInformation.averageDiscount)
        {
            saleRoomInformationViewHolder.priceTextView.setVisibility(View.GONE);
            saleRoomInformationViewHolder.priceTextView.setText(null);
        } else
        {
            saleRoomInformationViewHolder.priceTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.priceTextView.setText(price);
        }

        saleRoomInformationViewHolder.discountPriceTextView.setText(discountPrice);

        if (Util.isTextEmpty(roomInformation.option) == true)
        {
            saleRoomInformationViewHolder.optionTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.optionTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.optionTextView.setText(roomInformation.option);
        }

        if (Util.isTextEmpty(roomInformation.amenities) == true)
        {
            saleRoomInformationViewHolder.amenitiesTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.amenitiesTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.amenitiesTextView.setText(roomInformation.amenities);
        }

        if (Util.isTextEmpty(roomInformation.roomBenefit) == true)
        {
            saleRoomInformationViewHolder.benefitTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.benefitTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.benefitTextView.setText(roomInformation.roomBenefit);
        }

        if (roomInformation.isNRD == false)
        {
            saleRoomInformationViewHolder.nrdTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.nrdTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount()
    {
        if (mRoomInformationList == null)
        {
            return 0;
        }

        return mRoomInformationList.size();
    }

    private class SaleRoomInformationViewHolder extends RecyclerView.ViewHolder
    {
        View viewRoot;
        TextView roomTypeTextView;
        TextView priceTextView;
        TextView discountPriceTextView;
        TextView optionTextView;
        TextView amenitiesTextView;
        TextView benefitTextView;
        TextView nrdTextView;

        public SaleRoomInformationViewHolder(View itemView)
        {
            super(itemView);

            viewRoot = itemView;

            roomTypeTextView = (TextView) itemView.findViewById(R.id.roomTypeTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            discountPriceTextView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            optionTextView = (TextView) itemView.findViewById(R.id.optionTextView);
            amenitiesTextView = (TextView) itemView.findViewById(R.id.amenitiesTextView);
            benefitTextView = (TextView) itemView.findViewById(R.id.benefitTextView);
            nrdTextView = (TextView) itemView.findViewById(R.id.nrdTextView);

            itemView.setOnClickListener(mOnClickListener);
        }
    }
}
