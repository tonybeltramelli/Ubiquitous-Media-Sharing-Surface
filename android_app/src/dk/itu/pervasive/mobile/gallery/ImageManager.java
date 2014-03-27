package dk.itu.pervasive.mobile.gallery;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by centos on 3/27/14.
 */
public class ImageManager {

    private static final int IMAGE_CACHE_PERCENT = 8;

    private LruCache<Integer , Bitmap> _imageCache;

    private static ImageManager _instance;

    //private constructor
    private ImageManager(){
        createImageCache();
    }

    public static ImageManager getInstance(){
        if( _instance == null )
            _instance = new ImageManager();

        return _instance;
    }

    public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            _imageCache.put(key, bitmap);
        }
    }


    public  Bitmap getBitmapFromCache(int key) {
        return _imageCache.get(key);
    }


    public void loadImageThumbnailsId(){

    }




    private void createImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / IMAGE_CACHE_PERCENT;

        _imageCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

    }



}
