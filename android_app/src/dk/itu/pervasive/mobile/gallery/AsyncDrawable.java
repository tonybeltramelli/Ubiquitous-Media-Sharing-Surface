package dk.itu.pervasive.mobile.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by centos on 3/27/14.
 */


class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<ImageLoader> _imageLoaderTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
                         ImageLoader bitmapWorkerTask) {
        super(res, bitmap);
        _imageLoaderTaskReference =
                new WeakReference<ImageLoader>(bitmapWorkerTask);
    }

    public ImageLoader getImageLoader() {
        return _imageLoaderTaskReference.get();
    }
}
