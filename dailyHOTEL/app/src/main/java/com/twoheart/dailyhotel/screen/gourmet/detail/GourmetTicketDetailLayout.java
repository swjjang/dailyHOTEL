package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.network.model.GourmetTicket;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLineIndicator;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class GourmetTicketDetailLayout extends BaseLayout
{
    private NestedScrollView mNestedScrollView;
    protected DailyLoopViewPager mViewPager;
    protected DailyLineIndicator mDailyLineIndicator;
    protected View mMoreIconView;
    protected GourmetTicketDetailImagePagerAdapter mImageAdapter;

    private TextView mDescriptionTextView;
    private View mDefaultImageLayout, mBottomBarLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onImageClick(int position);

        void onReservationClick();
    }

    public GourmetTicketDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view, mContext.getString(R.string.label_gourmet_product_detail_menu_detail));

        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);

        initImageLayout(view);



        mBottomBarLayout = view.findViewById(R.id.bottomBarLayout);
    }

    private void initImageLayout(View view)
    {
        mDefaultImageLayout = view.findViewById(R.id.defaultImageLayout);

        // 이미지 ViewPage 넣기.
        mDailyLineIndicator = (DailyLineIndicator) mDefaultImageLayout.findViewById(R.id.viewpagerIndicator);

        mViewPager = (DailyLoopViewPager) mDefaultImageLayout.findViewById(R.id.defaulLoopViewPager);

        mImageAdapter = new GourmetTicketDetailImagePagerAdapter(mContext);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mDailyLineIndicator.setOnPageChangeListener(mOnPageChangeListener);

        ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
        layoutParams.height = Util.getLCDWidth(mContext);
        mViewPager.setLayoutParams(layoutParams);

        mMoreIconView = mDefaultImageLayout.findViewById(R.id.moreIconView);

        mDescriptionTextView = (TextView) mDefaultImageLayout.findViewById(R.id.descriptionTextView);
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
        });
    }

    public void setInformation(GourmetDetail gourmetDetail, int ticketIndex)
    {
        GourmetTicket gourmetTicket = gourmetDetail.getProduct(ticketIndex);


        // 이미지 정보
        List<ProductImageInformation> imageInformationList = gourmetTicket.getImageList();

        if(imageInformationList == null || imageInformationList.size() == 0)
        {
            mDefaultImageLayout.setVisibility(View.GONE);
        } else
        {
            mDefaultImageLayout.setVisibility(View.VISIBLE);

            mImageAdapter.setData(imageInformationList);
            mViewPager.setAdapter(mImageAdapter);

            mDailyLineIndicator.setViewPager(mViewPager);
            setLineIndicatorVisible(imageInformationList.size() > 0);
            setImageInformation((imageInformationList.size() > 0) ? imageInformationList.get(0).imageDescription : null);
        }

        // 메뉴 제목
        TextView productNameTextView = (TextView)mNestedScrollView.findViewById(R.id.productNameTextView);

        productNameTextView.setText(gourmetTicket.ticketName);






    }

    public void setImageInformation(String description)
    {
        if (Util.isTextEmpty(description) == false)
        {
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(description);
        } else
        {
            mDescriptionTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void setLineIndicatorVisible(boolean isShow)
    {
        mMoreIconView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        mDailyLineIndicator.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            if (mImageAdapter == null)
            {
                return;
            }

            ProductImageInformation imageInformation = mImageAdapter.getImageInformation(position);

            if (imageInformation == null)
            {
                return;
            }

            setImageInformation(imageInformation.imageDescription);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };
}