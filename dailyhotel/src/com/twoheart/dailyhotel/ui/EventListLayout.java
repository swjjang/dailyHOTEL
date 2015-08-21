package com.twoheart.dailyhotel.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.twoheart.dailyhotel.EventListFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class EventListLayout implements OnItemClickListener
{
	private Context mContext;
	private View mRootView;
	private View mEmptyView;
	private ListView mListView;
	private EventListAdapter mEventListAdapter;
	private EventListFragment.OnUserActionListener mOnUserActionListener;

	public EventListLayout(Context context)
	{
		mContext = context;
	}

	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.layout_eventlist, container, false);
		mRootView.setPadding(0, Util.dpToPx(container.getContext(), 56) + 1, 0, 0);

		mEmptyView = mRootView.findViewById(R.id.emptyLayout);

		mListView = (ListView) mRootView.findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);

		return mRootView;
	}

	public void setData(ArrayList<Event> list)
	{
		if (mEventListAdapter == null)
		{
			mEventListAdapter = new EventListAdapter(mContext, 0, new ArrayList<Event>());
		}

		mEventListAdapter.clear();

		if (list == null)
		{
			mListView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
		} else
		{
			mListView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);

			mEventListAdapter.addAll(list);
			mListView.setAdapter(mEventListAdapter);
			mEventListAdapter.notifyDataSetChanged();
		}
	}

	public void setOnUserActionListener(EventListFragment.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (mOnUserActionListener != null)
		{
			mOnUserActionListener.onEventClick(mEventListAdapter.getItem(position));
		}
	}

	private class EventListAdapter extends ArrayAdapter<Event>
	{
		private ArrayList<Event> mArrayList;
		private Context mContext;

		public EventListAdapter(Context context, int resourceId, ArrayList<Event> eventList)
		{
			super(context, resourceId, eventList);

			mContext = context;
			addAll(eventList);
		}

		public void addAll(Collection<? extends Event> collection)
		{
			if (collection == null)
			{
				return;
			}

			if (mArrayList == null)
			{
				mArrayList = new ArrayList<Event>();
			}

			mArrayList.addAll(collection);
		}

		@Override
		public int getCount()
		{
			if (mArrayList == null)
			{
				return 0;
			}

			return mArrayList.size();
		}

		@Override
		public Event getItem(int position)
		{
			if (mArrayList == null)
			{
				return null;
			}

			return mArrayList.get(position);
		}

		@Override
		public void clear()
		{
			if (mArrayList == null)
			{
				return;
			}

			mArrayList.clear();

			super.clear();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = null;

			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.list_row_event, parent, false);
			} else
			{
				view = convertView;
			}

			ImageView imageView = (ImageView) view.findViewById(R.id.eventImageView);
			Event event = getItem(position);

			// AQuery사용시 
			AQuery aquery = new AQuery(convertView);
			Bitmap cachedImg = VolleyImageLoader.getCache(event.imageUrl);

			if (cachedImg == null)
			{
				BitmapAjaxCallback cb = new BitmapAjaxCallback()
				{
					@Override
					protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
					{
						VolleyImageLoader.putCache(url, bm);
						super.callback(url, iv, bm, status);
					}
				};

				if (Util.getLCDWidth(mContext) < 720)
				{
					cb.url(event.imageUrl).animation(AQuery.FADE_IN);
					aquery.id(imageView).image(event.imageUrl, false, false, 240, 0, cb);
				} else
				{
					cb.url(event.imageUrl).animation(AQuery.FADE_IN);
					aquery.id(imageView).image(cb);
				}
			} else
			{
				imageView.setImageBitmap(cachedImg);
			}

			return view;
		}
	}
}