package co.techmov.checkout.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import co.techmov.checkout.R;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.util.UIHelper;
import io.realm.Realm;

/**
 * Created by victor on 09-04-15.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener{
    private GoogleMap map;
    private String TAG = MapActivity.class.getSimpleName();
    protected GoogleApiClient mGoogleApiClient;
    private LatLng start = null;
    private LatLng end = null;
    private ProgressDialog progress;
    private Polyline polyline;
    boolean enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle(Realm.getInstance(this).where(Login.class).findFirst().getUsername());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String startPoint = bundle.getString("startPoint");
        String endPoint = bundle.getString("endPoint");

        if(endPoint==null || endPoint.trim().equals("") || endPoint.trim().equals("0")){
            UIHelper.alertDialog(MapActivity.this, null, "No podemos crear la ruta").show();
        } else {
            if(bundle.getBoolean("haveAllPoints")){
                String[] point = startPoint.split(",");
                start = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
                point = endPoint.split(",");
                end = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
                configureGoogleGeo();
            }else{
                if(endPoint!=null){
                    String[] point = endPoint.split(",");
                    if(point.length==2){
                        end = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
                        configureGoogleGeo();
                    }
                }else{
                    finish();
                }
            }
        }
    }

    private void configureGoogleGeo() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Places.GEO_DATA_API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override public void onConnected(Bundle bundle) {}
                    @Override public void onConnectionSuspended(int i) {}
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, connectionResult.toString());
                    }
                }).build();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if(mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }
        });

        if(start == null){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5*60, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if(enable){
                                start = new LatLng(location.getLatitude(), location.getLongitude());
                                Routing routing = new Routing.Builder()
                                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                                        .withListener(MapActivity.this).waypoints(start, end).build();
                                try{
                                    progress = ProgressDialog.show(MapActivity.this, "Espere..", "Calculando ruta", true);
                                    routing.execute();
                                }catch (WindowManager.BadTokenException e){
                                    Log.d("EXCEPTION","Talvez la actividad se cerro repentinamente.");
                                    finish();
                                }
                            }
                            enable = false;
                        }
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}
                        @Override
                        public void onProviderEnabled(String provider) {}
                        @Override
                        public void onProviderDisabled(String provider) {}
                    });

        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this).waypoints(start, end).build();
            progress = ProgressDialog.show(this, "Espere..", "Calculando ruta", true);
            routing.execute();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onRoutingFailure() {
        progress.dismiss();
    }
    @Override
    public void onRoutingStart() {}

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.d("MENU","ITEM "+item.getItemId() );
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        progress.dismiss();

        CameraUpdate defaultPoint = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        MarkerOptions markerOptions0 = new MarkerOptions();
        markerOptions0.position(start);
        markerOptions0.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_point_a));

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(end);
        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_point_b));

        map.moveCamera(defaultPoint);
        map.animateCamera(zoom);
        map.addMarker(markerOptions0);
        map.addMarker(markerOptions1);

        if(polyline!=null)
            polyline.remove();

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.salmon));
        polyOptions.width(10);
        polyOptions.addAll(polylineOptions.getPoints());
        polyline = map.addPolyline(polyOptions);
    }

    @Override
    public void onRoutingCancelled() {}
}


//        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
//
//        locationManager.requestLocationUpdates(
//            LocationManager.NETWORK_PROVIDER, 5000, 0,
//            new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
//                    map.moveCamera(center);
//                    map.animateCamera(zoom);
//                }
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {}
//                @Override
//                public void onProviderEnabled(String provider) {}
//                @Override
//                public void onProviderDisabled(String provider) {}
//            });
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//            3000, 0, new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
//                    map.moveCamera(center);
//                    map.animateCamera(zoom);
//                }
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {}
//                @Override
//                public void onProviderEnabled(String provider) {}
//                @Override
//                public void onProviderDisabled(String provider) {}
//            });
