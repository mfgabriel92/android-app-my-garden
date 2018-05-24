package com.example.gabriel.mygarden.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.provider.PlantContract;
import com.example.gabriel.mygarden.utils.PlantUtils;

import java.util.Locale;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    MainActivityAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }
    
    @NonNull
    @Override
    public MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.plant_list_item, parent, false);
        return new MainActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int plantId = mCursor.getInt(mCursor.getColumnIndex(PlantContract.PlantEntry._ID));
        int plantType = mCursor.getInt(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE));
        long createdAt = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME));
        long wateredAt = mCursor.getLong(mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();

        int imgRes = PlantUtils.getPlantImage(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);

        holder.mImgPlantImage.setImageResource(imgRes);
        holder.mTvPlantName.setText(String.format(Locale.getDefault(), "Plant #%d", plantId));
        holder.mImgPlantImage.setTag(plantId);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null && mCursor != newCursor) {
            mCursor.close();
        }

        mCursor = newCursor;
        this.notifyDataSetChanged();
    }

    class MainActivityViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgPlantImage;
        private TextView mTvPlantName;

        MainActivityViewHolder(View itemView) {
            super(itemView);
            mImgPlantImage = itemView.findViewById(R.id.img_plant_image);
            mTvPlantName = itemView.findViewById(R.id.tv_plant_name);
        }
    }
}