package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
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

            Uri imageUri = Util.isTextEmpty(getItem(position).url) == true ? null : Uri.parse(getItem(position).url);
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
            final com.facebook.drawee.view.SimpleDraweeView imageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.imageView);

            if (Util.isTextEmpty(description) == false)
            {
                textView.setVisibility(View.VISIBLE);
                textView.setText(description);
            } else
            {
                textView.setVisibility(View.INVISIBLE);
            }

            DraweeController controller;
            BaseControllerListener baseControllerListener = new BaseControllerListener<ImageInfo>()
            {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
                {
                    if (imageInfo == null)
                    {
                        return;
                    }

                    setImageViewHeight(imageView, imageInfo.getWidth(), imageInfo.getHeight());
                }
            };

            if (Util.getLCDWidth(getContext()) >= 720)
            {
                controller = Fresco.newDraweeControllerBuilder()//
                    .setControllerListener(baseControllerListener)//
                    .setUri(imageUri).build();
            } else
            {
                final int resizeWidth = 360, resizeHeight = 240;

                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imageUri)//
                    .setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight))//
                    .build();

                controller = Fresco.newDraweeControllerBuilder()//
                    .setOldController(imageView.getController())//
                    .setImageRequest(imageRequest)//
                    .setControllerListener(baseControllerListener).build();
            }

            imageView.setController(controller);

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
