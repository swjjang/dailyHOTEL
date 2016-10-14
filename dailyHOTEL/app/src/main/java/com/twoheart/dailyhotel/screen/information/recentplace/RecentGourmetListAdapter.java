package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentGourmetListAdapter extends RecentPlacesListAdapter
{
    public RecentGourmetListAdapter(Context context, ArrayList<? extends Place> list, OnRecentPlacesItemListener listener)
    {
        super(context, list, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.list_row_gourmet, parent, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.getListRowHeight(mContext));
        view.setLayoutParams(layoutParams);

        return new GourmetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position)
    {
        Place item = getItem(position);
        if (item == null)
        {
            return;
        }

        final Gourmet gourmet = (Gourmet) item;
        GourmetViewHolder holder = (GourmetViewHolder) viewHolder;

        String strPrice = Util.getPriceFormat(mContext, gourmet.price, false);
        String strDiscount = Util.getPriceFormat(mContext, gourmet.discountPrice, false);

        String address = gourmet.addressSummary;

        int barIndex = address.indexOf('|');
        if (barIndex >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.addressView.setText(address);
        holder.nameView.setText(gourmet.name);

        // 인원
        if (gourmet.persons > 1)
        {
            holder.personsTextView.setVisibility(View.VISIBLE);
            holder.personsTextView.setText(mContext.getString(R.string.label_persions, gourmet.persons));
        } else
        {
            holder.personsTextView.setVisibility(View.GONE);
        }

        if (gourmet.price <= 0 || gourmet.price <= gourmet.discountPrice)
        {
            holder.priceView.setVisibility(View.INVISIBLE);
            holder.priceView.setText(null);
        } else
        {
            holder.priceView.setVisibility(View.VISIBLE);

            holder.priceView.setText(strPrice);
            holder.priceView.setPaintFlags(holder.priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (gourmet.satisfaction > 0)
        {
            holder.satisfactionView.setVisibility(View.VISIBLE);
            holder.satisfactionView.setText(gourmet.satisfaction + "%");
        } else
        {
            holder.satisfactionView.setVisibility(View.GONE);
        }

        holder.discountView.setText(strDiscount);
        holder.nameView.setSelected(true); // Android TextView marquee bug

        if (Util.isOverAPI16() == true)
        {
            holder.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

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
            holder.gradeView.setVisibility(View.GONE);
        } else
        {
            holder.gradeView.setVisibility(View.VISIBLE);
            holder.gradeView.setText(displayCategory);
        }

        Util.requestImageResize(mContext, holder.gourmetImageView, gourmet.imageUrl);

        // SOLD OUT 표시
        if (gourmet.isSoldOut)
        {
            holder.soldOutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.soldOutView.setVisibility(View.GONE);
        }

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            holder.distanceTextView.setVisibility(View.VISIBLE);
            holder.distanceTextView.setText("(거리:" + new DecimalFormat("#.#").format(gourmet.distance) + "km)");
        } else
        {
            holder.distanceTextView.setVisibility(View.GONE);
        }

        holder.deleteView.setVisibility(View.VISIBLE);
        holder.deleteView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    mListener.onDeleteClick(v, position);
                }
            }
        });
    }

    private class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        View gradientView;
        com.facebook.drawee.view.SimpleDraweeView gourmetImageView;
        TextView nameView;
        TextView priceView;
        TextView discountView;
        View soldOutView;
        TextView addressView;
        TextView gradeView;
        TextView satisfactionView;
        TextView personsTextView;
        TextView distanceTextView;
        View deleteView;

        public GourmetViewHolder(View itemView)
        {
            super(itemView);

            gradientView = itemView.findViewById(R.id.gradientView);
            gourmetImageView = (com.facebook.drawee.view.SimpleDraweeView) itemView.findViewById(R.id.imageView);
            nameView = (TextView) itemView.findViewById(R.id.nameTextView);
            priceView = (TextView) itemView.findViewById(R.id.priceTextView);
            satisfactionView = (TextView) itemView.findViewById(R.id.satisfactionView);
            discountView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            soldOutView = itemView.findViewById(R.id.soldoutView);
            addressView = (TextView) itemView.findViewById(R.id.addressTextView);
            gradeView = (TextView) itemView.findViewById(R.id.gradeTextView);
            personsTextView = (TextView) itemView.findViewById(R.id.personsTextView);
            distanceTextView = (TextView) itemView.findViewById(R.id.distanceTextView);
            deleteView = itemView.findViewById(R.id.deleteView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        mListener.onItemClick(v);
                    }
                }
            });
        }
    }
}
