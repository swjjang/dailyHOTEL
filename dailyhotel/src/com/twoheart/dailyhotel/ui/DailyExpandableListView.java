/*
 * Copyright (C) 2013 Leszek Mzyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twoheart.dailyhotel.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class DailyExpandableListView extends ExpandableListView
{

	public DailyExpandableListView(Context context)
	{
		super(context);
	}

	public DailyExpandableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public DailyExpandableListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	//	public boolean expandGroupWithAnimation(AnimationExpandableListAdapter adapter, int groupPos)
	//	{
	//		int groupFlatPos = getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPos));
	//		int scrollUpHeight = 0;
	//
	//		if (groupFlatPos != -1)
	//		{
	//			int childIndex = groupFlatPos - getFirstVisiblePosition();
	//			View v = getChildAt(childIndex);
	//
	//			int height = 0;
	//
	//			// 마지막 아이템인 경우., 146dp는 업데이트 정보영역의 높이이다.
	//			if (getExpandableListAdapter().getGroupCount() == groupPos + 1)
	//			{
	//				if (getBottom() > v.getBottom() && getBottom() < v.getBottom() + ThumbnailServer.dpToPixel(getContext(), 146))
	//				{
	//					scrollUpHeight = -1;
	//				} else
	//				{
	//					height = (v.getBottom() - getBottom()) + ThumbnailServer.dpToPixel(getContext(), 146);
	//				}
	//			} else
	//			{
	//				height = (v.getBottom() - getBottom()) + ThumbnailServer.dpToPixel(getContext(), 146);
	//			}
	//
	//			if (v.getBottom() >= getBottom())
	//			{
	//				adapter.notifyExpanded(groupPos);
	//				adapter.startMoreButtonAnimation(true, groupPos, height);
	//				return expandGroup(groupPos);
	//
	//			} else
	//			{
	//				if (height > 0)
	//				{
	//					scrollUpHeight = height;
	//				}
	//			}
	//		}
	//
	//		adapter.startAnimation(true, groupPos, 0, scrollUpHeight);
	//
	//		return expandGroup(groupPos);
	//	}
	//
	//	public boolean collapseGroupWithAnimation(AnimationExpandableListAdapter adapter, int groupPos)
	//	{
	//
	//		int groupFlatPos = getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPos));
	//		if (groupFlatPos != -1)
	//		{
	//			int childIndex = groupFlatPos - getFirstVisiblePosition();
	//			if (childIndex >= 0 && childIndex < getChildCount())
	//			{
	//				View v = getChildAt(childIndex);
	//				if (v.getBottom() >= getBottom())
	//				{
	//					adapter.startMoreButtonAnimation(false, groupPos, 0);
	//					adapter.notifyDataSetChanged();
	//					return collapseGroup(groupPos);
	//				}
	//			} else
	//			{
	//				adapter.startMoreButtonAnimation(false, groupPos, 0);
	//				adapter.notifyDataSetChanged();
	//				return collapseGroup(groupPos);
	//			}
	//		}
	//
	//		long packedPos = getExpandableListPosition(getFirstVisiblePosition());
	//		int firstChildPos = ExpandableListView.getPackedPositionChild(packedPos);
	//		int firstGroupPos = ExpandableListView.getPackedPositionGroup(packedPos);
	//
	//		firstChildPos = firstChildPos == -1 || firstGroupPos != groupPos ? 0 : firstChildPos;
	//
	//		adapter.startAnimation(false, groupPos, firstChildPos, 0);
	//		adapter.notifyDataSetChanged();
	//
	//		return isGroupExpanded(groupPos);
	//	}
}
