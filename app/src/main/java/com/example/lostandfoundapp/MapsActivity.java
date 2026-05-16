package com.example.lostandfoundapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private Spinner spRadius;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private List<Advert> allAdverts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Setup Action Bar with Back Button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lost and Found Map");
        }

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        spRadius = findViewById(R.id.spRadius);
        Button btnFilter = findViewById(R.id.btnFilter);

        // Define radius options for search
        String[] radii = {"All", "1 km", "5 km", "10 km"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, radii);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRadius.setAdapter(adapter);

        // Initialize the Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnFilter.setOnClickListener(v -> filterMarkers());
        
        loadAdvertsFromDb();
        requestUserLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Modern way to handle back navigation in AndroidX
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Fetches all advertisements from the SQLite database
    private void loadAdvertsFromDb() {
        allAdverts = new ArrayList<>();
        Cursor cursor = dbHelper.getAllAdverts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_TYPE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String cat = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String loc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
                String uri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URI));
                String ts = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));

                allAdverts.add(new Advert(id, type, name, phone, desc, cat, date, loc, uri, ts, lat, lng));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    // Requests the user's current location and permissions
    private void requestUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                userLocation = location;
                if (mMap != null) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestUserLocation();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        displayMarkersOnMap(allAdverts);
        
        // Center the camera if the location was already fetched
        if (userLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 12));
        }
    }

    // Helper to display a list of adverts as markers on the map
    private void displayMarkersOnMap(List<Advert> adverts) {
        if (mMap == null) return;
        mMap.clear();
        for (Advert advert : adverts) {
            if (advert.getLatitude() != 0 || advert.getLongitude() != 0) {
                LatLng pos = new LatLng(advert.getLatitude(), advert.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(advert.getName())
                        .snippet(advert.getPostType() + ": " + advert.getLocation()));
            }
        }
    }

    // Filters markers based on the radius selected in the Spinner
    private void filterMarkers() {
        String selected = spRadius.getSelectedItem().toString();
        if (selected.equals("All")) {
            displayMarkersOnMap(allAdverts);
            return;
        }

        if (userLocation == null) {
            Toast.makeText(this, "User location not available. Enable GPS and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        double radiusInKm = Double.parseDouble(selected.split(" ")[0]);
        List<Advert> filteredList = new ArrayList<>();

        // Logic: Calculate distance from user to each item and compare against radius
        for (Advert advert : allAdverts) {
            if (advert.getLatitude() != 0 || advert.getLongitude() != 0) {
                float[] results = new float[1];
                // WGS84 ellipsoid calculation for distance in meters
                Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                        advert.getLatitude(), advert.getLongitude(), results);
                
                if (results[0] <= radiusInKm * 1000) {
                    filteredList.add(advert);
                }
            }
        }
        displayMarkersOnMap(filteredList);
    }
}
