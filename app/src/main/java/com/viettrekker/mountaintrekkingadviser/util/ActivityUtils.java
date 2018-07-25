package com.viettrekker.mountaintrekkingadviser.util;

import android.content.Context;
import android.content.Intent;

/**
 * Utilities for switching between activities
 *
 * @author LongNC
 * 25/07/2018
 */
public class ActivityUtils {

    /**
     * Switch between 2 activities
     *
     * @param context Current activity context
     * @param cls Destination activity class
     * @param clearTask flag check whether clear activity stack or not
     */
    public static void changeActivity(Context context, Class cls, boolean clearTask) {
        Intent i = new Intent(context, cls);
        if (clearTask) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(i);
    }
}
