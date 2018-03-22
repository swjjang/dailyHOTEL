package com.daily.dailyhotel.screen.home.stay.inbound.detail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayRoom;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutStayDetailRoomDataBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayDetailRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private List<StayRoom> mStayRoomList;
    View.OnClickListener mOnClickListener;
    private int mSelectedPosition;
    private StayDetailPresenter.PriceType mPriceType;
    private int mNights;
    private boolean mRewardEnabled;

    public StayDetailRoomListAdapter(Context context, List<StayRoom> arrayList, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        addAll(arrayList);
        setPriceType(StayDetailPresenter.PriceType.TOTAL);
    }

    public void addAll(Collection<? extends StayRoom> collection)
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

    public void setNights(int nights)
    {
        mNights = nights;
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public void setSelected(int position)
    {
        mSelectedPosition = position;
    }

    public StayRoom getItem(int position)
    {
        if (mStayRoomList.size() <= position)
        {
            return null;
        }

        return mStayRoomList.get(position);
    }

    public void setPriceType(StayDetailPresenter.PriceType priceType)
    {
        mPriceType = priceType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutStayDetailRoomDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_stay_detail_room_data, parent, false);

        return new SaleRoomInformationViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        StayRoom stayRoom = getItem(position);

        if (stayRoom == null)
        {
            return;
        }

        SaleRoomInformationViewHolder saleRoomInformationViewHolder = (SaleRoomInformationViewHolder) holder;

        if (mSelectedPosition == position)
        {
            saleRoomInformationViewHolder.dataBinding.getRoot().setSelected(true);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.getRoot().setSelected(false);
        }

        saleRoomInformationViewHolder.dataBinding.roomTypeTextView.setText(stayRoom.name);

        String price = null;
        String discountPrice;

        switch (mPriceType)
        {
            case TOTAL:
            {
                if (stayRoom.price > 0 && stayRoom.price > stayRoom.discountAverage)
                {
                    price = DailyTextUtils.getPriceFormat(mContext, stayRoom.price * mNights, false);
                }

                discountPrice = DailyTextUtils.getPriceFormat(mContext, stayRoom.discountTotal, false);
                break;
            }

            case AVERAGE:
            default:
            {
                if (stayRoom.price > 0 && stayRoom.price > stayRoom.discountAverage)
                {
                    price = DailyTextUtils.getPriceFormat(mContext, stayRoom.price, false);
                }

                discountPrice = DailyTextUtils.getPriceFormat(mContext, stayRoom.discountAverage, false);
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

        if (DailyTextUtils.isTextEmpty(stayRoom.description1) == true)
        {
            saleRoomInformationViewHolder.dataBinding.optionTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.optionTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.optionTextView.setText(stayRoom.description1);
        }

        if (DailyTextUtils.isTextEmpty(stayRoom.description2) == true)
        {
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.amenitiesTextView.setText(stayRoom.description2);
        }

        if (DailyTextUtils.isTextEmpty(stayRoom.benefit) == true)
        {
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setVisibility(View.GONE);
        } else
        {
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.dataBinding.benefitTextView.setText(stayRoom.benefit);
        }

        saleRoomInformationViewHolder.dataBinding.nrdTextView.setVisibility(stayRoom.nrd ? View.VISIBLE : View.GONE);

        saleRoomInformationViewHolder.dataBinding.rewardTextView.setVisibility((mRewardEnabled && stayRoom.provideRewardSticker) ? View.VISIBLE : View.GONE);

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
        LayoutStayDetailRoomDataBinding dataBinding;

        public SaleRoomInformationViewHolder(LayoutStayDetailRoomDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            dataBinding.getRoot().setOnClickListener(mOnClickListener);
        }
    }
}
