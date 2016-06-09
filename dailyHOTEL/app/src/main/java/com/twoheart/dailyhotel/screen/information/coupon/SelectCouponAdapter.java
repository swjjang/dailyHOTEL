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
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.text.ParseException;
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

    public void setData(List<Coupon> list)
    {
        mList = list;
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

        String strAmount = Util.getPriceFormat(mContext, coupon.amount, false);
        holder.priceTextView.setText(strAmount);

        holder.descriptionTextView.setText(coupon.title);

        try
        {
            String expireText = Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyy.MM.dd");
            expireText = String.format("(~%s)", expireText);
            holder.expireTextView.setText(expireText);
            holder.expireTextView.setVisibility(View.VISIBLE);
        } catch (ParseException e)
        {
            ExLog.d(e.getMessage());
            holder.expireTextView.setVisibility(View.GONE);
        }

        if (coupon.amountMinimum > 0)
        {
            String strAmountMinimum = mContext.getResources().getString( //
                R.string.coupon_min_price_text, //
                Util.getPriceFormat(mContext, coupon.amountMinimum, false));

            holder.minPriceTextView.setText(strAmountMinimum);
            holder.minPriceTextView.setVisibility(View.VISIBLE);
        } else
        {
            holder.minPriceTextView.setText("");
            holder.minPriceTextView.setVisibility(View.GONE);
        }

        if (coupon.isDownloaded == true)
        {
            setSelectLayout(holder, position);
        } else
        {
            setDownLoadLayout(holder, position);
        }

        holder.upperDivider.setVisibility((position == 0) ? View.VISIBLE : View.GONE);
        //        holder.bottomDivider.setVisibility((getItemCount() - 1 == position) ? View.GONE : View.VISIBLE);

        holder.listItemLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Coupon coupon = getItem(position);
                if (coupon.isDownloaded == true)
                {
                    mSelectPosition = position;
                } else
                {
                    mSelectPosition = -1;
                    mListener.onDownloadClick(position);
                }

                notifyDataSetChanged();
                mListener.updatePositiveButton();
            }
        });

    }

    private void setDownLoadLayout(SelectViewHolder holder, int position)
    {
        holder.listItemLayout.setBackgroundResource(R.drawable.coupon_popup_dimmed);
        holder.iconImageView.setImageResource(R.drawable.coupon_ic_download);
        holder.verticalLine.setBackgroundColor(mContext.getResources().getColor(R.color.select_coupon_vertical_line_dimmed));
        holder.iconImageView.setSelected(false);
        holder.priceTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_red_wine_text));
        holder.downloadTextView.setVisibility(View.VISIBLE);
        holder.descriptionTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
        holder.expireTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
        holder.minPriceTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
    }

    private void setSelectLayout(SelectViewHolder holder, int position)
    {
        holder.listItemLayout.setBackgroundResource(R.drawable.coupon_popup_default);
        holder.iconImageView.setImageResource(R.drawable.selector_radio_button);
        holder.verticalLine.setBackgroundColor(mContext.getResources().getColor(R.color.default_line_cd0d0d0));
        holder.iconImageView.setSelected((mSelectPosition == position) ? true : false);
        holder.priceTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.downloadTextView.setVisibility(View.GONE);
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
        View verticalLine;
        TextView priceTextView;
        ImageView iconImageView;
        TextView downloadTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        TextView minPriceTextView;
        //        View bottomDivider;
        View upperDivider;


        public SelectViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            listItemLayout = itemView.findViewById(R.id.listItemLayout);
            verticalLine = itemView.findViewById(R.id.vericalLine);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            downloadTextView = (TextView) itemView.findViewById(R.id.downloadTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            expireTextView = (TextView) itemView.findViewById(R.id.expireTextView);
            minPriceTextView = (TextView) itemView.findViewById(R.id.minPriceTextView);
            //            bottomDivider = itemView.findViewById(R.id.bottomDivider);
            upperDivider = itemView.findViewById(R.id.upperDivider);
        }
    }


}
