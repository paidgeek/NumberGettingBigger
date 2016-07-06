package com.moybl.topnumber;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.moybl.topnumber.backend.TopNumberClient;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.tv_number)
	TextView mNumberTextView;
	@BindView(R.id.list_sources)
	ListView mSourcesList;

	private TopNumberClient mClient;
	private NumberData mNumberData;
	private SourcesAdapter mSourcesAdapter;
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

		mNumberData = NumberData.getInstance();
		mNumberData.load(this);

		mSourcesAdapter = new SourcesAdapter(this, mNumberData.getSources());
		mSourcesList.setAdapter(mSourcesAdapter);
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

		updateValues();
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mNumberData.update();
						mSourcesAdapter.update();
						updateValues();
					}
				});
			}
		}, 0, 1000);
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
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateValues() {
		double number = mNumberData.getNumber();

		mNumberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(number)) + "\n" + NumberUtil.powerName(this, number));
	}

}
