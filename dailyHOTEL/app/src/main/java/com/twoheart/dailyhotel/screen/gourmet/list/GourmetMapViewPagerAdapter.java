package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.util.Util;

public class GourmetMapViewPagerAdapter extends PlaceMapViewPagerAdapter
{
    public GourmetMapViewPagerAdapter(Context context)
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

        View view = layoutInflater.inflate(R.layout.viewpager_column_gourmet, null);

        PlaceViewItem item = mPlaceViewItemList.get(position);

        makeLayout(view, item.<Gourmet>getItem());

        container.addView(view, 0);

        return view;
    }

    private void makeLayout(final View view, final Gourmet gourmet)
    {
        //        View gradientView = view.findViewById(R.id.gradientView);
        com.facebook.drawee.view.SimpleDraweeView placeImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.imageView);
        TextView name = (TextView) view.findViewById(R.id.nameTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);
        TextView discountTextView = (TextView) view.findViewById(R.id.discountPriceTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        TextView grade = (TextView) view.findViewById(R.id.gradeTextView);
        View closeView = view.findViewById(R.id.closeImageVIew);
        TextView persions = (TextView) view.findViewById(R.id.personsTextView);
        View dBenefitLayout = view.findViewById(R.id.dBenefitLayout);
        TextView dBenefitTextView = (TextView) view.findViewById(R.id.dBenefitTextView);

        String address = gourmet.addressSummary;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        addressTextView.setText(address);
        name.setText(gourmet.name);

        // D.benefit
        if (Util.isTextEmpty(gourmet.dBenefitText) == false)
        {
            dBenefitLayout.setVisibility(View.VISIBLE);
            dBenefitTextView.setText(gourmet.dBenefitText);
        } else
        {
            dBenefitLayout.setVisibility(View.GONE);
        }

        // 인원
        if (gourmet.persons > 1)
        {
            persions.setVisibility(View.VISIBLE);
            persions.setText(mContext.getString(R.string.label_persions, gourmet.persons));
        } else
        {
            persions.setVisibility(View.GONE);
        }

        if (gourmet.price <= 0)
        {
            priceTextView.setVisibility(View.INVISIBLE);

            priceTextView.setText(null);
        } else
        {
            priceTextView.setVisibility(View.VISIBLE);

            priceTextView.setText(Util.getPriceFormat(mContext, gourmet.price, false));
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (gourmet.satisfaction > 0)
        {
            satisfactionView.setVisibility(View.VISIBLE);
            satisfactionView.setText(gourmet.satisfaction + "%");
        } else
        {
            satisfactionView.setVisibility(View.GONE);
        }

        discountTextView.setText(Util.getPriceFormat(mContext, gourmet.discountPrice, false));

        name.setSelected(true); // Android TextView marquee bug

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
            grade.setVisibility(View.INVISIBLE);
        } else
        {
            grade.setVisibility(View.VISIBLE);
            grade.setText(displayCategory);
        }

        Util.requestImageResize(mContext, placeImageView, gourmet.imageUrl);

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
