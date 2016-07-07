package com.moybl.topnumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moybl.topnumber.backend.TopNumberClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	// TODO prettify
	private static MainActivity sInstance;

	public static MainActivity getInstance() {
		return sInstance;
	}

	@BindView(R.id.tv_number)
	TextView mNumberTextView;
	@BindView(R.id.tv_number_name)
	TextView mNumberNameTextView;
	@BindView(R.id.list_sources)
	LinearLayout mSourcesList;
	@BindView(R.id.tv_rate)
	TextView mRateTextView;

	private TopNumberClient mClient;
	private NumberData mNumberData;
	private List<SourceView> mSourceViews;
	private boolean mUpdateRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sInstance = this;

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		mClient = TopNumberClient.getInstance();
		mClient.setContext(this);

		if (mClient.getPlayer() == null) {
			finish();
			return;
		}

		Prefs.load(this);
		Prefs.setDouble(NumberData.KEY_NUMBER, 10e10);
		NumberUtil.setContext(this);
		mNumberData = NumberData.getInstance();
		mNumberData.load();

		LayoutInflater inflater = LayoutInflater.from(this);
		mSourceViews = new ArrayList<>();
		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mNumberData.getSources()
					.get(i);

			View view = inflater.inflate(R.layout.item_source, mSourcesList, false);
			SourceView sourceView = new SourceView(view);
			sourceView.setSource(source);
			sourceView.update();

			float h = (((1.0f + (float)Math.sqrt(5)) * 5.0f) * i) % 360.0f;
			float s = Math.min(0.6f, i / (Source.COUNT * 0.4f));
			view.setBackgroundColor(Color.HSVToColor(new float[]{h, s, 0.6f}));

			mSourcesList.addView(view);
			mSourceViews.add(sourceView);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		mUpdateRunning = false;
		mNumberData.save();
	}

	@Override
	protected void onStart() {
		super.onStart();

		update();

		mUpdateRunning = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mUpdateRunning) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							update();
						}
					});
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	public void update() {
		mNumberData.update();
		for (int i = 0; i < mSourceViews.size(); i++) {
			SourceView sourceView = mSourceViews.get(i);
			sourceView.update();
		}
		double number = mNumberData.getNumber();

		mRateTextView.setText("+" + NumberUtil.formatNumber(mNumberData.getRate()) + "/s");
		mNumberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(number)));

		if (NumberUtil.powerOf(number) >= 3) {
			Util.setVisible(mNumberNameTextView);

			mNumberNameTextView.setText(NumberUtil.powerName(number));
		} else {
			Util.setGone(mNumberNameTextView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.option_log_out:
				mNumberData.save();
				finish();
				return true;
			case R.id.option_reset_progress:
				onResetProgressClick();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void onResetProgressClick() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						mNumberData.clear();
						finish();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.reset_progress_dialog))
				.setPositiveButton(getString(R.string.yes), dialogClickListener)
				.setNegativeButton(getString(R.string.no), dialogClickListener)
				.show();
	}

}
