package com.moybl.topnumber;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ChangeNameDialog extends DialogFragment {

	public interface OnClickListener {
		void onOkClick(String name);

		void onCancelClick();
	}

	private OnClickListener mOnClickListener;

	public void setOnClickListener(OnClickListener onClickListener) {
		this.mOnClickListener = onClickListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_change_name, null);

		final EditText editName = (EditText) v.findViewById(R.id.edit_name);

		builder.setView(v)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mOnClickListener.onOkClick(editName.getText()
								.toString());
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mOnClickListener.onCancelClick();
						ChangeNameDialog.this.getDialog()
								.cancel();
					}
				});
		return builder.create();
	}

}
