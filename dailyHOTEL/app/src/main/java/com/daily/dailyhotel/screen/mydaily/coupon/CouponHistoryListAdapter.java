package com.daily.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowCouponHistoryDataBinding;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.List;

/**
 * Created by iseung-won on 2017. 9. 28..
 */

public class CouponHistoryListAdapter extends RecyclerView.Adapter<CouponHistoryListAdapter.CouponViewHolder>
{
    Context mContext;
    private List<Coupon> mCouponList;

    public CouponHistoryListAdapter(Context context, List<Coupon> list)
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
        ListRowCouponHistoryDataBinding dataBinding = DataBindingUtil.inflate( //
            LayoutInflater.from(mContext), R.layout.list_row_coupon_history_data, parent, false);

        return new CouponViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position)
    {
        Coupon coupon = getItem(position);

        String strAmount = DailyTextUtils.getPriceFormat(mContext, coupon.amount, false);
        holder.dataBinding.priceTextView.setText(strAmount);

        holder.dataBinding.descriptionTextView.setText(coupon.title);

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.dataBinding.getRoot().getLayoutParams();

        if (position == getItemCount() - 1)
        {
            layoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 40);

            holder.dataBinding.lastBottomLine.setVisibility(View.VISIBLE);
        } else
        {
            layoutParams.bottomMargin = 0;

            holder.dataBinding.lastBottomLine.setVisibility(View.GONE);
        }

        holder.dataBinding.getRoot().setLayoutParams(layoutParams);

        int resId;
        if (coupon.isRedeemed == true)
        {
            resId = R.string.coupon_history_use_text;
        } else
        {
            resId = R.string.coupon_history_expire_text;
        }

        holder.dataBinding.stateTextView.setText(resId);

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
                builder.append("사용일: ");
            } else
            {
                builder.append("만료일: ");
            }

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

        holder.dataBinding.expireTextView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Paint paint = holder.dataBinding.expireTextView.getPaint();
                int width = holder.dataBinding.expireTextView.getMeasuredWidth() //
                    - holder.dataBinding.expireTextView.getPaddingRight() //
                    - holder.dataBinding.expireTextView.getPaddingLeft();
                int textSize = builder.toString().length();
                int endPosition = paint.breakText(builder.toString(), true, width, null);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.dataBinding.expireTextView.getLayoutParams();

                if (textSize > endPosition)
                {
                    String findText = " | ";
                    int index = builder.indexOf(findText);
                    builder.replace(index, index + findText.length(), "\n");
                    holder.dataBinding.expireTextView.setLineSpacing(0, 0.8f);
                    params.topMargin = ScreenUtils.dpToPx(mContext, 2d);
                } else
                {
                    holder.dataBinding.expireTextView.setLineSpacing(0, 1f);
                    params.topMargin = ScreenUtils.dpToPx(mContext, 4d);
                }

                holder.dataBinding.expireTextView.setText(builder.toString());
            }
        });

        holder.dataBinding.useableStayImageView.setImageResource(coupon.availableInStay == true //
            ? R.drawable.ic_badge_hotel_on : R.drawable.ic_badge_hotel_off);
        holder.dataBinding.useableGourmetImageView.setImageResource(coupon.availableInGourmet == true //
            ? R.drawable.ic_badge_gourmet_on : R.drawable.ic_badge_gourmet_off);
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
}
