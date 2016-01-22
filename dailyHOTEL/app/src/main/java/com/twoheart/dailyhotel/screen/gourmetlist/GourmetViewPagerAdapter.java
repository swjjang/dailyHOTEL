package com.twoheart.dailyhotel.screen.gourmetlist;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.PlaceViewPagerAdapter;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;

public class GourmetViewPagerAdapter extends PlaceViewPagerAdapter
{
    public GourmetViewPagerAdapter(Context context)
    {
        super(context);
    }

    @Override
    protected void makeLayout(View view, Place place)
    {
        final Gourmet gourmet = (Gourmet) place;

        View gradientView = view.findViewById(R.id.gradientView);
        com.facebook.drawee.view.SimpleDraweeView placeImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.imageView);
        TextView name = (TextView) view.findViewById(R.id.nameTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);
        TextView discountTextView = (TextView) view.findViewById(R.id.discountPriceTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        TextView grade = (TextView) view.findViewById(R.id.gradeTextView);
        View closeView = view.findViewById(R.id.closeImageVIew);
        TextView persions = (TextView) view.findViewById(R.id.personsTextView);

        DecimalFormat comma = new DecimalFormat("###,##0");

        String address = gourmet.address;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        addressTextView.setText(address);
        name.setText(gourmet.name);

        // 인원
        if (gourmet.persons > 1)
        {
            persions.setVisibility(View.VISIBLE);
            persions.setText(mContext.getString(R.string.label_persions, gourmet.persons));
        } else
        {
            persions.setVisibility(View.GONE);
        }

        Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));

        int price = gourmet.price;

        if (price <= 0)
        {
            priceTextView.setVisibility(View.INVISIBLE);

            priceTextView.setText(null);
        } else
        {
            priceTextView.setVisibility(View.VISIBLE);

            priceTextView.setText(comma.format(price) + currency);
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

        discountTextView.setText(comma.format(gourmet.discountPrice) + currency);

        name.setSelected(true); // Android TextView marquee bug

        // grade
        if (Util.isTextEmpty(gourmet.category) == true)
        {
            grade.setVisibility(View.INVISIBLE);
        } else
        {
            grade.setVisibility(View.VISIBLE);
            grade.setText(gourmet.category);
        }

        Util.requestImageResize(mContext, placeImageView, Uri.parse(gourmet.imageUrl));

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

        placeImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onInfoWindowClickListener(gourmet);
                }
            }
        });
    }
}
