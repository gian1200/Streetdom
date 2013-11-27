package com.gian1200.games.streetdom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

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

public class MapActivity extends Activity {
	private Mission mission;
	private Place[] places;
	private Marker[] markers;

	private LocationClient locationClient;
	private Marker currentLocation;
	private Circle circle;
	private ValueAnimator circleAnimator;

	private MapFragment mapFragment;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map));
		map = mapFragment.getMap();
		Bundle extras = getIntent().getExtras();
		mission = extras.getParcelable(getPackageName() + ".mission");
		places = mission.places;
		markers = new Marker[places.length];
		loadPlacesMarkers();
		moveCameraToFitAllMarkers(false);
		loadLocationUpdater();
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
		if (getResources().getInteger(R.integer.request_code_place_activity) == requestCode) {
			Bundle extras = data.getExtras();
			mission = extras.getParcelable(getPackageName() + ".mission");
			getIntent().putExtra(getPackageName() + ".mission", mission);
			places = mission.places;
			if (mission.isCompleted) {
				setResult(resultCode, data);// result_code_mission_completed
				finish();
			} else {
				// - Lugar encontrado result_code_right_place
				// - back pressed, lugar verificado o no
				updateMarkersColor();
			}
		}
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

	void moveCameraToFitAllMarkers(final boolean animateCamera) {
		final View mapView = mapFragment.getView();
		if (mapView == null) {
			return;
		}
		mapView.getViewTreeObserver();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							LatLngBounds.Builder builder = new LatLngBounds.Builder();
							for (Marker marker : markers) {
								builder.include(marker.getPosition());
							}
							if (currentLocation != null) {
								builder.include(currentLocation.getPosition());
							}
							LatLngBounds bounds = builder.build();
							int paddingInPixels = Math.min(mapView.getWidth(),
									mapView.getHeight());
							CameraUpdate newLatLngBounds = CameraUpdateFactory
									.newLatLngBounds(bounds,
											paddingInPixels / 4);
							if (animateCamera) {
								map.animateCamera(newLatLngBounds);
							} else {
								map.moveCamera(newLatLngBounds);
							}
						}
					});
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
		moveCameraToFitAllMarkers(true);
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

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					circle.setRadius((Float) animation.getAnimatedValue());
					circle.setStrokeColor(ColorUtil.setAlpha0to1(
							circle.getStrokeColor(),
							1 - animation.getAnimatedFraction()));
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
				// Display the connection status
				Toast.makeText(MapActivity.this,
						"Disconnected. Please re-connect.", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onConnected(Bundle connectionHint) {
				// Display the connection status
				Toast.makeText(MapActivity.this, "Connected",
						Toast.LENGTH_SHORT).show();
				Location lastLocation = locationClient.getLastLocation();
				if (lastLocation != null && currentLocation == null) {
					loadCurrentPosition(new LatLng(lastLocation.getLatitude(),
							lastLocation.getLongitude()));
				}
				locationClient.requestLocationUpdates(new LocationRequest(),
						new LocationListener() {

							@Override
							public void onLocationChanged(Location location) {
								LatLng position = new LatLng(location
										.getLatitude(), location.getLongitude());
								if (currentLocation == null) {
									loadCurrentPosition(position);
								} else {
									currentLocation.setPosition(position);
								}
							}
						});
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
		locationClient.connect();
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

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra(getPackageName() + ".mission", mission);
		setResult(RESULT_OK, data);
		finish();
	}

}
