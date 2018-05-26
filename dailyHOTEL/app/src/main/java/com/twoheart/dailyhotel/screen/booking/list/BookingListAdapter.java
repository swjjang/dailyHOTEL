package com.twoheart.dailyhotel.screen.booking.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Booking;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowBookingDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder>
{
    private List<Booking> mList;
    RecyclerView mRecyclerView;
    Context mContext;
    BookingListFragment.OnUserActionListener mOnUserActionListener;

    public BookingListAdapter(Context context, ArrayList<Booking> arrayList)
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

    public Booking getItem(int position)
    {
        if (mList == null)
        {
            return null;
        }

        return mList.get(position);
    }

    public void addAll(Collection<Booking> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public BookingListAdapter.BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ListRowBookingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_booking_data, parent, false);

        return new BookingViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(BookingListAdapter.BookingViewHolder holder, int position)
    {
        Booking booking = getItem(position);

        if (booking == null)
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

        Booking previousPositionBooking = null;

        if (position > 0)
        {
            previousPositionBooking = getItem(position - 1);
        }

        int bookingState = previousPositionBooking == null ? booking.bookingState : previousPositionBooking.bookingState;

        // 큰 원의 약간 윗점.
        switch (bookingState)
        {
            case Booking.BOOKING_STATE_RESERVATION_WAITING:
            case Booking.BOOKING_STATE_DEPOSIT_WAITING:
            case Booking.BOOKING_STATE_WAITING_REFUND:
            {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView01.getLayoutParams();
                layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 1);
                //                holder.dataBinding.verticalLineView01.requestLayout();

                holder.dataBinding.verticalLineView01.setBackgroundResource(R.color.default_line_cd9d9d9);
                break;
            }

            case Booking.BOOKING_STATE_BEFORE_USE:
            {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView01.getLayoutParams();
                layoutParams.topMargin = 0;
                //                holder.dataBinding.verticalLineView01.requestLayout();

                holder.dataBinding.verticalLineView01.setBackgroundResource(R.color.default_background_c99cfad80);
                break;
            }

            case Booking.BOOKING_STATE_AFTER_USE:
            {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView01.getLayoutParams();
                layoutParams.topMargin = 0;
                //                holder.dataBinding.verticalLineView01.requestLayout();

                holder.dataBinding.verticalLineView01.setBackgroundResource(R.color.default_background_ce0e0e0);
                break;
            }
        }

        // 왼쪽 선그리기
        switch (booking.bookingState)
        {
            case Booking.BOOKING_STATE_RESERVATION_WAITING:
            case Booking.BOOKING_STATE_DEPOSIT_WAITING:
            case Booking.BOOKING_STATE_WAITING_REFUND:
            {
                holder.dataBinding.topVerticalLineView.setBackgroundResource(R.drawable.bitmap_timeline_dark_tile);
                holder.dataBinding.circleView.setBackgroundResource(R.drawable.shape_circle_bd9d9d9);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView02.getLayoutParams();
                layoutParams.topMargin = 0;

                holder.dataBinding.verticalLineView02.setBackgroundResource(R.drawable.bitmap_timeline_dark_tile);

                break;
            }

            case Booking.BOOKING_STATE_BEFORE_USE:
            {
                holder.dataBinding.topVerticalLineView.setBackgroundResource(R.color.default_background_c99cfad80);
                holder.dataBinding.circleView.setBackgroundResource(R.drawable.shape_circle_bcfad80);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView02.getLayoutParams();
                layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 4);

                holder.dataBinding.verticalLineView02.setBackgroundResource(R.color.default_background_c99cfad80);
                break;
            }

            case Booking.BOOKING_STATE_AFTER_USE:
            {
                holder.dataBinding.topVerticalLineView.setBackgroundResource(R.color.default_background_ce0e0e0);
                holder.dataBinding.circleView.setBackgroundResource(R.drawable.shape_circle_bd9d9d9);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.verticalLineView02.getLayoutParams();
                layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 4);

                holder.dataBinding.verticalLineView02.setBackgroundResource(R.color.default_background_ce0e0e0);
                break;
            }
        }

        switch (booking.bookingState)
        {
            case Booking.BOOKING_STATE_WAITING_REFUND:
                holder.dataBinding.bookingStatusTextView.setText(R.string.frag_booking_wait_refund);
                holder.dataBinding.bookingStatusTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cda2853));
                holder.dataBinding.bookingDayTextView.setVisibility(View.GONE);
                holder.dataBinding.bookingStatusDescriptionTextView.setText(R.string.message_booking_free_cancel_waiting);
                holder.dataBinding.buttonLayout.setVisibility(View.GONE);
                break;

            case Booking.BOOKING_STATE_BEFORE_USE:
                holder.dataBinding.bookingStatusTextView.setText(R.string.label_booking_before_use);
                holder.dataBinding.bookingStatusTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_ccf9e5e));

                String description;
                String dayText;

                if (booking.placeType == Booking.PlaceType.STAY_OUTBOUND)
                {
                    description = null;
                    dayText = null;
                } else
                {
                    if (booking.remainingDays == 0)
                    {
                        // 당일
                        switch (booking.placeType)
                        {
                            case STAY:
                            {
                                description = mContext.getString(R.string.frag_booking_today_type_stay);
                                break;
                            }

                            case GOURMET:
                            {
                                description = mContext.getString(R.string.frag_booking_today_type_gourmet);
                                break;
                            }

                            default:
                                description = null;
                                break;
                        }

                        dayText = mContext.getString(R.string.label_booking_today);

                    } else if (booking.remainingDays > 0 && booking.remainingDays <= 3)
                    {
                        // 하루이상 남음
                        switch (booking.placeType)
                        {
                            case STAY:
                            {
                                description = mContext.getString(R.string.frag_booking_duedate_formet_stay, booking.remainingDays);
                                break;
                            }

                            case GOURMET:
                            {
                                description = mContext.getString(R.string.frag_booking_duedate_formet_gourmet, booking.remainingDays);
                                break;
                            }

                            default:
                                description = null;
                                break;
                        }

                        dayText = mContext.getString(R.string.label_booking_before_day, booking.remainingDays);
                    } else
                    {
                        description = null;
                        dayText = null;
                    }
                }

                holder.dataBinding.bookingStatusDescriptionTextView.setText(description);

                if (DailyTextUtils.isTextEmpty(dayText) == false)
                {
                    holder.dataBinding.bookingDayTextView.setVisibility(View.VISIBLE);
                    holder.dataBinding.bookingDayTextView.setText(dayText);
                } else
                {
                    holder.dataBinding.bookingDayTextView.setVisibility(View.GONE);
                }

                holder.dataBinding.buttonLayout.setVisibility(View.GONE);
                break;

            case Booking.BOOKING_STATE_AFTER_USE:
                holder.dataBinding.bookingStatusTextView.setText(R.string.label_booking_after_use);
                holder.dataBinding.bookingStatusTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c666666));
                holder.dataBinding.bookingDayTextView.setVisibility(View.GONE);
                holder.dataBinding.buttonLayout.setVisibility(View.VISIBLE);

                if (booking.availableReview)
                {
                    holder.dataBinding.bookingStatusDescriptionTextView.setText(R.string.message_booking_add_review);

                    holder.dataBinding.buttonVerticalLine.setVisibility(View.VISIBLE);
                    holder.dataBinding.reviewTextView.setVisibility(View.VISIBLE);
                } else
                {
                    holder.dataBinding.bookingStatusDescriptionTextView.setText(null);

                    holder.dataBinding.buttonVerticalLine.setVisibility(View.GONE);
                    holder.dataBinding.reviewTextView.setVisibility(View.GONE);
                }
                break;

            case Booking.BOOKING_STATE_DEPOSIT_WAITING:
                holder.dataBinding.bookingStatusTextView.setText(R.string.frag_booking_wait_account);
                holder.dataBinding.bookingStatusTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cda2853));
                holder.dataBinding.bookingDayTextView.setVisibility(View.GONE);
                holder.dataBinding.bookingStatusDescriptionTextView.setText(R.string.message_booking_please_deposit_completed);
                holder.dataBinding.buttonLayout.setVisibility(View.GONE);
                break;

            case Booking.BOOKING_STATE_RESERVATION_WAITING:
                holder.dataBinding.bookingStatusTextView.setText(R.string.frag_booking_wait_reservation);
                holder.dataBinding.bookingStatusTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cda2853));
                holder.dataBinding.bookingDayTextView.setVisibility(View.GONE);
                holder.dataBinding.bookingStatusDescriptionTextView.setText(R.string.message_booking_reservation_waiting);
                holder.dataBinding.buttonLayout.setVisibility(View.GONE);
                break;
        }

        // 호텔 이미지
        holder.dataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        Util.requestImageResize(mContext, holder.dataBinding.simpleDraweeView, booking.imageUrl);

        holder.dataBinding.placeNameTextView.setText(booking.placeName);

        try
        {
            final String BOOKING_DATE_FORMAT = "yyyy.MM.dd(EEE)";

            switch (booking.placeType)
            {
                case STAY:
                case STAY_OUTBOUND:
                {
                    String period = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT));

                    int nights = DailyCalendar.compareDateDay(booking.checkOutDateTime, booking.checkInDateTime);

                    holder.dataBinding.bookingDateTextView.setText(period + "·" + mContext.getString(R.string.label_nights, nights));
                    break;
                }

                case GOURMET:
                {
                    String period = DailyCalendar.convertDateFormatString(booking.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT);

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
                    mOnUserActionListener.onBookingClick(booking);
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
                    mOnUserActionListener.onAgainBookingClick(booking);
                }
            }
        });

        holder.dataBinding.reviewTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onReviewClick(booking);
                }
            }
        });

        if (isLastPosition == false)
        {
            switch (bookingState)
            {
                case Booking.BOOKING_STATE_DEPOSIT_WAITING:
                case Booking.BOOKING_STATE_WAITING_REFUND:

                    holder.dataBinding.getRoot().measure(View.MeasureSpec.makeMeasureSpec(mRecyclerView.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    final int height = holder.dataBinding.getRoot().getMeasuredHeight() - ScreenUtils.dpToPx(mContext, 13) - (isFirstPosition ? ScreenUtils.dpToPx(mContext, 51) : 0);
                    final int remainder = height % ScreenUtils.dpToPx(mContext, 6);
                    final int plusHeight = ScreenUtils.dpToPx(mContext, 3);

                    if (remainder == 0)
                    {
                        holder.dataBinding.bottomEmptyView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 49) + plusHeight;
                    } else
                    {
                        holder.dataBinding.bottomEmptyView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 49) - remainder + plusHeight;
                    }

                    holder.dataBinding.bottomEmptyView.requestLayout();
                    break;
            }
        } else
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
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder
    {
        ListRowBookingDataBinding dataBinding;

        public BookingViewHolder(ListRowBookingDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }
}
