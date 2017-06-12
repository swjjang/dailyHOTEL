package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CouponHistory;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.DailyCalendar;

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

        String strAmount = DailyTextUtils.getPriceFormat(mContext, coupon.amount, false);
        holder.priceTextView.setText(strAmount);

        holder.descriptionTextView.setText(coupon.title);

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.rootView.getLayoutParams();

        if (position == getItemCount() - 1)
        {
            layoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 40);

            holder.lastBottomLine.setVisibility(View.VISIBLE);
        } else
        {
            layoutParams.bottomMargin = 0;

            holder.lastBottomLine.setVisibility(View.GONE);
        }

        holder.rootView.setLayoutParams(layoutParams);

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

            //            builder.append(Util.simpleDateFormatISO8601toFormat(coupon.disabledAt, "yyyy.MM.dd"));
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

        holder.expireTextView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Paint paint = holder.expireTextView.getPaint();
                int width = holder.expireTextView.getMeasuredWidth() - holder.expireTextView.getPaddingRight() - holder.expireTextView.getPaddingLeft();
                int textSize = builder.toString().length();
                int endPosition = paint.breakText(builder.toString(), true, width, null);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.expireTextView.getLayoutParams();

                if (textSize > endPosition)
                {
                    String findText = " | ";
                    int index = builder.indexOf(findText);
                    builder.replace(index, index + findText.length(), "\n");
                    holder.expireTextView.setLineSpacing(0, 0.8f);
                    params.topMargin = ScreenUtils.dpToPx(mContext, 2d);
                } else
                {
                    holder.expireTextView.setLineSpacing(0, 1f);
                    params.topMargin = ScreenUtils.dpToPx(mContext, 4d);
                }

                holder.expireTextView.setText(builder.toString());
            }
        });

        holder.usableStayIcon.setImageResource(coupon.availableInStay == true ? R.drawable.ic_badge_hotel_on : R.drawable.ic_badge_hotel_off);
        holder.usableGourmetIcon.setImageResource(coupon.availableInGourmet == true ? R.drawable.ic_badge_gourmet_on : R.drawable.ic_badge_gourmet_off);
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        TextView priceTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        TextView stateTextView;
        ImageView usableStayIcon;
        ImageView usableGourmetIcon;
        View lastBottomLine;

        public CouponViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            expireTextView = (TextView) itemView.findViewById(R.id.expireTextView);
            stateTextView = (TextView) itemView.findViewById(R.id.stateTextView);
            usableStayIcon = (ImageView) itemView.findViewById(R.id.useableStayImageView);
            usableGourmetIcon = (ImageView) itemView.findViewById(R.id.useableGourmetImageView);
            lastBottomLine = itemView.findViewById(R.id.lastBottomLine);
        }
    }
}
