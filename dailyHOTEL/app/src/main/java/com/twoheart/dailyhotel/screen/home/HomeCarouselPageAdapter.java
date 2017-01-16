package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 16..
 */

public class HomeCarouselPageAdapter extends PagerAdapter
{
    private Context mContext;
    private ArrayList<? extends Place> mPlaceList;
    private ItemClickListener mItemClickListener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public HomeCarouselPageAdapter(Context context)
    {
        mContext = context;
    }

    public HomeCarouselPageAdapter(Context context, ItemClickListener listener)
    {
        mContext = context;
        mItemClickListener = listener;
    }

    public void setData(ArrayList<? extends Place> list)
    {
        mPlaceList = list;
    }

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_carousel_layout, null);

        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
        DailyTextView titleView = (DailyTextView) view.findViewById(R.id.contentTextView);
        DailyTextView discountPriceView = (DailyTextView) view.findViewById(R.id.contentDiscountPriceView);
        DailyTextView originPriceView = (DailyTextView) view.findViewById(R.id.contentOriginPriceView);
        DailyTextView provinceView = (DailyTextView) view.findViewById(R.id.contentProvinceView);
        DailyTextView gradeView = (DailyTextView) view.findViewById(R.id.contentGradeView);
        View dotView = view.findViewById(R.id.contentDotImageView);

        int width = Util.dpToPx(mContext, 239);
        int height = Util.getRatioHeightType16x9(width);

        if (mPlaceList == null || mPlaceList.size() == 0 || position < 0)
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            imageView.setLayoutParams(layoutParams);

            container.addView(view, 0);
            return view;
        }

        if (position < mPlaceList.size())
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            Place place = mPlaceList.get(position);

            Util.requestImageResize(mContext, imageView, place.imageUrl);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            imageView.setLayoutParams(layoutParams);

            titleView.setText(place.name);

            String strPrice = Util.getPriceFormat(mContext, place.price, false);
            String strDiscount = Util.getPriceFormat(mContext, place.discountPrice, false);

            discountPriceView.setText(strDiscount);

            if (place.price <= 0 || place.price <= place.discountPrice)
            {
                originPriceView.setVisibility(View.INVISIBLE);
                originPriceView.setText(null);
            } else
            {
                originPriceView.setVisibility(View.VISIBLE);
                originPriceView.setText(strPrice);
                originPriceView.setPaintFlags(originPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            provinceView.setText(place.districtName);

            if (place instanceof Stay)
            {
                gradeView.setText(((Stay) place).getGrade().getName(mContext));
                dotView.setVisibility(View.VISIBLE);
            } else if (place instanceof Gourmet)
            {
                Gourmet gourmet = (Gourmet) place;

                String displayCategory;
                if (Util.isTextEmpty(gourmet.subCategory) == false)
                {
                    displayCategory = gourmet.subCategory;
                } else
                {
                    displayCategory = gourmet.category;
                }

                // grade
                if (Util.isTextEmpty(displayCategory) == true)
                {
                    gradeView.setVisibility(View.GONE);
                    dotView.setVisibility(View.GONE);
                } else
                {
                    gradeView.setVisibility(View.VISIBLE);
                    dotView.setVisibility(View.VISIBLE);
                    gradeView.setText(displayCategory);
                }

                gradeView.setText(displayCategory);
            }

            view.setTag(place);
            container.addView(view, 0, layoutParams);
        } else
        {
            Util.restartApp(mContext);
        }

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    // 디폴트 로딩 이미지가 생성 될 예정으로 기본 카운트를 1로 함
    @Override
    public int getCount()
    {
        if (mPlaceList != null)
        {
            if (mPlaceList.size() == 0)
            {
                return 1;
            } else
            {
                return mPlaceList.size();
            }
        } else
        {
            return 1;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }
}
