package com.twoheart.dailyhotel.screen.hotel.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowStayDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StayListAdapter extends PlaceListAdapter
{
    protected int mNights;
    View.OnClickListener mOnClickListener;

    public StayListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener, View.OnClickListener eventBannerListener)
    {
        super(context, arrayList);

        mOnClickListener = listener;
        mOnEventBannerClickListener = eventBannerListener;

        setSortType(Constants.SortType.DEFAULT);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        try
        {
            mNights = ((StayBookingDay) placeBookingDay).getNights();
        } catch (Exception e)
        {
            mNights = 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_SECTION:
            {
                View view = mInflater.inflate(R.layout.list_row_default_section, parent, false);

                return new SectionViewHolder(view);
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                ListRowStayDataBinding dataBinding = DataBindingUtil.inflate(mInflater, R.layout.list_row_stay_data, parent, false);
                return new HotelViewHolder(dataBinding);
            }

            case PlaceViewItem.TYPE_EVENT_BANNER:
            {
                View view = mInflater.inflate(R.layout.list_row_eventbanner, parent, false);

                return new EventBannerViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_footer, parent, false);

                return new FooterViewHolder(view);
            }

            case PlaceViewItem.TYPE_LOADING_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_loading, parent, false);

                return new FooterViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        PlaceViewItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((HotelViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_EVENT_BANNER:
                onBindViewHolder((EventBannerViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onBindViewHolder(HotelViewHolder holder, PlaceViewItem placeViewItem)
    {
        final Stay stay = placeViewItem.getItem();

        String strPrice = DailyTextUtils.getPriceFormat(mContext, stay.price, false);
        String strDiscount = DailyTextUtils.getPriceFormat(mContext, stay.discountPrice, false);

        String address = stay.addressSummary;

        int barIndex = address.indexOf('|');
        if (barIndex >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.dataBinding.addressTextView.setText(address);
        holder.dataBinding.nameTextView.setText(stay.name);

        boolean isVisiblePrice = false;
        boolean isVisibleSatisfaction = false;

        if (stay.price <= 0 || stay.price <= stay.discountPrice)
        {
            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            holder.dataBinding.priceTextView.setText(null);
        } else
        {
            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.priceTextView.setText(strPrice);
            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            isVisiblePrice = true;
        }

        // 만족도
        if (stay.satisfaction > 0)
        {
            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
            holder.dataBinding.satisfactionView.setText(//
                mContext.getResources().getString(R.string.label_list_satisfaction, stay.satisfaction));

            isVisibleSatisfaction = true;
        } else
        {
            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        }

        if (mNights > 1)
        {
            holder.dataBinding.averageTextView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.averageTextView.setVisibility(View.GONE);
        }

        holder.dataBinding.discountPriceTextView.setText(strDiscount);
        holder.dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        holder.dataBinding.gradeTextView.setText(stay.getGrade().getName(mContext));
        holder.dataBinding.gradeTextView.setBackgroundResource(stay.getGrade().getColorResId());

        Util.requestImageResize(mContext, holder.dataBinding.imageView, stay.imageUrl);

        // SOLD OUT 표시
        if (stay.isSoldOut == true)
        {
            holder.dataBinding.soldoutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.soldoutView.setVisibility(View.GONE);
        }

        if (DailyTextUtils.isTextEmpty(stay.dBenefitText) == false)
        {
            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.dBenefitTextView.setText(stay.dBenefitText);
        } else
        {
            holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        }

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE || holder.dataBinding.trueVRView.getVisibility() == View.VISIBLE)
            {
                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dataBinding.dot1View.setVisibility(View.GONE);
            }

            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(stay.distance)));
        } else
        {
            holder.dataBinding.dot1View.setVisibility(View.GONE);
            holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        }

        // VR 여부
        if (stay.truevr == true && mTrueVREnabled == true)
        {
            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE)
            {
                holder.dataBinding.dot2View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dataBinding.dot2View.setVisibility(View.GONE);
            }

            holder.dataBinding.trueVRView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.dot2View.setVisibility(View.GONE);
            holder.dataBinding.trueVRView.setVisibility(View.GONE);
        }

        if (holder.dataBinding.satisfactionView.getVisibility() == View.GONE//
            && holder.dataBinding.trueVRView.getVisibility() == View.GONE//
            && holder.dataBinding.distanceTextView.getVisibility() == View.GONE)
        {
            holder.dataBinding.informationLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.informationLayout.setVisibility(View.VISIBLE);
        }
    }

    protected class HotelViewHolder extends RecyclerView.ViewHolder
    {
        public ListRowStayDataBinding dataBinding;

        public HotelViewHolder(ListRowStayDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            itemView.setOnClickListener(mOnClickListener);

            if (Util.supportPreview(mContext) == true)
            {
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (mOnLongClickListener == null)
                        {
                            return false;
                        } else
                        {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(70);

                            return mOnLongClickListener.onLongClick(v);
                        }
                    }
                });
            }
        }
    }
}
