package com.example.pratibhaswami.myapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
 
    private GoogleMap mMap;
    private static final String TAG = "Bla";
    PlaceAutocompleteFragment autocompleteFragment;
    PlaceAutocompleteFragment autocompleteFragment1;
    private GoogleApiClient client;
    double sourcelat;
    double sourcelog;
    double destlat;
    double destlog;
    String origin, origins;
    String dest, dests;
	public static final String Userid = "idKey";
	public static final String MyPREFERENCES = "MyPref" ;
	SharedPreferences sharedpreferences;
	String value;
	TextView estfare;
    TextView estfare1;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        estfare = (TextView) findViewById (R.id.estfare);
        estfare1 = (TextView) findViewById (R.id.estfare1);
 
        autocompleteFragment =
                (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
 
        autocompleteFragment1 =
                (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);
 
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                String location = (String) place.getAddress();

                Geocoder geocoder = new Geocoder(getBaseContext());
                try {
                    List addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList != null && addressList.size()>0){
                        Address address = (Address) addressList.get(0);
                        sourcelat = address.getLatitude();
                        sourcelog = address.getLongitude();
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                origin = (String) place.getName();

            }
 
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
 
 
        });
 
 
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                String location = (String) place.getAddress();

                Geocoder geocoder = new Geocoder(getBaseContext());
                try {
                    List addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList != null && addressList.size()>0){
                        Address address = (Address) addressList.get(0);
                        destlat = address.getLatitude();
                        destlog = address.getLongitude();
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        
                        getDirection();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dest = (String) place.getName();

		getestfare();
            }
 
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
		 
        });
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);	
	    value = sharedpreferences.getString(Userid, "");
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyBb8sOnnrIVDsq9dutdLve2Hf5esERl1gA");
        return urlString.toString();
    }

    private void getDirection(){
        //Getting the URL
        String url = makeURL(sourcelat, sourcelog, destlat, destlog);

        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from = new LatLng(sourcelat,sourcelog);
        LatLng to = new LatLng(destlat,destlog);
        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(10)
                    .color(Color.BLUE)
                    .geodesic(true)
            );


        }
        catch (JSONException e) {


        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
 
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
 
        LatLng Delhi = new LatLng(28.614055, 77.211033);
        mMap.addMarker(new MarkerOptions().position(Delhi).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Delhi));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
 
    public void onClick(View view) {
		sendDetails();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page").setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
 
    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }
 
    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    
	
	public void sendDetails(){
		RequestQueue MyRequestQueue = Volley.newRequestQueue(getBaseContext());
        String url = "http://192.168.1.8:8000/api/request";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent i = new Intent(MapsActivity.this, Details.class);
                startActivity(i);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Context context = getApplicationContext();
                Toast.makeText(context, R.string.fail, LENGTH_LONG)
                        .show();
                error.printStackTrace();
            }
        }){
		@Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("id",value);
                params.put("ts","1");
                params.put("slat", String.valueOf(sourcelat));
				params.put("slong", String.valueOf(sourcelog));
            params.put("dlat", String.valueOf(destlat));
            params.put("dlong", String.valueOf(destlog));
                return params;
            }
		};
        MyRequestQueue.add(MyStringRequest);

	}
	
	public String makeURL1 (double lat, double log){
		StringBuilder urlString = new StringBuilder();

		urlString.append(Double.toString(lat));
		urlString.append(",");
		urlString.append(Double.toString(log));

		return String.valueOf(urlString);

	    }

    public void getestfare(){
	origins = makeURL1(sourcelat, sourcelog);
        dests = makeURL1(destlat, destlog);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getBaseContext());
        String url = "http://192.168.1.8:8000/api/estimatefare";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                estfare1.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("origin", origins);
                params.put("dest", dests);
                return params;
            }
        };
        MyRequestQueue.add(MyStringRequest);
	}
	
}

