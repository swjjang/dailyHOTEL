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
import com.twoheart.dailyhotel.network.model.GourmetTicket;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GourmetTicketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<GourmetTicket> mGourmetTicketList;
    private OnTicketClickListener mOnTicketClickListener;

    public interface OnTicketClickListener
    {
        void onProductDetailClick(int position);

        void onReservationClick(int position);
    }

    public GourmetTicketListAdapter(Context context, List<GourmetTicket> arrayList, OnTicketClickListener listener)
    {
        mContext = context;
        mOnTicketClickListener = listener;

        mGourmetTicketList = new ArrayList<>();
        mGourmetTicketList.addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<GourmetTicket> collection)
    {
        if (collection == null)
        {
            return;
        }

        mGourmetTicketList.clear();
        mGourmetTicketList.addAll(collection);
    }

    //    public void setSelected(int position)
    //    {
    //        mSelectedPosition = position;
    //    }
    //
    //    public int setSelectIndex(int index)
    //    {
    //        if (mTicketInformationList == null)
    //        {
    //            return 0;
    //        }
    //
    //        int size = mTicketInformationList.size();
    //
    //        for (int i = 0; i < size; i++)
    //        {
    //            TicketInformation roomInformation = mTicketInformationList.get(i);
    //
    //            if (roomInformation.index == index)
    //            {
    //                setSelected(i);
    //                notifyDataSetChanged();
    //                return i;
    //            }
    //        }
    //
    //        return 0;
    //    }

    public GourmetTicket getItem(int position)
    {
        if (mGourmetTicketList.size() <= position)
        {
            return null;
        }

        return mGourmetTicketList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.list_row_detail_product, parent, false);

        return new TicketInformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        GourmetTicket gourmetTicket = getItem(position);

        if (gourmetTicket == null)
        {
            return;
        }

        TicketInformationViewHolder ticketInformationViewHolder = (TicketInformationViewHolder) holder;

        ticketInformationViewHolder.contentsLayout.setTag(position);
        ticketInformationViewHolder.contentsLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnTicketClickListener.onProductDetailClick((int) v.getTag());
            }
        });

        boolean hasThumbnail = true;

        ProductImageInformation productImageInformation = gourmetTicket.getPrimaryImage();
        if (productImageInformation == null)
        {
            hasThumbnail = false;
        } else
        {
            ticketInformationViewHolder.simpleDraweeView.setImageURI(Uri.parse(productImageInformation.imageUrl));
        }

        ticketInformationViewHolder.productNameTextView.setText(gourmetTicket.ticketName);

        if (hasThumbnail == false)
        {
            ticketInformationViewHolder.simpleDraweeView.setVisibility(View.GONE);

            Rect rect = new Rect(0, 0, 0, 0);

            ticketInformationViewHolder.contentsList.removeAllViews();

            if (Util.isTextEmpty(gourmetTicket.option) == false)
            {
                addTicketSubInformation(mInflater, ticketInformationViewHolder.contentsList, gourmetTicket.option, 0, -1, rect);
            }

            if (Util.isTextEmpty(gourmetTicket.benefit) == false)
            {
                addTicketSubInformation(mInflater, ticketInformationViewHolder.contentsList, gourmetTicket.benefit, 0, -1, rect);
            }
        } else
        {
            ticketInformationViewHolder.simpleDraweeView.setVisibility(View.VISIBLE);

            int titleTextViewWidth = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 30) - Util.dpToPx(mContext, 115);
            float titleTextViewHeight = Util.getTextViewHeight(ticketInformationViewHolder.productNameTextView, titleTextViewWidth);

            float startY = titleTextViewHeight + Util.dpToPx(mContext, 15);
            Rect rect = new Rect(0, 0, Util.dpToPx(mContext, 115), Util.dpToPx(mContext, 115));
            int textViewWidth = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 28) - Util.dpToPx(mContext, 15);

            ticketInformationViewHolder.contentsList.removeAllViews();

            if (Util.isTextEmpty(gourmetTicket.option) == false)
            {
                startY += addTicketSubInformation(mInflater, ticketInformationViewHolder.contentsList, gourmetTicket.option, startY, textViewWidth, rect);
            }

            if (Util.isTextEmpty(gourmetTicket.benefit) == false)
            {
                startY += addTicketSubInformation(mInflater, ticketInformationViewHolder.contentsList, gourmetTicket.benefit, startY, textViewWidth, rect);
            }
        }

        String price = Util.getPriceFormat(mContext, gourmetTicket.price, false);
        String discountPrice = Util.getPriceFormat(mContext, gourmetTicket.discountPrice, false);

        if (gourmetTicket.price <= 0 || gourmetTicket.price <= gourmetTicket.discountPrice)
        {
            ticketInformationViewHolder.priceTextView.setVisibility(View.GONE);
            ticketInformationViewHolder.priceTextView.setText(null);
        } else
        {
            ticketInformationViewHolder.priceTextView.setVisibility(View.VISIBLE);
            ticketInformationViewHolder.priceTextView.setText(price);
        }

        ticketInformationViewHolder.discountPriceTextView.setText(discountPrice);

        ticketInformationViewHolder.detailView.setTag(position);
        ticketInformationViewHolder.detailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnTicketClickListener != null)
                {
                    mOnTicketClickListener.onProductDetailClick((int) v.getTag());
                }
            }
        });

        ticketInformationViewHolder.reservationView.setTag(position);
        ticketInformationViewHolder.reservationView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnTicketClickListener != null)
                {
                    mOnTicketClickListener.onReservationClick((int) v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if (mGourmetTicketList == null)
        {
            return 0;
        }

        return mGourmetTicketList.size();
    }

    private float addTicketSubInformation(LayoutInflater layoutInflater, ViewGroup viewGroup, String contentText, float startY, int textViewWidth, Rect rect)
    {
        if (layoutInflater == null || viewGroup == null || Util.isTextEmpty(contentText) == true)
        {
            return startY;
        }

        View textLayout = layoutInflater.inflate(R.layout.list_row_detail_product_text, viewGroup, false);
        viewGroup.addView(textLayout);
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

            textView.setText(stringBuilder.toString() + "\n ");
            textViewHeight = Util.getTextViewHeight(textView, viewWidth);
        }

        textView.setText(stringBuilder.toString());

        // Bottom Margin : 10

        return Util.getTextViewHeight(textView, viewWidth) + Util.dpToPx(mContext, 10);
    }

    private class TicketInformationViewHolder extends RecyclerView.ViewHolder
    {
        View contentsLayout;
        SimpleDraweeView simpleDraweeView;
        TextView productNameTextView;
        TextView discountPriceTextView;
        TextView priceTextView;
        LinearLayout contentsList;
        View detailView;
        View reservationView;

        public TicketInformationViewHolder(View itemView)
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
}
