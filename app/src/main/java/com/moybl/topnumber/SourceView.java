package com.moybl.topnumber;

import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import java.util.List;

public class SourceView {

	private View mView;
	private Source mSource;
	private TextView mLevelTextView;
	private TextView mRateTextView;
	private Button mExchangeButton;
	private View mUnlockView;
	private Button mUnlockButton;
	private RepeatListener mRepeatListener;

	public SourceView(View view) {
		mView = view;
		mLevelTextView = (TextView) mView.findViewById(R.id.tv_source_level);
		mRateTextView = (TextView) mView.findViewById(R.id.tv_source_rate);
		mExchangeButton = (Button) mView.findViewById(R.id.btn_source_exchange);
		mUnlockView = mView.findViewById(R.id.source_unlock);
		mUnlockButton = (Button) mView.findViewById(R.id.btn_source_unlock);

		mUnlockButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParticleSystem ps = new ParticleSystem(MainActivity.getInstance(), 100, R.drawable.particle, 1000)
						.setSpeedRange(0.05f, 0.3f)
						.setRotationSpeedRange(90.0f, 180.0f)
						.setInitialRotationRange(0, 360)
						.setScaleRange(0.5f, 1.0f)
						.setFadeOut(1000);
				ps.oneShot(mView, 100);

				exchange();
				mUnlockButton.playSoundEffect(SoundEffectConstants.CLICK);
			}
		});

		mRepeatListener = new RepeatListener(500, 100, new RepeatListener.OnRepeatListener() {
			@Override
			public void onFirstClick(View v) {
				exchange();
				mUnlockButton.playSoundEffect(SoundEffectConstants.CLICK);
			}

			@Override
			public void onRepeatClick(View v) {
				exchange();
			}
		});
		mExchangeButton.setOnTouchListener(mRepeatListener);
	}

	private void exchange() {
		NumberData numberData = NumberData.getInstance();

		if (numberData.getNumber() >= mSource.getCost()) {
			numberData.exchange(mSource.getIndex());
		}

		MainActivity.getInstance()
				.update();
	}

	public void update() {
		NumberData numberData = NumberData.getInstance();
		List<Source> sources = numberData.getSources();

		if (numberData.getNumber() >= mSource.getCost()) {
			if (!mExchangeButton.isEnabled()) {
				mExchangeButton.setEnabled(true);
			}
		} else {
			if (mExchangeButton.isEnabled()) {
				mRepeatListener.cancel();
				mExchangeButton.setEnabled(false);
			}
		}

		int level = mSource.getLevel();
		int index = mSource.getIndex();
		double cost = mSource.getCost();
		double rate = mSource.getRate();

		Source previous = index > 0 ? sources.get(index - 1) : null;

		if (mSource.isUnlocked()) {
			Util.setVisible(mView);
			Util.setGone(mUnlockView);
		} else {
			if (previous != null && previous.isUnlocked()) {
				Util.setVisible(mView);
				Util.setVisible(mUnlockView);
				Util.setChangedText(mUnlockButton, "-" + NumberUtil.formatNumberWithNewLine(cost));
				mExchangeButton.setEnabled(false);

				mUnlockButton.setEnabled(numberData.getNumber() >= mSource.getCost());
			} else {
				Util.setGone(mView);
			}
		}

		if (mView.getVisibility() == View.VISIBLE) {
			Util.setChangedText(mLevelTextView, NumberUtil.format(level));
			Util.setChangedText(mRateTextView, "+" + NumberUtil.formatNumber(rate) + "/s");
			Util.setChangedText(mExchangeButton, "-" + NumberUtil.formatNumberWithNewLine(cost));
		}
	}

	public void setSource(Source source) {
		mSource = source;
	}

}
