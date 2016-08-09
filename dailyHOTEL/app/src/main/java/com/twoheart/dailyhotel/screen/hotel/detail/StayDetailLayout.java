package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

/**
 * 호텔 상세 정보 화면
 *
 * @author sheldon
 */
public class StayDetailLayout extends PlaceDetailLayout
{
    private StayDetailListAdapter mListAdapter;
    private RoomInformation mSelectedRoomInformation;

    private StayDetailRoomTypeListAdapter mRoomTypeListAdapter;

    public interface OnEventListener extends PlaceDetailLayout.OnEventListener
    {
        void doBooking(RoomInformation roomInformation);
    }

    public StayDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected String getProductTypeTitle()
    {
        return mContext.getString(R.string.act_hotel_search_room);
    }

    @Override
    protected View getTitleLayout()
    {
        if (mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getTitleLayout();
    }

    @Override
    protected View getGradeTextView()
    {
        if (mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getGradeTextView();
    }

    @Override
    protected View getMagicToolbarView()
    {
        if (mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getMagicToolbarView();
    }

    public void setDetail(SaleTime saleTime, StayDetail stayDetail, int imagePosition)
    {
        if (stayDetail == null)
        {
            return;
        }

        mPlaceDetail = stayDetail;

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        ArrayList<ImageInformation> imageInformationList = stayDetail.getImageInformationList();
        mImageAdapter.setData(imageInformationList);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);
        mDailyLineIndicator.setImageInformation((imageInformationList.size() > 0) //
            ? imageInformationList.get(0).description : null);

        if (mListAdapter == null)
        {
            mListAdapter = new StayDetailListAdapter(mContext, saleTime, stayDetail, (StayDetailLayout.OnEventListener) mOnEventListener, mEmptyViewOnTouchListener);
            mListView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(stayDetail, saleTime);
        }

        setCurrentImage(imagePosition);

        hideProductInformationLayout();

        // SOLD OUT 판단 조건.
        ArrayList<RoomInformation> saleRoomList = stayDetail.getSaleRoomList();

        if (saleRoomList == null || saleRoomList.size() == 0)
        {
            mBookingTextView.setVisibility(View.GONE);
            mSoldoutTextView.setVisibility(View.VISIBLE);

            setBookingStatus(STATUS_SOLD_OUT);
        } else
        {
            mBookingTextView.setVisibility(View.VISIBLE);
            mBookingTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (mBookingStatus)
                    {
                        case STATUS_BOOKING:
                            ((StayDetailLayout.OnEventListener) mOnEventListener).doBooking(mSelectedRoomInformation);
                            break;

                        case STATUS_SELECT_PRODUCT:
                            ((OnEventListener) mOnEventListener).showProductInformationLayout();
                            break;
                    }
                }
            });

            mSoldoutTextView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SELECT_PRODUCT);

            updateRoomTypeInformationLayout(saleRoomList);
        }

        mListAdapter.notifyDataSetChanged();
    }

    private void updateRoomTypeInformationLayout(ArrayList<RoomInformation> roomInformationList)
    {
        if (roomInformationList == null || roomInformationList.size() == 0)
        {
            return;
        }

        // 처음 세팅하는 경우 객실 타입 세팅
        if (mRoomTypeListAdapter == null)
        {
            mSelectedRoomInformation = roomInformationList.get(0);

            mRoomTypeListAdapter = new StayDetailRoomTypeListAdapter(mContext, roomInformationList, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = mProductTypeRecyclerView.getChildAdapterPosition(v);

                    if (position < 0)
                    {
                        return;
                    }

                    mSelectedRoomInformation = mRoomTypeListAdapter.getItem(position);
                    mRoomTypeListAdapter.setSelected(position);
                    mRoomTypeListAdapter.notifyDataSetChanged();

                    AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                        , AnalyticsManager.Action.ROOM_TYPE_ITEM_CLICKED, mSelectedRoomInformation.roomName, null);
                }
            });
        } else
        {
            // 재세팅 하는 경우
            mSelectedRoomInformation = roomInformationList.get(0);

            mRoomTypeListAdapter.addAll(roomInformationList);
            mRoomTypeListAdapter.setSelected(0);
            mRoomTypeListAdapter.notifyDataSetChanged();
        }

        // 객실 개수로 높이를 재지정해준다.
        int size = roomInformationList.size();
        int height = Util.dpToPx(mContext, 100) * size;
        final int maxHeight = Util.dpToPx(mContext, 350);
        ViewGroup.LayoutParams layoutParams = mProductTypeRecyclerView.getLayoutParams();

        if (height > maxHeight)
        {
            layoutParams.height = maxHeight;
        } else
        {
            layoutParams.height = height;
        }

        mProductTypeRecyclerView.setLayoutParams(layoutParams);
        mProductTypeRecyclerView.setAdapter(mRoomTypeListAdapter);
    }

    @Override
    public void setBookingStatus(int status)
    {
        mBookingStatus = status;

        switch (status)
        {
            case STATUS_NONE:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                break;
            }

            case STATUS_SELECT_PRODUCT:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);

                mBookingTextView.setText(R.string.act_hotel_search_room);
                break;
            }

            case STATUS_BOOKING:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);

                mBookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case STATUS_SOLD_OUT:
            {
                mBookingTextView.setVisibility(View.GONE);
                mSoldoutTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void hideAnimationProductInformationLayout()
    {
        super.hideAnimationProductInformationLayout();

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ROOM_TYPE_CANCEL_CLICKED, mPlaceDetail.name, null);
    }
}