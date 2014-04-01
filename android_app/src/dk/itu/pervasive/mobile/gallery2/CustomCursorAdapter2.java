package dk.itu.pervasive.mobile.gallery2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import dk.itu.pervasive.mobile.R;

/**
 * Created by centos on 4/1/14.
 */
public class CustomCursorAdapter2 extends CursorAdapter {
    Context _context;

    LayoutInflater inf;

    DisplayImageOptions options;

    public CustomCursorAdapter2(Context context, Cursor c, int flags) {
        super(context, c, flags);
        _context = context;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(_context.getResources().getDrawable(R.drawable.loading_bitmap))
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .build();

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inf.inflate( R.layout.gallery_image_item_layout , viewGroup , false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView iv = (ImageView) view.findViewById(R.id.image_view_item);

        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        String path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(index)).toString();

        ImageLoader.getInstance().displayImage(path, iv, options);
    }
}
