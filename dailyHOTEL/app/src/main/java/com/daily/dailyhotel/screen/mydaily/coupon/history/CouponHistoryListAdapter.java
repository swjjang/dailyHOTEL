package com.daily.dailyhotel.screen.mydaily.coupon.history;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowCouponHistoryDataBinding;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.List;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponHistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    private List<ObjectItem> mCouponList;

    public CouponHistoryListAdapter(Context context, List<ObjectItem> list)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("couponList must not be null");
        }

        mCouponList = list;
    }

    /**
     * 쿠폰아이템
     *
     * @param position 실제 포지션에서 -1 된 값(헤더 사이즈 뺀값)
     * @return
     */
    public ObjectItem getItem(int position)
    {
        return mCouponList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mCouponList == null ? 0 : mCouponList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return mCouponList.get(position).mType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ObjectItem.TYPE_ENTRY:
            {
                ListRowCouponHistoryDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_coupon_history_data, parent, false);

                return new CouponViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                View footerView = new View(mContext);
                footerView.setBackgroundResource(R.color.default_background);
                footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 47)));

                return new FooterViewHolder(footerView);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ObjectItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case ObjectItem.TYPE_ENTRY:
                onBindViewHolder((CouponViewHolder) holder, item.getItem(), position);
                break;
        }
    }


    public void onBindViewHolder(CouponViewHolder holder, Coupon coupon, int position)
    {
        holder.dataBinding.couponPriceTextView.setCompoundDrawablesWithIntrinsicBounds(coupon.type == Coupon.Type.REWARD ? R.drawable.vector_r_ic_s_17 : 0, 0, 0, 0);

        String strAmount = DailyTextUtils.getPriceFormat(mContext, coupon.amount, false);
        holder.dataBinding.couponPriceTextView.setText(strAmount);
        holder.dataBinding.couponNameTextView.setText(coupon.title);
        holder.dataBinding.stateTextView.setText(coupon.isRedeemed ? R.string.coupon_history_use_text : R.string.coupon_history_expire_text);

        // 사용기간 및 사용일자 또는 만료일자 구현 필요
        String strExpire = CouponUtil.getAvailableDatesString(coupon.validFrom, coupon.validTo);

        StringBuilder builder = new StringBuilder();

        try
        {
            if (ScreenUtils.getScreenWidth(mContext) < 720)
            {
                builder.append("\n");
            } else
            {
                builder.append(" | ");
            }

            if (coupon.isRedeemed == true)
            {
                builder.append(mContext.getString(R.string.coupon_history_day_of_use));
            } else
            {
                builder.append(mContext.getString(R.string.coupon_history_expiration_date));
            }

            builder.append(": ");
            builder.append(DailyCalendar.convertDateFormatString(coupon.disabledAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            builder.setLength(0);
        }
        if (DailyTextUtils.isTextEmpty(strExpire) == false)
        {
            builder.insert(0, strExpire);
        }

        holder.dataBinding.expireTextView.setText(builder.toString());
        holder.dataBinding.useableStayTextView.setVisibility(coupon.availableInStay ? View.VISIBLE : View.GONE);
        holder.dataBinding.useableGourmetTextView.setVisibility(coupon.availableInGourmet == true ? View.VISIBLE : View.GONE);

        // 마지막 라인 굵기 수정
        if (position == getItemCount() - 2)
        {
            holder.dataBinding.bottomLineView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 1);
        } else
        {
            holder.dataBinding.bottomLineView.getLayoutParams().height = 1;
        }

        holder.dataBinding.bottomLineView.requestLayout();
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder
    {
        ListRowCouponHistoryDataBinding dataBinding;

        public CouponViewHolder(ListRowCouponHistoryDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View view)
        {
            super(view);
        }
    }
}
