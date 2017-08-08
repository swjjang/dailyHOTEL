package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewCampaignTagTitleDataBinding;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.Locale;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class DailyCampainTagTitleView extends ConstraintLayout
{
    private DailyViewCampaignTagTitleDataBinding mViewDataBinding;
    private Context mContext;

//    private PlaceBookingDay mPlaceBookingDay;

    private DailyCampainTagTitleView.OnEventListener mEventListener;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();
    }

    public DailyCampainTagTitleView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyCampainTagTitleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyCampainTagTitleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mContext = context;

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_campaign_tag_title_data, this, true);

        mViewDataBinding.backImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mEventListener == null)
                {
                    return;
                }

                mEventListener.onBackClick();
            }
        });

        initCalendarView();

        setResultCount(0);
    }

    private void initCalendarView()
    {
        setCalendarText(null);

        mViewDataBinding.calendarLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mEventListener == null)
                {
                    return;
                }

                mEventListener.onCalendarClick();
            }
        });
    }

    public void setOnEventListener(OnEventListener eventListener)
    {
        mEventListener = eventListener;
    }

    public void setResultCount(int count)
    {
        if (mViewDataBinding == null || mContext == null)
        {
            return;
        }

        if (count > 0)
        {
            String resultString = mContext.getString(R.string.label_searchresult_resultcount, count);
            mViewDataBinding.resultCountView.setText(resultString);
            mViewDataBinding.resultCountView.setVisibility(View.VISIBLE);
        } else
        {
            mViewDataBinding.resultCountView.setText(null);
            mViewDataBinding.resultCountView.setVisibility(View.GONE);
        }
    }

    public void setTitleText(String title)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(title);
    }

    public void setTitleText(int titleResId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(titleResId);
    }

    public void setCalendarText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.calendarTextView.setText(text);
    }

//    private void setCalendarText(final PlaceBookingDay placeBookingDay)
//    {
//        if (mViewDataBinding == null)
//        {
//            return;
//        }
//
//        if (placeBookingDay == null)
//        {
//            return;
//        }
//
//        String calendarDate = null;
//        if (placeBookingDay instanceof StayBookingDay)
//        {
//            calendarDate = getStayCalendarDate((StayBookingDay) placeBookingDay);
//        } else if (placeBookingDay instanceof GourmetBookingDay)
//        {
//            calendarDate = getGourmetCalendarDate((GourmetBookingDay) placeBookingDay);
//        }
//
//        mViewDataBinding.calendarTextView.setText(calendarDate);
//    }
//
//    private String getStayCalendarDate(StayBookingDay stayBookingDay)
//    {
//        if (stayBookingDay == null)
//        {
//            return null;
//        }
//
//        try
//        {
//            String checkInDate = stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)");
//            String checkOutDate = stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)");
//
//            int nights = stayBookingDay.getNights();
//
//            return String.format(Locale.KOREA, "%s - %s, %d박", checkInDate, checkOutDate, nights);
//        } catch (Exception e)
//        {
//            ExLog.e(e.toString());
//        }
//
//        return null;
//    }
//
//    protected String getGourmetCalendarDate(GourmetBookingDay gourmetBookingDay)
//    {
//        if (gourmetBookingDay == null)
//        {
//            return null;
//        }
//
//        return gourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)");
//    }
}
