package com.twoheart.dailyhotel.screen.booking.list;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionListView.PinnedSectionListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

public class BookingListAdapter extends ArrayAdapter<Booking> implements PinnedSectionListAdapter
{
    private final String BOOKING_DATE_FORMAT = "yyyy.MM.dd(EEE)";
    private long mCurrentTime;
    private ArrayList<Booking> mBookingList;
    private Context mContext;
    BookingListFragment.OnUserActionListener mOnUserActionListener;

    public BookingListAdapter(Context context, int resourceId, ArrayList<Booking> items, long currentTime)
    {
        super(context, resourceId, items);

        if (mBookingList == null)
        {
            mBookingList = new ArrayList<>();
        }

        mBookingList.clear();
        mBookingList.addAll(items);

        this.mContext = context;
        this.mCurrentTime = currentTime;
    }

    public void setOnUserActionListener(BookingListFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    @Override
    public void clear()
    {
        if (mBookingList == null)
        {
            mBookingList = new ArrayList<>();
        }

        mBookingList.clear();

        super.clear();
    }

    @Override
    public Booking getItem(int position)
    {
        if (mBookingList == null)
        {
            return null;
        }

        return mBookingList.get(position);
    }

    @Override
    public int getCount()
    {
        if (mBookingList == null)
        {
            return 0;
        }

        return mBookingList.size();
    }

    @Override
    public void addAll(Collection<? extends Booking> collection)
    {
        if (collection == null)
        {
            return;
        }

        if (mBookingList == null)
        {
            mBookingList = new ArrayList<>();
        }

        mBookingList.addAll(collection);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == Booking.TYPE_SECTION;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Booking booking = getItem(position);

        if (convertView != null)
        {
            Integer tag = (Integer) convertView.getTag();

            if (tag == null || tag != booking.type)
            {
                convertView = null;
            }
        }

        boolean isLastPosition = false;

        int count = getCount();
        if (count > 0)
        {
            if (count - 1 == position)
            {
                isLastPosition = true;
            }
        }

        switch (booking.type)
        {
            case Booking.TYPE_ENTRY:
            {
                if (convertView == null)
                {
                    LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = layoutInflater.inflate(R.layout.list_row_booking, parent, false);
                    convertView.setTag(Booking.TYPE_ENTRY);

                    ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
                    layoutParams.height = Util.getListRowHeight(mContext);
                }

                convertView = getEntryView(convertView, booking, isLastPosition);
                break;
            }

            case Booking.TYPE_SECTION:
            {
                if (convertView == null)
                {
                    LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = layoutInflater.inflate(R.layout.list_row_default_section, parent, false);
                    convertView.setTag(Booking.TYPE_SECTION);
                }

                convertView = getSectionView(convertView, booking);
                break;
            }
        }

        return convertView;
    }

    private View getEntryView(View view, final Booking booking, boolean isLastPosition)
    {
        if (view == null || booking == null)
        {
            return view;
        }

        // 호텔 이미지
        com.facebook.drawee.view.SimpleDraweeView hotelImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.hotelImage);
        Util.requestImageResize(mContext, hotelImageView, booking.hotelImageUrl);

        TextView waitAccountTextView = (TextView) view.findViewById(R.id.waitAccountTextView);
        TextView name = (TextView) view.findViewById(R.id.placeNameTextView);
        TextView day = (TextView) view.findViewById(R.id.bookingDateTextView);
        TextView nights = (TextView) view.findViewById(R.id.bookingNightsTextView);
        View deleteView = view.findViewById(R.id.deleteView);
        View bottomDivider = view.findViewById(R.id.bottomDivider);

        name.setText(booking.placeName);

        bottomDivider.setVisibility(isLastPosition == false ? View.VISIBLE : View.GONE);

        switch (booking.placeType)
        {
            case HOTEL:
            {
                String period = String.format("%s - %s"//
                    , DailyCalendar.format(booking.checkinTime, BOOKING_DATE_FORMAT, TimeZone.getTimeZone("GMT"))//
                    , DailyCalendar.format(booking.checkoutTime, BOOKING_DATE_FORMAT, TimeZone.getTimeZone("GMT")));

                day.setText(period);

                int nightsCount = (int) ((getCompareDate(booking.checkoutTime) - getCompareDate(booking.checkinTime)) / SaleTime.MILLISECOND_IN_A_DAY);

                nights.setVisibility(View.VISIBLE);
                nights.setText(mContext.getString(R.string.label_nights, nightsCount));
                break;
            }

            case FNB:
            {
                String period = DailyCalendar.format(booking.checkinTime, BOOKING_DATE_FORMAT, TimeZone.getTimeZone("GMT"));

                day.setText(period);

                nights.setVisibility(View.GONE);
                break;
            }
        }

        if (booking.isUsed == true)
        {
            setGrayScale(hotelImageView);

            waitAccountTextView.setVisibility(View.GONE);

            deleteView.setVisibility(View.VISIBLE);

            // 삭제 버튼을 누를 경우;
            deleteView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.delete(booking);
                    }
                }
            });
        } else
        {
            hotelImageView.clearColorFilter();

            if (booking.payType == Constants.CODE_PAY_TYPE_ACCOUNT_WAIT)
            {
                waitAccountTextView.setVisibility(View.VISIBLE);
                waitAccountTextView.setText(booking.comment);
            } else
            {
                if (booking.readyForRefund == true)
                {
                    waitAccountTextView.setVisibility(View.GONE);
                } else
                {
                    String text;

                    int dayOfDays = (int) ((getCompareDate(booking.checkinTime) - getCompareDate(mCurrentTime)) / SaleTime.MILLISECOND_IN_A_DAY);
                    if (dayOfDays < 0 || dayOfDays > 3)
                    {
                        text = null;
                    } else if (dayOfDays > 0)
                    {
                        // 하루이상 남음
                        text = mContext.getString(R.string.frag_booking_duedate_formet, dayOfDays);
                    } else
                    {
                        // 당일
                        switch (booking.placeType)
                        {
                            case HOTEL:
                            {
                                text = mContext.getString(R.string.frag_booking_today_type_stay);
                                break;
                            }

                            case FNB:
                            {
                                text = mContext.getString(R.string.frag_booking_today_type_gourmet);
                                break;
                            }

                            default:
                                text = null;
                                break;
                        }
                    }

                    if (Util.isTextEmpty(text) == true)
                    {
                        waitAccountTextView.setVisibility(View.GONE);
                    } else
                    {
                        waitAccountTextView.setVisibility(View.VISIBLE);
                        waitAccountTextView.setText(text);
                    }
                }
            }

            deleteView.setVisibility(View.GONE);
        }

        return view;
    }

    private View getSectionView(View view, Booking booking)
    {
        if (view == null || booking == null)
        {
            return view;
        }

        TextView sectionName = (TextView) view.findViewById(R.id.sectionTextView);

        sectionName.setText(booking.placeName);

        return view;
    }

    private void setGrayScale(ImageView imageView)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        matrix.getArray()[18] = 0.8f;

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(colorFilter);
    }

    private long getCompareDate(long timeInMillis)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
