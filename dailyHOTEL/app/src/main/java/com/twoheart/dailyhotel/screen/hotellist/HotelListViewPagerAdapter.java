package com.twoheart.dailyhotel.screen.hotellist;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HotelListViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private ArrayList<PlaceViewItem> mHotelListViewItemList;
    private HotelMapFragment.OnUserActionListener mOnUserActionListener;

    public HotelListViewPagerAdapter(Context context)
    {
        mContext = context;

        mHotelListViewItemList = new ArrayList<PlaceViewItem>();
    }

    public void setOnUserActionListener(HotelMapFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mHotelListViewItemList == null || mHotelListViewItemList.size() < position)
        {
            return null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.viewpager_column_hotel, null);

        PlaceViewItem item = mHotelListViewItemList.get(position);

        makeLayout(view, item.<Hotel>getItem());

        container.addView(view, 0);

        return view;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
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
        View dBenefitImageView = view.findViewById(R.id.dBenefitImageView);

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
        if (hotel.isDBenefit == true)
        {
            dBenefitImageView.setVisibility(View.VISIBLE);
        } else
        {
            dBenefitImageView.setVisibility(View.GONE);
        }

        Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));

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

        discountTextView.setText(comma.format(hotel.averageDiscount) + currency);

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

    @Override
    public int getCount()
    {
        if (mHotelListViewItemList != null)
        {
            return mHotelListViewItemList.size();
        } else
        {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    public void setData(List<PlaceViewItem> list)
    {
        if (mHotelListViewItemList == null)
        {
            mHotelListViewItemList = new ArrayList<>();
        }

        mHotelListViewItemList.clear();

        if (list != null)
        {
            mHotelListViewItemList.addAll(list);
        }
    }
}
