package com.daily.dailyhotel.screen.booking.cancel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.BookingCancel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowBookingCancelDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by android_sam on 2017. 10. 19..
 */

public class BookingCancelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private List<BookingCancel> mList;
    private RecyclerView mRecyclerView;
    private BookingCancelListView.OnUserActionListener mOnUserActionListener;

    public BookingCancelListAdapter(Context context, ArrayList<BookingCancel> list)
    {
        mContext = context;

        mList = new ArrayList<>();

        addAll(list);
    }

    public void setOnUserActionListener(BookingCancelListView.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    public void clear()
    {
        mList.clear();
    }

    public void addAll(List<BookingCancel> list)
    {
        if (list == null || list.size() == 0)
        {
            return;
        }

        mList.addAll(list);
    }

    public BookingCancel getItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        return mList.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ListRowBookingCancelDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_booking_cancel_data, parent, false);

        return new BookingCancelViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        onBindViewHolder((BookingCancelViewHolder) holder, position);
    }

    private void onBindViewHolder(BookingCancelViewHolder holder, int position)
    {
        BookingCancel bookingCancel = getItem(position);
        if (bookingCancel == null)
        {
            return;
        }

        boolean isFirstPosition = position == 0;
        boolean isLastPosition = getItemCount() - 1 == position;

        if (isFirstPosition == true)
        {
            holder.dataBinding.topVerticalLineView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.topVerticalLineView.setVisibility(View.GONE);
        }

        ConstraintLayout.LayoutParams lineParams1 = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView01.getLayoutParams();
        lineParams1.topMargin = 0;
        //                holder.dataBinding.verticalLineView01.requestLayout();

        holder.dataBinding.verticalLineView01.setBackgroundResource(R.color.default_background_ce0e0e0);

        holder.dataBinding.topVerticalLineView.setBackgroundResource(R.color.default_background_ce0e0e0);
        holder.dataBinding.circleView.setBackgroundResource(R.drawable.shape_circle_bd9d9d9);

        ConstraintLayout.LayoutParams lineParams2 = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView02.getLayoutParams();
        lineParams2.topMargin = ScreenUtils.dpToPx(mContext, 4);

        holder.dataBinding.verticalLineView02.setBackgroundResource(R.color.default_background_ce0e0e0);

        holder.dataBinding.bookingStatusTextView.setText(R.string.label_booking_after_cancel);
        holder.dataBinding.bookingStatusTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c666666));
        holder.dataBinding.bookingDayTextView.setVisibility(View.GONE);
        holder.dataBinding.buttonLayout.setVisibility(View.VISIBLE);

        holder.dataBinding.bookingStatusDescriptionTextView.setText(null);

        holder.dataBinding.buttonVerticalLine.setVisibility(View.GONE);
        holder.dataBinding.reviewTextView.setVisibility(View.GONE);

        // 호텔 이미지
        holder.dataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        Util.requestImageResize(mContext, holder.dataBinding.simpleDraweeView, bookingCancel.imageUrl);

        holder.dataBinding.placeNameTextView.setText(bookingCancel.name);

        try
        {
            final String BOOKING_DATE_FORMAT = "yyyy.MM.dd(EEE)";

            switch (bookingCancel.placeType)
            {
                case STAY:
                case STAY_OUTBOUND:
                {
                    String period = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(bookingCancel.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT)//
                        , DailyCalendar.convertDateFormatString(bookingCancel.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT));

                    int nights = DailyCalendar.compareDateDay(bookingCancel.checkOutDateTime, bookingCancel.checkInDateTime);

                    holder.dataBinding.bookingDateTextView.setText(period + "·" + mContext.getString(R.string.label_nights, nights));
                    break;
                }

                case GOURMET:
                {
                    String period = DailyCalendar.convertDateFormatString(bookingCancel.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT);

                    holder.dataBinding.bookingDateTextView.setText(period);
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        // 하단 높이 조절
        if (isLastPosition == true)
        {
            holder.dataBinding.bottomEmptyView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 78)//
                + mContext.getResources().getDimensionPixelSize(R.dimen.bottom_navigation_height_over21);
        } else
        {
            holder.dataBinding.bottomEmptyView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 49);
        }

        holder.dataBinding.getRoot().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onBookingClick(bookingCancel);
                }
            }
        });

        // 해당 영역은 빈영역이므로 클릭할수 없도록 수정
        holder.dataBinding.bottomEmptyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        holder.dataBinding.againBookingTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onAgainBookingClick(bookingCancel);
                }
            }
        });

        if (isLastPosition == true)
        {
            holder.dataBinding.getRoot().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override
                public boolean onPreDraw()
                {
                    holder.dataBinding.getRoot().getViewTreeObserver().removeOnPreDrawListener(this);

                    if (holder.dataBinding.getRoot().getBottom() < mRecyclerView.getBottom())
                    {
                        holder.dataBinding.bottomEmptyView.getLayoutParams().height += mRecyclerView.getBottom() - holder.dataBinding.getRoot().getBottom();
                        holder.dataBinding.bottomEmptyView.requestLayout();
                    }

                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    private class BookingCancelViewHolder extends RecyclerView.ViewHolder
    {
        ListRowBookingCancelDataBinding dataBinding;

        public BookingCancelViewHolder(ListRowBookingCancelDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }
}
