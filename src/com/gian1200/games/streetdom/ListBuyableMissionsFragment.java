package com.gian1200.games.streetdom;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListBuyableMissionsFragment extends Fragment {
	ListView missionsList;
	ArrayList<Mission> missions;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		missionsList = (ListView) inflater.inflate(
				R.layout.fragment_missions_page, container, false);
		missions = getArguments().getParcelableArrayList("missions");
		missionsList.setAdapter(new MissionAdapter(getActivity(), missions));
		missionsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO ver caracteristicas de la mision y q te mande a
				// comprarla
				// Intent intent = new Intent(getActivity(),
				// MissionActivity.class);
				// intent.putExtra(getActivity().getPackageName() + ".mission",
				// ((MissionAdapter) parent.getAdapter())
				// .getItem(position));
				// startActivity(intent);
			}
		});
		return missionsList;
	}

	@Override
	public void onResume() {
		super.onResume();
		((MissionAdapter) missionsList.getAdapter()).notifyDataSetChanged();
	}

	private class MissionAdapter extends BaseAdapter {
		Activity activity;
		ArrayList<Mission> missions;

		public MissionAdapter(Activity activity, ArrayList<Mission> missions) {
			this.activity = activity;
			this.missions = missions;
		}

		@Override
		public int getCount() {
			return missions.size();
		}

		@Override
		public Mission getItem(int position) {
			return missions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return missions.get(position).id;
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
				// holder.progress = (TextView) convertView
				// .findViewById(R.id.mission_progress);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(getItem(position).name);
			// TODO mostrar precio?
			// holder.progress.setText(String.format("%3.0f%%",
			// getItem(position).progress * 100));
			return convertView;
		}

		private class ViewHolder {
			TextView name;
		}
	}
}
