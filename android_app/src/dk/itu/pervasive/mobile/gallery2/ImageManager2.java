package dk.itu.pervasive.mobile.gallery2;

import android.app.LoaderManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import dk.itu.pervasive.mobile.activity.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

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


    public void insertImageToMediaStore(String path) {

    }

    public void setUpImageList( Cursor cursor ){

        int indexIds = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int indexPath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        if ( cursor != null ){
            if( cursor.getCount() > 0){
                _imageIdsPaths = new ConcurrentHashMap<Integer, String>();

                cursor.moveToFirst();
                while (cursor.isAfterLast()){
                    _imageIdsPaths.put(cursor.getInt(indexIds) , cursor.getString(indexPath));
                    cursor.moveToNext();
                }
            }

        }
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
