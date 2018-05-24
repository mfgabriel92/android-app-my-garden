package com.example.gabriel.mygarden.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.ui.MainActivity;
import com.example.gabriel.mygarden.ui.PlantDetailActivity;

public class MyGardenWidgetProvider extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int imgRes, int plantId, boolean shouldWater, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews views;

        if (width < 300) {
            views = getSinglePlantRemoteView(context, imgRes, plantId, shouldWater);
        } else {
            views = getGardenGridRemoteView(context);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WaterPlantService.startActionUpdatePlantWidgets(context);
    }

    @Override
    public void onEnabled(Context context) { }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        WaterPlantService.startActionUpdatePlantWidgets(context);
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager, int imgRes, int plantId, boolean shouldWater, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, plantId, shouldWater, appWidgetId);
        }
    }

    private static RemoteViews getSinglePlantRemoteView(Context context, int imgRes, int plantId, boolean shouldWater) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_garden_widget_provider);

        views.setImageViewResource(R.id.widget_img_plant_image, imgRes);
        views.setOnClickPendingIntent(R.id.widget_img_water_button, startWaterPlant(context, plantId));
        views.setOnClickPendingIntent(R.id.widget_img_plant_image, startOnClickOnPlantWidget(context, plantId));

        if (shouldWater) {
            views.setViewVisibility(R.id.widget_img_water_button, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.widget_img_water_button, View.INVISIBLE);
        }

        return views;
    }

    private static RemoteViews getGardenGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.id.my_garden_widget_grid_view);

        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.my_garden_widget_grid_view, intent);

        Intent appIntent = new Intent(context, PlantDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        views.setPendingIntentTemplate(R.id.my_garden_widget_grid_view, appPendingIntent);
        views.setEmptyView(R.id.my_garden_widget_grid_view, R.id.widget_empty_view);

        return views;
    }

    private static PendingIntent startWaterPlant(Context context, int plantId) {
        Intent intent = new Intent(context, WaterPlantService.class);
        intent.putExtra(WaterPlantService.PLANT_EXTRA_ID, plantId);
        intent.setAction(WaterPlantService.ACTION_WATER_PLANT);

        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static PendingIntent startOnClickOnPlantWidget(Context context, int plantId) {
        Intent intent;

        if (plantId == PlantContract.INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(WaterPlantService.PLANT_EXTRA_ID, plantId);
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}