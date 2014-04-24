package dk.itu.pervasive.mobile.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import dk.itu.pervasive.mobile.R;

import java.util.ArrayList;

/**
 * Created by centos on 3/28/14.
 */
public class CustomArrayAdapter extends BaseAdapter{

    private Context _context;
    private ArrayList<Integer> _thumbnailIds;
    private LayoutInflater _inflater;


    public CustomArrayAdapter(Context c){
        _context = c;
        _thumbnailIds = ImageManager.getInstance().getListOfThumbnailIds();
        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    private class Holder{
        public ImageView _imageView;
    }


    @Override
    public int getCount() {
        return _thumbnailIds.size();
    }

    @Override
    public Object getItem(int position) {
        return _thumbnailIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return _thumbnailIds.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if( rootView == null ){
            rootView = _inflater.inflate(R.layout.gallery_image_item_layout , parent , false);
            Holder holder = new Holder();
            holder._imageView = (ImageView) rootView;
            rootView.setTag(holder);
        }

        Holder holder = (Holder) rootView.getTag();
        holder._imageView = (ImageView) rootView;

        ImageManager.getInstance().loadImage(_thumbnailIds.get(position) , (ImageView) rootView);
        return rootView;
    }
}
