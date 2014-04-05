package dk.itu.pervasive.mobile.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by centos on 3/27/14.
 */


class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<CustomImageLoader> _imageLoaderTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
                         CustomImageLoader bitmapWorkerTask) {
        super(res, bitmap);
        _imageLoaderTaskReference =
                new WeakReference<CustomImageLoader>(bitmapWorkerTask);
    }

    public CustomImageLoader getImageLoader() {
        return _imageLoaderTaskReference.get();
    }
}
