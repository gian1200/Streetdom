package com.gian1200.games.streetdom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gian1200.util.ColorUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements LocationListener {
	private Mission mission;
	private Place[] places;
	private Marker[] markers;

	private LocationClient locationClient;
	private Marker currentLocation;
	private Circle circle;
	private ValueAnimator circleAnimator;

	private MapFragment mapFragment;
	private GoogleMap map;

	private boolean mapHasLoaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapHasLoaded = false;
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(
				R.id.map);
		map = mapFragment.getMap();
		Bundle extras = getIntent().getExtras();
		mission = extras.getParcelable(getPackageName() + ".mission");
		places = mission.places;
		markers = new Marker[places.length];
		loadPlacesMarkers();
		moveCameraToFitAllMarkersWhenReady(false);
		loadLocationUpdater();
		ListView placesList = (ListView) findViewById(R.id.map_places_list);
		if (placesList != null) {
			placesList.setAdapter(new PlaceListAdapter(this, mission.places));
			placesList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					markers[position].showInfoWindow();
					map.animateCamera(CameraUpdateFactory
							.newLatLng(markers[position].getPosition()), 400,
							null);
				}
			});
		}
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
		// -12.07250575708222, -76.9514923542738), 16));

		// map.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng point) {
		// Log.d("", "new LatLng(" + point.latitude + ", "
		// + point.longitude + ");");
		// map.addMarker(new MarkerOptions().position(new LatLng(
		// point.latitude, point.longitude)));
		//
		// }
		// });
		// Polygon polygon = map.addPolygon(new PolygonOptions()
		// .add(new LatLng(-70, -75), new LatLng(-73, -75),
		// new LatLng(-70, -70)).strokeColor(Color.RED)
		// .fillColor(Color.BLUE).geodesic(true));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivityForResult(
					new Intent(this, SettingsActivity.class),
					getResources().getInteger(
							R.integer.request_code_settings_activity));
			return true;
		case R.id.action_focus_places:
			focusPlaces(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (circleAnimator != null && !circleAnimator.isRunning()) {
			circleAnimator.start();
		}
	}

	@Override
	protected void onPause() {
		if (circleAnimator != null && circleAnimator.isRunning()) {// isStarted()
			circleAnimator.cancel();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (locationClient.isConnected()) {
			locationClient.removeLocationUpdates(this);
		}
		locationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (getResources().getInteger(R.integer.result_code_erase_data) == resultCode) {
			setResult(resultCode);
			finish();
			return;
		}
		if (getResources().getInteger(R.integer.request_code_place_activity) == requestCode) {
			Bundle extras = data.getExtras();
			mission = extras.getParcelable(getPackageName() + ".mission");
			getIntent().putExtra(getPackageName() + ".mission", mission);
			places = mission.places;
			if (mission.isCompleted) {
				setResult(resultCode, data);// result_code_mission_completed
				finish();
				return;
			} else {
				// - Lugar encontrado result_code_right_place
				// - back pressed, lugar verificado o no
				updateMarkersColor();
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		LatLng position = new LatLng(location.getLatitude(),
				location.getLongitude());
		if (currentLocation == null) {
			loadCurrentPosition(position);
		} else {
			currentLocation.setPosition(position);
		}

	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra(getPackageName() + ".mission", mission);
		setResult(RESULT_OK, data);
		finish();
	}

	void loadPlacesMarkers() {
		for (int i = 0; i < places.length; i++) {
			Place place = places[i];
			// no visitado HUE_RED
			// visitado HUE_ORANGE
			// checked HUE_AZURE
			MarkerOptions markerOption = new MarkerOptions()
					.position(new LatLng(place.latitude, place.longitude))
					.title(place.name).snippet(place.description);
			if (i < mission.currentClue) {
				markerOption.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			} else {
				if (places[i].visited) {
					markerOption.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
				} else {
					// do nothing since it already has the right
					// color
				}
			}
			markers[i] = map.addMarker(markerOption);
		}
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				for (int i = 0; i < markers.length; i++) {
					if (marker.getId().equals(markers[i].getId())) {
						Intent intent = new Intent(MapActivity.this,
								PlaceActivity.class);
						intent.putExtra(getPackageName() + ".mission", mission);
						// intent.putExtra(getPackageName() +
						// ".currentLocation",
						// map.getMyLocation());
						intent.putExtra(getPackageName() + ".currentLocation",
								currentLocation.getPosition());
						intent.putExtra(getPackageName() + ".place", places[i]);
						startActivityForResult(
								intent,
								getResources().getInteger(
										R.integer.request_code_place_activity));
					}
				}
			}
		});
	}

	void moveCameraToFitAllMarkersWhenReady(final boolean animateCamera) {
		ViewTreeObserver viewTreeObserver = mapFragment.getView()
				.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver
					.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							mapHasLoaded = true;
							focusPlaces(animateCamera);
						}
					});
		}
	}

	void focusPlaces(boolean animateCamera) {
		View mapView = mapFragment.getView();
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : markers) {
			builder.include(marker.getPosition());
		}
		if (currentLocation != null) {
			builder.include(currentLocation.getPosition());
		}
		LatLngBounds bounds = builder.build();
		int paddingInPixels = Math.min(mapView.getWidth(), mapView.getHeight());
		CameraUpdate newLatLngBounds = CameraUpdateFactory.newLatLngBounds(
				bounds, paddingInPixels / 4);
		if (animateCamera) {
			map.animateCamera(newLatLngBounds);
		} else {
			map.moveCamera(newLatLngBounds);
		}
	}

	void loadCurrentPosition(LatLng position) {
		MarkerOptions markerOption = new MarkerOptions()
				.position(position)
				.anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.map_current_position));
		currentLocation = map.addMarker(markerOption);
		loadCircle(position);
		if (mapHasLoaded) {
			focusPlaces(true);
		}
	}

	void loadCircle(LatLng center) {
		if (circle == null) {
			circle = map.addCircle(new CircleOptions().center(center).radius(0)
					.strokeColor(getResources().getColor(R.color.light_green))
					.strokeWidth(2));
			circleAnimator = ValueAnimator.ofFloat(0, getResources()
					.getInteger(R.integer.radius_range));
			circleAnimator.setRepeatCount(ValueAnimator.INFINITE);
			circleAnimator.setRepeatMode(ValueAnimator.RESTART); /* PULSE */
			circleAnimator.setDuration(2500);

			circleAnimator.setInterpolator(new DecelerateInterpolator());
			circleAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					circle.setRadius((Float) animation.getAnimatedValue());
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
						circle.setStrokeColor(ColorUtil.setAlpha0to1(
								circle.getStrokeColor(),
								(float) (1 - (double) animation
										.getCurrentPlayTime()
										/ (double) animation.getDuration())));
					} else {
						circle.setStrokeColor(ColorUtil.setAlpha0to1(
								circle.getStrokeColor(),
								1 - animation.getAnimatedFraction()));
					}
				}
			});
			circleAnimator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					circle.setCenter(currentLocation.getPosition());
				}

			});
			if (!circleAnimator.isRunning()) {
				circleAnimator.start();
			}
		}
	}

	void loadLocationUpdater() {
		locationClient = new LocationClient(this, new ConnectionCallbacks() {

			@Override
			public void onDisconnected() {
			}

			@Override
			public void onConnected(Bundle connectionHint) {
				Location lastLocation = locationClient.getLastLocation();
				if (lastLocation != null && currentLocation == null) {
					loadCurrentPosition(new LatLng(lastLocation.getLatitude(),
							lastLocation.getLongitude()));
				}
				locationClient.requestLocationUpdates(LocationRequest.create(),
						MapActivity.this);

			}
		}, new OnConnectionFailedListener() {

			@Override
			public void onConnectionFailed(ConnectionResult result) {
				/*
				 * Google Play services can resolve some errors it detects. If
				 * the error has a resolution, try sending an Intent to start a
				 * Google Play services activity that can resolve error.
				 */
				if (result.hasResolution()) {
					try {
						// Start an Activity that tries to resolve the error
						result.startResolutionForResult(
								MapActivity.this,
								getResources()
										.getInteger(
												R.integer.result_code_connection_failure));
						/*
						 * Thrown if Google Play services canceled the original
						 * PendingIntent
						 */
					} catch (IntentSender.SendIntentException e) {
						// Log the error
						e.printStackTrace();
					}
				} else {
					/*
					 * If no resolution is available, display a dialog to the
					 * user with the error.
					 */
					Log.d("locationClient.onConnectionFailed",
							result.toString());
					GooglePlayServicesUtil.getErrorDialog(
							result.getErrorCode(),
							MapActivity.this,
							getResources().getInteger(
									R.integer.result_code_connection_failure))
							.show();

				}

			}
		});
	}

	void updateMarkersColor() {
		for (int i = 0; i < places.length; i++) {
			if (i < mission.currentClue) {
				markers[i].setIcon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			} else {
				if (places[i].visited) {
					markers[i].setIcon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
				} else {
					// do nothing since it already has the right
					// color HUE_RED (Default)
				}
			}
		}
	}

	private class PlaceListAdapter extends BaseAdapter {
		MapActivity mapActivity;
		Place[] places;

		public PlaceListAdapter(MapActivity mapActivity, Place[] places) {
			super();
			this.mapActivity = mapActivity;
			this.places = places;
		}

		@Override
		public int getCount() {
			return places.length;
		}

		@Override
		public Place getItem(int position) {
			return places[position];
		}

		@Override
		public long getItemId(int position) {
			return places[position].id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mapActivity.getLayoutInflater().inflate(
						R.layout.list_item_place_map, parent, false);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.place_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(getItem(position).name);
			return convertView;
		}

		private class ViewHolder {
			TextView name;
		}
	}
}
