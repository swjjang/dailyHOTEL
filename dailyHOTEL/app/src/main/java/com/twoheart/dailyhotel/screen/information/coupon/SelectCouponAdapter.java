package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

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
        holder.listItemLayout = view.findViewById(R.id.listItemLayout);
        holder.verticalLine = view.findViewById(R.id.vericalLine);
        holder.priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        holder.iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
        holder.downloadTextView = (TextView) view.findViewById(R.id.downloadTextView);
        holder.descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        holder.expireTextView = (TextView) view.findViewById(R.id.expireTextView);
        holder.minPriceTextView = (TextView) view.findViewById(R.id.minPriceTextView);
        holder.upperDivider = view.findViewById(R.id.upperDivider);
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

        holder.descriptionTextView.setText(coupon.title);

        try
        {
            //            String expireText = Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyy.MM.dd");
            String expireText = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
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
            setDownLoadLayout(holder);
        }

        holder.upperDivider.setVisibility((position == 0) ? View.VISIBLE : View.GONE);
        //        holder.bottomDivider.setVisibility((getItemCount() - 1 == position) ? View.GONE : View.VISIBLE);

        if (mIsSelected == true)
        {
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

    private void setDownLoadLayout(SelectViewHolder holder)
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

        if (mIsSelected == true)
        {
            holder.iconImageView.setVisibility(View.VISIBLE);
            holder.iconImageView.setImageResource(R.drawable.selector_radio_button);
            holder.iconImageView.setSelected((mSelectPosition == position));

            holder.priceTextView.setGravity(Gravity.LEFT);
            holder.priceTextView.setPadding(Util.dpToPx(mContext, 7), 0, 0, 0);
        } else
        {
            holder.iconImageView.setVisibility(View.GONE);
            holder.priceTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            holder.priceTextView.setPadding(0, 0, 0, 0);
        }

        holder.verticalLine.setBackgroundColor(mContext.getResources().getColor(R.color.default_line_cd0d0d0));
        holder.priceTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.downloadTextView.setVisibility(View.GONE);
        holder.descriptionTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.expireTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));
        holder.minPriceTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_description_text));
    }

    protected class SelectViewHolder
    {
        View listItemLayout;
        View verticalLine;
        TextView priceTextView;
        ImageView iconImageView;
        TextView downloadTextView;
        TextView descriptionTextView;
        TextView expireTextView;
        TextView minPriceTextView;
        View upperDivider;
    }
}
