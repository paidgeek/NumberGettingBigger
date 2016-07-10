package com.moybl.topnumber.backend;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpHeaders;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.moybl.topnumber.R;
import com.moybl.topnumber.backend.topNumber.TopNumber;
import com.moybl.topnumber.backend.topNumber.model.ListPlayersResponse;
import com.moybl.topnumber.backend.topNumber.model.Player;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
									headers.set("X-Access-Token", accessToken);
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

	public void changeName(final String name, final ResultCallback<VoidResult> callback) {
		doServiceCall(new ServiceCall<ObjectResult<Player>>() {
			@Override
			public ObjectResult<Player> procedure() {
				try {
					Player player = mTopNumber.players()
							.changeName(name)
							.execute();

					return new ObjectResult<>(player);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return new ObjectResult<>();
			}

			@Override
			public void finished(ObjectResult<Player> result) {
				if (result.isSuccess()) {
					Player p = result.getObject();

					mPlayer.setName(p.getName());
					mPlayer.setLastNameChangeAt(p.getLastNameChangeAt());
				}

				callback.onResult(new VoidResult(result.isSuccess()));
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
					ListPlayersResponse response = mTopNumber.numbers()
							.listTop()
							.setNextPageToken(nextPageToken)
							.execute();

					if (response.getPlayers()
							.size() == 0) {
						return new ListTopResult();
					}

					return new ListTopResult(response.getPlayers(), response.getNextPageToken());
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

	public void listPlayers(final List<String> playerIds, final ResultCallback<ObjectResult<List<Player>>> callback) {
		doServiceCall(new ServiceCall<ObjectResult<List<Player>>>() {
			@Override
			public ObjectResult<List<Player>> procedure() {
				try {
					StringBuilder playerIdsList = new StringBuilder();

					for (int i = 0; i < playerIds.size(); i++) {
						playerIdsList.append(playerIds.get(i));

						if (i < playerIds.size() - 1) {
							playerIdsList.append(',');
						}
					}

					List<Player> players = mTopNumber.numbers()
							.listPlayers(playerIdsList.toString())
							.execute()
							.getItems();

					return new ObjectResult<>(players);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return new ObjectResult<>();
			}

			@Override
			public void finished(ObjectResult<List<Player>> result) {
				callback.onResult(result);
			}
		});
	}

	public void listFriends(final ResultCallback<ListFriendsResult> callback) {
		new GraphRequest(
				AccessToken.getCurrentAccessToken(),
				"/me/friends",
				null,
				HttpMethod.GET,
				new GraphRequest.Callback() {
					public void onCompleted(final GraphResponse response) {
						if (response.getError() != null) {
							Log.e("Graph", response.getError()
									.getErrorMessage());
							callback.onResult(new ListFriendsResult());
							return;
						}

						JSONArray friendsData = response.getJSONObject()
								.optJSONArray("data");
						List<String> playerIds = new ArrayList<>();

						for (int i = 0; i < friendsData.length(); i++) {
							JSONObject friendData = friendsData.optJSONObject(i);

							playerIds.add(friendData.optString("id"));
						}

						// TODO: remove
						playerIds.addAll(Arrays.asList("100003233160538", "t406882518659246325", "t1865223393713574978"));

						listPlayers(playerIds, new ResultCallback<ObjectResult<List<Player>>>() {
							@Override
							public void onResult(@NonNull ObjectResult<List<Player>> result) {
								if (result.isSuccess()) {
									callback.onResult(new ListFriendsResult(result.getObject()));
								} else {
									callback.onResult(new ListFriendsResult());
								}
							}
						});
					}
				}
		).executeAsync();
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
