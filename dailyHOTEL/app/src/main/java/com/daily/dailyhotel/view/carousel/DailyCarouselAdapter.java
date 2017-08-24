package com.daily.dailyhotel.view.carousel;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.facebook.drawee.drawable.ScalingUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowCarouselItemDataBinding;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 24..
 */

public class DailyCarouselAdapter extends RecyclerView.Adapter<DailyCarouselAdapter.PlaceViewHolder>
{
    private Context mContext;
    private ArrayList<CarouselListItem> mList;
    protected PaintDrawable mPaintDrawable;
    protected ItemClickListener mItemClickListener;

    public interface ItemClickListener
    {
        void onItemClick(View view);

        void onItemLongClick(View view);
    }

    public DailyCarouselAdapter(Context context, ArrayList<CarouselListItem> list, ItemClickListener listener)
    {
        mContext = context;
        mList = list;
        mItemClickListener = listener;

        makeShaderFactory();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ListRowCarouselItemDataBinding dataBinding = DataBindingUtil.inflate( //
            LayoutInflater.from(mContext), R.layout.list_row_carousel_item_data, parent, false);
        return new PlaceViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position)
    {
        CarouselListItem item = getItem(position);
        if (item == null)
        {
            return;
        }

        setLayoutMargin(holder, position);

        holder.itemView.setTag(item);

        switch (item.mType)
        {
            case CarouselListItem.TYPE_HOMEPLACE:
            {
                onBindViewHolderByHomePlace(holder, item);
                break;
            }

            case CarouselListItem.TYPE_GOURMET:
            {
                onBindViewHolderByGourmet(holder, item);
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolderByHomePlace(PlaceViewHolder holder, CarouselListItem item)
    {
        final HomePlace place = item.getItem();

        holder.dataBinding.contentImageView.setTag(holder.dataBinding.contentImageView.getId(), item);
        Util.requestImageResize(mContext, holder.dataBinding.contentImageView, place.imageUrl);

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.dataBinding.gradientBottomView.setBackground(mPaintDrawable);
        } else
        {
            holder.dataBinding.gradientBottomView.setBackgroundDrawable(mPaintDrawable);
        }

        //        // SOLD OUT 표시
        //        if (place.isSoldOut == true)
        //        {
        //            holder.dataBinding.soldOutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.soldOutView.setVisibility(View.GONE);
        //        }

        holder.dataBinding.contentTextView.setText(place.title);

        Prices prices = place.prices;

        if (prices == null || prices.discountPrice == 0)
        {
            holder.dataBinding.priceLayout.setVisibility(View.GONE);
            holder.dataBinding.contentOriginPriceView.setText("");
            holder.dataBinding.contentDiscountPriceView.setText("");
            holder.dataBinding.contentPersonView.setText("");
        } else
        {
            holder.dataBinding.priceLayout.setVisibility(View.VISIBLE);

            String strPrice = DailyTextUtils.getPriceFormat(mContext, prices.normalPrice, false);
            String strDiscount = DailyTextUtils.getPriceFormat(mContext, prices.discountPrice, false);

            holder.dataBinding.contentDiscountPriceView.setText(strDiscount);

            if (prices.normalPrice <= 0 || prices.normalPrice <= prices.discountPrice)
            {
                holder.dataBinding.contentOriginPriceView.setText("");
            } else
            {
                holder.dataBinding.contentOriginPriceView.setText(strPrice);
                holder.dataBinding.contentOriginPriceView.setPaintFlags(holder.dataBinding.contentOriginPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.dataBinding.contentProvinceView.setText(place.regionName);

        if (place.placeType == Constants.PlaceType.HOTEL)
        {
            holder.dataBinding.contentGradeView.setText(place.details.stayGrade.getName(mContext));
            holder.dataBinding.contentDotImageView.setVisibility(View.VISIBLE);

            holder.dataBinding.contentPersonView.setText("");
            holder.dataBinding.contentPersonView.setVisibility(View.GONE);
        } else if (place.placeType == Constants.PlaceType.FNB)
        {
            // grade
            if (DailyTextUtils.isTextEmpty(place.details.category) == true)
            {
                holder.dataBinding.contentGradeView.setVisibility(View.GONE);
                holder.dataBinding.contentDotImageView.setVisibility(View.GONE);
                holder.dataBinding.contentGradeView.setText("");
            } else
            {
                holder.dataBinding.contentGradeView.setVisibility(View.VISIBLE);
                holder.dataBinding.contentDotImageView.setVisibility(View.VISIBLE);
                holder.dataBinding.contentGradeView.setText(place.details.category);
            }

            if (prices != null && place.details.persons > 1)
            {
                holder.dataBinding.contentPersonView.setText(//
                    mContext.getString(R.string.label_home_person_format, place.details.persons));
                holder.dataBinding.contentPersonView.setVisibility(View.VISIBLE);
            } else
            {
                holder.dataBinding.contentPersonView.setText("");
                holder.dataBinding.contentPersonView.setVisibility(View.GONE);
            }
        } else
        {
            // Stay Outbound 의 경우 PlaceType 이 없음
            holder.dataBinding.contentGradeView.setText("");
            holder.dataBinding.contentDotImageView.setVisibility(View.GONE);

            holder.dataBinding.contentPersonView.setText("");
            holder.dataBinding.contentPersonView.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolderByGourmet(PlaceViewHolder holder, CarouselListItem item)
    {
        final Gourmet gourmet = item.getItem();

        holder.dataBinding.contentImageView.setTag(holder.dataBinding.contentImageView.getId(), item);
        Util.requestImageResize(mContext, holder.dataBinding.contentImageView, gourmet.imageUrl);

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.dataBinding.gradientBottomView.setBackground(mPaintDrawable);
        } else
        {
            holder.dataBinding.gradientBottomView.setBackgroundDrawable(mPaintDrawable);
        }

        //        // SOLD OUT 표시
        //        if (place.isSoldOut == true)
        //        {
        //            holder.dataBinding.soldOutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.soldOutView.setVisibility(View.GONE);
        //        }

        holder.dataBinding.contentTextView.setText(gourmet.name);

        int originPrice = gourmet.price;
        int discountPrice = gourmet.discountPrice;

        if (originPrice == 0 || discountPrice == 0)
        {
            holder.dataBinding.priceLayout.setVisibility(View.GONE);
            holder.dataBinding.contentOriginPriceView.setText("");
            holder.dataBinding.contentDiscountPriceView.setText("");
            holder.dataBinding.contentPersonView.setText("");
        } else
        {
            holder.dataBinding.priceLayout.setVisibility(View.VISIBLE);

            String strPrice = DailyTextUtils.getPriceFormat(mContext, originPrice, false);
            String strDiscount = DailyTextUtils.getPriceFormat(mContext, discountPrice, false);

            holder.dataBinding.contentDiscountPriceView.setText(strDiscount);

            if (originPrice <= 0 || originPrice <= discountPrice)
            {
                holder.dataBinding.contentOriginPriceView.setText("");
            } else
            {
                holder.dataBinding.contentOriginPriceView.setText(strPrice);
                holder.dataBinding.contentOriginPriceView.setPaintFlags(holder.dataBinding.contentOriginPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.dataBinding.contentProvinceView.setText(gourmet.regionName);

        // grade
        if (DailyTextUtils.isTextEmpty(gourmet.category) == true)
        {
            holder.dataBinding.contentGradeView.setVisibility(View.GONE);
            holder.dataBinding.contentDotImageView.setVisibility(View.GONE);
            holder.dataBinding.contentGradeView.setText("");
        } else
        {
            holder.dataBinding.contentGradeView.setVisibility(View.VISIBLE);
            holder.dataBinding.contentDotImageView.setVisibility(View.VISIBLE);
            holder.dataBinding.contentGradeView.setText(gourmet.category);
        }

        if (gourmet.persons > 1)
        {
            holder.dataBinding.contentPersonView.setText(//
                mContext.getString(R.string.label_home_person_format, gourmet.persons));
            holder.dataBinding.contentPersonView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dataBinding.contentPersonView.setText("");
            holder.dataBinding.contentPersonView.setVisibility(View.GONE);
        }
    }

    public CarouselListItem getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
    }

    public ArrayList<CarouselListItem> getData()
    {
        return mList;
    }

    public void setData(ArrayList<CarouselListItem> list)
    {
        mList = list;
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    private void setLayoutMargin(PlaceViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        int outSide = ScreenUtils.dpToPx(mContext, 15d);
        int inSide = ScreenUtils.dpToPx(mContext, 12d);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        params.leftMargin = position == 0 ? outSide : inSide;
        params.rightMargin = position == getItemCount() - 1 ? outSide : inSide;
        holder.itemView.setLayoutParams(params);
    }

    private void makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.38f};

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

    protected class PlaceViewHolder extends RecyclerView.ViewHolder
    {
        ListRowCarouselItemDataBinding dataBinding;

        public PlaceViewHolder(ListRowCarouselItemDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            dataBinding.contentImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            dataBinding.contentImageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mItemClickListener == null)
                    {
                        return;
                    }

                    mItemClickListener.onItemClick(v);
                }
            });

            if (Util.supportPreview(mContext) == true)
            {
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (mItemClickListener == null)
                        {
                            return false;
                        } else
                        {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(70);

                            mItemClickListener.onItemLongClick(v);
                            return true;
                        }
                    }
                });
            }
        }
    }
}
