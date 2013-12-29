package com.gian1200.games.streetdom;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gian1200.util.ColorUtil;

public abstract class ListMissionsFragment extends Fragment {
	ListView missionsList;
	ArrayList<Mission> missions;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		missionsList = (ListView) inflater.inflate(
				R.layout.fragment_missions_page, container, false);
		missions = getMissions();
		missionsList
				.setAdapter(new ListMissionAdapter(getActivity(), missions));
		missionsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), MissionActivity.class);
				intent.putExtra(getActivity().getPackageName() + ".mission",
						((ListMissionAdapter) parent.getAdapter())
								.getItem(position));
				startActivityForResult(intent, 0);
			}
		});
		return missionsList;
	}

	@Override
	public void onResume() {
		super.onResume();
		((ListMissionAdapter) missionsList.getAdapter()).notifyDataSetChanged();
	}

	abstract ArrayList<Mission> getMissions();

	protected class ListMissionAdapter extends BaseAdapter {
		Activity activity;
		ArrayList<Mission> missions;
		int red, yellow, green;

		public ListMissionAdapter(Activity activity, ArrayList<Mission> missions) {
			this.activity = activity;
			this.missions = missions;
			yellow = activity.getResources().getColor(
					R.color.mission_progress_yellow);
			red = activity.getResources()
					.getColor(R.color.mission_progress_red);
			green = activity.getResources().getColor(
					R.color.mission_progress_green);
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
				holder.progress = (TextView) convertView
						.findViewById(R.id.mission_progress);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			float progress = getItem(position).progress * 100;
			if (progress <= 50) {
				holder.progress.setTextColor(ColorUtil.getColorBetween(red,
						yellow, progress / 50));
			} else {
				holder.progress.setTextColor(ColorUtil.getColorBetween(yellow,
						green, (progress - 50) / 50));
			}
			holder.name.setText(getItem(position).name);
			holder.progress.setText(getString(R.string.progress, progress));
			return convertView;
		}

		private class ViewHolder {
			TextView name, progress;
		}
	}
}
