package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutCouponboxCouponDataBinding;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.CouponUtil;

import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
@Deprecated
public class CouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private static final int VIEW_TYPE_FOOTER = 3;

    private static final int FOOTER_COUNT = 1;

    private List<Coupon> mList;
    Context mContext;
    OnCouponItemListener mListener;

    public interface OnCouponItemListener
    {
        void startNotice();

        void startCouponHistory();

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
    }

    /**
     * 쿠폰아이템
     *
     * @param position 실제 포지션에서 -1 된 값(헤더 사이즈 뺀값)
     * @return
     */
    public Coupon getItem(int position)
    {
        if (position >= getItemCount() - 1)
        {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size() + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position)
    {
        return (position == getItemCount() - 1) ? VIEW_TYPE_FOOTER : VIEW_TYPE_ITEM;
    }

    public void setData(List<Coupon> list)
    {
        mList = list;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_FOOTER)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_couponlist_footer, parent, false);
            return new FooterViewHolder(view);
        } else
        {
            LayoutCouponboxCouponDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_couponbox_coupon_data, parent, false);
            return new ItemViewHolder(viewDataBinding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_FOOTER)
        {
            // 마지막
            ((FooterViewHolder) holder).onBindViewHolder();
        } else
        {
            // 리스트 아이템
            ((ItemViewHolder) holder).onBindViewHolder(position);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder
    {
        LayoutCouponboxCouponDataBinding dataBinding;

        public ItemViewHolder(LayoutCouponboxCouponDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }

        public void onBindViewHolder(final int position)
        {
            Coupon coupon = getItem(position);
            if (coupon == null)
            {
                ExLog.d("position : " + position + " , coupon is null");
                return;
            }

            dataBinding.couponPriceTextView.setCompoundDrawablesWithIntrinsicBounds(coupon.type == Coupon.Type.REWARD ? R.drawable.vector_r_ic_s_17 : 0, 0, 0, 0);

            String strAmount = DailyTextUtils.getPriceFormat(mContext, coupon.amount, false);
            dataBinding.couponPriceTextView.setText(strAmount);
            dataBinding.couponNameTextView.setText(coupon.title);
            dataBinding.expireTextView.setText(CouponUtil.getAvailableDatesString(coupon.validFrom, coupon.validTo));

            int dueDate = CouponUtil.getDueDateCount(coupon.serverDate, coupon.validTo);
            String strDueDate;

            if (dueDate > 1)
            {
                // 2일 남음 이상
                strDueDate = mContext.getResources().getString(R.string.coupon_duedate_text, dueDate);
            } else
            {
                // 오늘까지
                strDueDate = mContext.getResources().getString(R.string.coupon_today_text);
            }

            if (dueDate > 7)
            {
                // 8일 남음 이상
                dataBinding.dueDateTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_description_text));

            } else
            {
                // 7일 남음 부터 오늘까지
                dataBinding.dueDateTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_red_wine_text));
            }

            dataBinding.dueDateTextView.setText(strDueDate);

            String lastLineText = "";
            boolean isEmptyStayFromTo = DailyTextUtils.isTextEmpty(coupon.stayFrom, coupon.stayTo);
            boolean isEmptyAmountMinimum = coupon.amountMinimum == 0;

            if (isEmptyAmountMinimum == false)
            {
                lastLineText += mContext.getResources().getString( //
                    isEmptyStayFromTo == false ? R.string.coupon_min_price_short_text : R.string.coupon_min_price_text, //
                    DailyTextUtils.getPriceFormat(mContext, coupon.amountMinimum, false));
            }

            if (isEmptyStayFromTo == false)
            {
                if (isEmptyAmountMinimum == false)
                {
                    lastLineText += ",\n";
                }

                lastLineText += CouponUtil.getDateOfStayAvailableString( //
                    mContext, coupon.stayFrom, coupon.stayTo);
            }

            int viewWidth = dataBinding.descriptionTextView.getWidth() - dataBinding.descriptionTextView.getPaddingLeft() - dataBinding.descriptionTextView.getPaddingRight();
            if (viewWidth == 0)
            {
                final String lineText = lastLineText;
                dataBinding.descriptionTextView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int viewWidth = dataBinding.descriptionTextView.getWidth() - dataBinding.descriptionTextView.getPaddingLeft() - dataBinding.descriptionTextView.getPaddingRight();
                        setDescriptionText(position, viewWidth, lineText);
                    }
                });
            } else
            {
                setDescriptionText(position, viewWidth, lastLineText);
            }

            if (coupon.isDownloaded == true)
            {
                //usable
                dataBinding.downloadIconView.setVisibility(View.GONE);
                dataBinding.useIconView.setVisibility(View.VISIBLE);
                dataBinding.baseView.setBackgroundResource(R.drawable.more_coupon_bg);
                dataBinding.noticeTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_description_text));
            } else
            {
                //download
                dataBinding.downloadIconView.setVisibility(View.VISIBLE);
                dataBinding.useIconView.setVisibility(View.GONE);
                dataBinding.baseView.setBackgroundResource(R.drawable.more_coupon_download_bg);
                dataBinding.noticeTextView.setTextColor(mContext.getResources().getColor(R.color.white_a80));
            }

            dataBinding.useableStayTextView.setVisibility(coupon.availableInStay ? View.VISIBLE : View.GONE);
            dataBinding.useableStayOutboundTextView.setVisibility(coupon.availableInOutboundHotel ? View.VISIBLE : View.GONE);
            dataBinding.useableGourmetTextView.setVisibility(coupon.availableInGourmet == true ? View.VISIBLE : View.GONE);

            String couponNotice = mContext.getString(R.string.coupon_notice_text);
            SpannableString spannableString = new SpannableString(couponNotice);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dataBinding.noticeTextView.setText(spannableString);
            dataBinding.noticeTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.showNotice(v, position);
                }
            });

            dataBinding.downloadIconView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.onDownloadClick(v, position);
                }
            });
        }

        void setDescriptionText(int position, int viewWidth, String lastLineText)
        {
            if (DailyTextUtils.isTextEmpty(lastLineText) == true)
            {
                lastLineText = "";
            }

            final Typeface typeface = FontManager.getInstance(mContext).getRegularTypeface();
            final float textWidth = DailyTextUtils.getTextWidth(mContext, lastLineText, 11d, typeface);

            //            ExLog.d("index : " + position + " , viewWidth : " + viewWidth + " , getWidth() : "//
            //                + minPriceTextView.getWidth() + " , getPaddingLeft() : " + minPriceTextView.getPaddingLeft() //
            //                + " , getPaddingRight() : " + minPriceTextView.getPaddingRight() //
            //                + " , textWidth : " + textWidth + " , gap : " + (textWidth - viewWidth));

            if (viewWidth <= textWidth)
            {
                lastLineText = lastLineText.replace(", ", ",\n");
            }

            dataBinding.descriptionTextView.setText(lastLineText);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        View rootView;
        DailyTextView noticeView;
        DailyTextView couponHistoryView;

        public FooterViewHolder(View itemView)
        {
            super(itemView);

            rootView = itemView;
            noticeView = itemView.findViewById(R.id.couponUseNoticeTextView);
            couponHistoryView = itemView.findViewById(R.id.couponHistoryTextView);
        }

        public void onBindViewHolder()
        {
            SpannableString spannableString = new SpannableString(noticeView.getText());
            noticeView.setText(spannableString);

            noticeView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.startNotice();
                }
            });
            couponHistoryView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.startCouponHistory();
                }
            });
        }
    }
}