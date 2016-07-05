package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.util.Util;

public class StayMapViewPagerAdapter extends PlaceMapViewPagerAdapter
{
    public StayMapViewPagerAdapter(Context context)
    {
        super(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mPlaceViewItemList == null || mPlaceViewItemList.size() < position)
        {
            return null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.viewpager_column_hotel, null);

        PlaceViewItem item = mPlaceViewItemList.get(position);

        makeLayout(view, item.<Stay>getItem());

        container.addView(view, 0);

        return view;
    }

    private void makeLayout(View view, final Stay stay)
    {
        View gradientView = view.findViewById(R.id.gradientView);
        com.facebook.drawee.view.SimpleDraweeView hotelImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.imageView);
        TextView name = (TextView) view.findViewById(R.id.nameTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);
        View averageTextView = view.findViewById(R.id.averageTextView);
        TextView discountTextView = (TextView) view.findViewById(R.id.discountPriceTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        TextView grade = (TextView) view.findViewById(R.id.gradeTextView);
        View closeView = view.findViewById(R.id.closeImageVIew);
        View dBenefitLayout = view.findViewById(R.id.dBenefitLayout);
        TextView dBenefitTextView = (TextView) view.findViewById(R.id.dBenefitTextView);

        String address = stay.addressSummary;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        addressTextView.setText(address);
        name.setText(stay.name);

        // D.benefit
        if (Util.isTextEmpty(stay.dBenefitText) == false)
        {
            dBenefitLayout.setVisibility(View.VISIBLE);
            dBenefitTextView.setText(stay.dBenefitText);
        } else
        {
            dBenefitLayout.setVisibility(View.GONE);
        }

        if (stay.price <= 0)
        {
            priceTextView.setVisibility(View.INVISIBLE);

            priceTextView.setText(null);
        } else
        {
            priceTextView.setVisibility(View.VISIBLE);

            priceTextView.setText(Util.getPriceFormat(mContext, stay.price, false));
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (stay.satisfaction > 0)
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(stay.satisfaction + "%");
        } else
        {
            satisfactionView.setVisibility(View.GONE);
        }

        if (stay.nights > 1)
        {
            averageTextView.setVisibility(View.VISIBLE);
        } else
        {
            averageTextView.setVisibility(View.GONE);
        }

        discountTextView.setText(Util.getPriceFormat(mContext, stay.getDiscountPrice(), false));

        name.setSelected(true); // Android TextView marquee bug

        // grade
        grade.setText(stay.getGrade().getName(mContext));
        grade.setBackgroundResource(stay.getGrade().getColorResId());

        Util.requestImageResize(mContext, hotelImageView, stay.imageUrl);

        closeView.setOnClickListener(new View.OnClickListener()
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

        hotelImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onInformationClick(stay);
                }
            }
        });
    }
}
