package com.moybl.numbergettingbigger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SourcesAdapter extends ArrayAdapter<Source> {

	private LayoutInflater mInflater;
	private List<SourceView> mSourceViews;

	public SourcesAdapter(Context context, List<Source> sources) {
		super(context, 0, sources);

		mInflater = LayoutInflater.from(context);
		mSourceViews = new ArrayList<>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SourceView sourceView = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_source, null);

			sourceView = new SourceView(convertView);
			mSourceViews.add(sourceView);

			convertView.setTag(sourceView);
		} else {
			sourceView = (SourceView) convertView.getTag();
		}

		Source source = getItem(position);
		sourceView.setSource(source);
		sourceView.bind();

		return convertView;
	}

	public void update() {
		for (int i = 0; i < mSourceViews.size(); i++) {
			mSourceViews.get(i)
					.update();
		}
	}

}
