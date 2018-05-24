package com.example.gabriel.mygarden.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabriel.mygarden.R;
import com.example.gabriel.mygarden.utils.PlantUtils;

public class PlantTypesAdapter extends RecyclerView.Adapter<PlantTypesAdapter.PlantViewHolder> {
    private Context mContext;
    private TypedArray mPlantTypes;

    PlantTypesAdapter(Context context) {
        mContext = context;
        Resources res = mContext.getResources();
        mPlantTypes = res.obtainTypedArray(R.array.plant_types);
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.plant_types_list_item, parent, false);

        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        int imgRes = PlantUtils.getPlantImageResource(
            mContext,
            position,
            PlantUtils.PlantStatus.ALIVE,
            PlantUtils.PlantSize.FULLY_GROWN
        );

        holder.mImgPlantType.setImageResource(imgRes);
        holder.mTvPlantType.setText(PlantUtils.getPlantTypeName(mContext, position));
        holder.mImgPlantType.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (mPlantTypes == null) {
            return 0;
        }

        return mPlantTypes.length();
    }

    class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgPlantType;
        TextView mTvPlantType;

        PlantViewHolder(View itemView) {
            super(itemView);
            mImgPlantType = itemView.findViewById(R.id.img_plant_type);
            mTvPlantType = itemView.findViewById(R.id.tv_plant_type);
        }
    }
}