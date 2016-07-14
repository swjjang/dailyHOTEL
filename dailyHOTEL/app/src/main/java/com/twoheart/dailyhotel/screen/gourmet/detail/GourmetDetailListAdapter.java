package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GourmetDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 6;

    private GourmetDetail mGourmetDetail;
    private SaleTime mSaleTime;
    private FragmentActivity mFragmentActivity;
    private View[] mDeatilViews;
    private int mImageHeight;
    protected View mTitleLayout;
    protected TextView mGradeTextView;
    protected TextView mNameTextView;
    protected View mMagicToolbar;

    private GourmetDetailActivity.OnUserActionListener mOnUserActionListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    public GourmetDetailListAdapter(FragmentActivity activity, GourmetDetail gourmetDetail, SaleTime saleTime, GourmetDetailActivity.OnUserActionListener onUserActionListener, View.OnTouchListener emptyViewOnTouchListener)
    {
        mFragmentActivity = activity;
        mGourmetDetail = gourmetDetail;
        mSaleTime = saleTime;
        mDeatilViews = new View[NUMBER_OF_ROWSLIST];
        mImageHeight = Util.getLCDWidth(activity);

        mOnUserActionListener = onUserActionListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setData(GourmetDetail gourmetDetail, SaleTime saleTime)
    {
        mGourmetDetail = gourmetDetail;
        mSaleTime = saleTime;
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

    public View getTitleLayout()
    {
        return mTitleLayout;
    }

    public View getGradeTextView()
    {
        return mGradeTextView;
    }

    public View getNameTextView()
    {
        return mNameTextView;
    }

    public View getMagicToolbarView()
    {
        return mMagicToolbar;
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

        getEmptyView(mDeatilViews[0]);
        linearLayout.addView(mDeatilViews[0]);

        // 호텔 등급과 이름.
        if (mDeatilViews[1] == null)
        {
            mDeatilViews[1] = layoutInflater.inflate(R.layout.list_row_detail02, parent, false);
        }

        getTitleView(mDeatilViews[1], mGourmetDetail);
        linearLayout.addView(mDeatilViews[1]);

        // 주소 및 맵
        if (mDeatilViews[2] == null)
        {
            mDeatilViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
        }

        getAddressView(mDeatilViews[2], mGourmetDetail);
        linearLayout.addView(mDeatilViews[2]);

        if (Util.isTextEmpty(mGourmetDetail.benefit) == false)
        {
            // D Benefit
            if (mDeatilViews[3] == null)
            {
                mDeatilViews[3] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
            }

            getBenefitView(mDeatilViews[3], mGourmetDetail);
            linearLayout.addView(mDeatilViews[3]);
        }

        // 호텔 정보
        if (mDeatilViews[4] == null)
        {
            mDeatilViews[4] = layoutInflater.inflate(R.layout.list_row_detail_more, parent, false);
        }

        getInformationView(layoutInflater, (ViewGroup) mDeatilViews[4], mGourmetDetail);
        linearLayout.addView(mDeatilViews[4]);

        // 카카오톡 문의
        if (mDeatilViews[5] == null)
        {
            mDeatilViews[5] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
        }

        getKakaoView(mDeatilViews[5]);
        linearLayout.addView(mDeatilViews[5]);

        return linearLayout;
    }

    /**
     * 빈화면
     *
     * @param view
     * @return
     */
    private View getEmptyView(View view)
    {
        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = mImageHeight;

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

        return view;
    }

    /**
     * 등급 및 이름
     *
     * @param view
     * @return
     */
    private View getTitleView(View view, PlaceDetail placeDetail)
    {
        GourmetDetail gourmetDetail = (GourmetDetail) placeDetail;

        mTitleLayout = view.findViewById(R.id.hotelTitleLayout);
        mTitleLayout.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        mMagicToolbar = view.findViewById(R.id.magicToolbar);

        // 등급
        mGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);

        if (Util.isTextEmpty(gourmetDetail.category) == true)
        {
            mGradeTextView.setVisibility(View.GONE);
        } else
        {
            mGradeTextView.setVisibility(View.VISIBLE);
            mGradeTextView.setText(gourmetDetail.category);
            mGradeTextView.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
            mGradeTextView.setBackgroundResource(R.drawable.shape_rect_blackcolor);
        }

        // 호텔명
        mNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
        mNameTextView.setText(gourmetDetail.name);

        int width = Util.getLCDWidth(mFragmentActivity) - Util.dpToPx(mFragmentActivity, 60) - Util.dpToPx(mFragmentActivity, 48);
        mNameTextView.setTag(mNameTextView.getId(), width);
        mNameTextView.setSelected(true);

        if (mNameTextView.getTag() == null)
        {
            mNameTextView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Rect rect = new Rect();
                    mNameTextView.getGlobalVisibleRect(rect);
                    mNameTextView.setTag(rect);
                }
            });
        }

        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);

        // 만족도
        if (Util.isTextEmpty(gourmetDetail.satisfaction) == true)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(gourmetDetail.satisfaction);
        }

        TextView dateView = (TextView) view.findViewById(R.id.dateView);

        // 날짜
        dateView.setText(mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(E)"));
        dateView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnUserActionListener.onCalendarClick(mSaleTime);
            }
        });

        return view;
    }

    /**
     * 주소 및 맵
     *
     * @param view
     * @param placeDetail
     * @return
     */
    private View getAddressView(final View view, PlaceDetail placeDetail)
    {
        view.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        // 주소지
        final TextView hotelAddressTextView = (TextView) view.findViewById(R.id.detailAddressTextView);

        final String address = placeDetail.address;
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
        navigatorView.setOnClickListener(new View.OnClickListener()
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
            , size, iconUrl, mGourmetDetail.latitude, mGourmetDetail.longitude, DailyHotelRequest.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        mapImageView.setImageURI(Uri.parse(url));

        if (Util.isInstallGooglePlayService(mFragmentActivity) == true)
        {
            mapImageView.setOnClickListener(new View.OnClickListener()
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
            mapImageView.setOnClickListener(new View.OnClickListener()
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
    private View getBenefitView(View view, PlaceDetail placeDetail)
    {
        if (view == null || placeDetail == null)
        {
            return view;
        }

        final TextView benefitTextView = (TextView) view.findViewById(R.id.benefitTextView);

        final String benefit = placeDetail.benefit;

        benefitTextView.setText(benefit);

        return view;
    }

    /**
     * 정보
     *
     * @return
     */
    private View getInformationView(LayoutInflater layoutInflater, ViewGroup viewGroup, PlaceDetail placeDetail)
    {
        if (layoutInflater == null || viewGroup == null || placeDetail == null)
        {
            return viewGroup;
        }

        viewGroup.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        ArrayList<DetailInformation> arrayList = placeDetail.getInformation();

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
     * 카톡 실시간 상담
     *
     * @param view
     * @return
     */
    private View getKakaoView(View view)
    {
        if (view == null)
        {
            return null;
        }

        view.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.white));

        // 카톡 1:1 실시간 상담
        View consultKakaoView = view.findViewById(R.id.kakaoImageView);
        consultKakaoView.setOnClickListener(new View.OnClickListener()
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