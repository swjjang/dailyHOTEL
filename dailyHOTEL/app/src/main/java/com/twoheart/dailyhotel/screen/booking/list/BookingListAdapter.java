package com.twoheart.dailyhotel.screen.booking.list;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionListView.PinnedSectionListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

public class BookingListAdapter extends ArrayAdapter<Booking> implements PinnedSectionListAdapter
{
    private final String BOOKING_DATE_FORMAT = "yyyy.MM.dd(EEE)";
    private ArrayList<Booking> mBookingList;
    private Context mContext;
    BookingListFragment.OnUserActionListener mOnUserActionListener;

    public BookingListAdapter(Context context, int resourceId, ArrayList<Booking> items)
    {
        super(context, resourceId, items);

        if (mBookingList == null)
        {
            mBookingList = new ArrayList<>();
        }

        mBookingList.clear();
        mBookingList.addAll(items);

        mContext = context;
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

        View listItemLayout = view.findViewById(R.id.listItemLayout);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listItemLayout.getLayoutParams();

        if (isLastPosition == true)
        {
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.bottom_navigation_height_over21);
        } else
        {
            layoutParams.bottomMargin = 0;
        }

        layoutParams.height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext));
        listItemLayout.setLayoutParams(layoutParams);

        // 호텔 이미지
        com.facebook.drawee.view.SimpleDraweeView hotelImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.hotelImage);
        Util.requestImageResize(mContext, hotelImageView, booking.hotelImageUrl);

        TextView waitAccountTextView = (TextView) view.findViewById(R.id.waitAccountTextView);
        TextView name = (TextView) view.findViewById(R.id.placeNameTextView);
        TextView day = (TextView) view.findViewById(R.id.bookingDateTextView);
        TextView nights = (TextView) view.findViewById(R.id.bookingNightsTextView);
        View deleteView = view.findViewById(R.id.deleteView);

        name.setText(booking.placeName);

        switch (booking.placeType)
        {
            case HOTEL:
            {
                String period = String.format(Locale.KOREA, "%s - %s"//
                    , DailyCalendar.format(booking.checkinTime, BOOKING_DATE_FORMAT, TimeZone.getTimeZone("GMT+09:00"))//
                    , DailyCalendar.format(booking.checkoutTime, BOOKING_DATE_FORMAT, TimeZone.getTimeZone("GMT+09:00")));

                day.setText(period);

                int nightsCount = (int) ((DailyCalendar.clearTField(booking.checkoutTime) - DailyCalendar.clearTField(booking.checkinTime)) / DailyCalendar.DAY_MILLISECOND);

                nights.setVisibility(View.VISIBLE);
                nights.setText(mContext.getString(R.string.label_nights, nightsCount));
                break;
            }

            case FNB:
            {
                String period = DailyCalendar.format(booking.checkinTime, BOOKING_DATE_FORMAT, TimeZone.getTimeZone("GMT+09:00"));

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

                    if (booking.leftFromToDay == 0)
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
                    } else if (booking.leftFromToDay > 0 && booking.leftFromToDay <= 3)
                    {
                        // 하루이상 남음
                        text = mContext.getString(R.string.frag_booking_duedate_formet, booking.leftFromToDay);
                    } else
                    {
                        text = null;
                    }

                    if (DailyTextUtils.isTextEmpty(text) == true)
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
}
