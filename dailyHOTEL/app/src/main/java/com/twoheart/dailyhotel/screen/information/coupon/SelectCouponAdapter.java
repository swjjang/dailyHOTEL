package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponAdapter extends RecyclerView.Adapter<SelectCouponAdapter.SelectViewHolder>
{

    private int mSelectPosition = -1;

    private List<Coupon> mList;
    private Context mContext;
    private OnCouponItemListener mListener;

    public interface OnCouponItemListener
    {
        void onDownloadClick(int position);

        void updatePositiveButton();
    }

    public SelectCouponAdapter(Context context, List<Coupon> list, OnCouponItemListener listener)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("couponList must not be null");
        }

        mList = list;
        mListener = listener;
    }

    /**
     * 쿠폰아이템
     *
     * @param position
     * @return
     */
    public Coupon getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public SelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_select_coupon, parent, false);
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectViewHolder holder, final int position)
    {
        Coupon coupon = getItem(position);

        DecimalFormat decimalFormat = new DecimalFormat("###,##0");
        String strPrice = decimalFormat.format(coupon.price) + mContext.getResources().getString(R.string.currency);
        holder.priceTextView.setText(strPrice);

        holder.descriptionTextView.setText(coupon.description);
        holder.expireTextView.setText(coupon.expiredTime);

        if (coupon.minPrice > 0)
        {
            String strMinPrice = decimalFormat.format(coupon.price) + mContext.getResources().getString(R.string.currency);
            holder.minPriceTextView.setText(strMinPrice);
        } else
        {
            holder.minPriceTextView.setText("");
        }

        if (coupon.state == 0)
        {
            setDownLoadLayout(holder, position);
        } else
        {
            setSelectLayout(holder, position);
        }

        holder.bottomDivider.setVisibility((getItemCount() - 1 == position) ? View.GONE : View.VISIBLE);

        holder.listItemLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Coupon coupon = getItem(position);
                if (coupon.state == 0)
                {
                    mListener.onDownloadClick(position);
                } else
                {
                    mSelectPosition = position;
                    notifyDataSetChanged();
                }

                mListener.updatePositiveButton();
            }
        });

    }

    private void setDownLoadLayout(SelectViewHolder holder, int position)
    {
        Coupon coupon = getItem(position);

        if (coupon.state != 0)
        {
            return;
        }

        holder.listItemLayout.setBackgroundResource(R.drawable.coupon_popup_dimmed);
        holder.iconImageView.setImageResource(R.drawable.coupon_ic_download);
        holder.iconImageView.setSelected(false);
        holder.priceTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_red_wine_text));
        holder.downloadTextView.setVisibility(View.VISIBLE);
        holder.descriptionTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
        holder.expireTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
        holder.minPriceTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
    }

    private void setSelectLayout(SelectViewHolder holder, int position)
    {
        Coupon coupon = getItem(position);

        if (coupon.state != 0)
        {
            return;
        }

        holder.listItemLayout.setBackgroundResource(R.drawable.coupon_popup_default);
        holder.iconImageView.setImageResource(R.drawable.selector_radio_button);
        holder.iconImageView.setSelected((mSelectPosition == position) ? true : false);
        holder.priceTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.downloadTextView.setVisibility(View.VISIBLE);
        holder.descriptionTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.expireTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));
        holder.minPriceTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_description_text));
    }

    public int getSelectPosition()
    {
        return mSelectPosition;
    }

    protected class SelectViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        View listItemLayout;
        View selectLayout;
        TextView priceTextView;
        ImageView iconImageView;
        TextView downloadTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        TextView minPriceTextView;
        View bottomDivider;


        public SelectViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            listItemLayout = itemView.findViewById(R.id.listItemLayout);
            selectLayout = itemView.findViewById(R.id.selectLayout);
            priceTextView = (TextView) itemView.findViewById(R.id.couponPriceTextView);
            iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            downloadTextView = (TextView) itemView.findViewById(R.id.downloadIconView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            expireTextView = (TextView) itemView.findViewById(R.id.expireTextView);
            minPriceTextView = (TextView) itemView.findViewById(R.id.minPriceTextView);
            bottomDivider = itemView.findViewById(R.id.bottomDivider);
        }
    }


}
