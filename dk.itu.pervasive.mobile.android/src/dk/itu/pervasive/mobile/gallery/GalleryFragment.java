package dk.itu.pervasive.mobile.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import dk.itu.pervasive.mobile.R;

/**
 * Created by centos on 3/28/14.
 */
public class GalleryFragment extends Fragment {


    public static final String FRAGMENT_TAG = "itu.dk.gallery_gragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gallery_fragment_layout, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_container);

        ImageManager.getInstance().setContext(this.getActivity());
        ImageManager.getInstance().loadImageThumbnailsId();

        CustomArrayAdapter adapter = new CustomArrayAdapter(this.getActivity());
        gridView.setAdapter(adapter);



        return rootView;
    }
}
