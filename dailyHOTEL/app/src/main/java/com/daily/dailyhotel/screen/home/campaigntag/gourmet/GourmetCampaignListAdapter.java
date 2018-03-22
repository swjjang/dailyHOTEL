package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Paint;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFooterDataBinding;
import com.twoheart.dailyhotel.databinding.ViewEmptyCampaignTagListBinding;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 8. 8..
 */

@Deprecated
public class GourmetCampaignListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;

    View.OnLongClickListener mOnLongClickListener;
    View.OnClickListener mOnWishClickListener;

    private List<PlaceViewItem> mPlaceViewItemList;

    OnEventListener mOnEventListener;
    private boolean mTrueVREnabled;

    public interface OnEventListener
    {
        void onItemClick(View view);

        void onEmptyChangeDateClick();

        void onEmptyResearchClick();

        void onEmptyCallClick();
    }

    public GourmetCampaignListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, OnEventListener listener)
    {
        mContext = context;
        mPlaceViewItemList = new ArrayList<>();
        mOnEventListener = listener;

        addAll(arrayList);
    }

    public void setOnLongClickListener(View.OnLongClickListener listener)
    {
        mOnLongClickListener = listener;
    }

    public void setOnWishClickListener(View.OnClickListener listener)
    {
        mOnWishClickListener = listener;
    }

    public void setTrueVREnabled(boolean enabled)
    {
        mTrueVREnabled = enabled;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
            {
                DailyGourmetCardView gourmetCardView = new DailyGourmetCardView(mContext);
                gourmetCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new GourmetViewHolder(gourmetCardView);
            }

            case PlaceViewItem.TYPE_EMPTY_VIEW:
            {
                ViewEmptyCampaignTagListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.view_empty_campaign_tag_list, parent, false);
                dataBinding.emptyIconImageView.setImageResource(R.drawable.no_gourmet_ic);

                return new EmptyViewHolder(dataBinding);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                LayoutFooterDataBinding viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.layout_footer_data, parent, false);

                return new BaseDataBindingViewHolder(viewDataBinding);
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

            case PlaceViewItem.TYPE_EMPTY_VIEW:
                onBindViewHolder((EmptyViewHolder) holder, item);
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return mPlaceViewItemList.get(position).mType;
    }

    @Override
    public int getItemCount()
    {
        if (mPlaceViewItemList == null)
        {
            return 0;
        }

        return mPlaceViewItemList.size();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem, int position)
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
        holder.gourmetCardView.setDistanceVisible(false);
        holder.gourmetCardView.setAddressText(gourmet.addressSummary);

        if (gourmet.availableTicketNumbers == 0 || gourmet.availableTicketNumbers < gourmet.minimumOrderQuantity || gourmet.expired == true)
        {
            holder.gourmetCardView.setPriceText(0, 0, 0, null, 0);
        } else
        {
            holder.gourmetCardView.setPriceText(gourmet.discountRate, gourmet.discountPrice, gourmet.price, gourmet.couponDiscountText, gourmet.persons);
        }

        holder.gourmetCardView.setBenefitText(gourmet.dBenefitText);

        // 캠페인 태그는 섹션이 없음
        if (position == 0)
        {
            holder.gourmetCardView.setDividerVisible(false);
        } else
        {
            holder.gourmetCardView.setDividerVisible(true);
        }
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

    public void clear()
    {
        mPlaceViewItemList.clear();
    }

    public void addAll(List<PlaceViewItem> placeViewItemList)
    {
        if (placeViewItemList == null)
        {
            return;
        }

        mPlaceViewItemList.addAll(placeViewItemList);
    }

    public void setAll(List<PlaceViewItem> placeViewItemList)
    {
        clear();
        addAll(placeViewItemList);
    }

    public PlaceViewItem getItem(int position)
    {
        if (position < 0 || mPlaceViewItemList.size() <= position)
        {
            return null;
        }

        return mPlaceViewItemList.get(position);
    }

    private class BaseDataBindingViewHolder extends RecyclerView.ViewHolder
    {
        public BaseDataBindingViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
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

    class GourmetViewHolder extends RecyclerView.ViewHolder
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
