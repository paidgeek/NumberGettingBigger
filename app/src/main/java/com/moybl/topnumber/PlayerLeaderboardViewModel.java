package com.moybl.topnumber;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.topNumber.model.Player;
import com.squareup.picasso.Picasso;

public class PlayerLeaderboardViewModel extends RecyclerView.ViewHolder {

  public PlayerLeaderboardViewModel(View itemView) {
    super(itemView);
  }

  public void setPlayer(Player player, int position) {
    ImageView picture = (ImageView) itemView.findViewById(R.id.img_player_picture);
    TextView nameTextView = (TextView) itemView.findViewById(R.id.tv_player_name);
    TextView numberTextView = (TextView) itemView.findViewById(R.id.tv_player_number);
    TextView numberNameTextView = (TextView) itemView.findViewById(R.id.tv_player_number_name);
    TextView orderTextView = (TextView) itemView.findViewById(R.id.tv_player_order);

    Picasso.with(itemView.getContext())
        .load(String.format("https://graph.facebook.com/%s/picture?type=square", player.getId()))
        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
        .into(picture);

    orderTextView.setText(NumberUtil.format(position + 1));

    nameTextView.setText(player.getName());
    numberTextView.setText(NumberUtil.format(NumberUtil.firstDigits(player.getNumber())));

    if (NumberUtil.powerOf(player.getNumber()) >= 3) {
      Util.setVisible(numberNameTextView);

      numberNameTextView.setText(NumberUtil.powerName(player.getNumber()));
    } else {
      Util.setGone(numberNameTextView);
    }

    if (player.getId().equals(TopNumberClient.getInstance().getPlayer().getId())) {
      itemView.setBackgroundResource(R.color.highlight_dark);
    } else {
      itemView.setBackgroundResource(R.color.primary_dark);
    }
  }

}
