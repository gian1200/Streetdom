package com.gian1200.games.streetdom.activities.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gian1200.games.streetdom.Clue;
import com.gian1200.games.streetdom.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClueFragment extends DialogFragment {

	private Clue clue;
	private String imageURI;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		clue = getArguments().getParcelable("clue");
		imageURI = getArguments().getString("missionImageURI");
		AlertDialog.Builder dialogBuilder;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			ContextThemeWrapper contextW = new ContextThemeWrapper(
					getActivity(), R.style.AppAlertDialogTheme);
			dialogBuilder = new AlertDialog.Builder(contextW);
		} else {
			dialogBuilder = new AlertDialog.Builder(getActivity(),
					R.style.AppAlertDialogTheme);
		}
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_title_clue, null);
		dialogBuilder.setCustomTitle(v);
		dialogBuilder.setMessage(clue.text);
		dialogBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		//TODO arreglar Bitmap too large to be uploaded into a texture 
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();
		ImageLoader.getInstance().displayImage(imageURI,
				(ImageView) v.findViewById(R.id.clue_image), options);
		((TextView) v.findViewById(R.id.clue_title)).setText(getString(
				R.string.clue_title, clue.name));
		return dialogBuilder.create();
	}
}
