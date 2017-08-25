package com.twoheart.dailyhotel.screen.hotel.list;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

public class StayListLayout extends PlaceListLayout
{
    protected StayCuration mStayCuration;

    public StayListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new StayListAdapter(context, arrayList, mOnItemClickListener, null);
    }

    //    @Override
    //    protected EventBanner getEventBanner(int index)
    //    {
    //        return StayEventBannerManager.getInstance().getEventBanner(index);
    //    }
    //
    //    @Override
    //    protected PlaceViewItem getEventBannerViewItem()
    //    {
    //        if (StayEventBannerManager.getInstance().getCount() == 0)
    //        {
    //            return null;
    //        }
    //
    //        PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
    //            StayEventBannerManager.getInstance().getList());
    //        return placeViewItem;
    //    }

    @Override
    protected void onInformationClick(View view, PlaceViewItem placeViewItem)
    {
        ((OnEventListener) mOnEventListener).onPlaceClick(view, placeViewItem);

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_MAP_DETAIL_VIEW_CLICKED, placeViewItem.<Place>getItem().name, null);
    }

    @Override
    protected void initEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView messageTextView01 = (TextView) mEmptyView.findViewById(R.id.messageTextView01);
        TextView messageTextView02 = (TextView) mEmptyView.findViewById(R.id.messageTextView02);

        messageTextView01.setText(R.string.message_stay_empty_message01);
        messageTextView02.setText(R.string.message_stay_empty_message02);

        View changeRegionView = mEmptyView.findViewById(R.id.changeRegionView);
        View changeDateView = mEmptyView.findViewById(R.id.changeDateView);

        changeRegionView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((StayListLayout.OnEventListener) mOnEventListener).onRegionClick();
            }
        });

        changeDateView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((StayListLayout.OnEventListener) mOnEventListener).onCalendarClick();
            }
        });


        TextView callTextView = (TextView) mEmptyView.findViewById(R.id.callTextView);
        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((StayListLayout.OnEventListener) mOnEventListener).onShowCallDialog();
            }
        });
    }

    @Override
    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                setScreenVisible(ScreenType.LIST);

                if (mPlaceListMapFragment != null)
                {
                    mPlaceListMapFragment.resetMenuBarLayoutTranslation();
                    fragmentManager.beginTransaction().remove(mPlaceListMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mPlaceListMapFragment = null;
                }

                ((StayListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
                ((StayListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                break;

            case MAP:
                setScreenVisible(ScreenType.MAP);

                if (isCurrentPage == true && mPlaceListMapFragment == null)
                {
                    try
                    {
                        mPlaceListMapFragment = new StayListMapFragment();
                        mPlaceListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                        fragmentManager.beginTransaction().add(mMapLayout.getId(), mPlaceListMapFragment).commitAllowingStateLoss();
                    } catch (IllegalStateException e)
                    {
                        Crashlytics.log("StayListLayout");
                        Crashlytics.logException(e);
                    }
                }

                ((StayListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
                ((StayListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                break;

            case GONE:
                StayCurationOption stayCurationOption = mStayCuration == null //
                    ? new StayCurationOption() //
                    : (StayCurationOption) mStayCuration.getCurationOption();

                if (stayCurationOption.isDefaultFilter() == true)
                {
                    setScreenVisible(ScreenType.EMPTY);
                    ((StayListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(false);
                } else
                {
                    setScreenVisible(ScreenType.FILTER_EMPTY);
                    ((StayListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
                }

                ((StayListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(false);

                if (mContext instanceof Activity)
                {
                    AnalyticsManager.getInstance(mContext).recordScreen((Activity) mContext, AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY, null);
                } else
                {
                    AnalyticsManager.getInstance(mContext).recordScreen(null, AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY, null);
                }
                break;
        }
    }

    public void setStayCuration(StayCuration curation)
    {
        mStayCuration = curation;
    }
}
