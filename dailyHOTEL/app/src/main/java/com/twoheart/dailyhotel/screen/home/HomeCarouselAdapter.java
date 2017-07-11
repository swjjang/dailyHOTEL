package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 19..
 */

public class HomeCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    protected LayoutInflater mInflater;
    private ArrayList<HomePlace> mList;
    ItemClickListener mItemClickListener;
    protected PaintDrawable mPaintDrawable;

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public HomeCarouselAdapter(Context context, ArrayList<HomePlace> list, ItemClickListener listener)
    {
        mContext = context;
        mList = list;
        mItemClickListener = listener;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.list_row_home_carousel_item_layout, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        HomePlace place = getItem(position);
        if (place == null)
        {
            return;
        }

        onBindViewHolder((PlaceViewHolder) holder, place, position);
    }

    public void onBindViewHolder(PlaceViewHolder holder, HomePlace place, final int position)
    {
        // left view 생성
        holder.leftView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

        // right view 생성
        int size = getItemCount();
        boolean isLast = size <= 0 || (position == size - 1);

        ViewGroup.LayoutParams rightViewParam = holder.rightView.getLayoutParams();
        int rightViewWidth = ScreenUtils.dpToPx(mContext, isLast == true ? 15 : 12);

        if (rightViewParam == null)
        {
            rightViewParam = new ViewGroup.LayoutParams(rightViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else
        {
            rightViewParam.width = rightViewWidth;
        }

        holder.rightView.setLayoutParams(rightViewParam);
        // left, right view end

        holder.imageView.setTag(holder.imageView.getId(), position);

        Util.requestImageResize(mContext, holder.imageView, place.imageUrl);

        //        // SOLD OUT 표시
        //        if (place.isSoldOut == true)
        //        {
        //            holder.soldOutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.soldOutView.setVisibility(View.GONE);
        //        }

        holder.titleView.setText(place.title);

        Prices prices = place.prices;

        if (prices == null)
        {
            holder.originPriceView.setText("");
            holder.discountPriceView.setText("");
            holder.personView.setText("");
        } else
        {
            String strPrice = DailyTextUtils.getPriceFormat(mContext, prices.normalPrice, false);
            String strDiscount = DailyTextUtils.getPriceFormat(mContext, prices.discountPrice, false);

            holder.discountPriceView.setText(strDiscount);

            if (prices.normalPrice <= 0 || prices.normalPrice <= prices.discountPrice)
            {
                holder.originPriceView.setText("");
            } else
            {
                holder.originPriceView.setText(strPrice);
                holder.originPriceView.setPaintFlags(holder.originPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.provinceView.setText(place.regionName);

        if (place.placeType == Constants.PlaceType.HOTEL)
        {
            holder.gradeView.setText(place.details.stayGrade.getName(mContext));
            holder.dotView.setVisibility(View.VISIBLE);

            holder.personView.setText("");
            holder.personView.setVisibility(View.GONE);
        } else if (place.placeType == Constants.PlaceType.FNB)
        {
            // grade
            if (DailyTextUtils.isTextEmpty(place.details.category) == true)
            {
                holder.gradeView.setVisibility(View.GONE);
                holder.dotView.setVisibility(View.GONE);
                holder.gradeView.setText("");
            } else
            {
                holder.gradeView.setVisibility(View.VISIBLE);
                holder.dotView.setVisibility(View.VISIBLE);
                holder.gradeView.setText(place.details.category);
            }

            if (prices != null && place.details.persons > 1)
            {
                holder.personView.setText(//
                    mContext.getString(R.string.label_home_person_format, place.details.persons));
                holder.personView.setVisibility(View.VISIBLE);
            } else
            {
                holder.personView.setText("");
                holder.personView.setVisibility(View.GONE);
            }
        } else
        {
            // Stay Outbound 의 경우 PlaceType 이 없음
            holder.gradeView.setText("");
            holder.dotView.setVisibility(View.GONE);

            holder.personView.setText("");
            holder.personView.setVisibility(View.GONE);
        }

        holder.itemView.setTag(place);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mItemClickListener != null)
                {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });

        if (Util.supportPreview(mContext) == true)
        {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
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

                        mItemClickListener.onItemLongClick(v, position);

                        return true;
                    }
                }
            });
        }
    }

    public HomePlace getItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        return mList.get(position);
    }

    public int getItemCount()
    {
        return mList != null && mList.size() > 0 ? mList.size() : 0;
    }

    public ArrayList<HomePlace> getData()
    {
        return mList;
    }

    public void setData(ArrayList<HomePlace> list)
    {
        mList = list;
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

    private class PlaceViewHolder extends RecyclerView.ViewHolder
    {
        SimpleDraweeView imageView;
        ImageView soldOutView;
        DailyTextView titleView;
        DailyTextView discountPriceView;
        DailyTextView originPriceView;
        DailyTextView personView;
        DailyTextView provinceView;
        DailyTextView gradeView;
        View dotView;
        View leftView;
        View rightView;

        public PlaceViewHolder(View view)
        {
            super(view);

            imageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
            soldOutView = (ImageView) view.findViewById(R.id.soldoutView);
            titleView = (DailyTextView) view.findViewById(R.id.contentTextView);
            discountPriceView = (DailyTextView) view.findViewById(R.id.contentDiscountPriceView);
            originPriceView = (DailyTextView) view.findViewById(R.id.contentOriginPriceView);
            personView = (DailyTextView) view.findViewById(R.id.contentPersonView);
            provinceView = (DailyTextView) view.findViewById(R.id.contentProvinceView);
            gradeView = (DailyTextView) view.findViewById(R.id.contentGradeView);
            dotView = view.findViewById(R.id.contentDotImageView);
            leftView = view.findViewById(R.id.leftLayout);
            rightView = view.findViewById(R.id.rightLayout);

            int width = imageView.getWidth() == 0 ? ScreenUtils.dpToPx(mContext, 239) : imageView.getWidth();
            int height = ScreenUtils.getRatioHeightType16x9(width);

            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            imageView.setLayoutParams(layoutParams);
            soldOutView.setLayoutParams(layoutParams);
        }
    }
}
