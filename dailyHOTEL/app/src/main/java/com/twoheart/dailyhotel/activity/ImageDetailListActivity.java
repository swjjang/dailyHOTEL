package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ImageDetailListActivity extends BaseActivity implements Constants
{
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail_list);

        final int position;

        Bundle bundle = getIntent().getExtras();
        ArrayList<ImageInformation> arrayList = null;

        if (bundle != null)
        {
            arrayList = bundle.getParcelableArrayList(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST);
            position = bundle.getInt(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION);
        } else
        {
            return;
        }

        if (arrayList == null)
        {
            finish();
            return;
        }

        mListView = (ListView) findViewById(R.id.listView);

        ImageDetailListAdapter adapter = new ImageDetailListAdapter(this, 0, arrayList);
        mListView.setAdapter(adapter);
        mListView.post(new Runnable()
        {
            @Override
            public void run()
            {
                mListView.setSelection(position);
            }
        });
    }

    private class ImageDetailListAdapter extends ArrayAdapter<ImageInformation>
    {
        public ImageDetailListAdapter(Context context, int resourceId, List<ImageInformation> list)
        {
            super(context, resourceId, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view;

            final String url = getItem(position).url;
            String description = getItem(position).description;

            if (convertView == null)
            {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.list_row_image, parent, false);
            } else
            {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(R.id.descriptionTextView);
            final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

            if (Util.isTextEmpty(description) == false)
            {
                textView.setVisibility(View.VISIBLE);
                textView.setText(description);
            } else
            {
                textView.setVisibility(View.INVISIBLE);
            }

            imageView.setImageBitmap(null);

            if (Util.getLCDWidth(ImageDetailListActivity.this) < 720)
            {
                Glide.with(ImageDetailListActivity.this).load(url).asBitmap().override(360, 240).listener(new RequestListener<String, Bitmap>()
                {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource)
                    {
                        imageView.setImageBitmap(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        return false;
                    }
                }).into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        setImageViewHeight(imageView, resource.getWidth(), resource.getHeight());
                        imageView.setImageBitmap(resource);
                    }
                });
            } else
            {
                Glide.with(ImageDetailListActivity.this).load(url).asBitmap().listener(new RequestListener<String, Bitmap>()
                {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource)
                    {
                        imageView.setImageBitmap(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        return false;
                    }
                }).into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        setImageViewHeight(imageView, resource.getWidth(), resource.getHeight());
                        imageView.setImageBitmap(resource);
                    }
                });
            }

            return view;
        }

        private void setImageViewHeight(ImageView imageView, int width, int height)
        {
            RelativeLayout.LayoutParams layoutParms = (android.widget.RelativeLayout.LayoutParams) imageView.getLayoutParams();

            float scale = (float) Util.getLCDWidth(getContext()) / width;
            int viewheight = (int) (scale * height);

            if (layoutParms == null)
            {
                layoutParms = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, viewheight);
            } else
            {
                layoutParms.height = viewheight;
            }

            imageView.setLayoutParams(layoutParms);
        }
    }
}
