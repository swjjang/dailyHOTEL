package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.text.ParseException;
import java.util.List;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponAdapter extends ArrayAdapter<Coupon>
{

    private static final int RESOURCE_ID = R.layout.list_row_select_coupon;

    private Context mContext;
    private List<Coupon> mList;
    private OnCouponItemListener mListener;
    private int mSelectPosition = -1;
    private boolean mIsSelected;

    public interface OnCouponItemListener
    {
        void onDownloadClick(int position);

        void updatePositiveButton();
    }

    public SelectCouponAdapter(Context context, List<Coupon> list, OnCouponItemListener listener)
    {
        super(context, RESOURCE_ID, list);

        mContext = context;
        mList = list;
        mListener = listener;
        setSelected(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = newView(convertView);

        if (convertView != null)
        {
            bindView(position, convertView);
        }

        return convertView;
    }


    private View newView(View convertView)
    {
        if (convertView != null)
        {
            return convertView;
        }

        View view = LayoutInflater.from(mContext).inflate(RESOURCE_ID, null);

        SelectViewHolder holder = new SelectViewHolder();

        holder.couponLayout = view;
        holder.priceTextView = (DailyTextView) view.findViewById(R.id.priceTextView);
        holder.titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        holder.minPriceTextView = (TextView) view.findViewById(R.id.minPriceTextView);
        holder.expireTextView = (TextView) view.findViewById(R.id.expireTextView);
        holder.hotelIconView = (DailyImageView) view.findViewById(R.id.hotelIconView);
        holder.gourmetIconView = (DailyImageView) view.findViewById(R.id.gourmetIconView);
        holder.downloadCouponView = view.findViewById(R.id.downloadCouponView);
        view.setTag(holder);

        return view;
    }

    private void bindView(final int position, View convertView)
    {
        final Object tag = convertView.getTag();
        if (tag == null || !(tag instanceof SelectViewHolder))
        {
            return;
        }

        SelectViewHolder holder = (SelectViewHolder) convertView.getTag();

        Coupon coupon = getItem(position);

        String strAmount = Util.getPriceFormat(mContext, coupon.amount, false);
        holder.priceTextView.setText(strAmount);

        holder.titleTextView.setText(coupon.title);

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

        try
        {
            String expireText = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
            expireText = String.format("(~%s)", expireText);
            holder.expireTextView.setText(expireText);
            holder.expireTextView.setVisibility(View.VISIBLE);
        } catch (ParseException e)
        {
            if (Constants.DEBUG == false)
            {
                Crashlytics.log("Select Coupon::coupon.vaildTo: " + (coupon != null ? coupon.validTo : ""));
            }
            ExLog.d(e.getMessage());
            holder.expireTextView.setVisibility(View.GONE);
        }

        // 스테이, 고메 쿠폰인지
        if(coupon.isStay == true)
        {
            holder.hotelIconView.setVectorImageResource(R.drawable.ic_badge_gourmet_on);
        } else
        {
            holder.hotelIconView.setVectorImageResource(R.drawable.ic_badge_gourmet_off);
        }

        if(coupon.isGourmet == true)
        {
            holder.hotelIconView.setVectorImageResource(R.drawable.ic_badge_hotel_on);
        } else
        {
            holder.hotelIconView.setVectorImageResource(R.drawable.ic_badge_hotel_off);
        }

        setDownLoadLayout(holder, coupon.isDownloaded);

        if (mIsSelected == true)
        {
            holder.couponLayout.setOnClickListener(new View.OnClickListener()
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
        } else
        {
            holder.couponLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Coupon coupon = getItem(position);
                    if (coupon.isDownloaded == false)
                    {
                        mListener.onDownloadClick(position);
                    }
                }
            });
        }
    }

    public void setData(List<Coupon> list)
    {
        mList = list;
        clear();
        addAll(list);
    }

    public void setSelected(boolean selected)
    {
        mIsSelected = selected;
    }

    public Coupon getCoupon(String userCouponCode)
    {
        if (mList == null)
        {
            return null;
        }

        for (Coupon coupon : mList)
        {
            if (coupon.userCouponCode.equalsIgnoreCase(userCouponCode) == true)
            {
                return coupon;
            }
        }

        return null;
    }

    public int getSelectPosition()
    {
        return mSelectPosition;
    }

    private void setDownLoadLayout(SelectViewHolder holder, boolean isDownload)
    {
        if (isDownload == true)
        {
            holder.priceTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            holder.titleTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            holder.minPriceTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));
            holder.expireTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));

            holder.hotelIconView.setAlpha(0);
            holder.gourmetIconView.setAlpha(0);
            holder.downloadCouponView.setVisibility(View.GONE);

        } else
        {
            int color = mContext.getResources().getColor(R.color.default_text_cc5c5c5);

            holder.priceTextView.setTextColor(color);
            holder.titleTextView.setTextColor(color);
            holder.minPriceTextView.setTextColor(color);
            holder.expireTextView.setTextColor(color);

            holder.hotelIconView.setAlpha(0.5f);
            holder.gourmetIconView.setAlpha(0.5f);
            holder.downloadCouponView.setVisibility(View.VISIBLE);
        }
    }

    protected class SelectViewHolder
    {
        View couponLayout;
        DailyTextView priceTextView;
        TextView titleTextView;
        TextView minPriceTextView;
        TextView expireTextView;
        DailyImageView hotelIconView;
        DailyImageView gourmetIconView;
        View downloadCouponView;
    }
}
