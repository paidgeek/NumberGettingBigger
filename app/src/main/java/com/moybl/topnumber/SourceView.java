package com.moybl.topnumber;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SourceView {

	private View mView;
	private Source mSource;
	private TextView mLevelTextView;
	private TextView mRateTextView;
	private Button mExchangeButton;
	private View mOverlay;

	public SourceView(View view) {
		mView = view;
		mLevelTextView = (TextView) mView.findViewById(R.id.tv_source_level);
		mRateTextView = (TextView) mView.findViewById(R.id.tv_source_rate);
		mExchangeButton = (Button) mView.findViewById(R.id.btn_source_exchange);
		mOverlay = mView.findViewById(R.id.source_overlay);

		mExchangeButton.setOnTouchListener(new RepeatListener(500, 100, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exchange();
			}
		}));
	}

	private void exchange() {
		NumberData numberData = NumberData.getInstance();

		if (numberData.getNumber() >= mSource.getCost()) {
			numberData.exchange(mSource.getIndex());
		}

		update();
	}

	public void update() {
		NumberData numberData = NumberData.getInstance();
		List<Source> sources = numberData.getSources();

		mExchangeButton.setEnabled(numberData.getNumber() >= mSource.getCost());
		int level = mSource.getLevel();
		int index = mSource.getIndex();
		double cost = mSource.getCost();
		double rate = mSource.getRate();

		mLevelTextView.setText(NumberUtil.format(level));
		mRateTextView.setText("+" + NumberUtil.formatNumber(rate) + "/s");
		mExchangeButton.setText("-" + NumberUtil.formatNumberWithNewLine(cost));

		Source previous = index > 0 ? sources.get(index - 1) : null;

		if (mSource.isUnlocked() || previous != null && previous.isUnlocked() && numberData.getNumber() >= mSource.getCost()) {
			mView.setVisibility(View.VISIBLE);
		} else {
			mView.setVisibility(View.GONE);
		}

		mOverlay.setVisibility(!mSource.isUnlocked() && numberData.getNumber() < mSource.getCost() ? View.VISIBLE : View.GONE);
	}

	public void setSource(Source source) {
		mSource = source;
	}

}
