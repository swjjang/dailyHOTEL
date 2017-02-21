package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLineIndicator;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class GourmetProductDetailLayout extends BaseLayout
{
    private NestedScrollView mNestedScrollView;
    protected DailyLoopViewPager mViewPager;
    protected DailyLineIndicator mDailyLineIndicator;
    protected View mMoreIconView;
    protected GourmetProductDetailImagePagerAdapter mImageAdapter;

    private TextView mDescriptionTextView;
    private View mDefaultImageLayout, mBottomBarLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onImageClick(int position);

        void onReservationClick();
    }

    public GourmetProductDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view, mContext.getString(R.string.label_product_detail));

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

        mImageAdapter = new GourmetProductDetailImagePagerAdapter(mContext);
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

    public void setInformation(GourmetDetail gourmetDetail, int productIndex)
    {
        GourmetProduct gourmetProduct = gourmetDetail.getProduct(productIndex);


        // 이미지 정보
        List<ProductImageInformation> imageInformationList = gourmetProduct.getImageList();

        if (imageInformationList == null || imageInformationList.size() == 0)
        {
            mDefaultImageLayout.setVisibility(View.GONE);
        } else
        {
            mDefaultImageLayout.setVisibility(View.VISIBLE);

            mImageAdapter.setData(imageInformationList, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((OnEventListener) mOnEventListener).onImageClick(mViewPager.getCurrentItem());
                }
            });

            mViewPager.setAdapter(mImageAdapter);

            mDailyLineIndicator.setViewPager(mViewPager);
            setLineIndicatorVisible(imageInformationList.size() > 0);
            setImageInformation((imageInformationList.size() > 0) ? imageInformationList.get(0).imageDescription : null);
        }

        // 메뉴 제목
        TextView productNameTextView = (TextView) mNestedScrollView.findViewById(R.id.productNameTextView);

        productNameTextView.setText(gourmetProduct.ticketName);

        // 베네핏
        View benefitLayout = mNestedScrollView.findViewById(R.id.benefitLayout);

        if (Util.isTextEmpty(gourmetProduct.benefit) == true)
        {
            benefitLayout.setVisibility(View.GONE);
        } else
        {
            benefitLayout.setVisibility(View.VISIBLE);

            TextView benefitTextView = (TextView) benefitLayout.findViewById(R.id.benefitTextView);
            benefitTextView.setText(gourmetProduct.benefit);
        }

        // 이용시간
        View timeLayout = mNestedScrollView.findViewById(R.id.timeLayout);

        if (Util.isTextEmpty(gourmetProduct.option) == true)
        {
            timeLayout.setVisibility(View.GONE);
        } else
        {
            timeLayout.setVisibility(View.VISIBLE);

            TextView timeTextView = (TextView) timeLayout.findViewById(R.id.timeTextView);
            timeTextView.setText(gourmetProduct.option);
        }

        // 확인사항
        View checkLayout = mNestedScrollView.findViewById(R.id.checkLayout);

        if (Util.isTextEmpty(gourmetProduct.checkList) == true)
        {
            checkLayout.setVisibility(View.GONE);
        } else
        {
            checkLayout.setVisibility(View.VISIBLE);

            TextView checkTextView = (TextView) checkLayout.findViewById(R.id.checkTextView);
            checkTextView.setText(gourmetProduct.checkList);
        }

        // 메뉴 설명
        View menuTextView = mNestedScrollView.findViewById(R.id.menuTextView);

        TextView menuSummaryTextView = (TextView) mNestedScrollView.findViewById(R.id.menuSummaryTextView);

        if (Util.isTextEmpty(gourmetProduct.menuSummary) == true)
        {
            menuSummaryTextView.setVisibility(View.GONE);
        } else
        {
            menuSummaryTextView.setVisibility(View.VISIBLE);
            menuSummaryTextView.setText(gourmetProduct.menuSummary);
        }

        ViewGroup menuDetailLayout = (ViewGroup) mNestedScrollView.findViewById(R.id.menuDetailLayout);

        List<String> menuDetail = new ArrayList<>();
        menuDetail.add(gourmetProduct.menuDetail);

        if (gourmetProduct.menuDetail == null || menuDetail.size() == 0)
        {
            menuDetailLayout.setVisibility(View.GONE);
        } else
        {
            menuDetailLayout.setVisibility(View.VISIBLE);
            setMenuDetail(mContext, menuDetailLayout, menuDetail);
        }

        if (menuSummaryTextView.getVisibility() == View.GONE && menuDetailLayout.getVisibility() == View.GONE)
        {
            menuTextView.setVisibility(View.GONE);
        } else
        {
            menuTextView.setVisibility(View.VISIBLE);
        }

        // bottom bar
        TextView discountPriceTextView = (TextView) mBottomBarLayout.findViewById(R.id.discountPriceTextView);
        TextView priceTextView = (TextView) mBottomBarLayout.findViewById(R.id.priceTextView);
        priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String price = Util.getPriceFormat(mContext, gourmetProduct.price, false);
        String discountPrice = Util.getPriceFormat(mContext, gourmetProduct.discountPrice, false);

        if (gourmetProduct.price <= 0 || gourmetProduct.price <= gourmetProduct.discountPrice)
        {
            priceTextView.setVisibility(View.GONE);
            priceTextView.setText(null);
        } else
        {
            priceTextView.setVisibility(View.VISIBLE);
            priceTextView.setText(price);
        }

        discountPriceTextView.setText(discountPrice);

        View reservationTextView = mBottomBarLayout.findViewById(R.id.reservationTextView);
        reservationTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onReservationClick();
            }
        });
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

    private void setMenuDetail(Context context, ViewGroup viewGroup, List<String> menuDetailList)
    {
        if (context == null || viewGroup == null || menuDetailList == null)
        {
            return;
        }

        viewGroup.removeAllViews();

        int size = menuDetailList.size();

        for (int i = 0; i < size; i++)
        {
            String contentText = menuDetailList.get(i);

            if (Util.isTextEmpty(contentText) == true)
            {
                continue;
            }

            View textLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_detail_text, viewGroup, false);
            TextView textView = (TextView) textLayout.findViewById(R.id.textView);
            textView.setText(contentText);

            viewGroup.addView(textLayout);
        }
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