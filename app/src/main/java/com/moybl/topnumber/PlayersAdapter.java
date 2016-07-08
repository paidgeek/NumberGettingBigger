package com.moybl.topnumber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moybl.topnumber.backend.PlayerModel;

import java.util.ArrayList;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayerViewModel> {

	private List<PlayerModel> mPlayers;
	private LayoutInflater mInflater;

	public PlayersAdapter(Context context) {
		mPlayers = new ArrayList<>();
		mInflater = LayoutInflater.from(context);
	}

	public List<PlayerModel> getPlayers() {
		return mPlayers;
	}

	@Override
	public PlayerViewModel onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = mInflater.inflate(R.layout.item_player_number, parent, false);

		return new PlayerViewModel(v);
	}

	@Override
	public void onBindViewHolder(PlayerViewModel holder, int position) {
		PlayerModel player = mPlayers.get(position);

		holder.setPlayer(player);
	}

	@Override
	public int getItemCount() {
		return mPlayers.size();
	}

}
