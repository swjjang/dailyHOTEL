package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StayDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 9;

    private StayDetail mStayDetail;
    private SaleTime mCheckInSaleTime;
    private FragmentActivity mFragmentActivity;
    private View[] mDeatilViews;
    private int mImageHeight;
    private View mHotelTitleLayout;
    private TextView mHotelGradeTextView;
    private TextView mHotelNameTextView;
    protected View mMagicToolbar;

    private StayDetailActivity.OnUserActionListener mOnUserActionListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    public StayDetailListAdapter(FragmentActivity activity, StayDetail stayDetail, SaleTime checkInSaleTime, StayDetailActivity.OnUserActionListener onUserActionListener, View.OnTouchListener emptyViewOnTouchListener)
    {
        mFragmentActivity = activity;
        mStayDetail = stayDetail;
        mCheckInSaleTime = checkInSaleTime;
        mDeatilViews = new View[NUMBER_OF_ROWSLIST];
        mImageHeight = Util.getLCDWidth(activity);

        mOnUserActionListener = onUserActionListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setData(StayDetail stayDetail, SaleTime checkInSaleTime)
    {
        mStayDetail = stayDetail;
        mCheckInSaleTime = checkInSaleTime;
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCount()
    {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout linearLayout;

        LayoutInflater layoutInflater = (LayoutInflater) mFragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            linearLayout = new LinearLayout(mFragmentActivity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        } else
        {
            linearLayout = (LinearLayout) convertView;
        }

        linearLayout.removeAllViews();

        // 빈화면
        if (mDeatilViews[0] == null)
        {
            mDeatilViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
        }

        getDetail00View(mDeatilViews[0]);
        linearLayout.addView(mDeatilViews[0]);

        // 호텔 등급과 이름.
        if (mDeatilViews[1] == null)
        {
            mDeatilViews[1] = layoutInflater.inflate(R.layout.list_row_detail02, parent, false);
        }

        getTitleView(mDeatilViews[1], mStayDetail);
        linearLayout.addView(mDeatilViews[1]);

        // 주소 및 맵
        if (mDeatilViews[2] == null)
        {
            mDeatilViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
        }

        getAddressView(mDeatilViews[2], mStayDetail);
        linearLayout.addView(mDeatilViews[2]);

        // D Benefit
        if (Util.isTextEmpty(mStayDetail.hotelBenefit) == false)
        {
            if (mDeatilViews[3] == null)
            {
                mDeatilViews[3] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
            }

            getDetailBenefitView(mDeatilViews[3], mStayDetail);
            linearLayout.addView(mDeatilViews[3]);
        }

        // 데일리's comment
        if (mDeatilViews[4] == null)
        {
            mDeatilViews[4] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
        }

        getDailysCommentView(layoutInflater, (ViewGroup) mDeatilViews[4], mStayDetail);
        linearLayout.addView(mDeatilViews[4]);

        // 호텔 정보
        if (mDeatilViews[5] == null)
        {
            mDeatilViews[5] = layoutInflater.inflate(R.layout.list_row_detail05, parent, false);
        }

        getDeatil05View(layoutInflater, (ViewGroup) mDeatilViews[5], mStayDetail);
        linearLayout.addView(mDeatilViews[5]);

        // 호텔 더보기 정보
        if (mDeatilViews[6] == null)
        {
            mDeatilViews[6] = layoutInflater.inflate(R.layout.list_row_detail_more, parent, false);
        }

        getDeatil06View(layoutInflater, (ViewGroup) mDeatilViews[6], mStayDetail);
        linearLayout.addView(mDeatilViews[6]);

        // 확인 사항
        if (mDeatilViews[7] == null)
        {
            mDeatilViews[7] = layoutInflater.inflate(R.layout.list_row_detail06, parent, false);
        }

        getDeatil07View(layoutInflater, (ViewGroup) mDeatilViews[7], mStayDetail);
        linearLayout.addView(mDeatilViews[7]);

        // 카카오톡 문의
        if (mDeatilViews[8] == null)
        {
            mDeatilViews[8] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
        }

        getDeatil08View(mDeatilViews[8]);
        linearLayout.addView(mDeatilViews[8]);

        return linearLayout;
    }

    public View getHotelTitleLayout()
    {
        return mHotelTitleLayout;
    }

    public View getHotelGradeTextView()
    {
        return mHotelGradeTextView;
    }

    public View getHotelNameTextView()
    {
        return mHotelNameTextView;
    }

    public View getMagicToolbarView()
    {
        return mMagicToolbar;
    }

    /**
     * 빈화면
     *
     * @param view
     * @return
     */
    private View getDetail00View(View view)
    {
        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = mImageHeight;

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

        return view;
    }

    /**
     * 호텔 등급 및 이름
     *
     * @param view
     * @param stayDetail
     * @return
     */
    private View getTitleView(View view, StayDetail stayDetail)
    {
        mHotelTitleLayout = view.findViewById(R.id.hotelTitleLayout);
        mHotelTitleLayout.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        mMagicToolbar = view.findViewById(R.id.magicToolbar);

        // 등급
        mHotelGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
        mHotelGradeTextView.setVisibility(View.VISIBLE);

        mHotelGradeTextView.setText(stayDetail.grade.getName(mFragmentActivity));
        mHotelGradeTextView.setBackgroundResource(stayDetail.grade.getColorResId());

        // 호텔명
        mHotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
        mHotelNameTextView.setText(stayDetail.hotelName);

        int width = Util.getLCDWidth(mFragmentActivity) - Util.dpToPx(mFragmentActivity, 60) - Util.dpToPx(mFragmentActivity, 48);
        mHotelNameTextView.setTag(mHotelNameTextView.getId(), width);
        mHotelNameTextView.setSelected(true);

        if (mHotelNameTextView.getTag() == null)
        {
            mHotelNameTextView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Rect rect = new Rect();
                    mHotelNameTextView.getGlobalVisibleRect(rect);
                    mHotelNameTextView.setTag(rect);
                }
            });
        }

        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);

        // 만족도
        if (Util.isTextEmpty(stayDetail.satisfaction) == true)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(stayDetail.satisfaction);
        }

        TextView dateView = (TextView) view.findViewById(R.id.dateView);

        // 체크인체크아웃 날짜
        final String checkInDate = mCheckInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        SaleTime checkOutSaletime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mStayDetail.nights);
        String checkOutDate = checkOutSaletime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        dateView.setText(String.format("%s - %s, %d박", checkInDate, checkOutDate, mStayDetail.nights));

        View changeDateLayout = view.findViewById(R.id.changeDateLayout);
        changeDateLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener == null)
                {
                    return;
                }

                mOnUserActionListener.onCalendarClick(mCheckInSaleTime, mStayDetail.nights, mStayDetail.hotelIndex);
            }
        });

        return view;
    }

    /**
     * 호텔 주소 및 맵
     *
     * @param view
     * @param stayDetail
     * @return
     */
    private View getAddressView(final View view, StayDetail stayDetail)
    {
        view.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        // 주소지
        final TextView hotelAddressTextView = (TextView) view.findViewById(R.id.detailAddressTextView);

        final String address = stayDetail.address;
        hotelAddressTextView.setText(address);

        View clipAddress = view.findViewById(R.id.copyAddressView);
        clipAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener == null)
                {
                    return;
                }

                mOnUserActionListener.clipAddress(address);
            }
        });

        //길찾기
        View navigatorView = view.findViewById(R.id.navigatorView);
        navigatorView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener == null)
                {
                    return;
                }

                mOnUserActionListener.showNavigatorDialog();
            }
        });

        com.facebook.drawee.view.SimpleDraweeView mapImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.mapImageView);

        double width = mImageHeight;
        double height = Util.dpToPx(mFragmentActivity, 200);
        double ratio = height / width;

        if (width >= 640)
        {
            width = 640;
        }

        height = width * ratio;

        String size = String.format("%dx%d", (int) width * 4 / 5, (int) height * 4 / 5);
        String iconUrl = "http://s3.dailyhotel.kr/resources/images/_banner/googlemap_marker-1.png";
        String url = String.format("http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=%s&markers=icon:%s|%s,%s&sensor=false&scale=2&format=png8&mobile=true&key=%s"//
            , size, iconUrl, mStayDetail.latitude, mStayDetail.longitude, DailyHotelRequest.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        mapImageView.setImageURI(Uri.parse(url));

        if (Util.isInstallGooglePlayService(mFragmentActivity) == true)
        {
            mapImageView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.showMap();
                    }
                }
            });
        } else
        {
            mapImageView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Util.installGooglePlayService((BaseActivity) mFragmentActivity);
                }
            });
        }

        return view;
    }

    /**
     * 호텔 Benefit
     *
     * @param view
     * @return
     */
    private View getDetailBenefitView(View view, StayDetail stayDetail)
    {
        if (view == null || stayDetail == null)
        {
            return view;
        }

        final TextView benefitTextView = (TextView) view.findViewById(R.id.benefitTextView);

        final String benefit = stayDetail.hotelBenefit;

        benefitTextView.setText(benefit);
        return view;
    }

    /**
     * 데일리 추천 이유
     *
     * @param layoutInflater
     * @param viewGroup
     * @param stayDetail
     * @return
     */
    private View getDailysCommentView(LayoutInflater layoutInflater, ViewGroup viewGroup, StayDetail stayDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayDetail == null)
        {
            return viewGroup;
        }

        View topDividerViw = viewGroup.findViewById(R.id.topDividerViw);

        if (Util.isTextEmpty(mStayDetail.hotelBenefit) == false)
        {
            topDividerViw.setVisibility(View.GONE);
        } else
        {
            topDividerViw.setVisibility(View.VISIBLE);
        }

        ArrayList<DetailInformation> arrayList = stayDetail.getInformation();

        if (arrayList != null)
        {
            DetailInformation information = arrayList.get(0);

            makeInformationLayout(layoutInflater, viewGroup, information);
        }

        return viewGroup;
    }

    /**
     * 호텔 정보
     *
     * @param layoutInflater
     * @param viewGroup
     * @param stayDetail
     * @return
     */
    private View getDeatil05View(LayoutInflater layoutInflater, ViewGroup viewGroup, StayDetail stayDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayDetail == null)
        {
            return viewGroup;
        }

        viewGroup.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        ArrayList<DetailInformation> arrayList = stayDetail.getInformation();

        if (arrayList != null)
        {
            DetailInformation information = arrayList.get(1);

            makeInformationLayout(layoutInflater, viewGroup, information);
        }

        return viewGroup;
    }

    /**
     * 호텔 더보기 정보
     *
     * @return
     */
    private View getDeatil06View(LayoutInflater layoutInflater, ViewGroup viewGroup, StayDetail stayDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayDetail == null)
        {
            return viewGroup;
        }

        viewGroup.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        ArrayList<DetailInformation> arrayList = stayDetail.getMoreInformation();

        if (arrayList != null)
        {
            viewGroup.removeAllViews();

            for (DetailInformation information : arrayList)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                makeInformationLayout(layoutInflater, childGroup, information);

                viewGroup.addView(childGroup);
            }
        }

        return viewGroup;
    }

    /**
     * 확인 사항
     *
     * @return
     */
    private View getDeatil07View(LayoutInflater layoutInflater, ViewGroup viewGroup, StayDetail stayDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayDetail == null)
        {
            return viewGroup;
        }

        viewGroup.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        ArrayList<DetailInformation> arrayList = stayDetail.getInformation();

        if (arrayList != null && arrayList.size() >= 3)
        {
            DetailInformation information = arrayList.get(2);

            makeInformationLayout(layoutInflater, viewGroup, information);
        }

        return viewGroup;
    }

    /**
     * 카톡 실시간 상담
     *
     * @return
     */
    private View getDeatil08View(View view)
    {
        if (view == null)
        {
            return null;
        }

        view.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        // 카톡 1:1 실시간 상담
        View consultKakaoView = view.findViewById(R.id.kakaoImageView);
        consultKakaoView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.doKakaotalkConsult();
                }
            }
        });

        return view;
    }

    private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information)
    {
        if (layoutInflater == null || viewGroup == null || information == null)
        {
            return;
        }

        LinearLayout contentsLayout = (LinearLayout) viewGroup.findViewById(R.id.contentsList);
        contentsLayout.removeAllViews();

        TextView titleTextView = (TextView) viewGroup.findViewById(R.id.titleTextView);
        titleTextView.setText(information.title);

        List<String> contentsList = information.getContentsList();

        if (contentsList != null)
        {
            int size = contentsList.size();

            for (int i = 0; i < size; i++)
            {
                View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
                TextView textView = (TextView) textLayout.findViewById(R.id.textView);
                textView.setText(contentsList.get(i));

                contentsLayout.addView(textLayout);
            }
        }
    }
}