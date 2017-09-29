package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewEmptyCampaignTagListBinding;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 8..
 */

public class GourmetCampaignListAdapter extends PlaceListAdapter
{
    OnEventListener mOnEventListener;

    public interface OnEventListener
    {
        void onItemClick(View view);

        void onEmptyChangeDateClick();

        void onEmptyResearchClick();

        void onEmptyCallClick();
    }

    public GourmetCampaignListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, OnEventListener listener)
    {
        super(context, arrayList);

        mOnEventListener = listener;

        setSortType(Constants.SortType.DEFAULT);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {
        //        if (placeBookingDay == null)
        //        {
        //            return;
        //        }
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
                DailyGourmetCardView gourmetCardView = new DailyGourmetCardView(mContext);
                gourmetCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new GourmetViewHolder(gourmetCardView);
            }

            case PlaceViewItem.TYPE_HEADER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_collection_header, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) //
                    + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new GourmetCampaignListAdapter.HeaderViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                ViewEmptyCampaignTagListBinding dataBinding = DataBindingUtil.inflate(mInflater, R.layout.view_empty_campaign_tag_list, parent, false);
                return new EmptyViewHolder(dataBinding);
            }

            case PlaceViewItem.TYPE_FOOTER_GUIDE_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_footer, parent, false);
                return new GourmetCampaignListAdapter.FooterGuideViewHolder(view);
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
                onBindViewHolder((EmptyViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Gourmet gourmet = placeViewItem.getItem();

        holder.gourmetCardView.setStickerVisible(false);
        holder.gourmetCardView.setDeleteVisible(false);
        holder.gourmetCardView.setWishVisible(false);

        holder.gourmetCardView.setTagStickerImage(gourmet.stickerUrl);
        holder.gourmetCardView.setImage(gourmet.imageUrl);

        holder.gourmetCardView.setGradeText(DailyTextUtils.isTextEmpty(gourmet.subCategory) == false ? gourmet.subCategory : gourmet.category);
        holder.gourmetCardView.setVRVisible(false);
        holder.gourmetCardView.setReviewText(gourmet.satisfaction, 0);

        holder.gourmetCardView.setNewVisible(false);

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
            if (gourmet.price > 0 && gourmet.price > gourmet.discountPrice)
            {
                holder.gourmetCardView.setPriceText(gourmet.price > 0 ? 100 * (gourmet.price - gourmet.discountPrice) / gourmet.price : 0, gourmet.discountPrice, gourmet.price, null, gourmet.persons);
            } else
            {
                holder.gourmetCardView.setPriceText(0, gourmet.discountPrice, gourmet.price, null, gourmet.persons);
            }
        }

        holder.gourmetCardView.setBenefitText(gourmet.dBenefitText);

        if (position < getItemCount() - 1 && getItem(position + 1).mType == PlaceViewItem.TYPE_SECTION)
        {
            holder.gourmetCardView.setDividerVisible(false);
        } else
        {
            holder.gourmetCardView.setDividerVisible(true);
        }


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

    private void onBindViewHolder(EmptyViewHolder holder, PlaceViewItem placeViewItem)
    {
        holder.dataBinding.changeDateView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onEmptyChangeDateClick();
            }
        });

        holder.dataBinding.researchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onEmptyResearchClick();
            }
        });

        holder.dataBinding.callTextView.setPaintFlags(holder.dataBinding.callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.dataBinding.callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onEmptyCallClick();
            }
        });
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

    private class EmptyViewHolder extends RecyclerView.ViewHolder
    {
        ViewEmptyCampaignTagListBinding dataBinding;

        public EmptyViewHolder(ViewEmptyCampaignTagListBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    private class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        DailyGourmetCardView gourmetCardView;

        public GourmetViewHolder(DailyGourmetCardView gourmetCardView)
        {
            super(gourmetCardView);

            this.gourmetCardView = gourmetCardView;

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnEventListener == null)
                    {
                        return;
                    }

                    mOnEventListener.onItemClick(v);
                }
            });

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
