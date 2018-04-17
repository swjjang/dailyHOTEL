package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.util.Util;

public class GourmetMapViewPagerAdapter extends PlaceMapViewPagerAdapter
{
    public GourmetMapViewPagerAdapter(Context context)
    {
        super(context);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mPlaceViewItemList == null || mPlaceViewItemList.size() < position)
        {
            return null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.viewpager_column_gourmet, null);

        PlaceViewItem item = mPlaceViewItemList.get(position);

        makeLayout(position, view, item.getItem());

        container.addView(view, 0);

        return view;
    }

    private void makeLayout(int position, final View view, final Gourmet gourmet)
    {
        //        View gradientView = view.findViewById(R.id.gradientView);
        com.facebook.drawee.view.SimpleDraweeView placeImageView = view.findViewById(R.id.imageView);
        TextView name = view.findViewById(R.id.nameTextView);
        TextView priceTextView = view.findViewById(R.id.priceTextView);
        TextView satisfactionView = view.findViewById(R.id.satisfactionView);
        TextView discountTextView = view.findViewById(R.id.discountPriceTextView);
        TextView addressTextView = view.findViewById(R.id.addressTextView);
        TextView grade = view.findViewById(R.id.gradeTextView);
        View closeView = view.findViewById(R.id.closeImageView);
        TextView persons = view.findViewById(R.id.personsTextView);
        View dBenefitLayout = view.findViewById(R.id.dBenefitLayout);
        TextView dBenefitTextView = view.findViewById(R.id.dBenefitTextView);

        addressTextView.setText(gourmet.addressSummary);
        name.setText(gourmet.name);

        // D.benefit
        if (DailyTextUtils.isTextEmpty(gourmet.dBenefitText) == false)
        {
            dBenefitLayout.setVisibility(View.VISIBLE);
            dBenefitTextView.setText(gourmet.dBenefitText);
        } else
        {
            dBenefitLayout.setVisibility(View.GONE);
        }

        // 인원
        if (gourmet.persons > 0)
        {
            persons.setVisibility(View.VISIBLE);
            persons.setText(mContext.getString(R.string.label_persons, gourmet.persons));
        } else
        {
            persons.setVisibility(View.GONE);
        }

        if (gourmet.price <= 0)
        {
            priceTextView.setVisibility(View.INVISIBLE);

            priceTextView.setText(null);
        } else
        {
            priceTextView.setVisibility(View.VISIBLE);

            priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, gourmet.price, false));
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (gourmet.satisfaction > 0)
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(//
                mContext.getResources().getString(R.string.label_list_satisfaction, gourmet.satisfaction));
        } else
        {
            satisfactionView.setVisibility(View.GONE);
        }

        discountTextView.setText(DailyTextUtils.getPriceFormat(mContext, gourmet.discountPrice, false));

        name.setSelected(true); // Android TextView marquee bug

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
            grade.setVisibility(View.INVISIBLE);
        } else
        {
            grade.setVisibility(View.VISIBLE);
            grade.setText(displayCategory);
        }

        placeImageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        Util.requestImageResize(mContext, placeImageView, gourmet.imageUrl);

        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onWishClick(position, gourmet);
                }
            }
        });

        placeImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onInformationClick(view, gourmet);
                }
            }
        });
    }
}
