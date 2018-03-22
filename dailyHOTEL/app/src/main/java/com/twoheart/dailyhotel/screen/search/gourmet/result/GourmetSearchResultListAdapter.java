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

@Deprecated
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
    }
}
