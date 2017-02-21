package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class GourmetProductListLayout extends BaseLayout
{
    private GourmetProductListAdapter mProductTypeListAdapter;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
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

    public void setProductInformationLayout(List<GourmetProduct> gourmetProductList)
    {
        if (gourmetProductList == null || gourmetProductList.size() == 0)
        {
            return;
        }

        List<PlaceViewItem> placeViewItemList = new ArrayList<>(gourmetProductList.size() + 1);

        for(GourmetProduct gourmetProduct : gourmetProductList)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmetProduct));
        }

        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));

        // 처음 세팅하는 경우 객실 타입 세팅setProductInformationLayout
        mProductTypeListAdapter = new GourmetProductListAdapter(mContext, placeViewItemList, new GourmetProductListAdapter.OnProductClickListener()
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


        mRecyclerView.setAdapter(mProductTypeListAdapter);

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