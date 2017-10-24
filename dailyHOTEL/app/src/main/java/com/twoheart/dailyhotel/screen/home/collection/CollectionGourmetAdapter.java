package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
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
        holder.gourmetCardView.setVRVisible(false);
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


        //        String strPrice = DailyTextUtils.getPriceFormat(mContext, recommendationGourmet.price, false);
        //        String strDiscount = DailyTextUtils.getPriceFormat(mContext, recommendationGourmet.discount, false);
        //
        //        String address = recommendationGourmet.addrSummary;
        //
        //        int barIndex = address.indexOf('|');
        //        if (barIndex >= 0)
        //        {
        //            address = address.replace(" | ", "ㅣ");
        //        } else if (address.indexOf('l') >= 0)
        //        {
        //            address = address.replace(" l ", "ㅣ");
        //        }
        //
        //        holder.dataBinding.addressTextView.setText(address);
        //        holder.dataBinding.nameTextView.setText(recommendationGourmet.name);
        //
        //        // 인원
        //        if (recommendationGourmet.persons > 1)
        //        {
        //            holder.dataBinding.personsTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.personsTextView.setText(mContext.getString(R.string.label_persions, recommendationGourmet.persons));
        //        } else
        //        {
        //            holder.dataBinding.personsTextView.setVisibility(View.GONE);
        //        }
        //
        //        if (recommendationGourmet.price <= 0 || recommendationGourmet.price <= recommendationGourmet.discount)
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //        } else
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
        //
        //            holder.dataBinding.priceTextView.setText(strPrice);
        //            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //        }
        //
        //        // 만족도
        //        if (recommendationGourmet.rating > 0)
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.satisfactionView.setText(//
        //                mContext.getResources().getString(R.string.label_list_satisfaction, recommendationGourmet.rating));
        //        } else
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        //        }
        //
        //        holder.dataBinding.discountPriceTextView.setText(strDiscount);
        ////        holder.dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug
        //
        //        if (VersionUtils.isOverAPI16() == true)
        //        {
        //            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        //        } else
        //        {
        //            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        //        }
        //
        //        String displayCategory;
        //        if (DailyTextUtils.isTextEmpty(recommendationGourmet.categorySub) == false)
        //        {
        //            displayCategory = recommendationGourmet.categorySub;
        //        } else
        //        {
        //            displayCategory = recommendationGourmet.category;
        //        }
        //
        //        // grade
        //        if (DailyTextUtils.isTextEmpty(displayCategory) == true)
        //        {
        //            holder.dataBinding.gradeTextView.setVisibility(View.GONE);
        //        } else
        //        {
        //            holder.dataBinding.gradeTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.gradeTextView.setText(displayCategory);
        //        }
        //
        //        if (mIsUsedMultiTransition == true && VersionUtils.isOverAPI21() == true)
        //        {
        //            holder.dataBinding.imageView.setTransitionName(null);
        //        }
        //
        //        // 스티커
        //        if (DailyTextUtils.isTextEmpty(recommendationGourmet.stickerUrl) == false)
        //        {
        //            holder.dataBinding.stickerSimpleDraweeView.setVisibility(View.VISIBLE);
        //
        //            DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
        //            {
        //                @Override
        //                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
        //                {
        //                    ViewGroup.LayoutParams layoutParams = holder.dataBinding.stickerSimpleDraweeView.getLayoutParams();
        //
        //                    int screenWidth = ScreenUtils.getScreenWidth(mContext);
        //                    if (screenWidth > Sticker.DEFAULT_SCREEN_WIDTH && screenWidth < Sticker.LARGE_SCREEN_WIDTH)
        //                    {
        //                        layoutParams.width = (int) (Sticker.MEDIUM_RATE * imageInfo.getWidth());
        //                        layoutParams.height = (int) (Sticker.MEDIUM_RATE * imageInfo.getHeight());
        //                    } else
        //                    {
        //                        layoutParams.width = imageInfo.getWidth();
        //                        layoutParams.height = imageInfo.getHeight();
        //                    }
        //
        //                    holder.dataBinding.stickerSimpleDraweeView.setLayoutParams(layoutParams);
        //                }
        //            }).setUri(Uri.parse(recommendationGourmet.stickerUrl)).build();
        //
        //            holder.dataBinding.stickerSimpleDraweeView.setController(controller);
        //        } else
        //        {
        //            holder.dataBinding.stickerSimpleDraweeView.setVisibility(View.GONE);
        //        }
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, recommendationGourmet.imageUrl);
        //
        //        // SOLD OUT 표시
        //        holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //        if (recommendationGourmet.availableTicketNumbers == 0 //
        //            || recommendationGourmet.availableTicketNumbers < recommendationGourmet.minimumOrderQuantity //
        //            || recommendationGourmet.isExpired == true)
        //        {
        //            holder.dataBinding.personsTextView.setVisibility(View.GONE);
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //            holder.dataBinding.discountPriceTextView.setText(mContext.getString(R.string.act_hotel_soldout));
        //        }
        //
        //        if (DailyTextUtils.isTextEmpty(recommendationGourmet.benefit) == false)
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.dBenefitTextView.setText(recommendationGourmet.benefit);
        //        } else
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        //        }
        //
        //        //        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        //        //        {
        //        //            holder.distanceTextView.setVisibility(View.VISIBLE);
        //        //            holder.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(gourmet.distance)));
        //        //        } else
        //        //        {
        //        holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        //        }
        //
        //        // VR 여부, 추후 고메가 VR이 생기면 화면에 보여주도록 한다.
        //        //        if (recommendationGourmet.truevr == true && mTrueVREnabled == true)
        //        //        {
        //        //            if (holder.satisfactionView.getVisibility() == View.VISIBLE)
        //        //            {
        //        //                holder.dot2View.setVisibility(View.VISIBLE);
        //        //            } else
        //        //            {
        //        //                holder.dot2View.setVisibility(View.GONE);
        //        //            }
        //        //
        //        //            holder.trueVRView.setVisibility(View.VISIBLE);
        //        //        } else
        //        {
        //            holder.dataBinding.dot2View.setVisibility(View.GONE);
        //            holder.dataBinding.trueVRView.setVisibility(View.GONE);
        //        }
        //
        //        if (holder.dataBinding.satisfactionView.getVisibility() == View.GONE//
        //            && holder.dataBinding.trueVRView.getVisibility() == View.GONE//
        //            && holder.dataBinding.distanceTextView.getVisibility() == View.GONE)
        //        {
        //            holder.dataBinding.informationLayout.setVisibility(View.GONE);
        //        } else
        //        {
        //            holder.dataBinding.informationLayout.setVisibility(View.VISIBLE);
        //        }
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
        }
    }
}
