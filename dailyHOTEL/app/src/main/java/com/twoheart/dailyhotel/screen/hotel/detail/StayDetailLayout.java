package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
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
public class StayDetailLayout extends PlaceDetailLayout implements RadioGroup.OnCheckedChangeListener
{
    public static final int VIEW_AVERAGE_PRICE = 0;
    public static final int VIEW_TOTAL_PRICE = 1;

    private StayDetailListAdapter mListAdapter;
    private RoomInformation mSelectedRoomInformation;

    private StayDetailRoomTypeListAdapter mRoomTypeListAdapter;

    public interface OnEventListener extends PlaceDetailLayout.OnEventListener
    {
        void doBooking(RoomInformation roomInformation);

        void onChangedViewPrice(int type);
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

    public void setTitleText(Stay.Grade grade, String placeName)
    {
        mTransTotelGradeTextView.setText(grade.getName(mContext));
        mTransTotelGradeTextView.setBackgroundResource(grade.getColorResId());
        mTransTotelGradeTextView.setTransitionName(mContext.getString(R.string.transition_place_grade));

        mTransPlacelNameTextView.setText(placeName);
        mTransPlacelNameTextView.setTransitionName(mContext.getString(R.string.transition_place_name));
    }

    public void setDetail(SaleTime saleTime, StayDetail stayDetail, int imagePosition)
    {
        if (stayDetail == null)
        {
            setLineIndicatorVisible(false);
            setWishListButtonCount(0);
            setWishListButtonSelected(false);
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
        setLineIndicatorVisible(imageInformationList.size() > 0);
        setImageInformation((imageInformationList.size() > 0) ? imageInformationList.get(0).description : null);

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

        if (stayDetail.nights > 1)
        {
            mPriceRadioGroup.check(R.id.averageRadioButton);
            mPriceOptionLayout.setVisibility(View.VISIBLE);
            mPriceRadioGroup.setOnCheckedChangeListener(this);
        } else
        {
            mPriceOptionLayout.setVisibility(View.GONE);
            mPriceRadioGroup.setOnCheckedChangeListener(null);
        }

        setWishListButtonSelected(stayDetail.myWish);
        setWishListButtonCount(stayDetail.wishCount);

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
        int productTitleBarHeight = Util.dpToPx(mContext, 52) + (mSelectedRoomInformation.nights > 1 ? Util.dpToPx(mContext, 40) : 0);
        int prodcutLayoutHeight = Util.dpToPx(mContext, 122) * size + productTitleBarHeight;

        // 화면 높이 - 상단 타이틀 - 하단 버튼
        final int maxHeight = ((View) mProductTypeLayout.getParent()).getHeight() - Util.dpToPx(mContext, 52) - Util.dpToPx(mContext, 64);
        ViewGroup.LayoutParams layoutParams = mProductTypeRecyclerView.getLayoutParams();

        if (prodcutLayoutHeight > maxHeight)
        {
            layoutParams.height = maxHeight - productTitleBarHeight;
        } else
        {
            layoutParams.height = prodcutLayoutHeight - productTitleBarHeight;
        }

        mProductTypeRecyclerView.setLayoutParams(layoutParams);
        mProductTypeRecyclerView.setAdapter(mRoomTypeListAdapter);
    }

    @Override
    public void setBookingStatus(int status)
    {
        mBookingStatus = status;

        if (mBookingTextView == null || mSoldoutTextView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        switch (status)
        {
            case STATUS_NONE:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishListButtonTextView.setVisibility(View.VISIBLE);
                break;
            }

            case STATUS_SELECT_PRODUCT:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishListButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_search_room);
                break;
            }

            case STATUS_BOOKING:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishListButtonTextView.setVisibility(View.GONE);

                mBookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case STATUS_SOLD_OUT:
            {
                mBookingTextView.setVisibility(View.GONE);
                mSoldoutTextView.setVisibility(View.VISIBLE);
                mWishListButtonTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void setSelectProduct(int index)
    {
        if(mRoomTypeListAdapter == null)
        {
            return;
        }

        int position = mRoomTypeListAdapter.setSelectIndex(index);
        mProductTypeRecyclerView.scrollToPosition(position);
    }

    @Override
    public void hideAnimationProductInformationLayout()
    {
        super.hideAnimationProductInformationLayout();

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ROOM_TYPE_CANCEL_CLICKED, mPlaceDetail.name, null);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        switch (checkedId)
        {
            case R.id.averageRadioButton:
                ((OnEventListener) mOnEventListener).onChangedViewPrice(VIEW_AVERAGE_PRICE);
                break;

            case R.id.totalRadioButton:
                ((OnEventListener) mOnEventListener).onChangedViewPrice(VIEW_TOTAL_PRICE);
                break;
        }
    }

    public void setChangedViewPrice(int type)
    {
        if (mRoomTypeListAdapter == null)
        {
            return;
        }

        mRoomTypeListAdapter.setChangedViewPrice(type);
        mRoomTypeListAdapter.notifyDataSetChanged();
    }
}