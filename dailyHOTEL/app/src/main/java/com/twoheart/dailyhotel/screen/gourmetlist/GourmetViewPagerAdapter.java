package com.twoheart.dailyhotel.screen.gourmetlist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.PlaceViewPagerAdapter;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;

import java.io.File;
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

        RelativeLayout placeLayout = (RelativeLayout) view.findViewById(R.id.ll_hotel_row_content);
        final ImageView placeImageView = (ImageView) view.findViewById(R.id.iv_hotel_row_img);
        TextView name = (TextView) view.findViewById(R.id.tv_hotel_row_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.tv_hotel_row_price);
        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);
        TextView discountTextView = (TextView) view.findViewById(R.id.tv_hotel_row_discount);
        TextView sold_out = (TextView) view.findViewById(R.id.tv_hotel_row_soldout);
        TextView addressTextView = (TextView) view.findViewById(R.id.tv_hotel_row_address);
        TextView grade = (TextView) view.findViewById(R.id.hv_hotel_grade);
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

        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.60f};

        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        p.setShaderFactory(sf);
        placeLayout.setBackgroundDrawable(p);

        // grade
        if (Util.isTextEmpty(gourmet.category) == true)
        {
            grade.setVisibility(View.INVISIBLE);
        } else
        {
            grade.setVisibility(View.VISIBLE);
            grade.setText(gourmet.category);
        }

        if (Util.getLCDWidth(mContext) < 720)
        {
            Glide.with(mContext).load(gourmet.imageUrl).into(placeImageView);
            Glide.with(mContext).load(gourmet.imageUrl).downloadOnly(new SimpleTarget<File>(360, 240)
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(gourmet.imageUrl, resource.getAbsolutePath());
                }
            });
        } else
        {
            Glide.with(mContext).load(gourmet.imageUrl).into(placeImageView);
            Glide.with(mContext).load(gourmet.imageUrl).downloadOnly(new SimpleTarget<File>()
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(gourmet.imageUrl, resource.getAbsolutePath());
                }
            });
        }

        // SOLD OUT 표시
        if (gourmet.isSoldOut == true)
        {
            sold_out.setVisibility(View.VISIBLE);
        } else
        {
            sold_out.setVisibility(View.GONE);
        }

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

        placeLayout.setOnClickListener(new View.OnClickListener()
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
