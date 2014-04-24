package dk.itu.pervasive.mobile.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import dk.itu.pervasive.mobile.R;

import java.util.ArrayList;

/**
 * Created by centos on 3/27/14.
 */
public class ImageManager {

    private static final int IMAGE_CACHE_PERCENT = 8;

    private LruCache<Integer, Bitmap> _imageCache;

    private static ImageManager _instance;

    private ArrayList<Integer> _imageIds;

    private Context _context;

    private Bitmap _loadingBitmap;

    //private constructor
    private ImageManager() {
        createImageCache();
        _imageIds = new ArrayList<Integer>();
    }

    public static ImageManager getInstance() {
        if (_instance == null)
            _instance = new ImageManager();

        return _instance;
    }

    public void setContext(Context c) {
        _context = c;
    }


    public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            _imageCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromCache(int key) {
        return _imageCache.get(key);
    }


    public void loadImageThumbnailsId() {
        String[] projection = {
                MediaStore.Images.Media._ID,
        };
// Create the cursor pointing to the SDCard
        Cursor cursor = _context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

        if (cursor != null) {
            //remove everything from the hashmap first
            _imageIds.clear();
            int imageIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                _imageIds.add(cursor.getInt(imageIdIndex));
                cursor.moveToNext();
            }
            Log.i("TAG", "Loaded image ids. Count : " + _imageIds.size());
        } else
            Log.i("TAG", "Cursor is null. query was invalid");

    }

    public ArrayList<Integer> getListOfThumbnailIds() {
        return new ArrayList<Integer>(_imageIds);
    }


    public int getImageCount() {
        return _imageIds.size();
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


    public void loadImage(int thumbnailId, ImageView imageView) {

        if (_loadingBitmap == null) {
            _loadingBitmap = decodeSampledBitmapFromResource(_context.getResources(),
                    R.drawable.loading_bitmap, 32, 32);
            Log.i("TAG", "loading bitmap h" + _loadingBitmap.getHeight());
            Log.i("TAG", "loading bitmap w" + _loadingBitmap.getWidth());
        }


        if (_imageCache == null)
            createImageCache();

        Bitmap bitmap = getBitmapFromCache(thumbnailId);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        if (cancelPotentialLoading(thumbnailId, imageView)) {
            CustomImageLoader task = new CustomImageLoader(_context, imageView);
            AsyncDrawable asyncDrawble = new AsyncDrawable(
                    _context.getResources(), _loadingBitmap, task);
            imageView.setImageDrawable(asyncDrawble);
            task.execute(thumbnailId);
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , thumbnailId);
        }
    }


    private static boolean cancelPotentialLoading(int thumbnailId,
                                                  ImageView imageView) {

        CustomImageLoader imageLoader = getImageLoaderFromImageView(imageView);

        if (imageLoader != null) {
            int bitmapUrl = imageLoader.getData();
            if ((bitmapUrl == 0) || bitmapUrl != thumbnailId) {
                imageLoader.cancel(true);
            } else {
                // The same URL is already being loading.
                return false;
            }
        }
        return true;
    }


    public static CustomImageLoader getImageLoaderFromImageView(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getImageLoader();
            }
        }
        return null;
    }


    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                   int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
