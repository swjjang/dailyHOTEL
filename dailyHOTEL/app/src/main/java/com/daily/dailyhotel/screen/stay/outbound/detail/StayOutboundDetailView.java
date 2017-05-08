package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDetailDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StayOutboundDetailView extends BaseView<StayOutboundDetailView.OnEventListener, ActivityStayOutboundDetailDataBinding> implements StayOutboundDetailViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;


    public interface OnEventListener extends OnBaseEventListener
    {
        void onImageClick();

        void onReviewClick();

        void onCalendarClick();

        void onDownloadCouponClick();

        void onMapClick();

        void onClipAddressClick(String address);

        void onNavigatorClick();

        void onWishClick();

        void onConciergeClick();

        void onBookingClick();
    }

    public StayOutboundDetailView(BaseActivity baseActivity, StayOutboundDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }


    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }


    @Override
    public void onClick(View v)
    {

    }

    @Override
    public void showRoomList()
    {

    }

    @Override
    public void hideRoomList()
    {

    }

    @Override
    public void setStayDetail(StayOutboundDetail stayOutboundDetail)
    {
        //        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //
        //        if (convertView == null)
        //        {
        //            linearLayout = new LinearLayout(mContext);
        //            linearLayout.setOrientation(LinearLayout.VERTICAL);
        //        } else
        //        {
        //            linearLayout = (LinearLayout) convertView;
        //        }
        //
        //        linearLayout.removeAllViews();
        //
        //        StayDetailParams stayDetailParams = mStayDetail.getStayDetailParams();
        //
        //        // 빈화면
        //        if (mDetailViews[0] == null)
        //        {
        //            mDetailViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
        //        }
        //
        //        getDetail00View(mDetailViews[0]);
        //        linearLayout.addView(mDetailViews[0]);
        //
        //        // 호텔 등급과 이름.
        //        if (mDetailViews[1] == null)
        //        {
        //            mDetailViews[1] = layoutInflater.inflate(R.layout.list_row_stay_detail02, parent, false);
        //        }
        //
        //        getTitleView(mDetailViews[1], mStayDetail);
        //        linearLayout.addView(mDetailViews[1]);
        //
        //        // 주소 및 맵
        //        if (mDetailViews[2] == null)
        //        {
        //            mDetailViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
        //        }
        //
        //        getAddressView(mDetailViews[2], mStayDetail);
        //        linearLayout.addView(mDetailViews[2]);
        //
        //        List<StayDetail.Pictogram> pictogramList = mStayDetail.getPictogramList();
        //
        //        if (pictogramList != null && pictogramList.size() > 0)
        //        {
        //            if (mDetailViews[3] == null)
        //            {
        //                mDetailViews[3] = layoutInflater.inflate(R.layout.list_row_detail_pictogram, parent, false);
        //            }
        //
        //            getAmenitiesView(mDetailViews[3], mStayDetail);
        //            linearLayout.addView(mDetailViews[3]);
        //        }
        //
        //        // D Benefit
        //        if (DailyTextUtils.isTextEmpty(stayDetailParams.benefit) == false)
        //        {
        //            if (mDetailViews[4] == null)
        //            {
        //                mDetailViews[4] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
        //            }
        //
        //            getDetailBenefitView(layoutInflater, mDetailViews[4], mStayDetail);
        //            linearLayout.addView(mDetailViews[4]);
        //        } else
        //        {
        //            // 베네핏이 없으면 정보화면의 상단 라인으로 대체한다.
        //            View view = new View(mContext);
        //            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 1));
        //            view.setLayoutParams(layoutParams);
        //            view.setBackgroundResource(R.color.default_line_cf0f0f0);
        //            linearLayout.addView(view);
        //        }
        //
        //        // 정보 화면
        //        if (mDetailViews[5] == null)
        //        {
        //            mDetailViews[5] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
        //        }
        //
        //        getInformationView(layoutInflater, (ViewGroup) mDetailViews[5], mStayDetail);
        //        linearLayout.addView(mDetailViews[5]);
        //
        //        // 카카오톡 문의
        //        if (mDetailViews[6] == null)
        //        {
        //            mDetailViews[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
        //        }
        //
        //        getConciergeView(mDetailViews[6]);
        //        linearLayout.addView(mDetailViews[6]);
    }
}
