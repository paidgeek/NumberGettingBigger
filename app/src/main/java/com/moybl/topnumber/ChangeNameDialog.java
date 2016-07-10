package com.moybl.topnumber;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeNameDialog extends DialogFragment {

	public interface OnClickListener {
		void onOkClick(String name);

		void onCancelClick();
	}

	private OnClickListener mOnClickListener;
	private Button mPositiveButton;

	public void setOnClickListener(OnClickListener onClickListener) {
		this.mOnClickListener = onClickListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_change_name, null);

		final EditText nameEdit = (EditText) v.findViewById(R.id.edit_name);

		builder.setView(v)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mOnClickListener.onOkClick(nameEdit.getText()
								.toString()
								.trim());
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mOnClickListener.onCancelClick();
						ChangeNameDialog.this.getDialog()
								.cancel();
					}
				});

		AlertDialog dialog = builder.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				mPositiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

				nameEdit.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (Util.isNameValid(s.toString())) {
							nameEdit.setError(null);
							mPositiveButton.setEnabled(true);
						} else {
							nameEdit.setError(getString(R.string.invalid_name));
							mPositiveButton.setEnabled(false);
						}
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}
		});

		return dialog;
	}

}
