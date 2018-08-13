package com.viettrekker.mountaintrekkingadviser.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.viettrekker.mountaintrekkingadviser.GlideApp;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class ImageUtils {
    public static int TWO_VERTICAL = 0;
    public static int TWO_HORIZONTAL = 1;

    public static int measureGridTwoItems(float[] size1, float[] size2) {
        float ratio1 = size1[0] / size1[1];
        float ratio2 = size2[0] / size2[1];
        if (ratio1 < 1 && ratio2 < 1) {
            return TWO_HORIZONTAL;
        } else {
            return TWO_VERTICAL;
        }
    }

//    public static int[] getImageSize(String path, Context context) throws IOException {
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
//        BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
//        return new int[]{options.outHeight, options.outWidth};
//    }

    public static float getRatio(float[] size) {
        return size[0] / size[1];
    }
}
