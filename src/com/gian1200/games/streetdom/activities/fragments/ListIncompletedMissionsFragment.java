package com.gian1200.games.streetdom;

import java.util.ArrayList;

public class ListIncompletedMissionsFragment extends ListMissionsFragment {

	@Override
	ArrayList<Mission> getMissions() {
		return ((Application) getActivity().getApplication()).incompletedMissions;
	}

}
