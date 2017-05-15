package com.daily.dailyhotel.screen.stay.outbound.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowFooterDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowLoadingDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowStayOutboundDataBinding;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Util;

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

    public ListItem getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
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
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.60f};

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

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)));
                dataBinding.getRoot().setLayoutParams(layoutParams);

                StayViewHolder stayViewHolder = new StayViewHolder(dataBinding.getRoot());
                stayViewHolder.setViewDataBinding(dataBinding);

                return stayViewHolder;
            }

            case ListItem.TYPE_FOOTER_VIEW:
            {
                ListRowFooterDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_footer_data, parent, false);

                BlankViewHolder blankViewHolder = new BlankViewHolder(dataBinding.getRoot());

                return blankViewHolder;
            }

            case ListItem.TYPE_LOADING_VIEW:
            {
                ListRowLoadingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_loading_data, parent, false);

                BlankViewHolder blankViewHolder = new BlankViewHolder(dataBinding.getRoot());

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

    private void onBindViewHolder(StayViewHolder holder, ListItem listItem)
    {
        if (holder == null || listItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = listItem.getItem();

        holder.dataBinding.addressTextView.setText(stayOutbound.locationDescription);
        holder.dataBinding.nameTextView.setText(stayOutbound.name);

        // 가격
        if (stayOutbound.promo == true)
        {
            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyBaseRateKrw, false));
            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            holder.dataBinding.priceTextView.setText(null);
        }

        holder.dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyRateKrw, false));

        // 만족도
        holder.dataBinding.satisfactionView.setVisibility(View.GONE);

        // 1박인 경우 전체가격과 1박가격이 같다.
        if (stayOutbound.nightlyRateKrw == stayOutbound.totalKrw)
        {
            holder.dataBinding.averageTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.averageTextView.setVisibility(View.VISIBLE);
        }

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        holder.dataBinding.gradeTextView.setText(Integer.toString(stayOutbound.rating));
        holder.dataBinding.gradeTextView.setBackgroundResource(Stay.Grade.special.getColorResId());

        if(ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            Util.requestImageResize(mContext, holder.dataBinding.imageView, stayOutbound.xxhdpiImageUrl);
        } else
        {
            Util.requestImageResize(mContext, holder.dataBinding.imageView, stayOutbound.hdpiImageUrl);
        }

        if (DailyTextUtils.isTextEmpty(stayOutbound.promoDescription) == false)
        {
            holder.dataBinding.dBenefitLayout.setVisibility(View.VISIBLE);
            holder.dataBinding.dBenefitTextView.setText(stayOutbound.promoDescription);
        } else
        {
            holder.dataBinding.dBenefitLayout.setVisibility(View.GONE);
        }

        if (mDistanceEnabled == true)
        {
            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE || holder.dataBinding.trueVRView.getVisibility() == View.VISIBLE)
            {
                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dataBinding.dot1View.setVisibility(View.GONE);
            }

            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.distanceTextView.setText("거리:" + new DecimalFormat("#.#").format(stayOutbound.distance) + "km");
        } else
        {
            holder.dataBinding.dot1View.setVisibility(View.GONE);
            holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        }

        // VR 여부
        holder.dataBinding.dot2View.setVisibility(View.GONE);
        holder.dataBinding.trueVRView.setVisibility(View.GONE);

        if (holder.dataBinding.satisfactionView.getVisibility() == View.GONE//
            && holder.dataBinding.trueVRView.getVisibility() == View.GONE//
            && holder.dataBinding.distanceTextView.getVisibility() == View.GONE)
        {
            holder.dataBinding.informationLayout.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.informationLayout.setVisibility(View.VISIBLE);
        }
    }

    private class StayViewHolder extends RecyclerView.ViewHolder
    {
        ListRowStayOutboundDataBinding dataBinding;

        public StayViewHolder(View itemView)
        {
            super(itemView);

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

        public void setViewDataBinding(ListRowStayOutboundDataBinding dataBinding)
        {
            this.dataBinding = dataBinding;
        }
    }

    protected class BlankViewHolder extends RecyclerView.ViewHolder
    {
        public BlankViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
