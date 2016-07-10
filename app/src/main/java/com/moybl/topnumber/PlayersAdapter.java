package com.moybl.topnumber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayerLeaderboardViewModel> {

	private List<Player> mPlayers;
	private LayoutInflater mInflater;

	public PlayersAdapter(Context context) {
		mPlayers = new ArrayList<>();
		mInflater = LayoutInflater.from(context);
	}

	public List<Player> getPlayers() {
		return mPlayers;
	}

	@Override
	public PlayerLeaderboardViewModel onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = mInflater.inflate(R.layout.item_player_number, parent, false);

		return new PlayerLeaderboardViewModel(v);
	}

	@Override
	public void onBindViewHolder(PlayerLeaderboardViewModel holder, int position) {
		Player player = mPlayers.get(position);

		holder.setPlayer(player, position);
	}

	@Override
	public int getItemCount() {
		return mPlayers.size();
	}

}
