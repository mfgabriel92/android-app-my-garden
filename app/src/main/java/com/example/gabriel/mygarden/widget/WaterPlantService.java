package com.example.gabriel.mygarden.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.utils.PlantUtils;

public class WaterPlantService extends IntentService {
    public static final String PLANT_EXTRA_ID = "com.example.gabriel.mygarden.extra.PLANT_EXTRA_ID";
    public static final String ACTION_WATER_PLANT = "com.example.gabriel.mygarden.widget.action.WATER_PLANT";
    public static final String ACTION_UPDATE_PLANT_WIDGETS = "com.example.gabriel.mygarden.widget.action.UPDATE_PLANT_WIDGETS";

    public WaterPlantService() {
        super(WaterPlantService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_WATER_PLANT.equals(action)) {
                int plantId = intent.getIntExtra(PLANT_EXTRA_ID, PlantContract.INVALID_PLANT_ID);
                handleActionWaterPlants(plantId);
            } else if (ACTION_UPDATE_PLANT_WIDGETS.equals(action)) {
                handleActionUpdatePlantWidgets();
            }
        }
    }

    public static void startActionWaterPlant(Context context) {
        Intent intent = new Intent(context, WaterPlantService.class);
        intent.setAction(ACTION_WATER_PLANT);
        context.startService(intent);
    }

    public static void startActionUpdatePlantWidgets(Context context) {
        Intent intent = new Intent(context, WaterPlantService.class);
        intent.setAction(ACTION_UPDATE_PLANT_WIDGETS);
        context.startService(intent);
    }

    private void handleActionWaterPlants(int plantId) {
        Uri uri = ContentUris.withAppendedId(PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build(), plantId);
        long now = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, now);

        getContentResolver().update(
            uri,
            values,
            PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + "> ?",
            new String[]{String.valueOf(now - PlantUtils.MAX_AGE_WITHOUT_WATER)}
        );
    }

    private void handleActionUpdatePlantWidgets() {
        Uri uri = PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(
            uri,
            null,
            null,
            null,
            PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME
        );

        long now = System.currentTimeMillis();
        int imgRes = R.drawable.grass;
        int plantId = PlantContract.INVALID_PLANT_ID;
        boolean shouldWater = false;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            plantId = cursor.getInt(cursor.getColumnIndex(PlantContract.PlantEntry._ID));
            long wateredAt = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
            long createdAt = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME));
            int plantType = cursor.getInt(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE));

            cursor.close();

            imgRes = PlantUtils.getPlantImage(this, now - createdAt, now - wateredAt, plantType);
            shouldWater = (now - wateredAt) > PlantUtils.MIN_AGE_BETWEEN_WATER && (now - wateredAt) > PlantUtils.MAX_AGE_WITHOUT_WATER;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MyGardenWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.my_garden_widget_grid_view);

        MyGardenWidgetProvider.updatePlantWidgets(this, appWidgetManager, imgRes, plantId, shouldWater, appWidgetIds);
    }
}
