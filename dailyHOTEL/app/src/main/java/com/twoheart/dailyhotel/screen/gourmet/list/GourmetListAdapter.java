package com.twoheart.dailyhotel.screen.gourmet.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class GourmetListAdapter extends PlaceListAdapter
{
    View.OnClickListener mOnClickListener;

    public GourmetListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener, View.OnClickListener eventBannerListener)
    {
        super(context, arrayList);

        mOnClickListener = listener;
        mOnEventBannerClickListener = eventBannerListener;

        setSortType(Constants.SortType.DEFAULT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_SECTION:
            {
                LayoutSectionDataBinding viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_section_data, parent, false);

                return new SectionViewHolder(viewDataBinding);
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                DailyGourmetCardView gourmetCardView = new DailyGourmetCardView(mContext);
                gourmetCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new GourmetViewHolder(gourmetCardView);
            }

            case PlaceViewItem.TYPE_EVENT_BANNER:
            {
                View view = mInflater.inflate(R.layout.list_row_eventbanner, parent, false);

                return new EventBannerViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                LayoutFooterDataBinding viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_footer_data, parent, false);

                // 원래 높이 175dp + 상단 툴바 높이 52dp
                viewDataBinding.footerTextView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 227);

                return new BaseDataBindingViewHolder(viewDataBinding);
            }

            case PlaceViewItem.TYPE_LOADING_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_loading, parent, false);

                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.probressbar_default), PorterDuff.Mode.SRC_IN);


                return new BaseViewHolder(view);
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
                onBindViewHolder((GourmetViewHolder) holder, item, position);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_EVENT_BANNER:
                onBindViewHolder((EventBannerViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Gourmet gourmet = placeViewItem.getItem();

        holder.gourmetCardView.setStickerVisible(false);
        holder.gourmetCardView.setDeleteVisible(false);
        holder.gourmetCardView.setWishVisible(true);
        holder.gourmetCardView.setWish(gourmet.myWish);

        holder.gourmetCardView.setTagStickerImage(gourmet.stickerUrl);
        holder.gourmetCardView.setImage(gourmet.imageUrl);

        holder.gourmetCardView.setGradeText(DailyTextUtils.isTextEmpty(gourmet.subCategory) == false ? gourmet.subCategory : gourmet.category);
        holder.gourmetCardView.setVRVisible(gourmet.truevr && mTrueVREnabled);
        holder.gourmetCardView.setReviewText(gourmet.satisfaction, gourmet.reviewCount);

        holder.gourmetCardView.setNewVisible(gourmet.newItem);

        holder.gourmetCardView.setGourmetNameText(gourmet.name);

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            holder.gourmetCardView.setDistanceVisible(true);
            holder.gourmetCardView.setDistanceText(gourmet.distance);
        } else
        {
            holder.gourmetCardView.setDistanceVisible(false);
        }

        holder.gourmetCardView.setAddressText(gourmet.addressSummary);

        if (gourmet.isSoldOut == true)
        {
            holder.gourmetCardView.setPriceText(0, 0, 0, null, 0);
        } else
        {
            holder.gourmetCardView.setPriceText(gourmet.discountRate, gourmet.discountPrice, gourmet.price, gourmet.couponDiscountText, gourmet.persons);
        }

        holder.gourmetCardView.setBenefitText(gourmet.dBenefitText);

        if (position == 0 || getItem(position - 1).mType != PlaceViewItem.TYPE_SECTION)
        {
            holder.gourmetCardView.setDividerVisible(true);
        } else
        {
            holder.gourmetCardView.setDividerVisible(false);
        }
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {

    }

    protected class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        public DailyGourmetCardView gourmetCardView;

        public GourmetViewHolder(DailyGourmetCardView gourmetCardView)
        {
            super(gourmetCardView);

            this.gourmetCardView = gourmetCardView;

            itemView.setOnClickListener(mOnClickListener);

            if (Util.supportPreview(mContext) == true)
            {
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (mOnLongClickListener == null)
                        {
                            return false;
                        } else
                        {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(70);

                            return mOnLongClickListener.onLongClick(v);
                        }
                    }
                });
            }

            gourmetCardView.setOnWishClickListener(v ->
            {
                if (mOnWishClickListener != null)
                {
                    mOnWishClickListener.onClick(gourmetCardView);
                }
            });
        }
    }
}
