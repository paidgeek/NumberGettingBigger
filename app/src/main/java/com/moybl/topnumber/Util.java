package com.moybl.topnumber;

import android.view.View;
import android.widget.TextView;

public class Util {

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

	public static boolean isNameValid(String name){
		return name.length() <= 50 && name.trim().matches("\\p{L}+[\\p{L}\\p{Z}\\p{P}]*");
	}

}
