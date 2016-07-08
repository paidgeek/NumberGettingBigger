package com.moybl.topnumber.backend;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpHeaders;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.moybl.topnumber.R;
import com.moybl.topnumber.backend.topNumber.TopNumber;
import com.moybl.topnumber.backend.topNumber.model.CollectionResponsePlayer;
import com.moybl.topnumber.backend.topNumber.model.Player;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	public void logInWithFacebook(final String accessToken, final boolean reset, final ResultCallback<ObjectResult<Player>> callback) {
		doServiceCall(new ServiceCall<ObjectResult<Player>>() {
			@Override
			public ObjectResult<Player> procedure() {
				try {
					TopNumber topNumber = new TopNumber.Builder(AndroidHttp.newCompatibleTransport(),
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

					Player player = topNumber.players()
							.logInWithFacebook()
							.setReset(reset)
							.execute();

					return new ObjectResult<>(player);
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

	public void insertNumber(final double number, final ResultCallback<VoidResult> callback) {
		doServiceCall(new ServiceCall<VoidResult>() {
			@Override
			public VoidResult procedure() {
				try {
					mTopNumber.numbers()
							.insert(number)
							.execute();

					return new VoidResult(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return new VoidResult(false);
			}

			@Override
			public void finished(VoidResult result) {
				callback.onResult(result);
			}
		});
	}

	public void listTop(final String nextPageToken, final ResultCallback<ListTopResult> callback) {
		doServiceCall(new ServiceCall<ListTopResult>() {
			@Override
			public ListTopResult procedure() {
				try {
					CollectionResponsePlayer response = mTopNumber.numbers()
							.listTop()
							.execute();

					StringBuilder ids = new StringBuilder();
					List<Player> players = response.getItems();
					List<PlayerModel> playerModels = new ArrayList<>();

					for (int i = 0; i < players.size(); i++) {
						Player p = players.get(i);
						playerModels.add(new PlayerModel(p.getId(), i + 1, p.getNumber()));

						if(!p.getId().startsWith("t")){
							ids.append(p.getId());

							if (i < players.size() - 1) {
								ids.append(",");
							}
						}
					}

					Bundle parameters = new Bundle();
					parameters.putString("fields", "first_name,picture{is_silhouette}");
					parameters.putString("ids", ids.toString());

					GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "", null);
					graphRequest.setParameters(parameters);
					GraphResponse graphResponse = graphRequest.executeAndWait();

					if(graphResponse.getError() != null){
						Log.e("Graph", graphResponse.getError().getErrorMessage());
					}

					JSONObject data = graphResponse.getJSONObject();
					Iterator<String> keyIterator = data.keys();

					while (keyIterator.hasNext()) {
						String userId = keyIterator.next();
						JSONObject userData = data.getJSONObject(userId);
						String firstName = userData.getString("first_name");
						JSONObject pictureData = userData.getJSONObject("picture")
								.getJSONObject("data");
						boolean isSilhouette = pictureData.getBoolean("is_silhouette");

						for (int i = 0; i < playerModels.size(); i++) {
							PlayerModel pm = playerModels.get(i);

							if (pm.getId().equals(userId)) {
								pm.setSilhouette(isSilhouette);
								pm.setName(firstName);
							}
						}
					}

					return new ListTopResult(playerModels, response.getNextPageToken());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return new ListTopResult();
			}

			@Override
			public void finished(ListTopResult result) {
				callback.onResult(result);
			}
		});
	}

	public void insertTestPlayers() {
		doServiceCall(new ServiceCall<VoidResult>() {
			@Override
			public VoidResult procedure() {
				try {
					mTopNumber.players()
							.insertTestPlayers()
							.execute();

					return new VoidResult(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return new VoidResult(false);
			}

			@Override
			public void finished(VoidResult result) {
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

	public void logOut() {
		new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
				.Callback() {
			@Override
			public void onCompleted(GraphResponse graphResponse) {
				LoginManager.getInstance()
						.logOut();
			}
		}).executeAsync();
	}

}
