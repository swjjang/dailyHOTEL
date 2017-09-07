package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowFooterDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowLoadingDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowStayOutboundDataBinding;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayOutboundListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private List<ListItem> mList;

    private PaintDrawable mPaintDrawable;

    View.OnClickListener mOnClickListener;
    View.OnLongClickListener mOnLongClickListener;

    private boolean mDistanceEnabled;
    private boolean mNightsEnabled; // 연박 여부

    public StayOutboundListAdapter(Context context, ArrayList<ListItem> arrayList)
    {
        mContext = context;

        mList = new ArrayList<>();

        addAll(arrayList);

        makeShaderFactory();
    }

    public void setDistanceEnabled(boolean enabled)
    {
        mDistanceEnabled = enabled;
    }

    public void setNightsEnabled(boolean enabled)
    {
        mNightsEnabled = enabled;
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

    public void add(ListItem listItem)
    {
        mList.add(listItem);
    }

    public void add(int position, ListItem placeViewItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends ListItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(Collection<? extends ListItem> collection)
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

    public ListItem getItem(int position)
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

    private void makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#E6000000"), Color.parseColor("#99000000"), Color.parseColor("#1A000000"), Color.parseColor("#00000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.24f, 0.66f, 0.8f, 1.0f};

        mPaintDrawable = new PaintDrawable();
        mPaintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        mPaintDrawable.setShaderFactory(sf);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ListItem.TYPE_ENTRY:
            {
                ListRowStayOutboundDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_stay_outbound_data, parent, false);

                StayViewHolder stayViewHolder = new StayViewHolder(dataBinding);

                return stayViewHolder;
            }

            case ListItem.TYPE_FOOTER_VIEW:
            {
                ListRowFooterDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_footer_data, parent, false);

                BlankViewHolder blankViewHolder = new BlankViewHolder(dataBinding);

                return blankViewHolder;
            }

            case ListItem.TYPE_LOADING_VIEW:
            {
                ListRowLoadingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_loading_data, parent, false);

                BlankViewHolder blankViewHolder = new BlankViewHolder(dataBinding);

                return blankViewHolder;
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ListItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case ListItem.TYPE_ENTRY:
                onBindViewHolder((StayViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayViewHolder holder, ListItem listItem)
    {
        if (holder == null || listItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = listItem.getItem();

        holder.dataBinding.addressTextView.setText(stayOutbound.locationDescription);
        holder.dataBinding.nameTextView.setText(stayOutbound.name);
        holder.dataBinding.nameEngTextView.setText("(" + stayOutbound.nameEng + ")");

        // 가격
        if (stayOutbound.promo == true)
        {
            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyBaseRate, false));
            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            holder.dataBinding.priceTextView.setText(null);
        }

        holder.dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyRate, false));

        if (mNightsEnabled == true)
        {
            holder.dataBinding.averageTextView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.averageTextView.setVisibility(View.GONE);
        }

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        holder.dataBinding.gradeTextView.setText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));

        // 별등급
        holder.dataBinding.ratingBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        holder.dataBinding.ratingBar.setRating(stayOutbound.rating);

        if (stayOutbound.tripAdvisorRating == 0.0f && mDistanceEnabled == false)
        {
            holder.dataBinding.tripAdvisorLayout.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams nameEngLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameEngTextView.getLayoutParams();
            nameEngLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 6);

            ConstraintLayout.LayoutParams nameLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameTextView.getLayoutParams();
            nameLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, -4);
        } else
        {
            ConstraintLayout.LayoutParams nameEngLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameEngTextView.getLayoutParams();
            nameEngLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 4);

            ConstraintLayout.LayoutParams nameLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameTextView.getLayoutParams();
            nameLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, -2);

            holder.dataBinding.tripAdvisorLayout.setVisibility(View.VISIBLE);
        }

        // tripAdvisor
        if (stayOutbound.tripAdvisorRating == 0.0f)
        {
            holder.dataBinding.tripAdvisorImageView.setVisibility(View.GONE);
            holder.dataBinding.tripAdvisorRatingBar.setVisibility(View.GONE);
            holder.dataBinding.tripAdvisorRatingTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.tripAdvisorImageView.setVisibility(View.VISIBLE);
            holder.dataBinding.tripAdvisorRatingBar.setVisibility(View.VISIBLE);
            holder.dataBinding.tripAdvisorRatingTextView.setVisibility(View.VISIBLE);

            holder.dataBinding.tripAdvisorRatingBar.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return true;
                }
            });
            holder.dataBinding.tripAdvisorRatingBar.setRating(stayOutbound.tripAdvisorRating);
            holder.dataBinding.tripAdvisorRatingTextView.setText(mContext.getString(R.string.label_stay_outbound_tripadvisor_rating, Float.toString(stayOutbound.tripAdvisorRating)));

            // 별등급이 기본이 5개 이기 때문에 빈공간에도 내용이 존재한다.
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.dataBinding.tripAdvisorRatingTextView.getLayoutParams();
            layoutParams.leftMargin = ScreenUtils.dpToPx(mContext, 3) - ScreenUtils.dpToPx(mContext, (5 - (int) Math.ceil(stayOutbound.tripAdvisorRating)) * 10);
            holder.dataBinding.tripAdvisorRatingTextView.setLayoutParams(layoutParams);
        }

        // Image
        holder.dataBinding.imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        ImageMap imageMap = stayOutbound.getImageMap();
        String url;

        if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
            {
                url = imageMap.smallUrl;
            } else
            {
                url = imageMap.bigUrl;
            }
        } else
        {
            if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
            {
                url = imageMap.smallUrl;
            } else
            {
                url = imageMap.mediumUrl;
            }
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFailure(String id, Throwable throwable)
            {
                if (throwable instanceof IOException == true)
                {
                    if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
                    {
                        imageMap.bigUrl = null;
                    } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
                    {
                        imageMap.mediumUrl = null;
                    } else
                    {
                        // 작은 이미지를 로딩했지만 실패하는 경우.
                        return;
                    }

                    holder.dataBinding.imageView.setImageURI(imageMap.smallUrl);
                }
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
            .setControllerListener(controllerListener).setUri(url).build();

        holder.dataBinding.imageView.setController(draweeController);

        // Promo 설명은 사용하지 않는다.
        holder.dataBinding.promoTextView.setVisibility(View.GONE);

        if (mDistanceEnabled == true)
        {
            if (holder.dataBinding.tripAdvisorImageView.getVisibility() == View.VISIBLE)
            {
                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dataBinding.dot1View.setVisibility(View.GONE);
            }

            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(stayOutbound.distance)));
        } else
        {
            holder.dataBinding.dot1View.setVisibility(View.GONE);
            holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        }
    }

    private class StayViewHolder extends RecyclerView.ViewHolder
    {
        ListRowStayOutboundDataBinding dataBinding;

        public StayViewHolder(ListRowStayOutboundDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

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

    protected class BlankViewHolder extends RecyclerView.ViewHolder
    {
        public BlankViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
        }
    }
}
