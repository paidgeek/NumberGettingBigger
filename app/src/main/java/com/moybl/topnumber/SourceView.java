package com.moybl.topnumber;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SourceView {

	private Source mSource;
	private TextView mLevelTextView;
	private TextView mRateTextView;
	private Button mExchangeButton;

	public SourceView(View view) {
		mLevelTextView = (TextView) view.findViewById(R.id.tv_source_level);
		mRateTextView = (TextView) view.findViewById(R.id.tv_source_rate);
		mExchangeButton = (Button) view.findViewById(R.id.btn_source_exchange);

		mExchangeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onExchangeClick();
			}
		});
	}

	public void onExchangeClick() {
		NumberData numberData = NumberData.getInstance();

		if (numberData.getNumber() >= mSource.getCost()) {
			numberData.exchange(mSource);
			bind();
		}
	}

	public void update() {
		NumberData numberData = NumberData.getInstance();

		mExchangeButton.setEnabled(numberData.getNumber() >= mSource.getCost());
	}

	public void bind() {
		mLevelTextView.setText(NumberUtil.format(mSource.getLevel()));
		mRateTextView.setText("+" + NumberUtil.format(mSource.getRate()) + "/s");
		mExchangeButton.setText("-" + NumberUtil.format(mSource.getCost()));
	}

	public void setSource(Source source) {
		mSource = source;
	}

}
