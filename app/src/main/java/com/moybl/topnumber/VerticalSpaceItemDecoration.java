package com.moybl.topnumber;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

	private int mVerticalSpaceHeight;

	public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
		mVerticalSpaceHeight = verticalSpaceHeight;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.bottom = mVerticalSpaceHeight;
	}

}
