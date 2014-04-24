package dk.itu.pervasive.mobile.gallery2;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import dk.itu.pervasive.mobile.activity.MainActivity;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by centos on 4/1/14.
 */
public class ImageManager2 {

    public static final String SDCARD_FOLDER = "Surface-folder";

    private static final int IMAGE_LOADER_ID = 0;

    private static ImageManager2 _instance;

    private Context _context;

    private ConcurrentHashMap<Integer, String> _imageIdsPaths;

    private ImageManager2() {
        _imageIdsPaths = new ConcurrentHashMap<Integer, String>();

        //set up uil
    }

    public static ImageManager2 getInstance() {
        if (_instance == null)
            _instance = new ImageManager2();

        return _instance;
    }

    public void init(Context c) {
        _context = c;
        initImageLoader(_context);
    }


    public void startCursorLoader(LoaderManager.LoaderCallbacks<Cursor> callbacks) {

        ((MainActivity) _context).getLoaderManager()
                .initLoader(IMAGE_LOADER_ID, null, callbacks);
    }

    public ArrayList<String> getImagePaths() {
        return new ArrayList<String>(_imageIdsPaths.values());
    }


    public void insertImageToGallery(String path) {
        String[] parts = path.split("/");

        String fileName = parts[parts.length - 1];
        String bucketName = parts[parts.length - 2];
        //check for regex
        String extension = fileName.split("\\.")[1];
        String name = fileName.split("\\.")[0];
        String description = "Image taken from microsoft surface";

        insertImageToMediaStore(path, name, bucketName, extension, description);
    }

    public void setUpImageList(Cursor cursor) {

        int indexIds = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int indexPath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                _imageIdsPaths = new ConcurrentHashMap<Integer, String>();

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    _imageIdsPaths.put(cursor.getInt(indexIds), cursor.getString(indexPath));
                    cursor.moveToNext();
                }
            }

        }
    }

    private void insertImageToMediaStore(String path, String fileName, String bucketName
            , String fileType , String description ) {

        ContentValues values = new ContentValues();

        if( fileType.equals("jpg") || fileType.equals("JPG"))
            fileType = "jpeg";

        Log.i("GALLERY" , "path : " + path );
        Log.i("GALLERY" , "filename : " + fileName );
        Log.i("GALLERY" , "type : " + fileType );
        Log.i("GALLERY" , "bucketname : " + bucketName );

        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.BUCKET_ID, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, bucketName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + fileType);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.MediaColumns.DATA, path);

        _context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    private void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(4)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize CustomImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


}
