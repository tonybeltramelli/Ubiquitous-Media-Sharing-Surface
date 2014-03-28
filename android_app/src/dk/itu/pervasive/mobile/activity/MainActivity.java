package dk.itu.pervasive.mobile.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.gallery.GalleryFragment;

public class MainActivity extends APrefActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        activateGallery();

		_bindService();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



    public void activateGallery(){
        TextView text = (TextView) this.findViewById(R.id.textViewHome);
        FrameLayout fragmentContainer = (FrameLayout) this.findViewById(R.id.fragment_container);

        text.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        GalleryFragment fragment = (GalleryFragment) this.getFragmentManager().findFragmentByTag(
                GalleryFragment.FRAGMENT_TAG
        );

        if( fragment == null ){
            fragment = new GalleryFragment();
            this.getFragmentManager().beginTransaction().
                    add(R.id.fragment_container , fragment , GalleryFragment.FRAGMENT_TAG)
                    .commit();
        }




    }

    public void deactivateGallery(){
        TextView text = (TextView) this.findViewById(R.id.textViewHome);
        FrameLayout fragmentContainer = (FrameLayout) this.findViewById(R.id.fragment_container);

        text.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);


    }
}
