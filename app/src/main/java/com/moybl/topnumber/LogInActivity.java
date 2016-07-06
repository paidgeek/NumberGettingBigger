package com.moybl.topnumber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
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

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends Activity {

	private CallbackManager mCallbackManager;

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
						boolean reset = Prefs.getBoolean(NumberData.KEY_RESET, false);

						final TopNumberClient client = TopNumberClient.getInstance();
						client.setContext(getApplicationContext());
						String accessToken = loginResult.getAccessToken()
								.getToken();

						client.logInWithFacebook(accessToken, reset, new ResultCallback<ObjectResult<Player>>() {
							@Override
							public void onResult(@NonNull ObjectResult<Player> result) {
								if (result.isSuccess()) {
									Prefs.removeAll();
									Prefs.save();

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
						Toast.makeText(getApplicationContext(), R.string.log_in_was_canceled, Toast.LENGTH_LONG)
								.show();
					}

					@Override
					public void onError(FacebookException error) {
						Toast.makeText(getApplicationContext(), R.string.unable_to_log_in, Toast.LENGTH_LONG)
								.show();
					}
				});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@OnClick(R.id.button_log_in)
	void onLogInClick() {
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
