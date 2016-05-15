package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;

public class HotelMapViewPagerAdapter extends PlaceMapViewPagerAdapter
{
    private HotelMapFragment.OnUserActionListener mOnUserActionListener;

    public HotelMapViewPagerAdapter(Context context)
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

        makeLayout(view, item.<Hotel>getItem());

        container.addView(view, 0);

        return view;
    }

    public void setOnUserActionListener(HotelMapFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    private void makeLayout(View view, final Hotel hotel)
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

        DecimalFormat comma = new DecimalFormat("###,##0");

        String address = hotel.addressSummary;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        addressTextView.setText(address);
        name.setText(hotel.name);

        // D.benefit
        if (Util.isTextEmpty(hotel.dBenefitText) == false)
        {
            dBenefitLayout.setVisibility(View.VISIBLE);
            dBenefitTextView.setText(hotel.dBenefitText);
        } else
        {
            dBenefitLayout.setVisibility(View.GONE);
        }

        String currency = mContext.getResources().getString(R.string.currency);

        if (hotel.price <= 0)
        {
            priceTextView.setVisibility(View.INVISIBLE);

            priceTextView.setText(null);
        } else
        {
            priceTextView.setVisibility(View.VISIBLE);

            priceTextView.setText(comma.format(hotel.price) + currency);
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (hotel.satisfaction > 0)
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(hotel.satisfaction + "%");
        } else
        {
            satisfactionView.setVisibility(View.GONE);
        }

        if (hotel.nights > 1)
        {
            averageTextView.setVisibility(View.VISIBLE);
        } else
        {
            averageTextView.setVisibility(View.GONE);
        }

        discountTextView.setText(comma.format(hotel.averageDiscountPrice) + currency);

        name.setSelected(true); // Android TextView marquee bug

        // grade
        grade.setText(hotel.getGrade().getName(mContext));
        grade.setBackgroundResource(hotel.getGrade().getColorResId());

        Util.requestImageResize(mContext, hotelImageView, hotel.imageUrl);

        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onCloseInfoWindowClickListener();
                }
            }
        });

        hotelImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onInfoWindowClickListener(hotel);
                }
            }
        });
    }
}
