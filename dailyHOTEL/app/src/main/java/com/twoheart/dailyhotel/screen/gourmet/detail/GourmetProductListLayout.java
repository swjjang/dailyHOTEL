package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

public class GourmetProductListLayout extends BaseLayout
{
    private GourmetDetailTicketTypeListAdapter mTicketTypeListAdapter;
    private RecyclerView mRecyclerView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onProductDetailClick(TicketInformation ticketInformation);

        void onReservationClick(TicketInformation ticketInformation);
    }

    @Override
    protected void initLayout(View view)
    {

    }

    public GourmetProductListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }





}