package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailRoomDataBinding;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayOutboundDetailRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private List<StayOutboundRoom> mStayRoomList;
    View.OnClickListener mOnClickListener;
    private int mSelectedPosition;
    private int mViewPriceType;

    public StayOutboundDetailRoomListAdapter(Context context, List<StayOutboundRoom> arrayList, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        addAll(arrayList);
        mViewPriceType = StayDetailLayout.VIEW_AVERAGE_PRICE;
    }

    public void addAll(Collection<? extends StayOutboundRoom> collection)
    {
        if (collection == null || collection.size() == 0)
        {
            return;
        }

        if (mStayRoomList == null)
        {
            mStayRoomList = new ArrayList<>(collection.size());
        }

        mStayRoomList.clear();
        mStayRoomList.addAll(collection);
    }

    public void setSelected(int position)
    {
        mSelectedPosition = position;
    }

    public StayOutboundRoom getItem(int position)
    {
        if (mStayRoomList.size() <= position)
        {
            return null;
        }

        return mStayRoomList.get(position);
    }

    public void setChangedViewPrice(int type)
    {
        mViewPriceType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutStayOutboundDetailRoomDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_stay_outbound_detail_room_data, parent, false);

        SaleRoomInformationViewHolder viewHolder = new SaleRoomInformationViewHolder(dataBinding.getRoot());
        viewHolder.setViewDataBinding(dataBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        StayOutboundRoom stayOutboundRoom = getItem(position);

        if (stayOutboundRoom == null)
        {
            return;
        }

        SaleRoomInformationViewHolder saleRoomInformationViewHolder = (SaleRoomInformationViewHolder) holder;

        saleRoomInformationViewHolder.dataBinding.getRoot().setTag(position);

        if (mSelectedPosition == position)
        {
            saleRoomInformationViewHolder.dataBinding.getRoot().setSelected(true);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.getRoot().setSelected(false);
        }

        boolean isMaxLine = true;

        saleRoomInformationViewHolder.dataBinding.roomTypeTextView.setText(stayOutboundRoom.roomName);

        String price, discountPrice;

        if (mViewPriceType == StayDetailLayout.VIEW_TOTAL_PRICE)
        {
            if (stayOutboundRoom.promotion == true)
            {
                try
                {
                    price = DailyTextUtils.getPriceFormat(mContext, Integer.parseInt(stayOutboundRoom.baseKrw), false);
                } catch (Exception e)
                {
                    price = null;
                }
            } else
            {
                price = null;
            }

            try
            {
                discountPrice = DailyTextUtils.getPriceFormat(mContext, Integer.parseInt(stayOutboundRoom.totalKrw), false);
            } catch (Exception e)
            {
                discountPrice = stayOutboundRoom.totalKrw;
            }
        } else
        {
            if (stayOutboundRoom.promotion == true)
            {
                try
                {
                    price = DailyTextUtils.getPriceFormat(mContext, Integer.parseInt(stayOutboundRoom.baseNightlyKrw), false);
                } catch (Exception e)
                {
                    price = null;
                }
            } else
            {
                price = null;
            }

            try
            {
                discountPrice = DailyTextUtils.getPriceFormat(mContext, Integer.parseInt(stayOutboundRoom.nightlyKrw), false);
            } catch (Exception e)
            {
                discountPrice = stayOutboundRoom.nightlyKrw;
            }
        }

        if (DailyTextUtils.isTextEmpty(price) == true)
        {
            saleRoomInformationViewHolder.dataBinding.priceTextView.setVisibility(View.GONE);
            saleRoomInformationViewHolder.dataBinding.priceTextView.setText(null);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.priceTextView.setPaintFlags(saleRoomInformationViewHolder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            saleRoomInformationViewHolder.dataBinding.priceTextView.setText(price);
        }

        saleRoomInformationViewHolder.dataBinding.discountPriceTextView.setText(discountPrice);

        String personOption;

        if (stayOutboundRoom.quotedOccupancy == 0 || stayOutboundRoom.rateOccupancyPerRoom == 0)
        {
            personOption = null;
        } else if (stayOutboundRoom.quotedOccupancy == stayOutboundRoom.rateOccupancyPerRoom)
        {
            personOption = mContext.getString(R.string.label_stay_outbound__room_max_person_free, stayOutboundRoom.quotedOccupancy)//
                + "/" + mContext.getString(R.string.label_stay_outbound__room_max_person_free, stayOutboundRoom.rateOccupancyPerRoom);
        } else
        {
            personOption = mContext.getString(R.string.label_stay_outbound__room_max_person_free, stayOutboundRoom.quotedOccupancy)//
                + "/" + mContext.getString(R.string.label_stay_outbound__room_max_person_charge, stayOutboundRoom.rateOccupancyPerRoom);
        }

        if (DailyTextUtils.isTextEmpty(personOption) == true)
        {
            saleRoomInformationViewHolder.dataBinding.optionTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.dataBinding.optionTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.optionTextView.setText(personOption);
        }

        if (DailyTextUtils.isTextEmpty(stayOutboundRoom.valueAddName) == true)
        {
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setText(stayOutboundRoom.valueAddName);

            float width = DailyTextUtils.getTextWidth(mContext, stayOutboundRoom.valueAddName //
                , saleRoomInformationViewHolder.dataBinding.amenitiesTextView.getTextSize() //
                , saleRoomInformationViewHolder.dataBinding.amenitiesTextView.getTypeface());

            int viewWidth = saleRoomInformationViewHolder.dataBinding.amenitiesTextView.getWidth() //
                - saleRoomInformationViewHolder.dataBinding.amenitiesTextView.getPaddingLeft() //
                - saleRoomInformationViewHolder.dataBinding.amenitiesTextView.getPaddingRight();

            if (width <= viewWidth)
            {
                isMaxLine = false;
            }
        }

        if (DailyTextUtils.isTextEmpty(stayOutboundRoom.promotionDescription) == true)
        {
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setText(stayOutboundRoom.promotionDescription);
        }

        if (stayOutboundRoom.nonRefundable == false)
        {
            saleRoomInformationViewHolder.dataBinding.nrdTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.dataBinding.nrdTextView.setVisibility(View.VISIBLE);
        }

        int layoutHeight;
        if (isMaxLine == true)
        {
            layoutHeight = mContext.getResources().getDimensionPixelSize(R.dimen.stay_detail_room_type_expand_height);
        } else
        {
            layoutHeight = mContext.getResources().getDimensionPixelSize(R.dimen.stay_detail_room_type_default_height);
        }

        ViewGroup.LayoutParams params = saleRoomInformationViewHolder.dataBinding.getRoot().getLayoutParams();
        if (params == null)
        {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
        } else
        {
            params.height = layoutHeight;
        }

        saleRoomInformationViewHolder.dataBinding.getRoot().setLayoutParams(params);
    }

    @Override
    public int getItemCount()
    {
        if (mStayRoomList == null)
        {
            return 0;
        }

        return mStayRoomList.size();
    }

    public class SaleRoomInformationViewHolder extends RecyclerView.ViewHolder
    {
        LayoutStayOutboundDetailRoomDataBinding dataBinding;

        public SaleRoomInformationViewHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(mOnClickListener);
        }

        public void setViewDataBinding(LayoutStayOutboundDetailRoomDataBinding dataBinding)
        {
            this.dataBinding = dataBinding;
        }
    }
}
