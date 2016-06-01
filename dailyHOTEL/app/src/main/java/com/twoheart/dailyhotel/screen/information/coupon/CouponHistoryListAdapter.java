package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 23..
 */
public class CouponHistoryListAdapter extends RecyclerView.Adapter<CouponHistoryListAdapter.CouponViewHolder>
{
    private Context mContext;
    private List<Coupon> mCouponList;
    private String mCurrentTimeMillis; // 현재 시간 -  계산되는 현재시간을 통일 하기 위해 어뎁터 생성시 현재시간으로 통일 type ISO-8601타입의 String

    public CouponHistoryListAdapter(Context context, List<Coupon> list)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("couponList must not be null");
        }

        mCouponList = list;

        mCurrentTimeMillis = Util.getISO8601String(System.currentTimeMillis());
    }

    /**
     * 쿠폰아이템
     *
     * @param position 실제 포지션에서 -1 된 값(헤더 사이즈 뺀값)
     * @return
     */
    public Coupon getItem(int position)
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
        Coupon coupon = getItem(position);

        DecimalFormat decimalFormat = new DecimalFormat("###,##0");
        String strAmout = decimalFormat.format(coupon.getAmount());
        holder.priceTextView.setText(strAmout + mContext.getResources().getString(R.string.currency));

        holder.descriptionTextView.setText(coupon.getTitle());

        holder.upperLine.setVisibility((position == 0) ? View.VISIBLE : View.GONE);

        int resId;
        String strPrefixExpire;
        if ("Y".equalsIgnoreCase(coupon.isDownloaded()))
        {
            resId = R.string.coupon_history_expire_text;
            strPrefixExpire = "만료일:";
        } else
        {
            resId = R.string.coupon_history_use_text;
            strPrefixExpire = "사용일:";
        }
        holder.stateTextView.setText(resId);

        // 사용기간 및 사용일자 또는 만료일자 구현 필요
        String strExpire = coupon.getExpiredString(coupon.getValidFrom(), coupon.getValidTo());
        if (Util.getLCDWidth(mContext) < 720)
        {
            strExpire += "\n";
        } else
        {
            strExpire += " | ";
        }
        strExpire += strPrefixExpire + coupon.getDueDate(coupon);
        holder.expireTextView.setText(strExpire);
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder
    {

        View rootView;
        TextView priceTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        TextView stateTextView;
        View upperLine;

        public CouponViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            expireTextView = (TextView) itemView.findViewById(R.id.expireTextView);
            stateTextView = (TextView) itemView.findViewById(R.id.stateTextView);
            upperLine = itemView.findViewById(R.id.upperLineView);
        }
    }
}
