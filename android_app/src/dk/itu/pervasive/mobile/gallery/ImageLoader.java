package dk.itu.pervasive.mobile.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by centos on 3/27/14.
 */
public class ImageLoader extends AsyncTask<Integer, Void, Bitmap>{

    private final WeakReference<ImageView> _imageViewReference;
    private int _data = 0;
    private Context _context;

    public ImageLoader(Context c , ImageView i) {
        _imageViewReference = new WeakReference<ImageView>(i);
        _context = c;
    }


    @Override
    protected Bitmap doInBackground(Integer... params) {

        _data = params[0];
        Bitmap thumbnail =  MediaStore.Images.Thumbnails.getThumbnail(_context.getContentResolver(),
                (long)_data , MediaStore.Images.Thumbnails.MICRO_KIND , null);

        if( thumbnail != null ){
            Log.i("TAG" , "thumbnail not null");
            ImageManager.getInstance().addBitmapToMemoryCache(_data, thumbnail);
        }
        else
            Log.i("TAG" , "thumbnail null");


        return thumbnail;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (this.isCancelled())
            bitmap = null;

        if (_imageViewReference != null && bitmap != null) {
            final ImageView imageView = _imageViewReference.get();

            ImageLoader bitmapDownloaderTask = ImageManager.getImageLoaderFromImageView(imageView);
            // Change bitmap only if this process is still associated with
            if (this == bitmapDownloaderTask)
                imageView.setImageBitmap(bitmap);
        }
    }

    public int getData(){
        return _data;
    }



}
