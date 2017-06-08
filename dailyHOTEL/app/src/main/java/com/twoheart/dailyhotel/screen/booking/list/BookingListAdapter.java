package com.twoheart.dailyhotel.screen.booking.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.ListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowBookingDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowDefaultSectionDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class BookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PinnedSectionRecyclerView.PinnedSectionListAdapter
{
    private final String BOOKING_DATE_FORMAT = "yyyy.MM.dd(EEE)";
    private List<ListItem> mList;
    private Context mContext;
    BookingListFragment.OnUserActionListener mOnUserActionListener;

    public BookingListAdapter(Context context, ArrayList<ListItem> arrayList)
    {
        mList = new ArrayList<>();

        addAll(arrayList);

        mContext = context;
    }

    public void setOnUserActionListener(BookingListFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    public void clear()
    {
        mList.clear();
    }

    public ListItem getItem(int position)
    {
        if (mList == null)
        {
            return null;
        }

        return mList.get(position);
    }

    public void addAll(Collection<? extends ListItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == ListItem.TYPE_SECTION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ListItem.TYPE_ENTRY:
            {
                ListRowBookingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_booking_data, parent, false);

                return new BookingViewHolder(dataBinding);
            }

            case ListItem.TYPE_SECTION:
            {
                ListRowDefaultSectionDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_default_section_data, parent, false);

                return new SectionViewHolder(dataBinding);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ListItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case ListItem.TYPE_ENTRY:
                onBindViewHolder((BookingViewHolder) holder, item, position);
                break;

            case ListItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).mType;
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

    private void onBindViewHolder(BookingViewHolder holder, ListItem listItem, int position)
    {
        if (holder == null || listItem == null)
        {
            return;
        }

        Booking booking = listItem.getItem();
        boolean isLastPosition = getItemCount() - 1 == position;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.dataBinding.listItemLayout.getLayoutParams();

        if (isLastPosition == true)
        {
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.bottom_navigation_height_over21);
        } else
        {
            layoutParams.bottomMargin = 0;
        }

        holder.dataBinding.listItemLayout.setLayoutParams(layoutParams);

        // 호텔 이미지
        Util.requestImageResize(mContext, holder.dataBinding.simpleDraweeView, booking.imageUrl);

        holder.dataBinding.placeNameTextView.setText(booking.placeName);

        try
        {
            switch (booking.placeType)
            {
                case STAY:
                {
                    String period = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, "yyyy-MM-dd", BOOKING_DATE_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkOutDateTime, "yyyy-MM-dd", BOOKING_DATE_FORMAT));

                    holder.dataBinding.bookingDateTextView.setText(period);

                    int nights = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(booking.checkOutDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));

                    holder.dataBinding.bookingNightsTextView.setVisibility(View.VISIBLE);
                    holder.dataBinding.bookingNightsTextView.setText(mContext.getString(R.string.label_nights, nights));
                    break;
                }

                case GOURMET:
                {
                    String period = DailyCalendar.convertDateFormatString(booking.checkInDateTime, "yyyy-MM-dd", BOOKING_DATE_FORMAT);

                    holder.dataBinding.bookingDateTextView.setText(period);
                    holder.dataBinding.bookingNightsTextView.setVisibility(View.GONE);
                    break;
                }

                case STAY_OUTBOUND:
                {
                    String period = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, "yyyy-MM-dd", BOOKING_DATE_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkOutDateTime, "yyyy-MM-dd", BOOKING_DATE_FORMAT));

                    holder.dataBinding.bookingDateTextView.setText(period);

                    int nights = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(booking.checkOutDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));

                    holder.dataBinding.bookingNightsTextView.setVisibility(View.VISIBLE);
                    holder.dataBinding.bookingNightsTextView.setText(mContext.getString(R.string.label_nights, nights));
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (booking.isUsed == true)
        {
            setGrayScale(holder.dataBinding.simpleDraweeView);

            holder.dataBinding.waitAccountTextView.setVisibility(View.GONE);

            holder.dataBinding.deleteView.setVisibility(View.VISIBLE);

            // 삭제 버튼을 누를 경우;
            holder.dataBinding.deleteView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.onDeleteClick(booking);
                    }
                }
            });
        } else
        {
            holder.dataBinding.simpleDraweeView.clearColorFilter();

            if (booking.statusPayment == Booking.WAIT_PAYMENT)
            {
                holder.dataBinding.waitAccountTextView.setVisibility(View.VISIBLE);
                holder.dataBinding.waitAccountTextView.setText(booking.comment);
            } else
            {
                if (booking.placeType == Booking.PlaceType.STAY_OUTBOUND)
                {
                    holder.dataBinding.waitAccountTextView.setVisibility(View.GONE);
                } else
                {
                    if (booking.readyForRefund == true)
                    {
                        holder.dataBinding.waitAccountTextView.setVisibility(View.GONE);
                    } else
                    {
                        String text;

                        if (booking.placeType == Booking.PlaceType.STAY_OUTBOUND)
                        {
                            text = null;
                        } else
                        {
                            if (booking.remainingDays == 0)
                            {
                                // 당일
                                switch (booking.placeType)
                                {
                                    case STAY:
                                    {
                                        text = mContext.getString(R.string.frag_booking_today_type_stay);
                                        break;
                                    }

                                    case GOURMET:
                                    {
                                        text = mContext.getString(R.string.frag_booking_today_type_gourmet);
                                        break;
                                    }

                                    default:
                                        text = null;
                                        break;
                                }
                            } else if (booking.remainingDays > 0 && booking.remainingDays <= 3)
                            {
                                // 하루이상 남음
                                text = mContext.getString(R.string.frag_booking_duedate_formet, booking.remainingDays);
                            } else
                            {
                                text = null;
                            }
                        }

                        if (DailyTextUtils.isTextEmpty(text) == true)
                        {
                            holder.dataBinding.waitAccountTextView.setVisibility(View.GONE);
                        } else
                        {
                            holder.dataBinding.waitAccountTextView.setVisibility(View.VISIBLE);
                            holder.dataBinding.waitAccountTextView.setText(text);
                        }
                    }
                }
            }

            holder.dataBinding.deleteView.setVisibility(View.GONE);
        }

        holder.dataBinding.getRoot().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onBookingClick(booking);
                }
            }
        });
    }

    private void onBindViewHolder(SectionViewHolder holder, ListItem listItem, int position)
    {
        if (holder == null || listItem == null)
        {
            return;
        }

        holder.dataBinding.sectionTextView.setText(listItem.getItem());
    }

    private void setGrayScale(ImageView imageView)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        matrix.getArray()[18] = 0.8f;

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(colorFilter);
    }


    private class BookingViewHolder extends RecyclerView.ViewHolder
    {
        ListRowBookingDataBinding dataBinding;

        public BookingViewHolder(ListRowBookingDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    private class SectionViewHolder extends RecyclerView.ViewHolder
    {
        ListRowDefaultSectionDataBinding dataBinding;

        public SectionViewHolder(ListRowDefaultSectionDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }
}
