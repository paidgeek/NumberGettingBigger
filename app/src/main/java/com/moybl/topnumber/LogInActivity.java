package com.moybl.topnumber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.moybl.topnumber.backend.ObjectResult;
import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends Activity {

	@BindView(R.id.tv_log_in_number)
	TextView mNumberTextView;
	@BindView(R.id.tv_log_in_number_name)
	TextView mNumberNameTextView;
	@BindView(R.id.loading_indicator)
	View mLoadingIndicator;
	private double mNumber;
	private CallbackManager mCallbackManager;
	private boolean mUpdateRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		ButterKnife.bind(this);

		mCallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance()
				.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Prefs.load(getApplicationContext());
						final boolean reset = Prefs.getBoolean(NumberData.KEY_RESET, false);

						final TopNumberClient client = TopNumberClient.getInstance();
						client.setContext(getApplicationContext());
						String accessToken = loginResult.getAccessToken()
								.getToken();

						client.logInWithFacebook(accessToken, reset, new ResultCallback<ObjectResult<Player>>() {
							@Override
							public void onResult(@NonNull ObjectResult<Player> result) {
								mLoadingIndicator.setVisibility(View.GONE);

								if (result.isSuccess()) {
									if(reset){
										Prefs.removeAll();
										Prefs.save();
									}

									Intent intent = new Intent(getApplicationContext(), MainActivity.class);
									startActivity(intent);
								} else {
									Toast.makeText(getApplicationContext(), R.string.unable_to_log_in, Toast.LENGTH_LONG)
											.show();
								}
							}
						});
					}

					@Override
					public void onCancel() {
						mLoadingIndicator.setVisibility(View.GONE);
						Toast.makeText(getApplicationContext(), R.string.log_in_was_canceled, Toast.LENGTH_LONG)
								.show();
					}

					@Override
					public void onError(FacebookException error) {
						mLoadingIndicator.setVisibility(View.GONE);
						Toast.makeText(getApplicationContext(), R.string.unable_to_log_in, Toast.LENGTH_LONG)
								.show();
					}
				});

		NumberUtil.setContext(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		mNumber = 1;
		mUpdateRunning = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				long lastTime = System.currentTimeMillis();

				while (mUpdateRunning) {
					long now = System.currentTimeMillis();
					final double delta = (now - lastTime) / 1000.0;
					lastTime = now;

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mNumber += mNumber * delta;

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mNumberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(mNumber)));

									if (NumberUtil.powerOf(mNumber) >= 3) {
										mNumberNameTextView.setVisibility(View.VISIBLE);
										mNumberNameTextView.setText(NumberUtil.powerName(mNumber));
									} else {
										mNumberNameTextView.setVisibility(View.GONE);
									}
								}
							});
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

	@Override
	protected void onStop() {
		super.onStop();
	}

	@OnClick(R.id.button_log_in)
	void onLogInClick() {
		mLoadingIndicator.setVisibility(View.VISIBLE);

		List<String> permissions = Arrays.asList("public_profile", "user_friends");
		LoginManager.getInstance()
				.logInWithReadPermissions(this, permissions);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

}
