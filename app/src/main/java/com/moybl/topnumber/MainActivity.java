package com.moybl.topnumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		mClient = TopNumberClient.getInstance();
		mClient.setContext(this);

		if (mClient.getPlayer() == null) {
			finish();
			return;
		}

		NumberUtil.setContext(this);
		mNumberData = NumberData.getInstance();
		mNumberData.load(this);

		LayoutInflater inflater = LayoutInflater.from(this);
		mSourceViews = new ArrayList<>();
		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mNumberData.getSources()
					.get(i);

			View view = inflater.inflate(R.layout.item_source, mSourcesList, false);
			SourceView sourceView = new SourceView(view);
			sourceView.setSource(source);
			sourceView.update();

			mSourcesList.addView(view);
			mSourceViews.add(sourceView);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		mTimer.cancel();
		mNumberData.save(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		update();
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						update();
					}
				});
			}
		}, 0, 1000);
	}

	private void update() {
		mNumberData.update();
		for (int i = 0; i < mSourceViews.size(); i++) {
			SourceView sourceView = mSourceViews.get(i);
			sourceView.update();
		}
		double number = mNumberData.getNumber();

		mRateTextView.setText("+" + NumberUtil.formatNumber(mNumberData.getRate()) + "/s");
		mNumberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(number)));

		if (NumberUtil.powerOf(number) >= 3) {
			mNumberNameTextView.setVisibility(View.VISIBLE);
			mNumberNameTextView.setText(NumberUtil.powerName(number));
		} else {
			mNumberNameTextView.setVisibility(View.GONE);
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
				mNumberData.save(this);
				finish();
				return true;
			case R.id.option_reset_progress:
				onResetProgressClick();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void onResetProgressClick() {
		final Activity activity = this;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						mNumberData.clear(activity);
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
