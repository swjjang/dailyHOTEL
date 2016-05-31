package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.FontManager;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
public class CouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private static final int HEADER_COUNT = 1;

    private List<Coupon> mList;
    private Context mContext;
    private OnCouponItemListener mListener;

    private String mCurrentTimeMillis; // 현재 시간 -  계산되는 현재시간을 통일 하기 위해 어뎁터 생성시 현재시간으로 통일 type ISO-8601 타입의 String

    public interface OnCouponItemListener
    {
        void startNotice();

        void showNotice(View view, int position);

        void onDownloadClick(View view, int position);
    }

    public CouponListAdapter(Context context, List<Coupon> list, OnCouponItemListener listener)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("couponList must not be null");
        }

        mList = list;
        mListener = listener;
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
        if (position > 0)
        {
            position = position - HEADER_COUNT;
        }
        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size() + HEADER_COUNT;
    }

    @Override
    public int getItemViewType(int position)
    {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_HEADER)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_couponlist_header, parent, false);
            return new HeaderViewHolder(view);
        } else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_coupon, parent, false);
            return new ItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_HEADER)
        {
            // 헤더
            ((HeaderViewHolder) holder).onBindViewHolder();
        } else
        {
            // 리스트 아이템
            ((ItemViewHolder) holder).onBindViewHolder(position);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        TextView couponPriceTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        DailyTextView dueDateTextView;
        TextView useablePlaceTextView;
        TextView minPriceTextView;
        TextView useIconView;
        TextView downloadIconView;
        TextView noticeTextView;

        public ItemViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            couponPriceTextView = (TextView) itemView.findViewById(R.id.couponPriceTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            expireTextView = (TextView) itemView.findViewById(R.id.expireTextView);
            dueDateTextView = (DailyTextView) itemView.findViewById(R.id.dueDateTextView);
            useablePlaceTextView = (TextView) itemView.findViewById(R.id.useablePlaceTextView);
            minPriceTextView = (TextView) itemView.findViewById(R.id.minPriceTextView);
            useIconView = (TextView) itemView.findViewById(R.id.useIconView);
            downloadIconView = (TextView) itemView.findViewById(R.id.downloadIconView);
            noticeTextView = (TextView) itemView.findViewById(R.id.noticeTextView);
        }

        public void onBindViewHolder(final int position)
        {

            Coupon coupon = getItem(position);

            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            String strAmount = decimalFormat.format(coupon.amount) + mContext.getResources().getString(R.string.currency);
            couponPriceTextView.setText(strAmount);

            descriptionTextView.setText(coupon.title);
            expireTextView.setText(coupon.getExpiredString(coupon.validFrom, coupon.validTo));

            int dueDate = coupon.getDueDate(mContext, mCurrentTimeMillis, coupon.validTo);

            if (dueDate > 0)
            {
                dueDateTextView.setTypeface(FontManager.getInstance(mContext).getRegularTypeface());
                dueDateTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_expire_text));
                String strDueDate = mContext.getResources().getString(R.string.coupon_duedate_text, dueDate);
                dueDateTextView.setText(strDueDate);
            } else
            {
                // 오늘까지
                dueDateTextView.setTypeface(FontManager.getInstance(mContext).getMediumTypeface());
                dueDateTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_red_wine_text));
                dueDateTextView.setText(mContext.getResources().getString(R.string.coupon_today_text));
            }

            if (coupon.amountMinimum > 0)
            {
                String strAmountMinimum = decimalFormat.format(coupon.amountMinimum) + mContext.getResources().getString(R.string.currency);
                minPriceTextView.setText(strAmountMinimum);
            } else
            {
                minPriceTextView.setText("");
            }

            useablePlaceTextView.setText(coupon.useablePlace);

            if ("Y".equalsIgnoreCase(coupon.isDownloaded))
            {
                //useable
                downloadIconView.setVisibility(View.GONE);
                useIconView.setVisibility(View.VISIBLE);
            } else
            {
                //download
                downloadIconView.setVisibility(View.VISIBLE);
                useIconView.setVisibility(View.GONE);
            }

            CharSequence charSequence = Util.isTextEmpty(noticeTextView.getText().toString()) ? "" : noticeTextView.getText().toString();
            SpannableString spannableString = new SpannableString(charSequence);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noticeTextView.setText(spannableString);
            noticeTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.showNotice(v, position);
                }
            });

            downloadIconView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.onDownloadClick(v, position);
                }
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        DailyTextView noticeView;

        public HeaderViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            noticeView = (DailyTextView) itemView.findViewById(R.id.couponUseNoticeTextView);
        }

        public void onBindViewHolder()
        {
            SpannableString spannableString = new SpannableString(noticeView.getText());
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noticeView.setText(spannableString);

            noticeView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.startNotice();
                }
            });
        }
    }

}