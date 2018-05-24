package com.example.gabriel.mygarden.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.example.gabriel.mygarden.R;

public class PlantUtils {
    private static final long MINUTE_MILLISECONDS = 1000 * 60;
    private static final long HOUR_MILLISECONDS = MINUTE_MILLISECONDS * 60;
    private static final long DAY_MILLISECONDS = HOUR_MILLISECONDS * 24;
    private static final long TINY_AGE = 0L;
    private static final long JUVENILE_AGE = DAY_MILLISECONDS;
    private static final long FULLY_GROWN_AGE = DAY_MILLISECONDS * 2;

    public static final long MIN_AGE_BETWEEN_WATER = HOUR_MILLISECONDS * 2;
    public static final long DANGER_AGE_WITHOUT_WATER = HOUR_MILLISECONDS * 6;
    public static final long MAX_AGE_WITHOUT_WATER = HOUR_MILLISECONDS * 12;

    public enum PlantStatus {
        ALIVE,
        DYING,
        DEAD
    }

    public enum PlantSize {
        TINY,
        JUVENILE,
        FULLY_GROWN
    }

    public static int getPlantImage(Context context, long plantAge, long waterAge, int type) {
        PlantStatus status = PlantStatus.ALIVE;

        if (waterAge > DANGER_AGE_WITHOUT_WATER) {
            status = PlantStatus.DYING;
        }

        if (waterAge > MAX_AGE_WITHOUT_WATER) {
            status = PlantStatus.DEAD;
        }

        if (plantAge > FULLY_GROWN_AGE) {
            return getPlantImageResource(context, type, status, PlantSize.FULLY_GROWN);
        } else if (plantAge > JUVENILE_AGE) {
            return getPlantImageResource(context, type, status, PlantSize.JUVENILE);
        } else if (plantAge > TINY_AGE) {
            return getPlantImageResource(context, type, status, PlantSize.TINY);
        } else {
            return R.drawable.art_empty_pot;
        }
    }

    public static int getPlantImageResource(Context context, int type, PlantStatus status, PlantSize size) {
        TypedArray plantTypes = context.getResources().obtainTypedArray(R.array.plant_types);
        String resName = plantTypes.getString(type);

        switch (status) {
            case DYING:
                resName += "_danger";
                break;
            case DEAD:
                resName += "_dead";
                break;
        }

        switch (size) {
            case TINY:
                resName += "_1";
                break;
            case JUVENILE:
                resName += "_2";
                break;
            case FULLY_GROWN:
                resName += "_3";
                break;
        }

        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    public static String getPlantTypeName(Context context, int type) {
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        String resName = plantTypes.getString(type);
        int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());

        try {
            return context.getResources().getString(resId);
        } catch (Resources.NotFoundException ex) {
            return context.getResources().getString(R.string.unknown_type);
        }
    }

    public static int getDisplayAgeNumber(long milliSeconds) {
        int days = (int) (milliSeconds / DAY_MILLISECONDS);

        if (days >= 1) {
            return days;
        }

        int hours = (int) (milliSeconds / HOUR_MILLISECONDS);

        if (hours >= 1) {
            return hours;
        }

        return (int) (milliSeconds / MINUTE_MILLISECONDS);
    }

    public static String getDisplayAgeUnit(Context context, long milliSeconds) {
        int days = (int) (milliSeconds / DAY_MILLISECONDS);

        if (days >= 1) {
            return context.getResources().getQuantityString(R.plurals.day, days);
        }

        int hours = (int) (milliSeconds / HOUR_MILLISECONDS);

        if (hours >= 1) {
            return context.getResources().getQuantityString(R.plurals.hour, hours);
        }

        int minutes = (int) (milliSeconds / MINUTE_MILLISECONDS);
        return context.getResources().getQuantityString(R.plurals.min, minutes);
    }
}
