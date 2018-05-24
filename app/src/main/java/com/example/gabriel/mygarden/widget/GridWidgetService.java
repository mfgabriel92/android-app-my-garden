package com.example.gabriel.mygarden.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.ui.PlantDetailActivity;
import com.example.gabriel.mygarden.utils.PlantUtils;

import java.util.Locale;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Cursor mCursor;

    GridRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = mContext.getContentResolver().query(
            PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build(),
            null,
            null,
            null,
            PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) {
            return null;
        }

        mCursor.moveToPosition(position);

        int plantId = mCursor.getInt(mCursor.getColumnIndex(PlantContract.PlantEntry._ID));
        long wateredAt = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long createdAt = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME));
        int plantType = mCursor.getInt(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE));
        long now = System.currentTimeMillis();

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.my_garden_widget_provider);
        int imgRes = PlantUtils.getPlantImage(mContext, now - createdAt, now - wateredAt, plantType);

        views.setImageViewResource(R.id.widget_img_plant_image, imgRes);
        views.setViewVisibility(R.id.widget_img_water_button, View.GONE);

        Bundle extras = new Bundle();
        extras.putInt(WaterPlantService.PLANT_EXTRA_ID, plantId);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        views.setOnClickFillInIntent(R.id.widget_img_plant_image, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
