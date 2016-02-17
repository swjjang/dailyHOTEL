package com.twoheart.dailyhotel.screen.hoteldetail;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.util.ArrayList;
import java.util.List;

public class HotelDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 9;

    private HotelDetail mHotelDetail;
    private FragmentActivity mFragmentActivity;
    private View[] mDeatilViews;
    private boolean[] mNeedRefreshData;
    private int mImageHeight;
    private View mHotelTitleLayout;
    private TextView mHotelGradeTextView;
    private TextView mHotelNameTextView;

    private HotelDetailActivity.OnUserActionListener mOnUserActionListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    public HotelDetailListAdapter(FragmentActivity activity, HotelDetail hotelDetail, HotelDetailActivity.OnUserActionListener onUserActionListener, View.OnTouchListener emptyViewOnTouchListener)
    {
        mFragmentActivity = activity;
        mHotelDetail = hotelDetail;

        mNeedRefreshData = new boolean[NUMBER_OF_ROWSLIST];

        for (int i = 0; i < NUMBER_OF_ROWSLIST; i++)
        {
            mNeedRefreshData[i] = true;
        }

        mDeatilViews = new View[NUMBER_OF_ROWSLIST];
        mImageHeight = Util.getLCDWidth(activity);

        mOnUserActionListener = onUserActionListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
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
        int count = NUMBER_OF_ROWSLIST;

        if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == true)
        {
            count--;
        }

        return count;
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

                    getDetail00View(mDeatilViews[0]);
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

                    getDetail01View(mDeatilViews[1], mHotelDetail);
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

                    getDetail02View(mDeatilViews[2], mHotelDetail);
                }

                view = mDeatilViews[2];
                break;

            // D Benefit or 데일리's comment
            case 3:
                if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                {
                    if (mDeatilViews[3] == null)
                    {
                        mDeatilViews[3] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
                        getDetailBenefitView(mDeatilViews[3], mHotelDetail);
                    }

                    if (mNeedRefreshData[3] == true)
                    {
                        mNeedRefreshData[3] = false;

                        getDetailBenefitView(mDeatilViews[3], mHotelDetail);
                    }

                    view = mDeatilViews[3];
                } else
                {
                    view = makeCommentView(layoutInflater, parent);
                }
                break;

            // 데일리 추천이유 or 호텔 정보
            case 4:
                if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                {
                    view = makeCommentView(layoutInflater, parent);
                } else
                {
                    view = makeHotelInfoView(layoutInflater, parent);
                }
                break;

            // 호텔 정보 or 호텔 더보기 정보
            case 5:
                if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                {
                    view = makeHotelInfoView(layoutInflater, parent);
                } else
                {
                    view = makeHotelMoreInfoView(layoutInflater, parent);
                }
                break;

            // 호텔 더보기 정보 or 확인 사항
            case 6:
                if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                {
                    view = makeHotelMoreInfoView(layoutInflater, parent);
                } else
                {
                    view = makeCheckListView(layoutInflater, parent);
                }
                break;

            // 확인 사항 or 카카오톡 문의
            case 7:
                if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                {
                    view = makeCheckListView(layoutInflater, parent);
                } else
                {
                    view = makeKakaoView(layoutInflater, parent);
                }
                break;

            // 카카오톡 문의
            case 8:
                view = makeKakaoView(layoutInflater, parent);
                break;
        }

        return view;
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

    private View makeCommentView(LayoutInflater layoutInflater, ViewGroup parent)
    {
        if (layoutInflater == null || parent == null)
        {
            return null;
        }

        if (mDeatilViews[4] == null)
        {
            mDeatilViews[4] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
        }

        if (mNeedRefreshData[4] == true)
        {
            mNeedRefreshData[4] = false;

            getDetail04View(layoutInflater, (ViewGroup) mDeatilViews[4], mHotelDetail);
        }

        return mDeatilViews[4];
    }

    private View makeHotelInfoView(LayoutInflater layoutInflater, ViewGroup parent)
    {
        if (layoutInflater == null || parent == null)
        {
            return null;
        }

        if (mDeatilViews[5] == null)
        {
            mDeatilViews[5] = layoutInflater.inflate(R.layout.list_row_detail05, parent, false);
        }

        if (mNeedRefreshData[5] == true)
        {
            mNeedRefreshData[5] = false;

            getDeatil05View(layoutInflater, (ViewGroup) mDeatilViews[5], mHotelDetail);
        }

        return mDeatilViews[5];
    }

    private View makeHotelMoreInfoView(LayoutInflater layoutInflater, ViewGroup parent)
    {
        if (layoutInflater == null || parent == null)
        {
            return null;
        }

        if (mDeatilViews[6] == null)
        {
            mDeatilViews[6] = layoutInflater.inflate(R.layout.list_row_detail_more, parent, false);
        }

        if (mNeedRefreshData[6] == true)
        {
            mNeedRefreshData[6] = false;

            getDeatil06View(layoutInflater, (ViewGroup) mDeatilViews[6], mHotelDetail);
        }

        return mDeatilViews[6];
    }

    private View makeCheckListView(LayoutInflater layoutInflater, ViewGroup parent)
    {
        if (layoutInflater == null || parent == null)
        {
            return null;
        }

        if (mDeatilViews[7] == null)
        {
            mDeatilViews[7] = layoutInflater.inflate(R.layout.list_row_detail06, parent, false);
        }

        if (mNeedRefreshData[7] == true)
        {
            mNeedRefreshData[7] = false;

            getDeatil07View(layoutInflater, (ViewGroup) mDeatilViews[7], mHotelDetail);
        }

        return mDeatilViews[7];
    }

    private View makeKakaoView(LayoutInflater layoutInflater, ViewGroup parent)
    {
        if (layoutInflater == null || parent == null)
        {
            return null;
        }

        if (mDeatilViews[8] == null)
        {
            mDeatilViews[8] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
        }

        if (mNeedRefreshData[8] == true)
        {
            mNeedRefreshData[8] = false;

            getDeatil08View(mDeatilViews[8]);
        }

        return mDeatilViews[8];
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
     * @param hotelDetail
     * @return
     */
    private View getDetail01View(View view, HotelDetail hotelDetail)
    {
        mHotelTitleLayout = view.findViewById(R.id.hotelTitleLayout);

        // 등급
        mHotelGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
        mHotelGradeTextView.setVisibility(View.VISIBLE);

        mHotelGradeTextView.setText(hotelDetail.grade.getName(mFragmentActivity));
        mHotelGradeTextView.setBackgroundResource(hotelDetail.grade.getColorResId());

        // 호텔명
        mHotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
        mHotelNameTextView.setText(hotelDetail.hotelName);

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
        if (Util.isTextEmpty(hotelDetail.satisfaction) == true)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(hotelDetail.satisfaction);
        }

        return view;
    }

    /**
     * 호텔 주소 및 맵
     *
     * @param view
     * @param hotelDetail
     * @return
     */
    private View getDetail02View(final View view, HotelDetail hotelDetail)
    {
        // 주소지
        final TextView hotelAddressTextView01 = (TextView) view.findViewById(R.id.hotelAddressTextView01);
        final TextView hotelAddressTextView02 = (TextView) view.findViewById(R.id.hotelAddressTextView02);
        final TextView hotelAddressTextView03 = (TextView) view.findViewById(R.id.hotelAddressTextView03);

        hotelAddressTextView02.setText(null);
        hotelAddressTextView02.setVisibility(View.GONE);

        hotelAddressTextView03.setText(null);
        hotelAddressTextView03.setVisibility(View.GONE);

        final String address = hotelDetail.address;

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

        final com.facebook.drawee.view.SimpleDraweeView mapImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.mapImageView);

        mapImageView.post(new Runnable()
        {
            @Override
            public void run()
            {
                double width = mapImageView.getWidth();
                double height = mapImageView.getHeight();
                double ratio = height / width;

                if (width >= 640)
                {
                    width = 640;
                }

                height = width * ratio;

                String size = String.format("%dx%d", (int) width * 4 / 5, (int) height * 4 / 5);
                String iconUrl = "http://s3.dailyhotel.kr/resources/images/_banner/googlemap_marker.png";

                mapImageView.setImageURI(Uri.parse("http://maps.googleapis.com/maps/api/staticmap?zoom=17&size="//
                    + size + "&markers=icon:" + iconUrl + "|" + mHotelDetail.latitude + "," + mHotelDetail.longitude + "&sensor=false&scale=2&format=png8&mobile=true"));
            }
        });

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
    private View getDetailBenefitView(View view, HotelDetail hotelDetail)
    {
        if (view == null || hotelDetail == null)
        {
            return view;
        }

        final TextView textView1Line = (TextView) view.findViewById(R.id.benefit1LineTextView);
        final TextView textView2Line = (TextView) view.findViewById(R.id.benefit2LineTextView);

        textView2Line.setText(null);
        textView2Line.setVisibility(View.GONE);

        final String benefit = hotelDetail.hotelBenefit;

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
     * 데일리 추천 이유
     *
     * @param view
     * @return
     */
    private View getDetail04View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
    {
        if (layoutInflater == null || viewGroup == null || hotelDetail == null)
        {
            return viewGroup;
        }

        ArrayList<DetailInformation> arrayList = hotelDetail.getInformation();

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
     * @param view
     * @return
     */
    private View getDeatil05View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
    {
        if (layoutInflater == null || viewGroup == null || hotelDetail == null)
        {
            return viewGroup;
        }

        ArrayList<DetailInformation> arrayList = hotelDetail.getInformation();

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
    private View getDeatil06View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
    {
        if (layoutInflater == null || viewGroup == null || hotelDetail == null)
        {
            return viewGroup;
        }

        ArrayList<DetailInformation> arrayList = hotelDetail.getMoreInformation();

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
    private View getDeatil07View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
    {
        if (layoutInflater == null || viewGroup == null || hotelDetail == null)
        {
            return viewGroup;
        }

        ArrayList<DetailInformation> arrayList = hotelDetail.getInformation();

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
            return view;
        }

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