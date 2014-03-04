package com.twoheart.dailyhotel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.DrawerMenu;

public class DrawerMenuListAdapter extends BaseAdapter {

	private List<DrawerMenu> list;
	private LayoutInflater inflater;
	private Context context;
	private int layout;

	public DrawerMenuListAdapter(Context context, int layout,
			List<DrawerMenu> list) {
		this.context = context;
		this.layout = layout;
		this.inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DrawerMenu item = list.get(position);

		if (item.gettype() == DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO) {
			convertView = inflater.inflate(R.layout.drawer_list_item_logo,
					null);
		} else if (item.gettype() == DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION) {
			convertView = inflater.inflate(R.layout.drawer_list_item_section,
					null);
			
			TextView drawerMenuItemTitle = (TextView) convertView
					.findViewById(R.id.drawerMenuItemTitle);

			drawerMenuItemTitle.setText(item.getTitle());
		} else if (item.gettype() == DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY) {
			convertView = inflater.inflate(R.layout.drawer_list_item_entry,
					null);

			ImageView drawerMenuItemIcon = (ImageView) convertView
					.findViewById(R.id.drawerMenuItemIcon);
			TextView drawerMenuItemTitle = (TextView) convertView
					.findViewById(R.id.drawerMenuItemTitle);

			drawerMenuItemIcon.setImageResource(item.getIcon());
			drawerMenuItemTitle.setText(item.getTitle());
		}

		return convertView;
	}
}
