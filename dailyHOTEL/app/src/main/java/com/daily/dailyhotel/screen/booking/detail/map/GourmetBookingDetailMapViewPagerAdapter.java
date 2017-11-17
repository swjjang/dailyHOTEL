package com.daily.dailyhotel.screen.booking.detail.map;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewpagerColumnGourmetDataBinding;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.util.Util;

public class GourmetBookingDetailMapViewPagerAdapter extends PlaceBookingDetailMapViewPagerAdapter
{
    public GourmetBookingDetailMapViewPagerAdapter(Context context)
    {
        super(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mPlaceList == null || mPlaceList.size() < position)
        {
            return null;
        }

        ViewpagerColumnGourmetDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.viewpager_column_gourmet_data, container, false);

        Gourmet gourmet = (Gourmet) mPlaceList.get(position);

        String address = gourmet.addressSummary;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        dataBinding.addressTextView.setText(address);
        dataBinding.nameTextView.setText(gourmet.name);

        // D.benefit
        if (DailyTextUtils.isTextEmpty(gourmet.dBenefitText) == false)
        {
            dataBinding.dBenefitLayout.setVisibility(View.VISIBLE);
            dataBinding.dBenefitTextView.setText(gourmet.dBenefitText);
        } else
        {
            dataBinding.dBenefitLayout.setVisibility(View.GONE);
        }

        // 인원
        if (gourmet.persons > 0)
        {
            dataBinding.personsTextView.setVisibility(View.VISIBLE);
            dataBinding.personsTextView.setText(mContext.getString(R.string.label_persons, gourmet.persons));
        } else
        {
            dataBinding.personsTextView.setVisibility(View.GONE);
        }

        // 가격
        if (gourmet.price > 0)
        {
            dataBinding.priceTextView.setVisibility(View.VISIBLE);
            dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, gourmet.price, false));
            dataBinding.priceTextView.setPaintFlags(dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            dataBinding.priceTextView.setText(null);
        }

        // 만족도
        if (gourmet.satisfaction > 0)
        {
            dataBinding.satisfactionView.setVisibility(View.VISIBLE);
            dataBinding.satisfactionView.setText(//
                mContext.getResources().getString(R.string.label_list_satisfaction, gourmet.satisfaction));
        } else
        {
            dataBinding.satisfactionView.setVisibility(View.GONE);
        }

        dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, gourmet.discountPrice, false));

        String displayCategory;
        if (DailyTextUtils.isTextEmpty(gourmet.subCategory) == false)
        {
            displayCategory = gourmet.subCategory;
        } else
        {
            displayCategory = gourmet.category;
        }

        // grade
        if (DailyTextUtils.isTextEmpty(displayCategory) == true)
        {
            dataBinding.gradeTextView.setVisibility(View.INVISIBLE);
        } else
        {
            dataBinding.gradeTextView.setVisibility(View.VISIBLE);
            dataBinding.gradeTextView.setText(displayCategory);
        }

        // Image
        dataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        Util.requestImageResize(mContext, dataBinding.simpleDraweeView, gourmet.imageUrl);

        dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug

        dataBinding.closeImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onCloseClick();
                }
            }
        });

        dataBinding.simpleDraweeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onPlaceClick(dataBinding.getRoot(), gourmet);
                }
            }
        });

        container.addView(dataBinding.getRoot(), 0);

        return dataBinding.getRoot();
    }
}
