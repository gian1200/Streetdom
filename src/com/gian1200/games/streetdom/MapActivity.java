package com.gian1200.games.streetdom;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

	Clue currentClue;
	Place[] places;
	String[] markersId;
	// private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	// private LocationClient locationClient;
	GoogleMap map;
	ValueAnimator circleAnimator;
	float radiusRange;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		radiusRange = getResources().getInteger(R.integer.radius_range);
		Bundle extras = getIntent().getExtras();
		Parcelable[] parcelables = extras.getParcelableArray(getPackageName()
				+ ".places");
		places = new Place[parcelables.length];
		System.arraycopy(parcelables, 0, places, 0, parcelables.length);
		currentClue = extras.getParcelable(getPackageName() + ".currentClue");
		markersId = new String[places.length];
		for (int i = 0; i < places.length; i++) {
			Place place = places[i];
			Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(place.latitude, place.longitude))
					.title(place.name).snippet(place.description));
			markersId[i] = marker.getId();
		}

		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			boolean firstTime = true;
			Circle circle;
			LatLng currentLocation;

			@Override
			public void onMyLocationChange(Location location) {
				currentLocation = new LatLng(location.getLatitude(), location
						.getLongitude());
				if (firstTime) {
					firstTime = false;

					map.animateCamera(CameraUpdateFactory.newLatLngZoom(
							currentLocation, 18));
					circle = map.addCircle(new CircleOptions()
							.center(currentLocation).radius(0)
							.strokeColor(Color.RED).strokeWidth(2));
					circleAnimator = ValueAnimator.ofFloat(0, radiusRange);
					circleAnimator.setRepeatCount(ValueAnimator.INFINITE);
					circleAnimator.setRepeatMode(ValueAnimator.RESTART); /* PULSE */
					circleAnimator.setDuration(1500);

					circleAnimator
							.setInterpolator(new DecelerateInterpolator());
					circleAnimator
							.addUpdateListener(new AnimatorUpdateListener() {

								@Override
								public void onAnimationUpdate(
										ValueAnimator animation) {
									float animatedFraction = (Float) animation
											.getAnimatedValue();
									circle.setRadius(animatedFraction);
								}
							});
					if (!circleAnimator.isRunning()) {
						circleAnimator.start();
					}
				}
				// map.addMarker(new MarkerOptions().position(currentLocation));
			}
		});
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				for (int i = 0; i < markersId.length; i++) {
					if (marker.getId().equals(markersId[i])) {
						Intent intent = new Intent(MapActivity.this,
								PlaceActivity.class);
						intent.putExtra(getPackageName() + ".currentLocation",
								map.getMyLocation());
						intent.putExtra(getPackageName() + ".currentClue",
								currentClue);
						intent.putExtra(getPackageName() + ".place", places[i]);
						startActivityForResult(intent, 0);
					}
				}
			}
		});

		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
		// -12.07250575708222, -76.9514923542738), 16));

		// locationClient = new LocationClient(this, new ConnectionCallbacks() {
		//
		// @Override
		// public void onDisconnected() {
		// // Display the connection status
		// Toast.makeText(MapActivity.this,
		// "Disconnected. Please re-connect.", Toast.LENGTH_SHORT)
		// .show();
		// }
		//
		// @Override
		// public void onConnected(Bundle connectionHint) {
		// // Display the connection status
		// Toast.makeText(MapActivity.this, "Connected",
		// Toast.LENGTH_SHORT).show();
		//
		// }
		// }, new OnConnectionFailedListener() {
		//
		// @Override
		// public void onConnectionFailed(ConnectionResult result) {
		// /*
		// * Google Play services can resolve some errors it detects. If
		// * the error has a resolution, try sending an Intent to start a
		// * Google Play services activity that can resolve error.
		// */
		// if (result.hasResolution()) {
		// try {
		// // Start an Activity that tries to resolve the error
		// result.startResolutionForResult(MapActivity.this,
		// CONNECTION_FAILURE_RESOLUTION_REQUEST);
		// /*
		// * Thrown if Google Play services canceled the original
		// * PendingIntent
		// */
		// } catch (IntentSender.SendIntentException e) {
		// // Log the error
		// e.printStackTrace();
		// }
		// } else {
		// /*
		// * If no resolution is available, display a dialog to the
		// * user with the error.
		// */
		// // TODO
		// // showErrorDialog(result.getErrorCode());
		// }
		//
		// }
		// });
		// locationClient.connect();

		// map.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng point) {
		// Log.i("", "new LatLng(" + point.latitude + ", "
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
	protected void onPause() {
		if (circleAnimator != null && circleAnimator.isRunning()) {// isStarted()
			circleAnimator.cancel();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (circleAnimator != null && !circleAnimator.isRunning()) {
			circleAnimator.start();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				Bundle extras = data.getExtras();
				if (getResources()
						.getInteger(R.integer.result_code_right_place) == extras
						.getInt(getPackageName() + ".resultCode")) {
					setResult(RESULT_OK, data);
					finish();
				}
			}
		}
	}

}
