package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class GourmetDetailLayout extends PlaceDetailLayout
{
    private GourmetDetailListAdapter mListAdapter;
    TicketInformation mSelectedTicketInformation;

    public interface OnEventListener extends PlaceDetailLayout.OnEventListener
    {
        void onProductDetailClick(TicketInformation ticketInformation);

        void onReservationClick(TicketInformation ticketInformation);
    }

    public GourmetDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected String getProductTypeTitle()
    {
        return mContext.getString(R.string.act_hotel_search_ticket);
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

    public void setTitleText(String placeName)
    {
        mTransTotalGradeTextView.setVisibility(View.INVISIBLE);

        if (Util.isTextEmpty(placeName) == false)
        {
            mTransPlaceNameTextView.setText(placeName);
            mTransPlaceNameTextView.setTransitionName(mContext.getString(R.string.transition_place_name));
        }
    }

    public void setDetail(SaleTime saleTime, GourmetDetail gourmetDetail, int imagePosition)
    {
        if (gourmetDetail == null)
        {
            setLineIndicatorVisible(false);
            setWishButtonSelected(false);
            setWishButtonCount(0);
            return;
        }

        mPlaceDetail = gourmetDetail;

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        ArrayList<ImageInformation> imageInformationList = gourmetDetail.getImageInformationList();
        mImageAdapter.setData(imageInformationList);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);
        setLineIndicatorVisible(imageInformationList.size() > 0);
        setImageInformation((imageInformationList.size() > 0) ? imageInformationList.get(0).description : null);

        if (mListAdapter == null)
        {
            mListAdapter = new GourmetDetailListAdapter(mContext, saleTime, (GourmetDetail) mPlaceDetail,//
                (GourmetDetailLayout.OnEventListener) mOnEventListener, mEmptyViewOnTouchListener);
            mListView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData((GourmetDetail) mPlaceDetail, saleTime);
        }

        setCurrentImage(imagePosition);
        showWishButton();

        // SOLD OUT 판단 조건.
        ArrayList<TicketInformation> ticketInformationList = gourmetDetail.getTicketInformation();

        if (ticketInformationList == null || ticketInformationList.size() == 0)
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
                            //                            ((GourmetDetailLayout.OnEventListener) mOnEventListener).doBooking(mSelectedTicketInformation);
                            break;

                        case STATUS_SELECT_PRODUCT:
                            ((OnEventListener) mOnEventListener).showProductInformationLayout();
                            break;
                    }
                }
            });

            mSoldoutTextView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SELECT_PRODUCT);

            updateTicketInformationLayout(ticketInformationList);
        }

        setWishButtonSelected(gourmetDetail.myWish);
        setWishButtonCount(gourmetDetail.wishCount);

        mListAdapter.notifyDataSetChanged();
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
                mWishButtonTextView.setVisibility(View.VISIBLE);
                break;
            }

            case STATUS_SELECT_PRODUCT:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_search_ticket);
                break;
            }

            case STATUS_BOOKING:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case STATUS_SOLD_OUT:
            {
                mBookingTextView.setVisibility(View.GONE);
                mSoldoutTextView.setVisibility(View.VISIBLE);
                mWishButtonTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }
}