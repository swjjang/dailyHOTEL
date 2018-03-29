package com.daily.dailyhotel.screen.home.stay.outbound.detail.coupon;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Coupon;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSelectCouponDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class SelectStayOutboundCouponDialogAdapter extends RecyclerView.Adapter<SelectStayOutboundCouponDialogAdapter.SelectCouponViewHolder>
{
    private Context mContext;
    private List<Coupon> mList;
    OnCouponItemListener mListener;
    int mSelectPosition = -1;
    private boolean mIsSelectedMode;

    public interface OnCouponItemListener
    {
        void onDownloadClick(int position);

        void updatePositiveButton();
    }

    public SelectStayOutboundCouponDialogAdapter(Context context, List<Coupon> list, OnCouponItemListener listener)
    {
        mContext = context;
        mList = list;
        mListener = listener;
        setSelectedMode(true);
    }

    @Override
    public int getItemCount()
    {
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    public void clear()
    {
        if (mList == null)
        {
            return;
        }

        mList.clear();
    }

    public void addAll(List<Coupon> list)
    {
        if (list == null)
        {
            return;
        }

        mList.addAll(list);
    }

    public void setAll(List<Coupon> list)
    {
        clear();
        addAll(list);
    }

    public Coupon getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
    }


    @Override
    public SelectStayOutboundCouponDialogAdapter.SelectCouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutSelectCouponDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_select_coupon_data, parent, false);

        return new SelectCouponViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(SelectStayOutboundCouponDialogAdapter.SelectCouponViewHolder holder, int position)
    {
        Coupon coupon = getItem(position);

        String strAmount = DailyTextUtils.getPriceFormat(mContext, coupon.amount, false);
        holder.dataBinding.couponPriceTextView.setText(strAmount);

        holder.dataBinding.couponNameTextView.setText(coupon.title);

        if (coupon.amountMinimum > 0)
        {
            String strAmountMinimum = mContext.getResources().getString( //
                R.string.coupon_min_price_text, //
                DailyTextUtils.getPriceFormat(mContext, coupon.amountMinimum, false));

            holder.dataBinding.descriptionTextView.setText(strAmountMinimum);
            holder.dataBinding.descriptionTextView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
        }

        try
        {
            String expireText = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
            expireText = String.format(Locale.KOREA, "- %s", expireText);
            holder.dataBinding.expireTextView.setText(expireText);
            holder.dataBinding.expireTextView.setVisibility(View.VISIBLE);
        } catch (ParseException e)
        {
            Crashlytics.log("Select Coupon::coupon.validTo: " + (coupon != null ? coupon.validTo : ""));
            ExLog.d(e.getMessage());
            holder.dataBinding.expireTextView.setVisibility(View.GONE);
        }

        // 스테이, 고메 쿠폰인지
        holder.dataBinding.useableStayTextView.setVisibility(coupon.availableInStay ? View.VISIBLE : View.GONE);
        holder.dataBinding.useableStayOutboundTextView.setVisibility(coupon.availableInOutboundHotel ? View.VISIBLE : View.GONE);
        holder.dataBinding.useableGourmetTextView.setVisibility(coupon.availableInGourmet ? View.VISIBLE : View.GONE);

        setDownLoadLayout(holder, coupon.type == Coupon.Type.REWARD, coupon.isDownloaded);

        if (mIsSelectedMode == true && coupon.isDownloaded == true)
        {
            setSelectedLayout(holder, coupon.type == Coupon.Type.REWARD, mSelectPosition == position);

            holder.dataBinding.getRoot().setOnClickListener(new View.OnClickListener()
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
            holder.dataBinding.getRoot().setOnClickListener(new View.OnClickListener()
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


    public void setSelectedMode(boolean selected)
    {
        mIsSelectedMode = selected;
    }

    public Coupon getCoupon(String couponCode)
    {
        if (mList == null)
        {
            return null;
        }

        for (Coupon coupon : mList)
        {
            if (coupon.couponCode.equalsIgnoreCase(couponCode) == true)
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

    private void setSelectedLayout(SelectCouponViewHolder holder, boolean rewardCoupon, boolean isSelected)
    {

        if (isSelected == true)
        {
            holder.dataBinding.getRoot().setBackgroundColor(mContext.getResources().getColor(R.color.default_background_cfafafb));

            holder.dataBinding.couponPriceTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cb70038));
            holder.dataBinding.couponPriceTextView.setCompoundDrawablesWithIntrinsicBounds(rewardCoupon ? R.drawable.vector_r_ic_s_17 : 0, 0, R.drawable.ic_check_s, 0);
            holder.dataBinding.couponPriceTextView.setSelected(true);

            holder.dataBinding.couponNameTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cb70038));
        } else
        {
            holder.dataBinding.getRoot().setBackgroundResource(R.color.white);

            holder.dataBinding.couponPriceTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.dataBinding.couponPriceTextView.setCompoundDrawablesWithIntrinsicBounds(rewardCoupon ? R.drawable.vector_r_ic_s_17 : 0, 0, 0, 0);
            holder.dataBinding.couponPriceTextView.setSelected(false);

            holder.dataBinding.couponNameTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }
    }

    private void setDownLoadLayout(SelectCouponViewHolder holder, boolean rewardCoupon, boolean isDownload)
    {
        holder.dataBinding.couponPriceTextView.setCompoundDrawablesWithIntrinsicBounds(rewardCoupon ? R.drawable.vector_r_ic_s_17 : 0, 0, 0, 0);
        holder.dataBinding.getRoot().setBackgroundResource(R.color.white);

        if (isDownload == true)
        {
            holder.dataBinding.couponPriceTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.dataBinding.couponNameTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.dataBinding.descriptionTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));
            holder.dataBinding.expireTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));

            holder.dataBinding.useableStayTextView.setAlpha(1.0f);
            holder.dataBinding.useableStayOutboundTextView.setAlpha(1.0f);
            holder.dataBinding.useableGourmetTextView.setAlpha(1.0f);
            holder.dataBinding.downloadCouponLayout.setVisibility(View.GONE);

        } else
        {
            int color = mContext.getResources().getColor(R.color.default_text_cc5c5c5);

            holder.dataBinding.couponPriceTextView.setTextColor(color);
            holder.dataBinding.couponNameTextView.setTextColor(color);
            holder.dataBinding.descriptionTextView.setTextColor(color);
            holder.dataBinding.expireTextView.setTextColor(color);

            holder.dataBinding.useableStayTextView.setAlpha(0.5f);
            holder.dataBinding.useableStayOutboundTextView.setAlpha(0.5f);
            holder.dataBinding.useableGourmetTextView.setAlpha(0.5f);
            holder.dataBinding.downloadCouponLayout.setVisibility(View.VISIBLE);
        }
    }

    class SelectCouponViewHolder extends RecyclerView.ViewHolder
    {
        public LayoutSelectCouponDataBinding dataBinding;

        public SelectCouponViewHolder(LayoutSelectCouponDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }
}
