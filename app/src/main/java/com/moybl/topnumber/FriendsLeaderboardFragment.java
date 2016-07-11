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

import com.moybl.topnumber.backend.ListFriendsResult;
import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendsLeaderboardFragment extends Fragment {

	private RecyclerView mFriendsRecycler;
	private PlayersAdapter mFriendsAdapter;
	private TopNumberClient mClient;
	private LinearLayoutManager mLayoutManager;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void load() {
		final View loadingIndicator = LeaderboardActivity.getInstance()
				.getLoadingIndicator();
		loadingIndicator.setVisibility(View.VISIBLE);

		mClient.listFriends(new ResultCallback<ListFriendsResult>() {
			@Override
			public void onResult(@NonNull ListFriendsResult result) {
				loadingIndicator.setVisibility(View.GONE);

				if (!result.isSuccess()) {
					return;
				}

				List<Player> friends = result.getFriends();
				friends.add(mClient.getPlayer());
				Collections.sort(friends, new Comparator<Player>() {
					@Override
					public int compare(Player lhs, Player rhs) {
						return rhs.getNumber().compareTo(lhs.getNumber());
					}
				});

				mFriendsAdapter.getPlayers()
						.clear();
				mFriendsAdapter.getPlayers().addAll(friends);
				mFriendsAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		load();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friends_leaderboard, container, false);
		mFriendsRecycler = (RecyclerView) v.findViewById(R.id.friends_recycler);

		mLayoutManager = new LinearLayoutManager(getActivity());
		mFriendsAdapter = new PlayersAdapter(getActivity());

		mFriendsRecycler.setLayoutManager(mLayoutManager);
		mFriendsRecycler.addItemDecoration(new VerticalSpaceItemDecoration(10));
		mFriendsRecycler.setAdapter(mFriendsAdapter);
		mFriendsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
						load();
					}
				}
			}
		});

		mClient = TopNumberClient.getInstance();

		return v;
	}

}
