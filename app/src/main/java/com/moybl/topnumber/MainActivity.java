package com.moybl.topnumber;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.VoidResult;
import com.moybl.topnumber.backend.topNumber.model.Player;

import org.codechimp.apprater.AppRater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

	private static final long VIDEO_AD_PRELOAD_DELAY = 20000;

	// TODO prettify
	private static MainActivity sInstance;

	public static MainActivity getInstance() {
		return sInstance;
	}

	@BindView(R.id.ad_banner_bottom)
	AdView mAdView;
	@BindView(R.id.tv_number)
	TextView mNumberTextView;
	@BindView(R.id.tv_number_name)
	TextView mNumberNameTextView;
	@BindView(R.id.list_sources)
	LinearLayout mSourcesList;
	@BindView(R.id.tv_rate)
	TextView mRateTextView;
	@BindView(R.id.btn_video_ad)
	View mVideoAdButton;

	private TopNumberClient mClient;
	private NumberData mNumberData;
	private List<SourceView> mSourceViews;
	private boolean mUpdateRunning;
	private AppLovinIncentivizedInterstitial mIncentivizedInterstitial;

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

		Prefs.load(this, mClient.getPlayer().getId());
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

			float h = (((1.0f + (float) Math.sqrt(5)) * 5.0f) * i) % 360.0f;
			float s = Math.min(0.6f, i / (Source.COUNT * 0.4f));
			view.setBackgroundColor(Color.HSVToColor(new float[]{h, s, 0.6f}));

			mSourcesList.addView(view);
			mSourceViews.add(sourceView);
		}

		Animation a = AnimationUtils.loadAnimation(this, R.anim.wiggle);
		mVideoAdButton.startAnimation(a);

		AppRater.app_launched(this);
		AppRater.setDarkTheme();

		mIncentivizedInterstitial = AppLovinIncentivizedInterstitial.create(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		mUpdateRunning = false;

		scheduleNotificationSetup();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mNumberData.save();
		mAdView.destroy();
	}

	private void scheduleNotificationSetup() {
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		alarmIntent.putExtra(AlarmReceiver.KEY_REQUEST_CODE, AlarmReceiver.SCHEDULE_SETUP);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, AlarmReceiver.SCHEDULE_SETUP, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
	}

	private void cancelNotificationSetup() {
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, AlarmReceiver.SCHEDULE_SETUP, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
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

		loadVideoAd();
		cancelNotificationSetup();
	}

	@OnClick(R.id.btn_video_ad)
	void onVideoAdClick() {
		showVideoAd();
	}

	private void loadVideoAd() {
		mVideoAdButton.setVisibility(View.GONE);

		mIncentivizedInterstitial.preload(new AppLovinAdLoadListener() {
			@Override
			public void adReceived(AppLovinAd appLovinAd) {
				Log.d("AppLovin", "adReceived");
				mVideoAdButton.setVisibility(View.VISIBLE);
			}

			@Override
			public void failedToReceiveAd(int i) {
				Log.d("AppLovin", "failedToReceiveAd");

				final Handler h = new Handler();
				h.postDelayed(new Runnable() {
					@Override
					public void run() {
						loadVideoAd();
					}
				}, VIDEO_AD_PRELOAD_DELAY);
			}
		});
	}

	private void showVideoAd() {
		if (mIncentivizedInterstitial.isAdReadyToDisplay()) {
			mIncentivizedInterstitial.show(this, new AppLovinAdRewardListener() {
				@Override
				public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
					Log.d("REWARD", "rewarded");

					Player p = mClient.getPlayer();
					p.setNumber(p.getNumber() * 2.0);
				}

				@Override
				public void userOverQuota(AppLovinAd appLovinAd, Map map) {
				}

				@Override
				public void userRewardRejected(AppLovinAd appLovinAd, Map map) {
				}

				@Override
				public void validationRequestFailed(AppLovinAd appLovinAd, int i) {
				}

				@Override
				public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
				}
			}, null, null);

			mVideoAdButton.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
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
				mClient.logOut();
				mNumberData.save();
				finish();
				return true;
			case R.id.option_reset_progress:
				onResetProgressClick();
				return true;
			case R.id.option_change_name:
				onChangeNameClick();
				break;
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

	private void onChangeNameClick() {
		ChangeNameDialog d = new ChangeNameDialog();
		d.setOnClickListener(new ChangeNameDialog.OnClickListener() {
			@Override
			public void onOkClick(String name) {
				mClient.changeName(name, new ResultCallback<VoidResult>() {
					@Override
					public void onResult(@NonNull VoidResult result) {
						if (!result.isSuccess()) {
							Toast.makeText(getApplicationContext(), R.string.unable_to_change_name, Toast.LENGTH_LONG)
									.show();
						}
					}
				});
			}

			@Override
			public void onCancelClick() {
			}
		});
		d.show(getSupportFragmentManager(), "ChangeNameDialog");
	}

	@OnClick(R.id.btn_leaderboard)
	void onLeaderboardClick() {
		Intent intent = new Intent(this, LeaderboardActivity.class);
		startActivity(intent);
	}

}
