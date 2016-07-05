package com.moybl.topnumber;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.moybl.topnumber.backend.TopNumberClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.tv_number)
	TextView mNumberTextView;
	@BindView(R.id.list_sources)
	ListView mSourcesList;

	private NumberData mNumberData;
	private SourcesAdapter mSourcesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		TopNumberClient.getInstance()
				.setContext(this);

		mNumberData = NumberData.getInstance();
		mNumberData.load(this);

		mSourcesAdapter = new SourcesAdapter(this, mNumberData.getSources());
		mSourcesList.setAdapter(mSourcesAdapter);

		updateValues();

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				mNumberData.update();
				mSourcesAdapter.update();
				updateValues();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void onStop() {
		super.onStop();

		mNumberData.save(this);
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
			case R.id.option_clear_data:
				mNumberData.clear(this);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateValues() {
		double number = mNumberData.getNumber();

		mNumberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(number)) + "\n" + NumberUtil.powerName(this, number));
	}

}
