package com.moybl.numbergettingbigger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.tv_number)
	TextView mNumberTextView;
	@BindView(R.id.list_sources)
	ListView mSourcesList;

	private NumberData mNumberData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		mNumberData = NumberData.getInstance();
		mNumberData.load(this);

		SourcesAdapter adapter = new SourcesAdapter(this, mNumberData.getSources());
		mSourcesList.setAdapter(adapter);

		updateValues();
	}

	@Override
	protected void onStop() {
		super.onStop();

		mNumberData.save(this);
	}

	private void updateValues() {
		double number = mNumberData.getNumber();

		mNumberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(number)) + "\n" + NumberUtil.powerName(this, number));
	}

}
