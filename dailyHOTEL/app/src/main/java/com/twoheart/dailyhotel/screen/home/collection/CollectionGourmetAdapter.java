package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.home.HomeRecommendationLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class CollectionGourmetAdapter extends PlaceListAdapter
{
    private boolean mIsUsedMultiTransition;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnRecommendationClickListener;

    public CollectionGourmetAdapter(Context context, ArrayList<PlaceViewItem> arrayList //
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
                View view = mInflater.inflate(R.layout.view_empty_gourmet_collection, parent, false);
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
                return new RecommendViewHolder(view);
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

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                int marginBottom;
                if (position == getItemCount() - 1)
                {
                    marginBottom = ScreenUtils.dpToPx(mContext, 82d);
                } else
                {
                    marginBottom = ScreenUtils.dpToPx(mContext, 48d);
                }

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                if (params == null)
                {
                    params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 34d));
                    params.topMargin = ScreenUtils.dpToPx(mContext, 15d);
                    params.leftMargin = ScreenUtils.dpToPx(mContext, 24d);
                    params.rightMargin = ScreenUtils.dpToPx(mContext, 24d);
                }

                params.bottomMargin = marginBottom;

                holder.itemView.setLayoutParams(params);
                break;
            }

            case PlaceViewItem.TYPE_EMPTY_VIEW:
            {
                boolean isLast = getItemCount() - 1 == position;
                int height;
                int topMargin;

                if (isLast)
                {
                    height = ScreenUtils.getScreenHeight(mContext) - ScreenUtils.dpToPx(mContext, 97) //
                        - ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) //
                        + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97);

                    topMargin = ScreenUtils.dpToPx(mContext, -30d);
                } else
                {
                    height = ScreenUtils.dpToPx(mContext, 256d);
                    topMargin = ScreenUtils.dpToPx(mContext, -15d);
                }


                View itemLayout = holder.itemView.findViewById(R.id.emptyLayout);
                FrameLayout.LayoutParams itemLayoutLayoutParams = (FrameLayout.LayoutParams) itemLayout.getLayoutParams();
                if (itemLayoutLayoutParams == null)
                {
                    itemLayoutLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                itemLayoutLayoutParams.topMargin = topMargin;

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , height);

                holder.itemView.setLayoutParams(layoutParams);
                break;
            }

            case PlaceViewItem.TYPE_RECOMMEND_VIEW:
                onBindViewHolder((RecommendViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

        holder.gourmetCardView.setStickerVisible(false);
        holder.gourmetCardView.setDeleteVisible(false);
        holder.gourmetCardView.setWishVisible(true);
        holder.gourmetCardView.setWish(recommendationGourmet.myWish);

        holder.gourmetCardView.setTagStickerImage(recommendationGourmet.stickerUrl);
        holder.gourmetCardView.setImage(recommendationGourmet.imageUrl);

        holder.gourmetCardView.setGradeText(DailyTextUtils.isTextEmpty(recommendationGourmet.categorySub) == false ? recommendationGourmet.categorySub : recommendationGourmet.category);
        holder.gourmetCardView.setVRVisible(recommendationGourmet.truevr && mTrueVREnabled);
        holder.gourmetCardView.setReviewText(recommendationGourmet.rating, recommendationGourmet.reviewCount);
        holder.gourmetCardView.setNewVisible(recommendationGourmet.newItem);
        holder.gourmetCardView.setGourmetNameText(recommendationGourmet.name);
        holder.gourmetCardView.setDistanceVisible(false);
        holder.gourmetCardView.setAddressText(recommendationGourmet.addrSummary);

        if (recommendationGourmet.availableTicketNumbers == 0 //
            || recommendationGourmet.availableTicketNumbers < recommendationGourmet.minimumOrderQuantity //
            || recommendationGourmet.isExpired == true)
        {
            holder.gourmetCardView.setPriceText(0, 0, 0, null, 0);
        } else
        {
            holder.gourmetCardView.setPriceText(recommendationGourmet.discountRate, recommendationGourmet.discount, recommendationGourmet.price, recommendationGourmet.couponDiscountText, recommendationGourmet.persons);
        }

        holder.gourmetCardView.setBenefitText(recommendationGourmet.benefit);

        // 최상위에는 빈뷰이가 1번째가 첫번째다.
        if (position == 1)
        {
            holder.gourmetCardView.setDividerVisible(false);
        } else
        {
            holder.gourmetCardView.setDividerVisible(true);
        }
    }

    private void onBindViewHolder(RecommendViewHolder holder, PlaceViewItem placeViewItem)
    {
        final ArrayList<Recommendation> list = placeViewItem.getItem();

        holder.recommendationLayout.setData(list);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {

    }

    class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        DailyGourmetCardView gourmetCardView;

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

            gourmetCardView.setOnWishClickListener(v -> {
                if (mOnWishClickListener != null)
                {
                    mOnWishClickListener.onClick(gourmetCardView);
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
            this.recommendationLayout.showTopMarginViewVisible(false);
            this.recommendationLayout.showBottomMarginViewVisible(true);

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
