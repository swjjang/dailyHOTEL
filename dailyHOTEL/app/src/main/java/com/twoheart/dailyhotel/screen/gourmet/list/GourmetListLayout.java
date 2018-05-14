package com.twoheart.dailyhotel.screen.gourmet.list;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import java.util.ArrayList;

public class GourmetListLayout extends PlaceListLayout
{
    protected GourmetCuration mGourmetCuration;

    public GourmetListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new GourmetListAdapter(context, arrayList, mOnItemClickListener, null);
    }

    //    @Override
    //    protected EventBanner getEventBanner(int index)
    //    {
    //        return GourmetEventBannerManager.getInstance().getEventBanner(index);
    //    }
    //
    //    @Override
    //    protected PlaceViewItem getEventBannerViewItem()
    //    {
    //        if (GourmetEventBannerManager.getInstance().getCount() == 0)
    //        {
    //            return null;
    //        }
    //
    //        PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
    //            GourmetEventBannerManager.getInstance().getList());
    //        return placeViewItem;
    //    }

    @Override
    public void notifyWishChanged(int position, boolean wish)
    {
        if (mRecyclerView == null)
        {
            return;
        }

        mRecyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                GourmetListAdapter.GourmetViewHolder gourmetViewHolder = (GourmetListAdapter.GourmetViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

                if (gourmetViewHolder != null)
                {
                    gourmetViewHolder.gourmetCardView.setWish(wish);
                }
            }
        });
    }

    @Override
    public void notifyMapWishChanged(int position, boolean wish)
    {
        if (mPlaceListMapFragment == null)
        {
            return;
        }

        mPlaceListMapFragment.notifyViewPagerDataSetChanged();
    }

    @Override
    protected void onInformationClick(View view, PlaceViewItem placeViewItem)
    {
        ((OnEventListener) mOnEventListener).onPlaceClick(-1, view, placeViewItem);

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.GOURMET_MAP_DETAIL_VIEW_CLICKED, placeViewItem.<Place>getItem().name, null);
    }

    @Override
    protected void initEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView messageTextView01 = mEmptyView.findViewById(R.id.messageTextView01);
        TextView messageTextView02 = mEmptyView.findViewById(R.id.messageTextView02);

        messageTextView01.setText(R.string.message_gourmet_empty_message01);
        messageTextView02.setText(R.string.message_gourmet_empty_message02);

        View changeRegionView = mEmptyView.findViewById(R.id.changeRegionView);
        View changeDateView = mEmptyView.findViewById(R.id.changeDateView);

        changeRegionView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((GourmetListLayout.OnEventListener) mOnEventListener).onRegionClick();
            }
        });

        changeDateView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((GourmetListLayout.OnEventListener) mOnEventListener).onCalendarClick();
            }
        });


        TextView callTextView = mEmptyView.findViewById(R.id.callTextView);
        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((GourmetListLayout.OnEventListener) mOnEventListener).onShowCallDialog();
            }
        });
    }

    @Override
    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, Constants.EmptyStatus emptyStatus, boolean isCurrentPage)
    {
        if (emptyStatus == Constants.EmptyStatus.EMPTY)
        {
            GourmetCurationOption gourmetCurationOption = mGourmetCuration == null //
                ? new GourmetCurationOption() //
                : (GourmetCurationOption) mGourmetCuration.getCurationOption();

            if (gourmetCurationOption.isDefaultFilter() == true)
            {
                setScreenVisible(ScreenType.EMPTY);
                ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(false);
            } else
            {
                setScreenVisible(ScreenType.FILTER_EMPTY);
                mFilterEmptyView.setVisibility(View.VISIBLE);
                ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
            }

            if (viewType == Constants.ViewType.LIST)
            {
                ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(false);
            } else
            {
                ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
            }

            if (mContext instanceof Activity)
            {
                AnalyticsManager.getInstance(mContext).recordScreen((Activity) mContext, Screen.DAILYGOURMET_LIST_EMPTY, null);
            } else
            {
                AnalyticsManager.getInstance(mContext).recordScreen(null, Screen.DAILYGOURMET_LIST_EMPTY, null);
            }
        } else
        {
            switch (viewType)
            {
                case LIST:
                    setScreenVisible(ScreenType.LIST);

                    if (mPlaceListMapFragment != null)
                    {
                        mPlaceListMapFragment.resetMenuBarLayoutTranslation();
                        fragmentManager.beginTransaction().remove(mPlaceListMapFragment).commitAllowingStateLoss();

                        if (mMapLayout.getChildCount() > 0)
                        {
                            mMapLayout.removeAllViews();
                        }

                        mPlaceListMapFragment = null;
                    }

                    ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);

                    if (emptyStatus != Constants.EmptyStatus.NONE)
                    {
                        ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                    }
                    break;

                case MAP:
                    setScreenVisible(ScreenType.MAP);

                    if (isCurrentPage == true && mPlaceListMapFragment == null)
                    {
                        try
                        {
                            mPlaceListMapFragment = new GourmetListMapFragment();
                            mPlaceListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                            fragmentManager.beginTransaction().add(mMapLayout.getId(), mPlaceListMapFragment).commitAllowingStateLoss();
                        } catch (IllegalStateException e)
                        {
                            Crashlytics.log("GourmetListLayout");
                            Crashlytics.logException(e);
                        }
                    }

                    ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);

                    if (emptyStatus != Constants.EmptyStatus.NONE)
                    {
                        ((GourmetListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                    }
                    break;
            }
        }
    }

    public void setGourmetCuration(GourmetCuration curation)
    {
        mGourmetCuration = curation;
    }
}
