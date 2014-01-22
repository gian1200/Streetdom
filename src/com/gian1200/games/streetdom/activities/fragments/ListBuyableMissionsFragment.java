package com.gian1200.games.streetdom.activities.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.gian1200.games.streetdom.Application;
import com.gian1200.games.streetdom.Mission;
import com.gian1200.games.streetdom.R;

public class ListBuyableMissionsFragment extends ListMissionsFragment {

	@Override
	ArrayList<Mission> getMissions() {
		return ((Application) getActivity().getApplication()).buyableMissions;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		missionsList = (ListView) inflater.inflate(
				R.layout.fragment_missions_page, container, false);
		missions = getMissions();
		missionsList.setAdapter(new BuyableMissionAdapter(getActivity(),
				missions));
		missionsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO ver caracteristicas de la mision y q te mande a
				// comprarla
			}
		});
		return missionsList;
	}

	private class BuyableMissionAdapter extends ListMissionAdapter {

		public BuyableMissionAdapter(Activity activity,
				ArrayList<Mission> missions) {
			super(activity, missions);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(
						R.layout.list_item_mission, parent, false);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.mission_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(getItem(position).name);
			// TODO mostrar precio?
			return convertView;
		}

		private class ViewHolder {
			TextView name;
		}
	}

}
