package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CouponHistory;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 23..
 */
public class CouponHistoryListAdapter extends RecyclerView.Adapter<CouponHistoryListAdapter.CouponViewHolder>
{
    private Context mContext;
    private List<CouponHistory> mCouponList;

    public CouponHistoryListAdapter(Context context, List<CouponHistory> list)
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
    public CouponHistory getItem(int position)
    {
        return mCouponList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mCouponList == null ? 0 : mCouponList.size();
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_coupon_history, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position)
    {
        CouponHistory coupon = getItem(position);

        String strAmount = Util.getPriceFormat(mContext, coupon.amount, false);
        holder.priceTextView.setText(strAmount);

        holder.descriptionTextView.setText(coupon.title);

        holder.upperLine.setVisibility((position == 0) ? View.VISIBLE : View.GONE);

        holder.lastBottomLine.setVisibility(position == getItemCount() - 1 ? View.VISIBLE : View.GONE);

        int resId;
        if (coupon.isRedeemed == true)
        {
            resId = R.string.coupon_history_use_text;
        } else
        {
            resId = R.string.coupon_history_expire_text;
        }

        holder.stateTextView.setText(resId);

        // 사용기간 및 사용일자 또는 만료일자 구현 필요
        String strExpire = CouponUtil.getAvailableDatesString(coupon.validFrom, coupon.validTo);

        StringBuilder builder = new StringBuilder();

        try
        {
            if (Util.getLCDWidth(mContext) < 720)
            {
                builder.append("\n");
            } else
            {
                builder.append(" | ");
            }

            if (coupon.isRedeemed == true)
            {
                builder.append("사용일: ");
            } else
            {
                builder.append("만료일: ");
            }

            //            builder.append(Util.simpleDateFormatISO8601toFormat(coupon.disabledAt, "yyyy.MM.dd"));
            builder.append(DailyCalendar.convertDateFormatString(coupon.disabledAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            builder.setLength(0);
        }


        holder.expireTextView.setText(strExpire + builder.toString());
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder
    {

        View rootView;
        TextView priceTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        TextView stateTextView;
        View upperLine;
        View lastBottomLine;

        public CouponViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            expireTextView = (TextView) itemView.findViewById(R.id.expireTextView);
            stateTextView = (TextView) itemView.findViewById(R.id.stateTextView);
            upperLine = itemView.findViewById(R.id.upperLineView);
            lastBottomLine = itemView.findViewById(R.id.lastBottomLine);
        }
    }
}
