package com.moybl.topnumber;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;

public class Util {

	private static Context sContext;

	public static void setContext(Context context){
		sContext = context;
	}

	public static void setVisible(View view) {
		int v = view.getVisibility();

		if (v == View.GONE) {
			view.setVisibility(View.VISIBLE);
		}
	}

	public static void setGone(View view) {
		int v = view.getVisibility();

		if (v == View.VISIBLE) {
			view.setVisibility(View.GONE);
		}
	}

	public static void setChangedText(TextView textView, String text) {
		if (!textView.getText()
				.equals(text)) {
			textView.setText(text);
		}
	}

	public static void playSound( int resId) {
		MediaPlayer mp = MediaPlayer.create(sContext, resId);
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		mp.start();
	}

}
