package com.gian1200.games.streetdom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ClueFragment extends DialogFragment {

	private Clue clue;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity(), R.style.AppAlertDialogTheme);
		clue = getArguments().getParcelable("clue");
		dialogBuilder.setTitle(getString(R.string.clue_title, clue.name));
		dialogBuilder.setMessage(clue.text);
		// dialogBuilder.setView(getActivity().getLayoutInflater().inflate(
		// R.layout.fragment_clue, null));
		dialogBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		return dialogBuilder.create();
	}
}
