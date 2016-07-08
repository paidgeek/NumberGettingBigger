package com.moybl.topnumber;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.GraphUtil;
import com.moybl.topnumber.R;
import com.moybl.topnumber.backend.ListTopResult;
import com.moybl.topnumber.backend.ObjectResult;
import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.topNumber.model.CollectionResponsePlayer;
import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderboardActivity extends Activity {

	@BindView(R.id.players_recycler)
	RecyclerView mPlayersRecycler;
	private PlayersAdapter mPlayersAdapter;
	private TopNumberClient mClient;
	private String mNextPageToken;
	private LinearLayoutManager mLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
		ButterKnife.bind(this);

		ActionBar actionBar = getActionBar();

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mLayoutManager = new LinearLayoutManager(this);
		mPlayersAdapter = new PlayersAdapter(this);

		mPlayersRecycler.setLayoutManager(mLayoutManager);
		mPlayersRecycler.addItemDecoration(new VerticalSpaceItemDecoration(10));
		mPlayersRecycler.setAdapter(mPlayersAdapter);
		mPlayersRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0) {
					int childCount = mLayoutManager.getChildCount();
					int totalCount = mLayoutManager.getItemCount();
					int firstVisible = mLayoutManager.findFirstVisibleItemPosition();

					if ((childCount + firstVisible) >= totalCount) {
						loadNextPage();
					}
				}
			}
		});

		mClient = TopNumberClient.getInstance();
	}

	@Override
	protected void onStart() {
		super.onStart();

		loadNextPage();
	}

	private void loadNextPage() {
		mClient.listTop(mNextPageToken, new ResultCallback<ListTopResult>() {
			@Override
			public void onResult(@NonNull ListTopResult result) {
				if (!result.isSuccess()) {
					finish();
					return;
				}

				mPlayersAdapter.getPlayers()
						.addAll(result.getPlayers());
				mPlayersAdapter.notifyDataSetChanged();
				mNextPageToken = result.getNextPageToken();
			}
		});
	}

}
