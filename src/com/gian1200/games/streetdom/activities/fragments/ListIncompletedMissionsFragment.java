package com.gian1200.games.streetdom.activities.fragments;

import java.util.ArrayList;

import com.gian1200.games.streetdom.Application;
import com.gian1200.games.streetdom.Mission;

public class ListIncompletedMissionsFragment extends ListMissionsFragment {

	@Override
	ArrayList<Mission> getMissions() {
		return ((Application) getActivity().getApplication()).incompletedMissions;
	}

}
