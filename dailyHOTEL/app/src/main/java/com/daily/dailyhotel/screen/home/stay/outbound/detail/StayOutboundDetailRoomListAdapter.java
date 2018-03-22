package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailRoomDataBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayOutboundDetailRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private List<StayOutboundRoom> mStayRoomList;
    View.OnClickListener mOnClickListener;
    private int mSelectedPosition;
    private StayOutboundDetailPresenter.PriceType mPriceType;
    private boolean mRewardEnabled;

    public StayOutboundDetailRoomListAdapter(Context context, List<StayOutboundRoom> arrayList, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        addAll(arrayList);
        mPriceType = StayOutboundDetailPresenter.PriceType.AVERAGE;
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

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public StayOutboundRoom getItem(int position)
    {
        if (mStayRoomList.size() <= position)
        {
            return null;
        }

        return mStayRoomList.get(position);
    }

    public void setPriceType(StayOutboundDetailPresenter.PriceType priceType)
    {
        mPriceType = priceType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutStayOutboundDetailRoomDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_stay_outbound_detail_room_data, parent, false);

        return new SaleRoomInformationViewHolder(dataBinding);
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

        saleRoomInformationViewHolder.dataBinding.roomTypeTextView.setText(stayOutboundRoom.roomName);

        String price, discountPrice;

        switch (mPriceType)
        {
            case TOTAL:
            {
                if (stayOutboundRoom.total < stayOutboundRoom.base)
                {
                    price = DailyTextUtils.getPriceFormat(mContext, stayOutboundRoom.base, false);
                } else
                {
                    price = null;
                }

                discountPrice = DailyTextUtils.getPriceFormat(mContext, stayOutboundRoom.total, false);
                break;
            }

            case AVERAGE:
            default:
            {
                if (stayOutboundRoom.nightly < stayOutboundRoom.baseNightly)
                {
                    price = DailyTextUtils.getPriceFormat(mContext, stayOutboundRoom.baseNightly, false);
                } else
                {
                    price = null;
                }

                discountPrice = DailyTextUtils.getPriceFormat(mContext, stayOutboundRoom.nightly, false);
                break;
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
            personOption = mContext.getString(R.string.label_stay_outbound_room_default_person, stayOutboundRoom.quotedOccupancy)//
                + "/" + mContext.getString(R.string.label_stay_outbound_room_max_person_free, stayOutboundRoom.rateOccupancyPerRoom);
        } else
        {
            personOption = mContext.getString(R.string.label_stay_outbound_room_default_person, stayOutboundRoom.quotedOccupancy)//
                + "/" + mContext.getString(R.string.label_stay_outbound_room_max_person_charge, stayOutboundRoom.rateOccupancyPerRoom);
        }

        if (DailyTextUtils.isTextEmpty(personOption) == true)
        {
            saleRoomInformationViewHolder.dataBinding.optionTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.optionTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.optionTextView.setText(personOption);
        }

        if (DailyTextUtils.isTextEmpty(stayOutboundRoom.valueAddName) == true)
        {
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setText(stayOutboundRoom.valueAddName);
        }

        if (stayOutboundRoom.promotion == true && DailyTextUtils.isTextEmpty(stayOutboundRoom.promotionDescription) == false)
        {
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setText(stayOutboundRoom.promotionDescription);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setVisibility(View.GONE);
        }

        if (stayOutboundRoom.nonRefundable == false || DailyTextUtils.isTextEmpty(stayOutboundRoom.nonRefundableDescription) == true)
        {
            saleRoomInformationViewHolder.dataBinding.nrdTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.nrdTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.nrdTextView.setText(stayOutboundRoom.nonRefundableDescription);
        }

        saleRoomInformationViewHolder.dataBinding.rewardTextView.setVisibility((mRewardEnabled && stayOutboundRoom.provideRewardSticker) ? View.VISIBLE : View.GONE);

        // 마지막 목록에는 하단에 10dp여유를 준다.
        if (position == getItemCount() - 1)
        {
            saleRoomInformationViewHolder.dataBinding.getRoot().setPadding(0, 0, 0, ScreenUtils.dpToPx(mContext, 10));
        } else
        {
            saleRoomInformationViewHolder.dataBinding.getRoot().setPadding(0, 0, 0, 0);
        }
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

        public SaleRoomInformationViewHolder(LayoutStayOutboundDetailRoomDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            dataBinding.getRoot().setOnClickListener(mOnClickListener);
        }
    }
}
