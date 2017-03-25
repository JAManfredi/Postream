package com.jm.apps.postream.utilities;

/**
 * Created by Jared12 on 3/25/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.codepath.apps.postream.R;

public class BorderedCircleTransform extends BitmapTransformation {
    private int BORDER_COLOR = Color.DKGRAY;
    private final int BORDER_RADIUS = 1;

    public BorderedCircleTransform(Context context) {
        super(context);
        BORDER_COLOR = ContextCompat.getColor(context, R.color.colorDarkGray);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int size = Math.min(toTransform.getWidth(), toTransform.getHeight());

        int x = (toTransform.getWidth() - size) / 2;
        int y = (toTransform.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(toTransform, x, y, size, size);
        if (squaredBitmap != toTransform) {
            toTransform.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, toTransform.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;

        // Prepare the background
        Paint paintBg = new Paint();
        paintBg.setColor(BORDER_COLOR);
        paintBg.setAntiAlias(true);

        canvas.drawColor(Color.WHITE);

        // Draw the background circle
        canvas.drawCircle(r, r, r, paintBg);

        // Draw the image smaller than the background so a little border will be seen
        canvas.drawCircle(r, r, r - BORDER_RADIUS, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String getId() {
        return "circle";
    }
}