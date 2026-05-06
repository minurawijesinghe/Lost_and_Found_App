package com.example.lostandfoundapp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvType, tvName, tvCategory, tvDate, tvLocation, tvPhone, tvDescription, tvTimestamp;
    private ImageView ivImage;
    private DatabaseHelper dbHelper;
    private int advertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Item Details");
        }

        dbHelper = new DatabaseHelper(this);
        advertId = getIntent().getIntExtra("ADVERT_ID", -1);

        tvType = findViewById(R.id.tvDetailType);
        ivImage = findViewById(R.id.ivDetailImage);
        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDate = findViewById(R.id.tvDetailDate);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvPhone = findViewById(R.id.tvDetailPhone);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvTimestamp = findViewById(R.id.tvDetailTimestamp);
        Button btnRemove = findViewById(R.id.btnRemove);

        loadAdvertDetails();

        btnRemove.setOnClickListener(v -> {
            dbHelper.deleteAdvert(advertId);
            Toast.makeText(DetailsActivity.this, "Advert removed successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAdvertDetails() {
        Cursor cursor = dbHelper.getAdvertById(advertId);
        if (cursor != null && cursor.moveToFirst()) {
            tvType.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_TYPE)));
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            tvCategory.setText("Category: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)));
            tvDate.setText("Date: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)));
            tvLocation.setText("Location: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION)));
            tvPhone.setText("Contact: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)));
            tvDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)));
            tvTimestamp.setText("Posted on: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP)));

            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URI));
            if (imageUriString != null && !imageUriString.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imageUriString);
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ivImage.setImageBitmap(bitmap);
                    ivImage.setVisibility(View.VISIBLE);
                    if (inputStream != null) inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    ivImage.setImageResource(android.R.drawable.ic_menu_report_image);
                    ivImage.setVisibility(View.VISIBLE);
                }
            } else {
                ivImage.setVisibility(View.GONE);
            }
            cursor.close();
        }
    }
}
