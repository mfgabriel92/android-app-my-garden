package com.example.gabriel.mygarden.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.widget.WaterPlantService;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GARDEN_LOADER_ID = 100;
    private MainActivityAdapter mAdapter;
    private RecyclerView mRvMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRvMainActivity = findViewById(R.id.rv_main_activity);
        mRvMainActivity.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new MainActivityAdapter(this, null);
        mRvMainActivity.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(GARDEN_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            this,
            PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build(),
            null,
            null,
            null,
            PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    public void onPlantClick(View view) {
        int plantId = (int) view.findViewById(R.id.img_plant_image).getTag();

        Intent intent = new Intent(this, PlantDetailActivity.class);
        intent.putExtra(WaterPlantService.PLANT_EXTRA_ID, plantId);

        startActivity(intent);
    }

    public void onAddFabClick(View view) {
        Intent intent = new Intent(this, AddPlantActivity.class);
        startActivity(intent);
    }
}
