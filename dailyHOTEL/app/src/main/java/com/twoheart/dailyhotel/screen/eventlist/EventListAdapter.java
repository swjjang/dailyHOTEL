package com.twoheart.dailyhotel.screen.eventlist;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;

public class EventListAdapter extends ArrayAdapter<Event>
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

        com.facebook.drawee.view.SimpleDraweeView imageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.eventImageView);
        Event event = getItem(position);

        Util.requestImageResize(mContext, imageView, Uri.parse(event.imageUrl));

        return view;
    }
}