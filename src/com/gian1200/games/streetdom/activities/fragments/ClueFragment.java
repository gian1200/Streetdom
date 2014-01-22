package com.gian1200.games.streetdom.activities.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

import com.gian1200.games.streetdom.Clue;
import com.gian1200.games.streetdom.R;

public class ClueFragment extends DialogFragment {

	private Clue clue;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		clue = getArguments().getParcelable("clue");
		AlertDialog.Builder dialogBuilder;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			ContextThemeWrapper contextW = new ContextThemeWrapper(
					getActivity(), R.style.AppAlertDialogTheme);
			dialogBuilder = new AlertDialog.Builder(contextW);
		} else {
			dialogBuilder = new AlertDialog.Builder(getActivity(),
					R.style.AppAlertDialogTheme);
		}
		dialogBuilder.setTitle(getString(R.string.clue_title, clue.name));
		dialogBuilder.setMessage(clue.text);
		dialogBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		return dialogBuilder.create();
	}
}
