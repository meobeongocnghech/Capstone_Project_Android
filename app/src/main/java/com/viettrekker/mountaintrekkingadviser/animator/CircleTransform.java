package com.viettrekker.mountaintrekkingadviser.animator;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Create circular image by transformation
 *
 * @author LongNC
 * 25/07/2018
 */
public class CircleTransform  implements Transformation {

    /**
     * transform method implement from Transformation interface
     * draw a circle bitmap using canvas from source
     *
     * @param source  source bitmap object
     * @return bitmap object after transform
     */
    @Override
    public Bitmap transform(Bitmap source) {
        //square image size, min of width and height
        int size = Math.min(source.getWidth(), source.getHeight());

        //get position of starting cut point
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        //create square bitmap right in the center of source bitmap
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);

        //remove source object if it is not a square bitmap
        if (squaredBitmap != source) {
            source.recycle();
        }

        //layout bitmap for drawing
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        //initiate and config canvas before draw
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        //draw a circle on square bitmap and paint it on layout bitmap
        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        //free square bitmap and return layout bitmap after draw
        squaredBitmap.recycle();
        return bitmap;
    }

    /**
     * implement by default
     *
     * @return transformation key
     */
    @Override
    public String key() {
        return "circle";
    }
}