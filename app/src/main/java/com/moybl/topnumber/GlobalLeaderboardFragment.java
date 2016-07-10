package com.moybl.topnumber;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moybl.topnumber.backend.ListTopResult;
import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;

public class GlobalLeaderboardFragment extends Fragment {

	private RecyclerView mPlayersRecycler;
	private PlayersAdapter mPlayersAdapter;
	private TopNumberClient mClient;
	private String mNextPageToken;
	private LinearLayoutManager mLayoutManager;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void loadNextPage() {
		final View loadingIndicator = LeaderboardActivity.getInstance()
				.getLoadingIndicator();
		loadingIndicator.setVisibility(View.VISIBLE);

		mClient.listTop(mNextPageToken, new ResultCallback<ListTopResult>() {
			@Override
			public void onResult(@NonNull ListTopResult result) {
				loadingIndicator.setVisibility(View.GONE);

				if (!result.isSuccess()) {
					return;
				}

				mPlayersAdapter.getPlayers()
						.addAll(result.getPlayers());
				mPlayersAdapter.notifyDataSetChanged();
				mNextPageToken = result.getNextPageToken();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		loadNextPage();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_global_leaderboard, container, false);
		mPlayersRecycler = (RecyclerView) v.findViewById(R.id.players_recycler);

		mLayoutManager = new LinearLayoutManager(getActivity());
		mPlayersAdapter = new PlayersAdapter(getActivity());

		mPlayersRecycler.setLayoutManager(mLayoutManager);
		mPlayersRecycler.addItemDecoration(new VerticalSpaceItemDecoration(10));
		mPlayersRecycler.setAdapter(mPlayersAdapter);
		mPlayersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

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

		return v;
	}

}
