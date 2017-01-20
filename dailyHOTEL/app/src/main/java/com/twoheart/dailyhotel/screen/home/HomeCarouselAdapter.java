package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

import static com.twoheart.dailyhotel.util.Util.dpToPx;

/**
 * Created by android_sam on 2017. 1. 19..
 */

public class HomeCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    protected LayoutInflater mInflater;
    private ArrayList<? extends Place> mList;
    private ItemClickListener mItemClickListener;
    protected PaintDrawable mPaintDrawable;

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public HomeCarouselAdapter(Context context, ArrayList<? extends Place> list, ItemClickListener listener)
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
        Place place = getItem(position);
        if (place == null)
        {
            return;
        }

        onBindViewHolder((PlaceViewHolder) holder, place, position);
    }

    public void onBindViewHolder(PlaceViewHolder holder, Place place, final int position)
    {
        // left view 생성
        holder.leftView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

        // right view 생성
        int size = getItemCount();
        boolean isLast = size > 0 ? (position == size - 1) : true;

        ViewGroup.LayoutParams rightViewParam = holder.rightView.getLayoutParams();
        int rightViewWidth = Util.dpToPx(mContext, isLast == true ? 15 : 12);

        if (rightViewParam == null)
        {
            rightViewParam = new ViewGroup.LayoutParams(rightViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            rightViewParam.width = rightViewWidth;
        }

        holder.rightView.setLayoutParams(rightViewParam);
        // left, right view end

        holder.imageView.setTag(holder.imageView.getId(), position);

        Util.requestImageResize(mContext, holder.imageView, place.imageUrl);

        holder.titleView.setText(place.name);

        String strPrice = Util.getPriceFormat(mContext, place.price, false);
        String strDiscount = Util.getPriceFormat(mContext, place.discountPrice, false);

        holder.discountPriceView.setText(strDiscount);

        if (place.price <= 0 || place.price <= place.discountPrice)
        {
            holder.originPriceView.setVisibility(View.INVISIBLE);
            holder.originPriceView.setText(null);
        } else
        {
            holder.originPriceView.setVisibility(View.VISIBLE);
            holder.originPriceView.setText(strPrice);
            holder.originPriceView.setPaintFlags(holder.originPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.provinceView.setText(place.districtName);

        if (place instanceof Stay)
        {
            holder.gradeView.setText(((Stay) place).getGrade().getName(mContext));
            holder.dotView.setVisibility(View.VISIBLE);
        } else if (place instanceof Gourmet)
        {
            Gourmet gourmet = (Gourmet) place;

            String displayCategory;
            if (Util.isTextEmpty(gourmet.subCategory) == false)
            {
                displayCategory = gourmet.subCategory;
            } else
            {
                displayCategory = gourmet.category;
            }

            // grade
            if (Util.isTextEmpty(displayCategory) == true)
            {
                holder.gradeView.setVisibility(View.GONE);
                holder.dotView.setVisibility(View.GONE);
            } else
            {
                holder.gradeView.setVisibility(View.VISIBLE);
                holder.dotView.setVisibility(View.VISIBLE);
                holder.gradeView.setText(displayCategory);
            }

            holder.gradeView.setText(displayCategory);
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
    }

    public Place getItem(int position)
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

    public void setData(ArrayList<? extends Place> list)
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
        DailyTextView titleView;
        DailyTextView discountPriceView;
        DailyTextView originPriceView;
        DailyTextView provinceView;
        DailyTextView gradeView;
        View dotView;
        View leftView;
        View rightView;

        public PlaceViewHolder(View view)
        {
            super(view);

            imageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
            titleView = (DailyTextView) view.findViewById(R.id.contentTextView);
            discountPriceView = (DailyTextView) view.findViewById(R.id.contentDiscountPriceView);
            originPriceView = (DailyTextView) view.findViewById(R.id.contentOriginPriceView);
            provinceView = (DailyTextView) view.findViewById(R.id.contentProvinceView);
            gradeView = (DailyTextView) view.findViewById(R.id.contentGradeView);
            dotView = view.findViewById(R.id.contentDotImageView);
            leftView = view.findViewById(R.id.leftLayout);
            rightView = view.findViewById(R.id.rightLayout);

            int width = imageView.getWidth() == 0 ? dpToPx(mContext, 239) : imageView.getWidth();
            int height = Util.getRatioHeightType16x9(width);

            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            imageView.setLayoutParams(layoutParams);
        }
    }
}
