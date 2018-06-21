package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.home.HomeRecommendationLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class CollectionStayAdapter extends PlaceListAdapter
{
    private boolean mIsUsedMultiTransition;
    private int mNights;

    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnRecommendationClickListener;

    public CollectionStayAdapter(Context context, ArrayList<PlaceViewItem> arrayList //
        , View.OnClickListener listener, View.OnClickListener recommendationListener)
    {
        super(context, arrayList);

        mOnClickListener = listener;
        mOnRecommendationClickListener = recommendationListener;

        setSortType(Constants.SortType.DEFAULT);
    }

    public void setUsedMultiTransition(boolean isUsedMultiTransition)
    {
        mIsUsedMultiTransition = isUsedMultiTransition;
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        try
        {
            mNights = ((StayBookingDay) placeBookingDay).getNights();
        } catch (Exception e)
        {
            mNights = 1;
        }
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
                DailyStayCardView stayCardView = new DailyStayCardView(mContext);
                stayCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayViewHolder(stayCardView);
            }

            case PlaceViewItem.TYPE_HEADER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_collection_header, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new BaseViewHolder(view);
            }

            case PlaceViewItem.TYPE_EMPTY_VIEW:
            {
                View view = mInflater.inflate(R.layout.view_empty_stay_collection, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getScreenHeight(mContext) - ScreenUtils.dpToPx(mContext, 97) - ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new BaseViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_users_place_footer, parent, false);
                return new BaseViewHolder(view);
            }

            case PlaceViewItem.TYPE_RECOMMEND_VIEW:
            {
                HomeRecommendationLayout view = new HomeRecommendationLayout(mContext);
                view.setListener(new HomeRecommendationLayout.HomeRecommendationListener()
                {
                    @Override
                    public void onRecommendationClick(View view, Recommendation recommendation, int position)
                    {
                        ExLog.d("sam - todo");
                    }
                });

                return new CollectionStayAdapter.RecommendViewHolder(view);
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
                onBindViewHolder((StayViewHolder) holder, item, position);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_RECOMMEND_VIEW:
                onBindViewHolder((RecommendViewHolder) holder, item);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onBindViewHolder(StayViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final RecommendationStay recommendationStay = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(mRewardEnabled && recommendationStay.provideRewardSticker);
        holder.stayCardView.setDeleteVisible(false);
        holder.stayCardView.setWishVisible(true);
        holder.stayCardView.setWish(recommendationStay.myWish);

        holder.stayCardView.setImage(recommendationStay.imageUrl);

        holder.stayCardView.setGradeText(Stay.Grade.valueOf(recommendationStay.grade).getName(mContext));
        holder.stayCardView.setVRVisible(recommendationStay.truevr && mTrueVREnabled);
        holder.stayCardView.setReviewText(recommendationStay.rating, recommendationStay.reviewCount);
        holder.stayCardView.setNewVisible(recommendationStay.newItem);
        holder.stayCardView.setStayNameText(recommendationStay.name);
        holder.stayCardView.setDistanceVisible(false);
        holder.stayCardView.setAddressText(recommendationStay.addrSummary);

        if (recommendationStay.availableRooms > 0)
        {
            holder.stayCardView.setPriceText(recommendationStay.discountRate, recommendationStay.discount, recommendationStay.price, recommendationStay.couponDiscountText, mNights > 1);
        } else
        {
            holder.stayCardView.setPriceText(0, 0, 0, null, false);
        }

        holder.stayCardView.setBenefitText(recommendationStay.benefit);

        // 최상위에는 빈뷰이가 1번째가 첫번째다.
        if (position == 1)
        {
            holder.stayCardView.setDividerVisible(false);
        } else
        {
            holder.stayCardView.setDividerVisible(true);
        }
    }

    private void onBindViewHolder(RecommendViewHolder holder, PlaceViewItem placeViewItem)
    {
        final ArrayList<Recommendation> list = placeViewItem.getItem();

        holder.recommendationLayout.setData(list);
    }

    class StayViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayCardView stayCardView;

        public StayViewHolder(DailyStayCardView stayCardView)
        {
            super(stayCardView);

            this.stayCardView = stayCardView;

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

            stayCardView.setOnWishClickListener(v -> {
                if (mOnWishClickListener != null)
                {
                    mOnWishClickListener.onClick(stayCardView);
                }
            });
        }
    }

    class RecommendViewHolder extends RecyclerView.ViewHolder
    {
        HomeRecommendationLayout recommendationLayout;

        public RecommendViewHolder(HomeRecommendationLayout recommendationLayout)
        {
            super(recommendationLayout);

            this.recommendationLayout = recommendationLayout;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.recommendationLayout.setLayoutParams(params);

            recommendationLayout.setListener(new HomeRecommendationLayout.HomeRecommendationListener()
            {
                @Override
                public void onRecommendationClick(View view, Recommendation recommendation, int position)
                {
                    view.setTag(recommendation);

                    if (mOnRecommendationClickListener != null)
                    {
                        mOnRecommendationClickListener.onClick(view);
                    }
                }
            });
        }
    }
}
