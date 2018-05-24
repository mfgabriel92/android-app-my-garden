package com.example.gabriel.mygarden.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.widget.WaterPlantService;

public class AddPlantActivity extends AppCompatActivity {
    private RecyclerView mTypesRecyclerView;
    private PlantTypesAdapter mTypesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        mTypesAdapter = new PlantTypesAdapter(this);
        mTypesRecyclerView = findViewById(R.id.plant_types_recycler_view);
        mTypesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTypesRecyclerView.setAdapter(mTypesAdapter);
    }

    public void onPlantTypeClick(View view) {
        ImageView imgView = view.findViewById(R.id.img_plant_type);
        int plantType = (int) imgView.getTag();
        long timeNow = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContract.PlantEntry.COLUMN_PLANT_TYPE, plantType);
        contentValues.put(PlantContract.PlantEntry.COLUMN_CREATION_TIME, timeNow);
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);

        getContentResolver().insert(PlantContract.PlantEntry.CONTENT_URI, contentValues);
        WaterPlantService.startActionUpdatePlantWidgets(this);
        finish();
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}