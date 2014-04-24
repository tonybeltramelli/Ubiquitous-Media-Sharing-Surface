package dk.itu.pervasive.mobile.gallery2;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.activity.MainActivity;

/**
 * Created by centos on 4/1/14.
 */
public class GalleryFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    GridView _gridView;

    public static final String FRAGMENT_TAG = "gallery_fragment_2";

    public interface ServiceCallbacks{
        public void onLoadingFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gallery_fragment_layout , container , false);

        _gridView = (GridView) rootView.findViewById(R.id.grid_view_container);

        CustomCursorAdapter2 adapter = new CustomCursorAdapter2(this.getActivity() , null , 0);

        _gridView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageManager2.getInstance().startCursorLoader(this);
    }



    //methods that work for the cursor loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("GALLERY", "Created new cursor loader");
        return new CursorLoader( this.getActivity() ,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,
                new String[] {MediaStore.Images.Media._ID , MediaStore.Images.Media.DATA },
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("GALLERY", "Cursor loader finished loading");
        ((CustomCursorAdapter2)_gridView.getAdapter()).swapCursor(data);
        ImageManager2.getInstance().setUpImageList(data);
        ((MainActivity)this.getActivity()).onLoadingFinished();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("GALLERY", "Cursos loader was reset");
    }
}
