package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GourmetProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<PlaceViewItem> mGourmetProductList;
    OnProductClickListener mOnProductClickListener;


    public interface OnProductClickListener
    {
        void onProductDetailClick(int position);

        void onReservationClick(int position);
    }

    public GourmetProductListAdapter(Context context, List<PlaceViewItem> arrayList, OnProductClickListener listener)
    {
        mContext = context;
        mOnProductClickListener = listener;

        mGourmetProductList = new ArrayList<>();
        mGourmetProductList.addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<PlaceViewItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mGourmetProductList.clear();
        mGourmetProductList.addAll(collection);
    }

    public PlaceViewItem getItem(int position)
    {
        if (position < 0 || mGourmetProductList.size() <= position)
        {
            return null;
        }

        return mGourmetProductList.get(position);
    }

    @Override
    public int getItemViewType(int position)
    {
        return mGourmetProductList.get(position).mType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
            {
                View view = mInflater.inflate(R.layout.list_row_detail_product, parent, false);

                return new ProductInformationViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_footer, parent, false);

                return new FooterViewHolder(view);
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
                onBindViewHolder((ProductInformationViewHolder) holder, position, item);
                break;

            case PlaceViewItem.TYPE_FOOTER_VIEW:
                break;
        }
    }

    private void onBindViewHolder(ProductInformationViewHolder productInformationViewHolder, int position, PlaceViewItem placeViewItem)
    {
        GourmetProduct gourmetProduct = placeViewItem.getItem();

        if (gourmetProduct == null)
        {
            return;
        }

        productInformationViewHolder.contentsLayout.setTag(position);
        productInformationViewHolder.contentsLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnProductClickListener.onProductDetailClick((int) v.getTag());
            }
        });

        boolean hasThumbnail = true;

        ProductImageInformation productImageInformation = gourmetProduct.getPrimaryImage();
        if (productImageInformation == null)
        {
            hasThumbnail = false;
        } else
        {
            productInformationViewHolder.simpleDraweeView.setImageURI(Uri.parse(productImageInformation.imageUrl));
        }

        productInformationViewHolder.productNameTextView.setText(gourmetProduct.ticketName);

        if (hasThumbnail == false)
        {
            productInformationViewHolder.simpleDraweeView.setVisibility(View.GONE);

            productInformationViewHolder.contentsList.removeAllViews();

            if (Util.isTextEmpty(gourmetProduct.menuBenefit) == true && Util.isTextEmpty(gourmetProduct.needToKnow) == true)
            {
                productInformationViewHolder.contentsList.setVisibility(View.GONE);
            } else
            {
                productInformationViewHolder.contentsList.setVisibility(View.VISIBLE);

                // 베네핏
                if (Util.isTextEmpty(gourmetProduct.menuBenefit) == false)
                {
                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.menuBenefit, R.drawable.ic_detail_item_02_benefit, false);
                }

                // 확인 사항
                if (Util.isTextEmpty(gourmetProduct.needToKnow) == false)
                {
                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.needToKnow, R.drawable.ic_detail_item_01_info, true);
                }
            }
        } else
        {
            productInformationViewHolder.simpleDraweeView.setVisibility(View.VISIBLE);

            productInformationViewHolder.contentsList.removeAllViews();

            if (Util.isTextEmpty(gourmetProduct.menuBenefit) == true && Util.isTextEmpty(gourmetProduct.needToKnow) == true)
            {
                productInformationViewHolder.contentsList.setVisibility(View.GONE);
            } else
            {
                productInformationViewHolder.contentsList.setVisibility(View.VISIBLE);

                int titleTextViewWidth = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 30) - Util.dpToPx(mContext, 98);
                float titleTextViewHeight = Util.getTextViewHeight(productInformationViewHolder.productNameTextView, titleTextViewWidth);

                float startY = titleTextViewHeight + Util.dpToPx(mContext, 8);
                Rect rect = new Rect(0, 0, Util.dpToPx(mContext, 98), Util.dpToPx(mContext, 98));
                int textViewWidth = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 39) - Util.dpToPx(mContext, 15);

                // 베네핏
                if (Util.isTextEmpty(gourmetProduct.menuBenefit) == false)
                {
                    startY = addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.menuBenefit, R.drawable.ic_detail_item_02_benefit, startY, textViewWidth, rect, false);
                }

                // 확인 사항
                if (Util.isTextEmpty(gourmetProduct.needToKnow) == false)
                {
                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.needToKnow, R.drawable.ic_detail_item_01_info, startY, textViewWidth, rect, true);
                }
            }
        }

        String price = Util.getPriceFormat(mContext, gourmetProduct.price, false);
        String discountPrice = Util.getPriceFormat(mContext, gourmetProduct.discountPrice, false);

        if (gourmetProduct.price <= 0 || gourmetProduct.price <= gourmetProduct.discountPrice)
        {
            productInformationViewHolder.priceTextView.setVisibility(View.GONE);
            productInformationViewHolder.priceTextView.setText(null);
        } else
        {
            productInformationViewHolder.priceTextView.setVisibility(View.VISIBLE);
            productInformationViewHolder.priceTextView.setText(price);
        }

        productInformationViewHolder.discountPriceTextView.setText(discountPrice);

        productInformationViewHolder.detailView.setTag(position);
        productInformationViewHolder.detailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnProductClickListener != null)
                {
                    mOnProductClickListener.onProductDetailClick((int) v.getTag());
                }
            }
        });

        productInformationViewHolder.reservationView.setTag(position);
        productInformationViewHolder.reservationView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnProductClickListener != null)
                {
                    mOnProductClickListener.onReservationClick((int) v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if (mGourmetProductList == null)
        {
            return 0;
        }

        return mGourmetProductList.size();
    }

    private void addProductSubInformation(LayoutInflater layoutInflater, ViewGroup viewGroup, String contentText, int iconResId, boolean hasTopMargin)
    {
        if (layoutInflater == null || viewGroup == null || Util.isTextEmpty(contentText) == true)
        {
            return;
        }

        View textLayout = layoutInflater.inflate(R.layout.list_row_detail_product_text, viewGroup, false);
        viewGroup.addView(textLayout);

        if (hasTopMargin == true)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = Util.dpToPx(mContext, 6);
            textLayout.setLayoutParams(layoutParams);
        }

        DailyImageView iconImageView = (DailyImageView) textLayout.findViewById(R.id.iconImageView);
        iconImageView.setVectorImageResource(iconResId);

        TextView textView = (TextView) textLayout.findViewById(R.id.textView);
        textView.setText(contentText);
    }

    private float addProductSubInformation(LayoutInflater layoutInflater, ViewGroup viewGroup, String contentText, int iconResId, float startY, int textViewWidth, Rect rect, boolean hasTopMargin)
    {
        if (layoutInflater == null || viewGroup == null || Util.isTextEmpty(contentText) == true)
        {
            return startY;
        }

        View textLayout = layoutInflater.inflate(R.layout.list_row_detail_product_text, viewGroup, false);
        viewGroup.addView(textLayout);

        if (hasTopMargin == true)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = Util.dpToPx(mContext, 6);
            textLayout.setLayoutParams(layoutParams);

            startY += layoutParams.topMargin;
        } else
        {
            startY += Util.dpToPx(mContext, 2);
        }

        DailyImageView iconImageView = (DailyImageView) textLayout.findViewById(R.id.iconImageView);
        iconImageView.setVectorImageResource(iconResId);

        TextView textView = (TextView) textLayout.findViewById(R.id.textView);

        if (textViewWidth <= 0)
        {
            textView.setText(contentText);
            return 0;
        }

        return measureText(textView, contentText, startY, textViewWidth, rect);
    }

    protected float measureText(TextView textView, String text, float viewY, int viewWidth, Rect rect)
    {
        if (textView == null || viewWidth <= 0 || Util.isTextEmpty(text) == true)
        {
            return 0;
        }

        final int length = text.length();
        StringBuilder stringBuilder = new StringBuilder();
        Paint paint = textView.getPaint();

        float textViewHeight = 0.0f;
        int startIndex = 0;
        int textCount = 0;
        float textWidth, realWidth;
        boolean isSettingRange = false;

        char[] texts = text.toCharArray();

        while (startIndex < length)
        {
            if (viewY + textViewHeight < rect.bottom)
            {
                realWidth = viewWidth - rect.width();
            } else
            {
                realWidth = viewWidth;
            }

            textCount = paint.breakText(texts, startIndex, texts.length - startIndex, realWidth, null);

            if (stringBuilder.length() > 0)
            {
                stringBuilder.append('\n');
            }

            stringBuilder.append(texts, startIndex, textCount);

            startIndex += textCount;

            textView.setText(stringBuilder.toString());
            textViewHeight = Util.getTextViewHeight(textView, viewWidth);
        }

        textView.setText(stringBuilder.toString());

        return viewY + Util.getTextViewHeight(textView, viewWidth);
    }

    private class ProductInformationViewHolder extends RecyclerView.ViewHolder
    {
        View contentsLayout;
        SimpleDraweeView simpleDraweeView;
        TextView productNameTextView;
        TextView discountPriceTextView;
        TextView priceTextView;
        LinearLayout contentsList;
        View detailView;
        View reservationView;

        public ProductInformationViewHolder(View itemView)
        {
            super(itemView);

            contentsLayout = itemView.findViewById(R.id.contentsLayout);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpleDraweeView);
            productNameTextView = (TextView) itemView.findViewById(R.id.productNameTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            discountPriceTextView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            contentsList = (LinearLayout) itemView.findViewById(R.id.contentsList);
            detailView = itemView.findViewById(R.id.detailView);
            reservationView = itemView.findViewById(R.id.reservationView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
