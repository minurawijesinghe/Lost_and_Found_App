package com.example.lostandfoundapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView lvAdverts;
    private Spinner spFilterCategory;
    private DatabaseHelper dbHelper;
    private List<Advert> advertList;
    private AdvertAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lost and Found Items");
        }

        dbHelper = new DatabaseHelper(this);
        lvAdverts = findViewById(R.id.lvAdverts);
        spFilterCategory = findViewById(R.id.spFilterCategory);

        // Setup Filter Spinner
        String[] categories = {"All", "Electronics", "Pets", "Wallets", "Keys", "Bags", "Documents", "Other"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterCategory.setAdapter(filterAdapter);

        advertList = new ArrayList<>();
        adapter = new AdvertAdapter(this, advertList);
        lvAdverts.setAdapter(adapter);

        spFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAdverts(categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        lvAdverts.setOnItemClickListener((parent, view, position, id) -> {
            Advert selectedAdvert = advertList.get(position);
            Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
            intent.putExtra("ADVERT_ID", selectedAdvert.getId());
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from details
        loadAdverts(spFilterCategory.getSelectedItem().toString());
    }

    private void loadAdverts(String category) {
        advertList.clear();
        Cursor cursor = dbHelper.getAdvertsByCategory(category);

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
                
                // Get lat/lng to match the new Advert constructor
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));

                advertList.add(new Advert(id, type, name, phone, desc, cat, date, loc, uri, ts, lat, lng));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}
