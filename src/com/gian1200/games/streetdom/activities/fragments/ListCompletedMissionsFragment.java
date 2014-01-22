package com.gian1200.games.streetdom;

import java.util.ArrayList;

public class ListCompletedMissionsFragment extends ListMissionsFragment {

	@Override
	ArrayList<Mission> getMissions() {
		return ((Application) getActivity().getApplication()).completedMissions;
	}

}
