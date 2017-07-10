package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentGourmetListAdapter extends RecentPlacesListAdapter
{
    public RecentGourmetListAdapter(Context context, ArrayList<PlaceViewItem> list, OnRecentPlacesItemListener listener)
    {
        super(context, list, listener);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
            {
                View view = mInflater.inflate(R.layout.list_row_gourmet_data, parent, false);

                return new GourmetViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_users_place_footer, parent, false);
                return new FooterViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        PlaceViewItem item = getItem(position);
        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((GourmetViewHolder) viewHolder, item, position);
                break;

            case PlaceViewItem.TYPE_FOOTER_VIEW:
                break;
        }
    }

    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Gourmet gourmet = placeViewItem.getItem();

        String strPrice = DailyTextUtils.getPriceFormat(mContext, gourmet.price, false);
        String strDiscount = DailyTextUtils.getPriceFormat(mContext, gourmet.discountPrice, false);

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
            holder.satisfactionView.setText(//
                mContext.getResources().getString(R.string.label_list_satisfaction, gourmet.satisfaction));
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
        if (DailyTextUtils.isTextEmpty(gourmet.subCategory) == false)
        {
            displayCategory = gourmet.subCategory;
        } else
        {
            displayCategory = gourmet.category;
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

        // 스티커
        if (DailyTextUtils.isTextEmpty(gourmet.stickerUrl) == false)
        {
            holder.stickerSimpleDraweeView.setVisibility(View.VISIBLE);

            DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
            {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
                {
                    ViewGroup.LayoutParams layoutParams = holder.stickerSimpleDraweeView.getLayoutParams();

                    int screenWidth = ScreenUtils.getScreenWidth(mContext);
                    if (screenWidth > Sticker.DEFAULT_SCREEN_WIDTH && screenWidth < Sticker.LARGE_SCREEN_WIDTH)
                    {
                        layoutParams.width = (int) (Sticker.MEDIUM_RATE * imageInfo.getWidth());
                        layoutParams.height = (int) (Sticker.MEDIUM_RATE * imageInfo.getHeight());
                    } else
                    {
                        layoutParams.width = imageInfo.getWidth();
                        layoutParams.height = imageInfo.getHeight();
                    }

                    holder.stickerSimpleDraweeView.setLayoutParams(layoutParams);
                }
            }).setUri(Uri.parse(gourmet.stickerUrl)).build();

            holder.stickerSimpleDraweeView.setController(controller);
        } else
        {
            holder.stickerSimpleDraweeView.setVisibility(View.GONE);
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

        if (DailyTextUtils.isTextEmpty(gourmet.dBenefitText) == false)
        {
            holder.dBenefitTextView.setVisibility(View.VISIBLE);
            holder.dBenefitTextView.setText(gourmet.dBenefitText);
        } else
        {
            holder.dBenefitTextView.setVisibility(View.GONE);
        }

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            if (holder.satisfactionView.getVisibility() == View.VISIBLE || holder.trueVRView.getVisibility() == View.VISIBLE)
            {
                holder.dot1View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dot1View.setVisibility(View.GONE);
            }

            holder.distanceTextView.setVisibility(View.VISIBLE);
            holder.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(gourmet.distance)));
        } else
        {
            holder.dot1View.setVisibility(View.GONE);
            holder.distanceTextView.setVisibility(View.GONE);
        }

        // VR 여부
        //        if (gourmet.truevr == true && mTrueVREnabled == true)
        //        {
        //            if (holder.satisfactionView.getVisibility() == View.VISIBLE)
        //            {
        //                holder.dot2View.setVisibility(View.VISIBLE);
        //            } else
        //            {
        //                holder.dot2View.setVisibility(View.GONE);
        //            }
        //
        //            holder.trueVRView.setVisibility(View.VISIBLE);
        //        } else
        {
            holder.dot2View.setVisibility(View.GONE);
            holder.trueVRView.setVisibility(View.GONE);
        }

        holder.deleteView.setVisibility(View.VISIBLE);
        holder.deleteView.setTag(position);
        holder.deleteView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    mListener.onDeleteClick(v, (Integer) v.getTag());
                }
            }
        });

        if (holder.satisfactionView.getVisibility() == View.GONE//
            && holder.trueVRView.getVisibility() == View.GONE//
            && holder.distanceTextView.getVisibility() == View.GONE)
        {
            holder.informationLayout.setVisibility(View.GONE);
        } else
        {
            holder.informationLayout.setVisibility(View.VISIBLE);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        View gradientView;
        com.facebook.drawee.view.SimpleDraweeView gourmetImageView;
        com.facebook.drawee.view.SimpleDraweeView stickerSimpleDraweeView;
        TextView nameView;
        TextView priceView;
        TextView discountView;
        View soldOutView;
        TextView addressView;
        TextView gradeView;
        TextView satisfactionView;
        TextView personsTextView;
        TextView distanceTextView;
        TextView dBenefitTextView;
        View deleteView;
        View informationLayout;
        View trueVRView;
        View dot1View;
        View dot2View;


        public GourmetViewHolder(View itemView)
        {
            super(itemView);

            dBenefitTextView = (TextView) itemView.findViewById(R.id.dBenefitTextView);
            gradientView = itemView.findViewById(R.id.gradientView);
            gourmetImageView = (com.facebook.drawee.view.SimpleDraweeView) itemView.findViewById(R.id.imageView);
            stickerSimpleDraweeView = (com.facebook.drawee.view.SimpleDraweeView) itemView.findViewById(R.id.stickerSimpleDraweeView);
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
            informationLayout = itemView.findViewById(R.id.informationLayout);
            trueVRView = itemView.findViewById(R.id.trueVRView);
            dot1View = itemView.findViewById(R.id.dot1View);
            dot2View = itemView.findViewById(R.id.dot2View);

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

            if (Util.supportPreview(mContext) == true)
            {
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (mListener == null)
                        {
                            return false;
                        } else
                        {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(70);

                            mListener.onItemLongClick(v);
                            return true;
                        }
                    }
                });
            }
        }
    }
}
