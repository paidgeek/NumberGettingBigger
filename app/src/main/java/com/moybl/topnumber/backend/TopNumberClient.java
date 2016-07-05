package com.moybl.topnumber.backend;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpHeaders;

import android.content.Context;
import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.moybl.topnumber.R;
import com.moybl.topnumber.backend.topNumber.TopNumber;
import com.moybl.topnumber.backend.topNumber.model.Player;

import java.io.IOException;

public class TopNumberClient {

	private static TopNumberClient sInstance;

	public synchronized static TopNumberClient getInstance() {
		if (sInstance == null) {
			sInstance = new TopNumberClient();
		}

		return sInstance;
	}

	private TopNumber mTopNumber;
	private Context mContext;
	private Player mPlayer;

	public void logInWithFacebook(final String accessToken, final ResultCallback<ObjectResult<Player>> callback) {
		doServiceCall(new ServiceCall<ObjectResult<Player>>() {
			@Override
			public ObjectResult<Player> procedure() {
				try {
					TopNumber yayNay = new TopNumber.Builder(AndroidHttp.newCompatibleTransport(),
							new AndroidJsonFactory(), null)
							.setApplicationName(mContext.getPackageName())
							.setRootUrl(mContext.getString(R.string.api_url))
							.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
								@Override
								public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
									HttpHeaders headers = request.getRequestHeaders();
									headers.setAuthorization(accessToken);
									request.setRequestHeaders(headers);
								}
							})
							.build();

					Player asker = yayNay.players()
							.logInWithFacebook()
							.execute();

					return new ObjectResult<>(asker);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return new ObjectResult<>();
			}

			@Override
			public void finished(ObjectResult<Player> result) {
				mPlayer = result.getObject();
				createAuthenticatedClient();
				callback.onResult(result);
			}
		});
	}

	private void createAuthenticatedClient() {
		if (mPlayer == null) {
			return;
		}

		mTopNumber = new TopNumber.Builder(AndroidHttp.newCompatibleTransport(),
				new AndroidJsonFactory(), null)
				.setApplicationName(mContext.getPackageName())
				.setRootUrl(mContext.getString(R.string.api_url))
				.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
					@Override
					public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
						HttpHeaders headers = request.getRequestHeaders();
						headers.set("X-Player-Id", mPlayer.getId());
						headers.set("X-Session-Token", mPlayer.getSessionToken());
						request.setRequestHeaders(headers);
					}
				})
				.build();
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public Player getPlayer() {
		return mPlayer;
	}

	private interface ServiceCall<T> {
		T procedure();

		void finished(T result);
	}

	private <T> void doServiceCall(final ServiceCall<T> serviceCall) {
		new AsyncTask<Void, Void, T>() {
			@Override
			protected T doInBackground(Void... params) {
				return serviceCall.procedure();
			}

			@Override
			protected void onPostExecute(T result) {
				serviceCall.finished(result);
			}
		}.execute();
	}

}
