package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.StayProduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayDetailRoomTypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<StayProduct> mStayProductList;
    View.OnClickListener mOnClickListener;
    private int mSelectedPosition;
    private int mViewPriceType;
    private int mNights;

    public StayDetailRoomTypeListAdapter(Context context, List<StayProduct> arrayList, int nights, View.OnClickListener listener)
    {
        mContext = context;
        mOnClickListener = listener;

        mStayProductList = new ArrayList<>();
        mStayProductList.addAll(arrayList);
        mViewPriceType = StayDetailLayout.VIEW_AVERAGE_PRICE;

        mNights = nights;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<? extends StayProduct> collection, int nights)
    {
        if (collection == null)
        {
            return;
        }

        mStayProductList.clear();
        mStayProductList.addAll(collection);

        mNights = nights;
    }

    public void setSelected(int position)
    {
        mSelectedPosition = position;
    }

    public int setSelectIndex(int index)
    {
        if (mStayProductList == null)
        {
            return 0;
        }

        int size = mStayProductList.size();

        for (int i = 0; i < size; i++)
        {
            StayProduct stayProduct = mStayProductList.get(i);

            if (stayProduct.roomIndex == index)
            {
                setSelected(i);
                notifyDataSetChanged();
                return i;
            }
        }

        return 0;
    }

    public StayProduct getItem(int position)
    {
        if (mStayProductList.size() <= position)
        {
            return null;
        }

        return mStayProductList.get(position);
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
        StayProduct stayProduct = getItem(position);

        if (stayProduct == null)
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

        boolean isMaxLine = true;

        saleRoomInformationViewHolder.roomTypeTextView.setText(stayProduct.roomName);

        String price, discountPrice;

        if (mViewPriceType == StayDetailLayout.VIEW_TOTAL_PRICE)
        {
            price = com.daily.base.util.TextUtils.getPriceFormat(mContext, stayProduct.price * mNights, false);
            discountPrice = com.daily.base.util.TextUtils.getPriceFormat(mContext, stayProduct.totalDiscount, false);
        } else
        {
            price = com.daily.base.util.TextUtils.getPriceFormat(mContext, stayProduct.price, false);
            discountPrice = com.daily.base.util.TextUtils.getPriceFormat(mContext, stayProduct.averageDiscount, false);
        }

        if (stayProduct.price <= 0 || stayProduct.price <= stayProduct.averageDiscount)
        {
            saleRoomInformationViewHolder.priceTextView.setVisibility(View.GONE);
            saleRoomInformationViewHolder.priceTextView.setText(null);
        } else
        {
            saleRoomInformationViewHolder.priceTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.priceTextView.setText(price);
        }

        saleRoomInformationViewHolder.discountPriceTextView.setText(discountPrice);

        if (com.daily.base.util.TextUtils.isTextEmpty(stayProduct.option) == true)
        {
            saleRoomInformationViewHolder.optionTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.optionTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.optionTextView.setText(stayProduct.option);
        }

        if (com.daily.base.util.TextUtils.isTextEmpty(stayProduct.amenities) == true)
        {
            saleRoomInformationViewHolder.amenitiesTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.amenitiesTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.amenitiesTextView.setText(stayProduct.amenities);

            float width = com.daily.base.util.TextUtils.getTextWidth(mContext, stayProduct.amenities //
                , saleRoomInformationViewHolder.amenitiesTextView.getTextSize() //
                , saleRoomInformationViewHolder.amenitiesTextView.getTypeface());

            int viewWidth = saleRoomInformationViewHolder.amenitiesTextView.getWidth() //
                - saleRoomInformationViewHolder.amenitiesTextView.getPaddingLeft() //
                - saleRoomInformationViewHolder.amenitiesTextView.getPaddingRight();

            if (width <= viewWidth)
            {
                isMaxLine = false;
            }
        }

        if (com.daily.base.util.TextUtils.isTextEmpty(stayProduct.roomBenefit) == true)
        {
            saleRoomInformationViewHolder.benefitTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.benefitTextView.setVisibility(View.VISIBLE);
            saleRoomInformationViewHolder.benefitTextView.setText(stayProduct.roomBenefit);
        }

        if (stayProduct.isNRD == false)
        {
            saleRoomInformationViewHolder.nrdTextView.setVisibility(View.GONE);
            isMaxLine = false;
        } else
        {
            saleRoomInformationViewHolder.nrdTextView.setVisibility(View.VISIBLE);
        }

        int layoutHeight;
        if (isMaxLine == true)
        {
            layoutHeight = mContext.getResources().getDimensionPixelSize(R.dimen.stay_detail_room_type_expand_height);
        } else
        {
            layoutHeight = mContext.getResources().getDimensionPixelSize(R.dimen.stay_detail_room_type_default_height);
        }

        ViewGroup.LayoutParams params = saleRoomInformationViewHolder.viewRoot.getLayoutParams();
        if (params == null)
        {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
        } else
        {
            params.height = layoutHeight;
        }

        saleRoomInformationViewHolder.viewRoot.setLayoutParams(params);
    }

    @Override
    public int getItemCount()
    {
        if (mStayProductList == null)
        {
            return 0;
        }

        return mStayProductList.size();
    }

    public class SaleRoomInformationViewHolder extends RecyclerView.ViewHolder
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
