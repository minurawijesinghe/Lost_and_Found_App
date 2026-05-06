package com.example.lostandfoundapp;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup rgPostType;
    private EditText etName, etPhone, etDescription, etDate, etLocation;
    private Spinner spCategory;
    private ImageView ivSelectedImage;
    private Uri selectedImageUri;
    private DatabaseHelper dbHelper;
    private final Calendar calendar = Calendar.getInstance();

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivSelectedImage.setImageURI(uri);
                    ivSelectedImage.setVisibility(View.VISIBLE);
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

        // Date Picker Setup
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePicker());

        // Setup Spinner
        String[] categories = {"Electronics", "Pets", "Wallets", "Keys", "Bags", "Documents", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnUploadImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnSave.setOnClickListener(v -> saveAdvert());
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

        // Image is now optional
        String savedImagePath = "";
        if (selectedImageUri != null) {
            savedImagePath = saveImageToInternalStorage(selectedImageUri);
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        long id = dbHelper.insertAdvert(postType, name, phone, description, category, date, location, savedImagePath, timestamp);

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
