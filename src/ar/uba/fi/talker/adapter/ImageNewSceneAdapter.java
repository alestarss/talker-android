package ar.uba.fi.talker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ar.uba.fi.talker.R;
import ar.uba.fi.talker.dao.ImagesDao;
import ar.uba.fi.talker.fragment.OutdoorScenarioDialogFragment;

public class ImageNewSceneAdapter extends BaseAdapter {

	    private Context mContext;
		private OutdoorScenarioDialogFragment parentFragment;
	    private static Long itemSelectedId;
	    private static int pos;
	    private static int RESULT_LOAD_IMAGE = 1;
	    private List<View> gridItems = new ArrayList<View>();
	    
	    public ImageNewSceneAdapter(Context c) {
	        mContext = c;
	    }

	    @Override
	    public int getCount() {
	        return ImagesDao.getScenarioSize();
	    }
	    @Override
	    public Object getItem(int position) {
	    	View grid = new View(mContext);
	    	return (ImageView)grid.findViewById(ImagesDao.getScenarioImageByPos(position));
	    }
	    @Override
	    public long getItemId(int position) {
	    	return ImagesDao.getScenarioImageByPos(position);
	    }
	    
	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {	          
			final View gridItem = addItem(position, convertView, parent);
			gridItems.add(gridItem);
			TextView textView = (TextView) gridItem.findViewById(R.id.text);
			ImageView imageView = (ImageView) gridItem.findViewById(R.id.image);
			textView.setText(mContext.getResources().getString(ImagesDao.getScenarioNameByPos(position)));
			imageView.setImageResource(ImagesDao.getScenarioImageByPos(position));
			//esto hay que ponerselo a la grilla o a cada imageView segun corresponda
			
			imageView.setOnClickListener(new OnClickListener() {
			  

			@Override
			  public void onClick(View v) {

					Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
					
					if (position == 5) {
						Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
						parentFragment.startActivityForResult(i, RESULT_LOAD_IMAGE);						
					}
					else{
						ImageButton startScenarioBttn = (ImageButton) ((ActionBarActivity) mContext).findViewById(R.id.new_scene_start);
						startScenarioBttn.setEnabled(true);
						ImageButton editNameScenarioBttn = (ImageButton) ((ActionBarActivity) mContext).findViewById(R.id.new_scene_edit_scenario_name);
						editNameScenarioBttn.setEnabled(true);
						v.setSelected(true);
						itemSelectedId = getItemId(position);
						pos = position;
					}					
				}
			});
	        return gridItem;
	    }
	    
		public static Long getItemSelectedId() {
			return itemSelectedId;
		}
		public static long getPosition() {
			return pos;
		}

		public void setParentFragment(OutdoorScenarioDialogFragment outdoorScenarioDialogFragment) {
			this.parentFragment = outdoorScenarioDialogFragment;
		}

		public View setItem(View gridItem,String text,Bitmap imageBitmap){
			TextView textView = (TextView) gridItem.findViewById(R.id.text);
 	        ImageView imageView = (ImageView)gridItem.findViewById(R.id.image);
 	        textView.setText(text); 	        
 	        imageView.setImageBitmap(imageBitmap); 	        
			return gridItem;
		}
		
		public View setItem(View gridItem,String text){
			TextView textView = (TextView) gridItem.findViewById(R.id.text);
 	        textView.setText(text);        
			return gridItem;
		}
		
		public View addItem(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  	      	View gridItem=null;	  
  	      	if (convertView == null) {
  	    	  gridItem = new View(mContext);
  	          gridItem = inflater.inflate(R.layout.row_grid, parent, false);
  	      	} else {
  	      		gridItem = (View) convertView;
  	      	}  	      
  	      	return gridItem;
		}

		public View getItemGrid(int position){
			return this.gridItems.get(position);
		}

}
