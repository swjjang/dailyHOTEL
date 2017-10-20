package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFooterDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowLoadingDataBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayOutboundListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    private List<ObjectItem> mList;

    View.OnClickListener mOnClickListener;
    View.OnLongClickListener mOnLongClickListener;

    private boolean mDistanceEnabled;
    private boolean mNightsEnabled; // 연박 여부
    private boolean mRewardEnabled;

    public StayOutboundListAdapter(Context context, ArrayList<ObjectItem> arrayList)
    {
        mContext = context;

        mList = new ArrayList<>();

        addAll(arrayList);
    }

    public void setDistanceEnabled(boolean enabled)
    {
        mDistanceEnabled = enabled;
    }

    public void setNightsEnabled(boolean enabled)
    {
        mNightsEnabled = enabled;
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public void setOnClickListener(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener)
    {
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    public void clear()
    {
        mList.clear();
    }

    public void add(ObjectItem objectItem)
    {
        mList.add(objectItem);
    }

    public void add(int position, ObjectItem placeViewItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends ObjectItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(Collection<? extends ObjectItem> collection)
    {
        clear();
        addAll(collection);
    }

    public void remove(int position)
    {
        if (mList == null || mList.size() <= position)
        {
            return;
        }

        mList.remove(position);
    }

    public ObjectItem getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
    }

    @Override
    public int getItemViewType(int position)
    {
        return mList.get(position).mType;
    }

    @Override
    public int getItemCount()
    {
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ObjectItem.TYPE_ENTRY:
            {
                DailyStayOutboundCardView stayOutboundCardView = new DailyStayOutboundCardView(mContext);
                stayOutboundCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayViewHolder(stayOutboundCardView);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                LayoutFooterDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_footer_data, parent, false);

                return new BaseDataBindingViewHolder(viewDataBinding);
            }

            case ObjectItem.TYPE_LOADING_VIEW:
            {
                ListRowLoadingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_loading_data, parent, false);

                return new BaseDataBindingViewHolder(dataBinding);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ObjectItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case ObjectItem.TYPE_ENTRY:
                onBindViewHolder((StayViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayViewHolder holder, ObjectItem objectItem)
    {
        if (holder == null || objectItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = objectItem.getItem();

        holder.stayOutboundCardView.setStickerVisible(mRewardEnabled && stayOutbound.dailyReward);
        holder.stayOutboundCardView.setDeleteVisible(false);
        holder.stayOutboundCardView.setWishVisible(false);

        holder.stayOutboundCardView.setImage(stayOutbound.getImageMap());

        holder.stayOutboundCardView.setGradeText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));
        holder.stayOutboundCardView.setVRVisible(false);
        holder.stayOutboundCardView.setRatingText(stayOutbound.tripAdvisorRating);

        holder.stayOutboundCardView.setNewVisible(false);

        holder.stayOutboundCardView.setStayNameText(stayOutbound.name, stayOutbound.nameEng);

        if (mDistanceEnabled == true)
        {
            holder.stayOutboundCardView.setDistanceVisible(true);
            holder.stayOutboundCardView.setDistanceText(stayOutbound.distance);
        } else
        {
            holder.stayOutboundCardView.setDistanceVisible(false);
        }

        holder.stayOutboundCardView.setAddressText(stayOutbound.locationDescription);
        holder.stayOutboundCardView.setPriceText(stayOutbound.discountRate, stayOutbound.nightlyRate, stayOutbound.nightlyBaseRate, null, mNightsEnabled);

        holder.stayOutboundCardView.setBenefitText(null);
        holder.stayOutboundCardView.setDividerVisible(true);


        //        holder.dataBinding.addressTextView.setText(stayOutbound.locationDescription);
        //        holder.dataBinding.nameTextView.setText(stayOutbound.name);
        //        holder.dataBinding.nameEngTextView.setText("(" + stayOutbound.nameEng + ")");

        //        // 가격
        //        if (stayOutbound.promo == true)
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyBaseRate, false));
        //            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //        } else
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //        }
        //
        //        holder.dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyRate, false));
        //
        //        if (mNightsEnabled == true)
        //        {
        //            holder.dataBinding.averageTextView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.averageTextView.setVisibility(View.GONE);
        //        }
        //
        //        if (VersionUtils.isOverAPI16() == true)
        //        {
        //            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        //        } else
        //        {
        //            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        //        }

        // grade
        //        holder.dataBinding.gradeTextView.setText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));

        // 별등급
        //        holder.dataBinding.ratingBar.setOnTouchListener(new View.OnTouchListener()
        //        {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event)
        //            {
        //                return true;
        //            }
        //        });
        //        holder.dataBinding.ratingBar.setRating(stayOutbound.rating);

        //        if (stayOutbound.tripAdvisorRating == 0.0f && mDistanceEnabled == false)
        //        {
        //            holder.dataBinding.tripAdvisorLayout.setVisibility(View.GONE);
        //
        //            ConstraintLayout.LayoutParams nameEngLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameEngTextView.getLayoutParams();
        //            nameEngLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 6);
        //
        //            ConstraintLayout.LayoutParams nameLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameTextView.getLayoutParams();
        //            nameLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, -4);
        //        } else
        //        {
        //            ConstraintLayout.LayoutParams nameEngLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameEngTextView.getLayoutParams();
        //            nameEngLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 4);
        //
        //            ConstraintLayout.LayoutParams nameLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameTextView.getLayoutParams();
        //            nameLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, -2);
        //
        //            holder.dataBinding.tripAdvisorLayout.setVisibility(View.VISIBLE);
        //        }

        //        // tripAdvisor
        //        if (stayOutbound.tripAdvisorRating == 0.0f)
        //        {
        //            holder.dataBinding.tripAdvisorImageView.setVisibility(View.GONE);
        //            holder.dataBinding.tripAdvisorRatingBar.setVisibility(View.GONE);
        //            holder.dataBinding.tripAdvisorRatingTextView.setVisibility(View.GONE);
        //        } else
        //        {
        //            holder.dataBinding.tripAdvisorImageView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.tripAdvisorRatingBar.setVisibility(View.VISIBLE);
        //            holder.dataBinding.tripAdvisorRatingTextView.setVisibility(View.VISIBLE);
        //
        //            holder.dataBinding.tripAdvisorRatingBar.setOnTouchListener(new View.OnTouchListener()
        //            {
        //                @Override
        //                public boolean onTouch(View v, MotionEvent event)
        //                {
        //                    return true;
        //                }
        //            });
        //            holder.dataBinding.tripAdvisorRatingBar.setRating(stayOutbound.tripAdvisorRating);
        //            holder.dataBinding.tripAdvisorRatingTextView.setText(mContext.getString(R.string.label_stay_outbound_tripadvisor_rating, Float.toString(stayOutbound.tripAdvisorRating)));
        //
        //            // 별등급이 기본이 5개 이기 때문에 빈공간에도 내용이 존재한다.
        //            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.dataBinding.tripAdvisorRatingTextView.getLayoutParams();
        //            layoutParams.leftMargin = ScreenUtils.dpToPx(mContext, 3) - ScreenUtils.dpToPx(mContext, (5 - (int) Math.ceil(stayOutbound.tripAdvisorRating)) * 10);
        //            holder.dataBinding.tripAdvisorRatingTextView.setLayoutParams(layoutParams);
        //        }

        // Image
        //        holder.dataBinding.imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        //
        //        ImageMap imageMap = stayOutbound.getImageMap();
        //        String url;
        //
        //        if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        //        {
        //            if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
        //            {
        //                url = imageMap.smallUrl;
        //            } else
        //            {
        //                url = imageMap.bigUrl;
        //            }
        //        } else
        //        {
        //            if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
        //            {
        //                url = imageMap.smallUrl;
        //            } else
        //            {
        //                url = imageMap.mediumUrl;
        //            }
        //        }
        //
        //        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        //        {
        //            @Override
        //            public void onFailure(String id, Throwable throwable)
        //            {
        //                if (throwable instanceof IOException == true)
        //                {
        //                    if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
        //                    {
        //                        imageMap.bigUrl = null;
        //                    } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
        //                    {
        //                        imageMap.mediumUrl = null;
        //                    } else
        //                    {
        //                        // 작은 이미지를 로딩했지만 실패하는 경우.
        //                        return;
        //                    }
        //
        //                    holder.dataBinding.imageView.setImageURI(imageMap.smallUrl);
        //                }
        //            }
        //        };
        //
        //        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
        //            .setControllerListener(controllerListener).setUri(url).build();
        //
        //        holder.dataBinding.imageView.setController(draweeController);

        // Promo 설명은 사용하지 않는다.
        //        holder.dataBinding.promoTextView.setVisibility(View.GONE);
        //
        //        if (mDistanceEnabled == true)
        //        {
        //            if (holder.dataBinding.tripAdvisorImageView.getVisibility() == View.VISIBLE)
        //            {
        //                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
        //            } else
        //            {
        //                holder.dataBinding.dot1View.setVisibility(View.GONE);
        //            }
        //
        //            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(stayOutbound.distance)));
        //        } else
        //        {
        //            holder.dataBinding.dot1View.setVisibility(View.GONE);
        //            holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        }
    }

    private class StayViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayOutboundCardView stayOutboundCardView;

        public StayViewHolder(DailyStayOutboundCardView stayOutboundCardView)
        {
            super(stayOutboundCardView);

            this.stayOutboundCardView = stayOutboundCardView;

            itemView.setOnClickListener(mOnClickListener);
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

    private class BaseDataBindingViewHolder extends RecyclerView.ViewHolder
    {
        public BaseDataBindingViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
        }
    }
}
