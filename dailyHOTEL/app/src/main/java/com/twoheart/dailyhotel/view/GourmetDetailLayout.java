package com.twoheart.dailyhotel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.DetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.util.ArrayList;
import java.util.List;

public class GourmetDetailLayout extends PlaceDetailLayout
{
    private static final int NUMBER_OF_ROWSLIST = 6;

    private GourmetDetailListAdapter mListAdapter;
    private View mGoogleMapLayout;

    public GourmetDetailLayout(BaseActivity activity, String defaultImageUrl)
    {
        super(activity, defaultImageUrl);
    }

    @Override
    protected void initLayout(LayoutInflater inflater, BaseActivity activity)
    {
        mGoogleMapLayout = (ViewGroup) inflater.inflate(R.layout.view_map, null, false);
    }

    @Override
    protected int getSearchTicketName()
    {
        return R.string.act_hotel_search_ticket;
    }

    @Override
    public void setDetail(PlaceDetail placeDetail, int imagePosition)
    {
        if (placeDetail == null)
        {
            return;
        }

        mPlaceDetail = placeDetail;

        // 호텔 상세 정보를 얻어와서 리스트 개수가 몇개 필요한지 검색한다.
        if (mNeedRefreshData == null)
        {
            mNeedRefreshData = new boolean[NUMBER_OF_ROWSLIST];
        }

        for (int i = 0; i < NUMBER_OF_ROWSLIST; i++)
        {
            mNeedRefreshData[i] = true;
        }

        if (mDeatilViews == null)
        {
            mDeatilViews = new View[NUMBER_OF_ROWSLIST];
        }

        mActionBarTextView.setText(placeDetail.name);

        if (mImageAdapter == null)
        {
            mImageAdapter = new DetailImageViewPagerAdapter(mActivity);
        }

        mImageAdapter.setOnImageActionListener(mOnImageActionListener);

        mImageAdapter.setData(placeDetail.getImageUrlList());
        mViewPager.setAdapter(mImageAdapter);
        mDailyViewPagerIndicator.setTotalCount(placeDetail.getImageUrlList().size());

        if (mListAdapter == null)
        {
            mListAdapter = new GourmetDetailListAdapter((FragmentActivity) mActivity);
            mListView.setAdapter(mListAdapter);
        }

        setCurrentImage(imagePosition);

        hideTicketInformationLayout();

        // 호텔 sold out시
        View bookingView = mViewGroupRoot.findViewById(R.id.bookingTextView);
        View soldoutView = mViewGroupRoot.findViewById(R.id.soldoutTextView);

        // SOLD OUT 판단 조건.
        ArrayList<TicketInformation> ticketInformationList = placeDetail.getTicketInformation();

        if (ticketInformationList == null || ticketInformationList.size() == 0)
        {
            bookingView.setVisibility(View.GONE);
            soldoutView.setVisibility(View.VISIBLE);

            setBookingStatus(STATUS_SOLD_OUT);
        } else
        {
            bookingView.setVisibility(View.VISIBLE);
            bookingView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (mBookingStatus)
                    {
                        case STATUS_BOOKING:
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.doBooking(mSelectedTicketInformation);
                            }
                            break;

                        case STATUS_SEARCH_TICKET:
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showTicketInformationLayout();
                            }
                            break;
                    }
                }
            });

            soldoutView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SEARCH_TICKET);

            initTicketInformationLayout(0, ticketInformationList);
        }

        if (mListAdapter != null)
        {
            mListAdapter.notifyDataSetChanged();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Adapter
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class GourmetDetailListAdapter extends BaseAdapter
    {
        private FragmentActivity mFragmentActivity;
        private GoogleMap mGoogleMap;
        private SupportMapFragment mMapFragment;

        public GourmetDetailListAdapter(FragmentActivity activity)
        {
            mFragmentActivity = activity;
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
            if (mDeatilViews == null)
            {
                return 0;
            } else
            {
                int count = NUMBER_OF_ROWSLIST;

                if (Util.isTextEmpty(mPlaceDetail.benefit) == true)
                {
                    count--;
                }

                return count;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = null;

            LayoutInflater layoutInflater = (LayoutInflater) mFragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (position)
            {
                // 빈화면
                case 0:
                    if (mDeatilViews[0] == null)
                    {
                        mDeatilViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
                    }

                    if (mNeedRefreshData[0] == true)
                    {
                        mNeedRefreshData[0] = false;

                        getEmptyView(mDeatilViews[0]);
                    }

                    view = mDeatilViews[0];
                    break;

                // 호텔 등급과 이름.
                case 1:
                    if (mDeatilViews[1] == null)
                    {
                        mDeatilViews[1] = layoutInflater.inflate(R.layout.list_row_detail02, parent, false);
                    }

                    if (mNeedRefreshData[1] == true)
                    {
                        mNeedRefreshData[1] = false;

                        getTitleView(mDeatilViews[1], mPlaceDetail);
                    }

                    view = mDeatilViews[1];
                    break;

                // 주소 및 맵
                case 2:
                    if (mDeatilViews[2] == null)
                    {
                        mDeatilViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
                    }

                    if (mNeedRefreshData[2] == true)
                    {
                        mNeedRefreshData[2] = false;

                        getAddressView(mDeatilViews[2], mPlaceDetail);
                    }

                    view = mDeatilViews[2];
                    break;

                // D Benefit or 호텔 정보
                case 3:
                    if (Util.isTextEmpty(mPlaceDetail.benefit) == false)
                    {
                        if (mDeatilViews[3] == null)
                        {
                            mDeatilViews[3] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
                            getBenefitView(mDeatilViews[3], mPlaceDetail);
                        }

                        if (mNeedRefreshData[3] == true)
                        {
                            mNeedRefreshData[3] = false;

                            getBenefitView(mDeatilViews[3], mPlaceDetail);
                        }

                        view = mDeatilViews[3];
                    } else
                    {
                        view = makeInformationView(layoutInflater, parent);
                    }
                    break;

                // 호텔 정보 or 카카오톡 문의
                case 4:
                    if (Util.isTextEmpty(mPlaceDetail.benefit) == false)
                    {
                        view = makeInformationView(layoutInflater, parent);
                    } else
                    {
                        view = makeKakaoView(layoutInflater, parent);
                    }
                    break;

                // 카카오톡 문의
                case 5:
                    view = makeKakaoView(layoutInflater, parent);
                    break;
            }

            return view;
        }

        private View makeInformationView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilViews[4] == null)
            {
                mDeatilViews[4] = layoutInflater.inflate(R.layout.list_row_detail_more, parent, false);
            }

            if (mNeedRefreshData[4] == true)
            {
                mNeedRefreshData[4] = false;

                getInformationView(layoutInflater, (ViewGroup) mDeatilViews[4], mPlaceDetail);
            }

            return mDeatilViews[4];
        }

        private View makeKakaoView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilViews[5] == null)
            {
                mDeatilViews[5] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
            }

            if (mNeedRefreshData[5] == true)
            {
                mNeedRefreshData[5] = false;

                getKakaoView(mDeatilViews[5]);
            }

            return mDeatilViews[5];
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

            // 등급
            mGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
            mGradeTextView.setVisibility(View.VISIBLE);

            mGradeTextView.setText(gourmetDetail.category);
            mGradeTextView.setBackgroundResource(R.drawable.shape_rect_whitecolor);

            // 호텔명
            mNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
            mNameTextView.setText(gourmetDetail.name);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(Util.dpToPx(mActivity, 15), 0, Util.dpToPx(mActivity, 40), 0);
            mNameTextView.setLayoutParams(layoutParams);

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

                        Rect rect01 = new Rect();
                        mActionBarTextView.getGlobalVisibleRect(rect01);

                        int actionBarWidth = rect01.width() - mActionBarTextView.getPaddingLeft();
                        int paddingRight = actionBarWidth - rect.width();

                        mActionBarTextView.setPadding(mActionBarTextView.getPaddingLeft(), 0, paddingRight, 0);
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
            // 주소지
            final TextView hotelAddressTextView01 = (TextView) view.findViewById(R.id.hotelAddressTextView01);
            final TextView hotelAddressTextView02 = (TextView) view.findViewById(R.id.hotelAddressTextView02);
            final TextView hotelAddressTextView03 = (TextView) view.findViewById(R.id.hotelAddressTextView03);

            hotelAddressTextView02.setText(null);
            hotelAddressTextView02.setVisibility(View.GONE);

            hotelAddressTextView03.setText(null);
            hotelAddressTextView03.setVisibility(View.GONE);

            final String address = placeDetail.address;

            hotelAddressTextView01.setText(address);
            hotelAddressTextView01.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Layout layout = hotelAddressTextView01.getLayout();

                    if (layout == null || Util.isTextEmpty(address) == true)
                    {
                        return;
                    }

                    String[] lineString = new String[2];
                    int lineCount = layout.getLineCount();

                    // 한줄이상인 경우.
                    if (lineCount == 2)
                    {
                        int firstLineEnd = layout.getLineEnd(0);

                        try
                        {
                            if (firstLineEnd < address.length())
                            {
                                lineString[0] = address.substring(firstLineEnd, address.length());

                                hotelAddressTextView02.setVisibility(View.VISIBLE);
                                hotelAddressTextView02.setText(lineString[0]);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    } else if (lineCount > 2)
                    {
                        int firstLineEnd = layout.getLineEnd(0);
                        int secondLineEnd = layout.getLineEnd(1);

                        try
                        {
                            if (firstLineEnd < address.length())
                            {
                                lineString[0] = address.substring(firstLineEnd, secondLineEnd);

                                hotelAddressTextView02.setVisibility(View.VISIBLE);
                                hotelAddressTextView02.setText(lineString[0]);
                            }

                            if (secondLineEnd < address.length())
                            {
                                lineString[1] = address.substring(secondLineEnd, address.length());

                                hotelAddressTextView03.setVisibility(View.VISIBLE);
                                hotelAddressTextView03.setText(lineString[1]);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            });

            FrameLayout googleMapLayout = (FrameLayout) view.findViewById(R.id.googleMapLayout);

            if (Util.isInstallGooglePlayService(mFragmentActivity) == true)
            {
                if (googleMapLayout.getBackground() == null)
                {
                    googleMapSetting(googleMapLayout);
                }
            } else
            {
                googleMapLayout.setOnClickListener(new View.OnClickListener()
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

        private void googleMapSetting(final FrameLayout googleMapLayout)
        {
            if (googleMapLayout == null)
            {
                return;
            }

            googleMapLayout.setOnClickListener(null);
            googleMapLayout.removeAllViews();

            if (mGoogleMapLayout == null)
            {
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mGoogleMapLayout = (ViewGroup) inflater.inflate(R.layout.view_map, null, false);
            }

            googleMapLayout.addView(mGoogleMapLayout);

            mMapFragment = (SupportMapFragment) mFragmentActivity.getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            mMapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    mGoogleMap = googleMap;

                    final LatLng latlng = new LatLng(mPlaceDetail.latitude, mPlaceDetail.longitude);

                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latlng));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(15).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
                    {
                        @Override
                        public boolean onMarkerClick(Marker marker)
                        {
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showMap();
                            }

                            return true;
                        }
                    });

                    mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
                    mGoogleMap.setOnMapClickListener(new OnMapClickListener()
                    {
                        @Override
                        public void onMapClick(LatLng latlng)
                        {
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showMap();
                            }
                        }
                    });

                    mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback()
                    {
                        @Override
                        public void onMapLoaded()
                        {
                            if (mGoogleMap == null)
                            {
                                return;
                            }

                            mGoogleMap.snapshot(new SnapshotReadyCallback()
                            {
                                @Override
                                public void onSnapshotReady(Bitmap bitmap)
                                {
                                    if (Util.isOverAPI16() == true)
                                    {
                                        googleMapLayout.setBackground(new BitmapDrawable(mFragmentActivity.getResources(), bitmap));
                                    } else
                                    {
                                        googleMapLayout.setBackgroundDrawable(new BitmapDrawable(mFragmentActivity.getResources(), bitmap));
                                    }

                                    mFragmentActivity.getSupportFragmentManager().beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
                                    googleMapLayout.removeAllViews();

                                    mMapFragment = null;
                                    mGoogleMap = null;
                                    mGoogleMapLayout = null;

                                    googleMapLayout.setOnClickListener(new View.OnClickListener()
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
                                }
                            });
                        }
                    });
                }
            });
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

            final TextView textView1Line = (TextView) view.findViewById(R.id.benefit1LineTextView);
            final TextView textView2Line = (TextView) view.findViewById(R.id.benefit2LineTextView);

            textView2Line.setText(null);
            textView2Line.setVisibility(View.GONE);

            final String benefit = placeDetail.benefit;

            textView1Line.setText(benefit);
            textView1Line.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Layout layout = textView1Line.getLayout();

                    if (layout == null || Util.isTextEmpty(benefit) == true)
                    {
                        return;
                    }

                    String[] lineString = new String[1];
                    int lineCount = layout.getLineCount();

                    // 한줄이상인 경우.
                    if (lineCount > 1)
                    {
                        int firstLineEnd = layout.getLineEnd(0);

                        try
                        {
                            if (firstLineEnd < benefit.length())
                            {
                                lineString[0] = benefit.substring(firstLineEnd, benefit.length());

                                textView2Line.setVisibility(View.VISIBLE);
                                textView2Line.setText(lineString[0]);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            });

            return view;
        }

        /**
         * 정보
         *
         * @param view
         * @return
         */
        private View getInformationView(LayoutInflater layoutInflater, ViewGroup viewGroup, PlaceDetail placeDetail)
        {
            if (layoutInflater == null || viewGroup == null || placeDetail == null)
            {
                return viewGroup;
            }

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
                return view;
            }

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
                    textView.setTypeface(FontManager.getInstance(mFragmentActivity).getDemiLightTypeface());

                    if (Util.isOverAPI21() == true)
                    {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        layoutParams.bottomMargin = Util.dpToPx(mFragmentActivity, 5);
                        contentsLayout.addView(textLayout, layoutParams);
                    } else
                    {
                        contentsLayout.addView(textLayout);
                    }
                }
            }
        }
    }
}