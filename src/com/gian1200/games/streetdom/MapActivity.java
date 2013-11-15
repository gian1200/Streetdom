package com.gian1200.games.streetdom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	Mission mission;
	Place[] places;
	Marker[] markers;
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
		mission = extras.getParcelable(getPackageName() + ".mission");
		places = mission.places;
		markers = new Marker[places.length];
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
							.center(currentLocation)
							.radius(0)
							.strokeColor(
									getResources()
											.getColor(R.color.light_green))
							.strokeWidth(2));
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
							circle.setCenter(currentLocation);
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
				for (int i = 0; i < markers.length; i++) {
					if (marker.getId().equals(markers[i].getId())) {
						Intent intent = new Intent(MapActivity.this,
								PlaceActivity.class);
						intent.putExtra(getPackageName() + ".mission", mission);
						intent.putExtra(getPackageName() + ".currentLocation",
								map.getMyLocation());
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
					// Lugar encontrado
					mission = extras.getParcelable(getPackageName()
							+ ".mission");
					getIntent()
							.putExtra(getPackageName() + ".mission", mission);
					places = mission.places;
					if (mission.isCompleted) {
						setResult(RESULT_OK, data);
						finish();
					} else {
						for (int i = 0; i < places.length; i++) {
							if (i < mission.currentClue) {
								markers[i]
										.setIcon(BitmapDescriptorFactory
												.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
							} else {
								if (places[i].visited) {
									markers[i]
											.setIcon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
								} else {
									// do nothing since it already has the right
									// color
								}
							}
						}
					}
				}
			}
		}
	}

}
