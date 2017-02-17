package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.GourmetTicket;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class GourmetProductListLayout extends BaseLayout
{
    private GourmetDetailTicketTypeListAdapter mTicketTypeListAdapter;
    private RecyclerView mRecyclerView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onProductDetailClick(int position);

        void onReservationClick(int position);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view, mContext.getString(R.string.label_select_product));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    private void initToolbar(View view, String title)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        }, false);
    }

    public GourmetProductListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public void setProductInformationLayout(List<GourmetTicket> gourmetTicketList)
    {
        if (gourmetTicketList == null || gourmetTicketList.size() == 0)
        {
            return;
        }

        // 처음 세팅하는 경우 객실 타입 세팅
        mTicketTypeListAdapter = new GourmetDetailTicketTypeListAdapter(mContext, gourmetTicketList, new GourmetDetailTicketTypeListAdapter.OnTicketClickListener()
        {
            @Override
            public void onProductDetailClick(int position)
            {
                ((OnEventListener) mOnEventListener).onProductDetailClick(position);
            }

            @Override
            public void onReservationClick(int position)
            {
                ((OnEventListener) mOnEventListener).onReservationClick(position);
            }
        });

        //                new OnClickListener()
        //            {
        //                @Override
        //                public void onClick(View v)
        //                {
        //                    int position = mProductTypeRecyclerView.getChildAdapterPosition(v);
        //
        //                    if (position < 0)
        //                    {
        //                        return;
        //                    }
        //
        //                    mSelectedRoomInformation = mRoomTypeListAdapter.getItem(position);
        //                    mRoomTypeListAdapter.setSelected(position);
        //                    mRoomTypeListAdapter.notifyDataSetChanged();
        //
        //                    AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
        //                        , AnalyticsManager.Action.ROOM_TYPE_ITEM_CLICKED, mSelectedRoomInformation.roomName, null);
        //                }
        //            });
    }
}