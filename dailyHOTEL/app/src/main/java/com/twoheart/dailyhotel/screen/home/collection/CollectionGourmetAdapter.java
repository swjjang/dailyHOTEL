package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class CollectionGourmetAdapter extends PlaceListAdapter
{
    private boolean mIsUsedMultiTransition;
    View.OnClickListener mOnClickListener;

    public CollectionGourmetAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener)
    {
        super(context, arrayList);

        mOnClickListener = listener;

        setSortType(Constants.SortType.DEFAULT);
    }

    public void setUsedMultiTransition(boolean isUsedMultiTransition)
    {
        mIsUsedMultiTransition = isUsedMultiTransition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_SECTION:
            {
                View view = mInflater.inflate(R.layout.list_row_default_section, parent, false);

                return new SectionViewHolder(view);
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                View view = mInflater.inflate(R.layout.list_row_gourmet, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)));
                view.setLayoutParams(layoutParams);

                return new GourmetViewHolder(view);
            }

            case PlaceViewItem.TYPE_HEADER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_collection_header, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new HeaderViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.view_empty_gourmet_collection, parent, false);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getScreenHeight(mContext) - ScreenUtils.dpToPx(mContext, 97) - ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new FooterViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_GUIDE_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_users_place_footer, parent, false);
                return new FooterGuideViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        PlaceViewItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((GourmetViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem)
    {
        final RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

        String strPrice = DailyTextUtils.getPriceFormat(mContext, recommendationGourmet.price, false);
        String strDiscount = DailyTextUtils.getPriceFormat(mContext, recommendationGourmet.discount, false);

        String address = recommendationGourmet.addrSummary;

        int barIndex = address.indexOf('|');
        if (barIndex >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.addressView.setText(address);
        holder.nameView.setText(recommendationGourmet.name);

        // 인원
        if (recommendationGourmet.persons > 1)
        {
            holder.personsTextView.setVisibility(View.VISIBLE);
            holder.personsTextView.setText(mContext.getString(R.string.label_persions, recommendationGourmet.persons));
        } else
        {
            holder.personsTextView.setVisibility(View.GONE);
        }

        if (recommendationGourmet.price <= 0 || recommendationGourmet.price <= recommendationGourmet.discount)
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
        if (recommendationGourmet.rating > 0)
        {
            holder.satisfactionView.setVisibility(View.VISIBLE);
            holder.satisfactionView.setText(//
                mContext.getResources().getString(R.string.label_list_satisfaction, recommendationGourmet.rating));
        } else
        {
            holder.satisfactionView.setVisibility(View.GONE);
        }

        holder.discountView.setText(strDiscount);
        holder.nameView.setSelected(true); // Android TextView marquee bug

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

        String displayCategory;
        if (DailyTextUtils.isTextEmpty(recommendationGourmet.categorySub) == false)
        {
            displayCategory = recommendationGourmet.categorySub;
        } else
        {
            displayCategory = recommendationGourmet.category;
        }

        // grade
        if (DailyTextUtils.isTextEmpty(displayCategory) == true)
        {
            holder.gradeView.setVisibility(View.GONE);
        } else
        {
            holder.gradeView.setVisibility(View.VISIBLE);
            holder.gradeView.setText(displayCategory);
        }

        if (mIsUsedMultiTransition == true)
        {
            holder.gourmetImageView.setTransitionName(null);
        }

        Util.requestImageResize(mContext, holder.gourmetImageView, recommendationGourmet.imageUrl);

        // SOLD OUT 표시
        if (recommendationGourmet.isSoldOut)
        {
            holder.soldOutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.soldOutView.setVisibility(View.GONE);
        }

        if (DailyTextUtils.isTextEmpty(recommendationGourmet.benefit) == false)
        {
            holder.dBenefitLayout.setVisibility(View.VISIBLE);
            holder.dBenefitTextView.setText(recommendationGourmet.benefit);
        } else
        {
            holder.dBenefitLayout.setVisibility(View.GONE);
        }

        //        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        //        {
        //            holder.distanceTextView.setVisibility(View.VISIBLE);
        //            holder.distanceTextView.setText("(거리:" + new DecimalFormat("#.#").format(gourmet.distance) + "km)");
        //        } else
        //        {
        holder.distanceTextView.setVisibility(View.GONE);
        //        }
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {

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
        View dBenefitLayout;
        TextView dBenefitTextView;

        public GourmetViewHolder(View itemView)
        {
            super(itemView);

            dBenefitLayout = itemView.findViewById(R.id.dBenefitLayout);
            dBenefitTextView = (TextView) dBenefitLayout.findViewById(R.id.dBenefitTextView);
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

            itemView.setOnClickListener(mOnClickListener);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        public HeaderViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class FooterGuideViewHolder extends RecyclerView.ViewHolder
    {
        public FooterGuideViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
