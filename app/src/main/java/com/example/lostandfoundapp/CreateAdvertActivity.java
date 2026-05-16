package com.example.lostandfoundapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup rgPostType;
    private EditText etName, etPhone, etDescription, etDate, etLocation;
    private Spinner spCategory;
    private ImageView ivSelectedImage;
    private Uri selectedImageUri;
    private DatabaseHelper dbHelper;
    private final Calendar calendar = Calendar.getInstance();
    
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivSelectedImage.setImageURI(uri);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<Intent> autocompleteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    etLocation.setText(place.getAddress());
                    if (place.getLatLng() != null) {
                        currentLatitude = place.getLatLng().latitude;
                        currentLongitude = place.getLatLng().longitude;
                    }
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    // Handle error from Google Places Autocomplete
                    Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Toast.makeText(this, "Search Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    // Log the error for debugging
                    System.out.println("Places API Error: " + status.toString());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Advert");
        }

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Places with the provided API Key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDL_cMNSGsbm4M3SyMIbCfR3DleRjrF4bY");
        }

        rgPostType = findViewById(R.id.rgPostType);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        spCategory = findViewById(R.id.spCategory);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        Button btnSearchAddress = findViewById(R.id.btnSearchAddress);

        // Date Picker Setup
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePicker());

        // Location Setup - Allow manual typing
        etLocation.setFocusableInTouchMode(true);
        etLocation.setFocusable(true);
        etLocation.setOnClickListener(null); 

        // Search Button - Open Google Places Autocomplete
        btnSearchAddress.setOnClickListener(v -> startAutocomplete());

        // Get Current Location Setup
        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        // Setup Spinner
        String[] categories = {"Electronics", "Pets", "Wallets", "Keys", "Bags", "Documents", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnUploadImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnSave.setOnClickListener(v -> saveAdvert());
    }

    private void startAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        autocompleteLauncher.launch(intent);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                updateLocationAddress(currentLatitude, currentLongitude);
            } else {
                Toast.makeText(this, "Unable to get location. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLocationAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                etLocation.setText(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
            etLocation.setText(lat + ", " + lng);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String format = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            etDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAdvert() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        int selectedId = rgPostType.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(selectedId);
        String postType = rbSelected != null ? rbSelected.getText().toString() : "Lost";

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle manual address geocoding if coordinates are still 0.0
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(location, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    currentLatitude = addresses.get(0).getLatitude();
                    currentLongitude = addresses.get(0).getLongitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String savedImagePath = "";
        if (selectedImageUri != null) {
            savedImagePath = saveImageToInternalStorage(selectedImageUri);
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        long id = dbHelper.insertAdvert(postType, name, phone, description, category, date, location, savedImagePath, timestamp, currentLatitude, currentLongitude);

        if (id > 0) {
            Toast.makeText(this, "Advert saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return Uri.fromFile(file).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
