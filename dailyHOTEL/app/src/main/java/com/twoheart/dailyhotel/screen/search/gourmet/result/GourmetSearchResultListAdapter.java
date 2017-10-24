package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListAdapter;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class GourmetSearchResultListAdapter extends GourmetListAdapter
{
    public GourmetSearchResultListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener, View.OnClickListener eventBannerListener)
    {
        super(context, arrayList, listener, eventBannerListener);
    }

    @Override
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
        holder.gourmetCardView.setVRVisible(false);
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

        if (gourmet.availableTicketNumbers == 0 || gourmet.availableTicketNumbers < gourmet.minimumOrderQuantity || gourmet.expired == true)
        {
            holder.gourmetCardView.setPriceText(0, 0, 0, null, 0);
        } else
        {
            holder.gourmetCardView.setPriceText(gourmet.discountRate, gourmet.discountPrice, gourmet.price, gourmet.couponDiscountText, gourmet.persons);
        }

        holder.gourmetCardView.setBenefitText(gourmet.dBenefitText);

        if (position == 0)
        {
            holder.gourmetCardView.setDividerVisible(false);
        } else
        {
            holder.gourmetCardView.setDividerVisible(true);
        }


        //
        //        String strPrice = DailyTextUtils.getPriceFormat(mContext, gourmet.price, false);
        //        String strDiscount = DailyTextUtils.getPriceFormat(mContext, gourmet.discountPrice, false);
        //
        //        String address = gourmet.addressSummary;
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
        //        holder.dataBinding.nameTextView.setText(gourmet.name);
        //
        //        // 인원
        //        if (gourmet.persons > 1)
        //        {
        //            holder.dataBinding.personsTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.personsTextView.setText(mContext.getString(R.string.label_persions, gourmet.persons));
        //        } else
        //        {
        //            holder.dataBinding.personsTextView.setVisibility(View.GONE);
        //        }
        //
        //        if (gourmet.price <= 0 || gourmet.price <= gourmet.discountPrice)
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
        //        if (gourmet.satisfaction > 0)
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.satisfactionView.setText(//
        //                mContext.getResources().getString(R.string.label_list_satisfaction, gourmet.satisfaction));
        //        } else
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        //        }
        //
        //        holder.dataBinding.discountPriceTextView.setText(strDiscount);
        //        holder.dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug
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
        //        if (DailyTextUtils.isTextEmpty(gourmet.subCategory) == false)
        //        {
        //            displayCategory = gourmet.subCategory;
        //        } else
        //        {
        //            displayCategory = gourmet.category;
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
        //        // 스티커
        //        if (DailyTextUtils.isTextEmpty(gourmet.stickerUrl) == false)
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
        //            }).setUri(Uri.parse(gourmet.stickerUrl)).build();
        //
        //            holder.dataBinding.stickerSimpleDraweeView.setController(controller);
        //        } else
        //        {
        //            holder.dataBinding.stickerSimpleDraweeView.setVisibility(View.GONE);
        //        }
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, gourmet.imageUrl);
        //
        //        // SOLD OUT 표시
        //        holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //
        //        if (gourmet.availableTicketNumbers == 0 || gourmet.availableTicketNumbers < gourmet.minimumOrderQuantity || gourmet.expired == true)
        //        {
        //            holder.dataBinding.personsTextView.setVisibility(View.GONE);
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //            holder.dataBinding.discountPriceTextView.setText(mContext.getString(R.string.act_hotel_soldout));
        //        }
        //
        //        if (DailyTextUtils.isTextEmpty(gourmet.dBenefitText) == false)
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.dBenefitTextView.setText(gourmet.dBenefitText);
        //        } else
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        //        }
        //
        //        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        //        {
        //            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE || holder.dataBinding.trueVRView.getVisibility() == View.VISIBLE)
        //            {
        //                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
        //            } else
        //            {
        //                holder.dataBinding.dot1View.setVisibility(View.GONE);
        //            }
        //
        //            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(gourmet.distance)));
        //        } else
        //        {
        //            holder.dataBinding.dot1View.setVisibility(View.GONE);
        //            holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        }
        //
        //        // VR 여부
        //        //        if (gourmet.truevr == true && mTrueVREnabled == true)
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
}
