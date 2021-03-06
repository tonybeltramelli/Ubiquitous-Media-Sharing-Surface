package dk.itu.pervasive.mobile.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import dk.itu.pervasive.mobile.R;
import dk.itu.pervasive.mobile.gallery.GalleryFragment;
import dk.itu.pervasive.mobile.gallery2.GalleryFragment2;
import dk.itu.pervasive.mobile.gallery2.ImageManager2;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;

public class MainActivity extends APrefActivity implements GalleryFragment2.ServiceCallbacks
{

    private boolean isInitialized = false;

    private boolean isActive;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        ImageManager2.getInstance().init(this);
        activateGallery();
        isActive = true;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onStop()
	{
		_unbindService();
		super.onStop();
        isActive = false;
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

        GalleryFragment2 fragment = (GalleryFragment2) this.getFragmentManager().findFragmentByTag(
                GalleryFragment2.FRAGMENT_TAG
        );

        if( fragment == null ){
            fragment = new GalleryFragment2();
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

    @Override
    public void onLoadingFinished() {

        if( !isInitialized ){
            isInitialized = true;
            Log.i("TAG" , "initialized");
            _bindService();
        }
   }

   public boolean isActivityRunning(){
       return isActive;
   }
}
