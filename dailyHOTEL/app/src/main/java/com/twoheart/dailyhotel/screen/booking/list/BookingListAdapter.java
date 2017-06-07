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
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.ListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionListView.PinnedSectionListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class BookingListAdapter extends ArrayAdapter<ListItem> implements PinnedSectionListAdapter
{
    private final String BOOKING_DATE_FORMAT = "yyyy.MM.dd(EEE)";
    private List<ListItem> mList;
    private Context mContext;
    BookingListFragment.OnUserActionListener mOnUserActionListener;

    public BookingListAdapter(Context context, int resourceId, ArrayList<ListItem> arrayList)
    {
        super(context, resourceId, arrayList);

        mList = new ArrayList<>();

        addAll(arrayList);

        mContext = context;
    }

    public void setOnUserActionListener(BookingListFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    @Override
    public void clear()
    {
        mList.clear();

        super.clear();
    }

    @Override
    public ListItem getItem(int position)
    {
        if (mList == null)
        {
            return null;
        }

        return mList.get(position);
    }

    @Override
    public int getCount()
    {
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    @Override
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
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).mType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ListItem listItem = getItem(position);

        if (convertView != null)
        {
            Integer tag = (Integer) convertView.getTag();

            if (tag == null || tag != listItem.mType)
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

        switch (listItem.mType)
        {
            case ListItem.TYPE_ENTRY:
            {
                if (convertView == null)
                {
                    LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = layoutInflater.inflate(R.layout.list_row_booking, parent, false);
                    convertView.setTag(com.twoheart.dailyhotel.model.Booking.TYPE_ENTRY);
                }

                convertView = getEntryView(convertView, listItem.getItem(), isLastPosition);
                break;
            }

            case ListItem.TYPE_SECTION:
            {
                if (convertView == null)
                {
                    LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = layoutInflater.inflate(R.layout.list_row_default_section, parent, false);
                    convertView.setTag(com.twoheart.dailyhotel.model.Booking.TYPE_SECTION);
                }

                convertView = getSectionView(convertView, listItem.getItem());
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
        Util.requestImageResize(mContext, hotelImageView, booking.imageUrl);

        TextView waitAccountTextView = (TextView) view.findViewById(R.id.waitAccountTextView);
        TextView name = (TextView) view.findViewById(R.id.placeNameTextView);
        TextView day = (TextView) view.findViewById(R.id.bookingDateTextView);
        TextView nights = (TextView) view.findViewById(R.id.bookingNightsTextView);
        View deleteView = view.findViewById(R.id.deleteView);

        name.setText(booking.placeName);

        try
        {
            switch (booking.placeType)
            {
                case STAY:
                {
                    String period = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT));

                    day.setText(period);

                    int nightsCount = DailyCalendar.compareDateDay(booking.checkOutDateTime, booking.checkInDateTime);

                    nights.setVisibility(View.VISIBLE);
                    nights.setText(mContext.getString(R.string.label_nights, nightsCount));
                    break;
                }

                case GOURMET:
                {
                    String period = DailyCalendar.convertDateFormatString(booking.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT);

                    day.setText(period);

                    nights.setVisibility(View.GONE);
                    break;
                }

                case STAY_OUTBOUND:
                {
                    String period = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(booking.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, BOOKING_DATE_FORMAT));

                    day.setText(period);

                    int nightsCount = DailyCalendar.compareDateDay(booking.checkOutDateTime, booking.checkInDateTime);

                    nights.setVisibility(View.VISIBLE);
                    nights.setText(mContext.getString(R.string.label_nights, nightsCount));
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
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
                if (booking.placeType == Booking.PlaceType.STAY_OUTBOUND)
                {
                    waitAccountTextView.setVisibility(View.GONE);
                } else
                {
                    if (booking.readyForRefund == true)
                    {
                        waitAccountTextView.setVisibility(View.GONE);
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
                            waitAccountTextView.setVisibility(View.GONE);
                        } else
                        {
                            waitAccountTextView.setVisibility(View.VISIBLE);
                            waitAccountTextView.setText(text);
                        }
                    }
                }
            }

            deleteView.setVisibility(View.GONE);
        }

        return view;
    }

    private View getSectionView(View view, com.twoheart.dailyhotel.model.Booking booking)
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
