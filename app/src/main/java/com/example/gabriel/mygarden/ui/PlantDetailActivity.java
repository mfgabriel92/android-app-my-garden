package com.example.gabriel.mygarden.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.utils.PlantUtils;
import com.example.gabriel.mygarden.widget.WaterPlantService;

import java.util.Locale;

public class PlantDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ImageView mImgPlantDetailImage;
    private TextView mTvPlantDetailName;
    private TextView mTvPlantAgeNumber;
    private TextView mTvPlantAgeUnit;
    private TextView mTvLastWateredNumber;
    private TextView mTvLastWateredUnit;
    private WaterLevelView mWaterLevel;
    private static final int SINGLE_LOADER_ID = 200;
    public int mPlantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        mPlantId = getIntent().getIntExtra(WaterPlantService.PLANT_EXTRA_ID, PlantContract.INVALID_PLANT_ID);

        mImgPlantDetailImage = findViewById(R.id.img_plant_detail_image);
        mTvPlantDetailName = findViewById(R.id.tv_plant_detail_name);
        mTvPlantAgeNumber = findViewById(R.id.tv_plant_age_number);
        mTvPlantAgeUnit = findViewById(R.id.tv_plant_age_unit);
        mTvLastWateredNumber = findViewById(R.id.tv_last_watered_number);
        mTvLastWateredUnit = findViewById(R.id.tv_last_watered_unit);
        mWaterLevel = findViewById(R.id.water_level);

        getSupportLoaderManager().initLoader(SINGLE_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            this,
            ContentUris.withAppendedId(PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build(), mPlantId),
            null,
            null,
            null,
            null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        cursor.moveToFirst();

        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int planTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
        int plantType = cursor.getInt(planTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();
        int plantImgRes = PlantUtils.getPlantImage(this, timeNow - createdAt, timeNow - wateredAt, plantType);

        mImgPlantDetailImage.setImageResource(plantImgRes);
        mTvPlantDetailName.setText(String.valueOf(String.format(Locale.getDefault(), "Plant #%d", mPlantId)));
        mTvPlantAgeNumber.setText(String.valueOf(PlantUtils.getDisplayAgeNumber(timeNow - createdAt)));
        mTvPlantAgeUnit.setText(PlantUtils.getDisplayAgeUnit(this, timeNow - createdAt));
        mTvLastWateredNumber.setText(String.valueOf(PlantUtils.getDisplayAgeNumber(timeNow - wateredAt)));
        mTvLastWateredUnit.setText(PlantUtils.getDisplayAgeUnit(this, timeNow - wateredAt));

        int waterPercent = 100 - ((int) (100 * (timeNow - wateredAt) / PlantUtils.MAX_AGE_WITHOUT_WATER));

        mWaterLevel.setValue(waterPercent);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void onBackButtonClick(View view) {
        finish();
    }

    public void onWaterButtonClick(View view) {
        Uri uri = ContentUris.withAppendedId(PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build(), mPlantId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        cursor.moveToFirst();

        long lastWatered = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();

        if ((timeNow - lastWatered) > PlantUtils.MAX_AGE_WITHOUT_WATER) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(uri, contentValues, null, null);

        WaterPlantService.startActionUpdatePlantWidgets(this);

        cursor.close();
    }

    public void onCutButtonClick(View view) {
        getContentResolver().delete(
            ContentUris.withAppendedId(PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build(), mPlantId),
            null,
            null
        );

        WaterPlantService.startActionUpdatePlantWidgets(this);

        finish();
    }
}