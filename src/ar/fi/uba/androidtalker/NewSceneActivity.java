package ar.fi.uba.androidtalker;

import java.io.ByteArrayOutputStream;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import ar.fi.uba.androidtalker.fragment.innerScenariDialogFragment;

public class NewSceneActivity extends ActionBarActivity {

//	int idSelected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_scenes);

	    final GridView gridview = (GridView) findViewById(R.id.gridView);
	    gridview.setAdapter(new ImageNewSceneAdapter(this));
	    
		Button exitBttn = (Button) findViewById(R.id.new_scene_exit);
		Button innerBttn = (Button) findViewById(R.id.new_scene_inner);
		Button startScenarioBttn = (Button) findViewById(R.id.new_scene_start);
		
		exitBttn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				System.exit(0);
			}
		});
		FragmentManager fm = getFragmentManager();
		innerScenariDialogFragment newFragment = (innerScenariDialogFragment)fm.findFragmentById(R.id.fragment1);
        
		FragmentTransaction tran = fm.beginTransaction();
		tran.hide(newFragment);
		tran.commit();
		innerBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageNewSceneAdapter adapter = ((ImageNewSceneAdapter)gridview.getAdapter());
				long imageViewId = adapter.getItemSelectedId();
				Log.i("ID", " "+ imageViewId);
				FragmentManager fm = getFragmentManager();
				innerScenariDialogFragment newFragment = (innerScenariDialogFragment)fm.findFragmentById(R.id.fragment1);
		        
				newFragment.setSelectId(imageViewId);
				//newFragment.update();
				
				FragmentTransaction tran = fm.beginTransaction();
				tran.show(newFragment).commit();
			}
		});
		
		startScenarioBttn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageNewSceneAdapter adapter = ((ImageNewSceneAdapter)gridview.getAdapter());
				long imageViewId = adapter.getItemSelectedId();
				byte[] bytes = transformImage(imageViewId); 

				Bundle extras = new Bundle();
				extras.putByteArray("BMP",bytes);
				Intent intent = new Intent(getApplicationContext(), CanvasActivity.class);
				intent.putExtras(extras);
				startActivity(intent);
			}

			private byte[] transformImage(long imageViewId) {
				Bitmap image = BitmapFactory.decodeResource(getResources(),(int) imageViewId);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
		        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
		        byte[] bytes = stream.toByteArray();
				return bytes;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scenes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
